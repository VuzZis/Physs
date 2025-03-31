package com.skoow.physs.gen;


import com.moandjiezana.toml.Toml;
import com.skoow.physs.parsing.lexer.TokenInfo;
import com.skoow.physs.parsing.lexer.TokenManager;
import com.skoow.physs.util.FileClassCompiler;
import com.squareup.javapoet.*;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MetaGenerator {

    public static final String classesFile = "ast.toml";
    public static final String tokensFile = "tokens.toml";
    public static final String genPackage = "com.skoow.physs.gen";
    public static final String physsPackage = "com.skoow.physs";
    public static Toml toml;

    public static void main(String[] args) throws IOException {
        Path outputPath = Paths.get("src/main/java");

        File[] gens = Paths.get("src/main/java/com/skoow/physs/gen/ast").toFile().listFiles(f -> !f.getName().equals("ClassGen.java"));
        for (File gen : gens)
            gen.delete();
        gens = Paths.get("src/main/java/com/skoow/physs/gen/tokens").toFile().listFiles(f -> !f.getName().equals("ClassGen.java"));
        for (File gen : gens)
            gen.delete();
        Files.createDirectories(outputPath);

        generateMetaTokens(outputPath);
        generateMetaAST(outputPath);
    }

    public static void generateMetaTokens(Path outputPath) throws IOException {
        InputStream stream = MetaGenerator.class.getClassLoader().getResourceAsStream(tokensFile);
        if (stream == null)
            throw new FileNotFoundException("TOML tokens file not found: " + tokensFile);
        toml = new Toml();
        toml.read(stream);

        System.out.println("Generating meta tokens");

        TypeSpec.Builder builder = TypeSpec.classBuilder("TokenMeta")
                .addAnnotation(AnnotationSpec.builder(Generated.class).addMember("value", "$S", MetaGenerator.class.getName()).build())
                .addModifiers(Modifier.PUBLIC);

        JavaFile file = JavaFile.builder("com.skoow.physs.gen.tokens",builder.build()).build();
        file.writeTo(outputPath);

        CodeBlock.Builder block = CodeBlock.builder();

        String[] containers = new String[] {"all","singular","doubl","keywords","primitive"};
        for (String container : containers) {
            builder.addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(LinkedHashMap.class),
                                    TypeName.get(String.class),TypeName.get(TokenInfo.class)),
                            container,Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL)
                    .initializer("new LinkedHashMap<>()")
                    .build());
        }

        for (Map.Entry<String, Object> entry : toml.entrySet()) {
            if(entry.getValue() instanceof Toml t) {
                String id = entry.getKey();

                System.out.println("- Group: "+id);

                if(Objects.equals(id, "double")) id = "doubl";
                if(Objects.equals(id, "other")) id = "primitive";
                if(Objects.equals(id, "keyword")) id = "keywords";
                String finalId = id;

                System.out.println("- Remapped Group: "+finalId);

                t.toMap().forEach((k, v) -> {
                    var info = info(k);
                    block.addStatement("$N.put($S,new TokenInfo((short) $L,$S))", finalId,v,info.idx(),k);
                    block.addStatement("all.put($S,new TokenInfo((short) $L,$S))",v,info.idx(),k);

                    System.out.println("- - Added "+info.id()+" at "+info.idx());
                });
            }
        }
        ClassName tokenManager = ClassName.get(TokenManager.class);
        for (String container : containers) {
            block.addStatement("$T.$N = $N",tokenManager,container,container);
        }

        builder.addStaticBlock(block.build());
        builder.addMethod(MethodSpec.methodBuilder("init").addModifiers(Modifier.PUBLIC,Modifier.STATIC).build());

        file = JavaFile.builder("com.skoow.physs.gen.tokens",builder.build())
                .build();
        file.writeTo(outputPath);
    }

    public static void generateMetaAST(Path outputPath) throws IOException {
        InputStream stream = MetaGenerator.class.getClassLoader().getResourceAsStream(classesFile);
        if(stream == null)
            throw new FileNotFoundException("TOML classes file not found: "+classesFile);
        toml = new Toml();
        toml.read(stream);
        Map<String, Map<String,Object>> classSettings = new LinkedHashMap<>();
        Map<String, Map<String,Object>> classConfigs = new LinkedHashMap<>();

        List<String> order = new ArrayList<>();

        for (Map.Entry<String, Object> entry : toml.entrySet()) {
            String name = entry.getKey();
            Object val = entry.getValue();

            if(val instanceof Toml valt) {
                classSettings.putIfAbsent(name,new LinkedHashMap<>());
                classConfigs.putIfAbsent(name,new LinkedHashMap<>());
                Map<String, Object> fields = classSettings.get(name);
                Map<String, Object> configs = classConfigs.get(name);

                for (Map.Entry<String, Object> classE : valt.entrySet()) {
                    String fieldName = classE.getKey();
                    if(classE.getValue() instanceof Toml) {
                        configs.put(fieldName,classE.getValue());
                        continue;
                    }
                    Object fieldType = classE.getValue();
                    fields.put(fieldName,fieldType);
                }
            } else {
                if(name.equals("order"))
                    order = (List<String>) val;
            }
        }

        Map<String,Map<String,Object>> orderedClasses = new LinkedHashMap<>();
        for (String s : order)
            orderedClasses.put(s,classSettings.get(s));

        classSettings = orderedClasses;

        try {
            for (Map.Entry<String, Map<String, Object>> entry : classSettings.entrySet())
                genAst(entry.getKey(), entry.getValue(), classConfigs.get(entry.getKey()), outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void genAst(String className, Map<String, Object> settings, Map<String,Object> configs, Path outputPath) throws IOException {
        System.out.println("Generating class: "+className);
        String extending = (String) settings.get("extends");
        settings.remove("extends");

        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addAnnotation(AnnotationSpec.builder(Generated.class).addMember("value", "$S", MetaGenerator.class.getName()).build())
                .addModifiers(Modifier.PUBLIC);

        JavaFile file = JavaFile.builder("com.skoow.physs.gen.ast",builder.build()).build();
        file.writeTo(outputPath);

        LinkedHashMap<String,Class<?>> superParams = new LinkedHashMap<>();

        if(extending != null) {
            System.out.println("- extends "+extending);
            Class<?> ext = getType(extending);
            builder.superclass(ext);
            for (Constructor<?> constructor : ext.getConstructors()) {
                for (Parameter parameter : constructor.getParameters())
                    superParams.put(parameter.getName(),parameter.getType());
                break;
            }
        }

        MethodSpec.Builder initBuilder = MethodSpec.constructorBuilder();
        initBuilder.addModifiers(Modifier.PUBLIC);

        if(extending != null) {
            StringBuilder superStmt = new StringBuilder("super(");
            superParams.forEach((n,t) -> {
                initBuilder.addParameter(t,n);
            });
            superStmt.append(String.join(",",superParams.keySet()));
            superStmt.append(")");
            System.out.println("- "+ superStmt);
            initBuilder.addStatement(superStmt.toString());
        }

        Map<String,Object> initConfig = ((Toml) configs.getOrDefault("init",new Toml())).toMap();
        List<String> removeParams = (List<String>) initConfig.getOrDefault("remove",new ArrayList<>());
        Map<String,Object> setParams = (Map<String, Object>) initConfig.getOrDefault("set",new LinkedHashMap<>());

        setParams.forEach((n,o) -> {
            System.out.println("- Set field '"+n+"' to "+(o instanceof String s ? "\""+s+"\"" : o));
            initBuilder.addStatement("this.$N = $L",n,o instanceof String s ? "\""+s+"\"" : o);
        });
        settings.forEach((k,v) -> {
            Type type = getType((String) v);
            System.out.println("- Generate field '"+k+"' of type '"+type.getTypeName()+"'");
            builder.addField(FieldSpec.builder(
                            type,
                            k,
                            Modifier.PUBLIC)
                    .build());
            if(removeParams.contains(k)) {
                System.out.println("- Remove constructor parameter '"+k+"'");
                return;
            }
            initBuilder.addParameter(type,k);
            initBuilder.addStatement("this.$N = $N",k,k);
        });

        builder.addMethod(initBuilder.build());

        file = JavaFile.builder("com.skoow.physs.gen.ast",builder.build()).build();
        file.writeTo(outputPath);
    }

    private static Class<?> getType(String type) {
        return switch (type) {
            case "int" -> int.class;
            case "String" -> String.class;
            case "boolean" -> boolean.class;
            case "double" -> double.class;
            case "float" -> float.class;
            case "long" -> long.class;
            case "char" -> char.class;
            default -> {
                try {
                    if(type.startsWith("@")) {
                        try {
                            String className = genPackage + "." + type.substring(1);
                            yield Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            String className = genPackage + "." + type.substring(1);
                            yield FileClassCompiler.compile(className,
                                    new File("src/main/java/"),
                                    new File("src/main/java/com/skoow/physs/gen/"+type.substring(1)+".java"));
                        }
                    }
                    if(type.startsWith("#")) {
                        String className = physsPackage + "." + type.substring(1);
                        yield Class.forName(className);
                    }
                    yield Class.forName(type);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("Unknown class type: "+type);
                }
            }
        };
    }
    static short lastId = 0;
    static TokenInfo info(String identifier) {
        return new TokenInfo(nextId(),identifier);
    }
    static short nextId() {
        return lastId++;
    }
}
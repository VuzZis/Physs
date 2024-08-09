package com.skoow.physs.runtime;

import com.skoow.physs.ast.expression.*;
import com.skoow.physs.ast.literal.BasicLiteral;
import com.skoow.physs.ast.statement.*;
import com.skoow.physs.engine.Context;
import com.skoow.physs.engine.component.Translatable;
import com.skoow.physs.error.PhyssReporter;
import com.skoow.physs.error.errors.RunException;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.TokenType;
import com.skoow.physs.lexer.scanner.Position;
import com.skoow.physs.runtime.exc.Return;
import com.skoow.physs.runtime.exc.UndefinedValue;
import com.skoow.physs.runtime.wrap.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Interpreter {
    private final Program program;
    public final Scope scope;
    public int classIndex = 0;

    public Interpreter(Program program,Scope scope) {
        this.program = program;
        this.scope = scope;
    }

    public void interpreteProgram() {
        for (Stmt line : program.lines) {
            try {
                evaluate(line, scope);
            } catch(RunException e) {
                PhyssReporter.reportError(line.line(),line.symbol(),Translatable.get("log.runtime"),e.getMessage());
                break;
            } catch (Return r) {
                PhyssReporter.reportError(line.line(),line.symbol(),Translatable.get("log.runtime"),
                        Translatable.get("runtime.return_outside_block"));
                break;
            }
        }
    }

    private Object evaluate(Stmt line,Scope scope) {
        if(line == null) return null;
        String nodeKind = line.getClass().getSimpleName();
        return switch (nodeKind) {
            case "BlockStmt" -> blockStmt(((BlockStmt) line).stmts,scope);

            case "IfStmt" -> ifStmt((IfStmt) line,scope);
            case "WhileStmt" -> whileStmt((WhileStmt) line, scope);
            case "ReturnStmt" -> returnStmt((ReturnStmt) line,scope);

            case "BasicLiteral" -> ((BasicLiteral) line).value;
            case "IdentifierLiteral" -> line.toString();

            case "VarDeclarStmt" -> varDeclaration((VarDeclarStmt) line,scope);
            case "FunctionStmt" -> fnDeclaration((FunctionStmt) line,scope);
            case "ClassStmt" -> classDeclaration((ClassStmt) line,scope);

            case "PrintStmt" -> print((PrintStmt) line, scope);
            case "ExprStmt" -> evaluate(((ExprStmt) line).expr,scope);

            case "LambdaExpr" -> lambdaExpr((LambdaExpr) line, scope);
            case "CallExpr" -> callExpr((CallExpr) line,scope);
            case "GetPropertyExpr" -> getPropertyExpr((GetPropertyExpr) line,scope);
            case "PropertyAssignmentExpr" -> propAssignExpr((PropertyAssignmentExpr) line,scope);
            case "CastExpr" -> castExpr((CastExpr) line, scope);
            case "InputExpr" -> inputExpr((InputExpr) line, scope);
            case "AssignmentExpr" -> assignExpr((AssignmentExpr) line,scope);
            case "VarGetExpr" -> varExpr((VarGetExpr) line,scope);
            case "BinaryExpr" -> binaryExpr((BinaryExpr) line,scope);
            case "UnaryExpr" -> unaryExpr((UnaryExpr) line,scope);
            case "GroupExpr" -> evaluate(((GroupExpr) line).expr,scope);

            default -> throw error(line,String.format(Translatable.get("runtime.unx_node"),nodeKind));
        };
    }

    private Object propAssignExpr(PropertyAssignmentExpr line, Scope scope) {
        Object object = evaluate(line.object,scope);
        if(!(object instanceof PhyssClassInstance)) throw
            error(line.object,Translatable.getf("runtime.object_not_property-able",getName(object)));
        Object value = evaluate(line.value,scope);
        PhyssClassInstance instance = (PhyssClassInstance) object;
        if(instance.getNoError(line.var.lexeme) instanceof PhyssFn) throw
            error(line.object,Translatable.getf("runtime.property_is_method",line.var.lexeme));
        instance.set(line.var.lexeme,value);
        return value;
    }

    private Object getPropertyExpr(GetPropertyExpr line, Scope scope) {
        Object object = evaluate(line.expr,scope);
        if(object instanceof PhyssClassInstance instance) {
            return instance.get(line.var.lexeme);
        } else if(object instanceof PhyssClass classe) {
            PhyssFn fn = classe.getMethods().get(line.var.lexeme);
            if(fn instanceof PhyssClassFn classFn && !classFn.isStatic())
                throw error(line,Translatable.getf("runtime.object_not_property-able",getName(object)));
            return fn;
        } else if(!object.getClass().getName().startsWith("java.lang")) {
            try {
                Method[] methods = object.getClass().getMethods();
                Field[] fields = object.getClass().getFields();
                for (Method method : methods) {
                    if (method.getName().equals(line.var.lexeme))
                        return createMethodFn(method, object);
                }
                for (Field field : fields)
                    if (field.getName().equals(line.var.lexeme))
                        return field.get(object);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else if(object instanceof Class<?> klass) {
            try {
                Method[] methods = klass.getMethods();
                Field[] fields = klass.getFields();
                for (Method method : methods)
                    if (method.getName().equals(line.var.lexeme))
                        return createMethodFn(method,object);
                for (Field field : fields)
                    if (field.getName().equals(line.var.lexeme))
                        return field.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw error(line,Translatable.getf("runtime.object_not_property-able",getName(object)));
    }

    private PhyssFn createMethodFn(Method method, Object object) {
        PhyssFn fn = new PhyssFn() {
            @Override
            public int argCount() {
                return method.getParameterCount();
            }

            @Override
            public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
                Object[] argsObj = args.toArray();
                try {
                    return method.invoke(object, argsObj);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return fn;
    }

    private Object classDeclaration(ClassStmt line, Scope scope) {
        String name = line.varName.lexeme;
        List<FunctionStmt> methods = line.methods;
        Map<String,PhyssFn> methodsObjected = new HashMap<>();
        methods.forEach(m -> methodsObjected.put(m.varName.lexeme,new PhyssProgramFn(m)));
        PhyssClass clazz = new PhyssClass(name,methodsObjected);
        scope.defineVariable(name,clazz);
        return clazz;
    }

    private Object castExpr(CastExpr line, Scope scope) {
        Object val = evaluate(line.expr,scope);
        try {
            return line.operator.cast(val);
        } catch (ClassCastException e) {
            throw error(line,String.format(Translatable.get("runtime.cannot_cast"),val.toString(),line.operator.name()));
        }
    }

    private Object lambdaExpr(LambdaExpr line, Scope scope) {
        PhyssProgramFn fn = new PhyssProgramFn(
                new FunctionStmt(
                        new Token(TokenType.IDENTIFIER,"lambda","lambda",0,0),
                        line.params,
                        Arrays.asList(line.body),
                        new Position(line.line(),line.symbol())));
        return fn;
    }

    private Object returnStmt(ReturnStmt line, Scope scope) {
        Object value = null;
        if(line.expr != null) value = evaluate(line.expr,scope);
        throw new Return(value);
    }

    private Object fnDeclaration(FunctionStmt line, Scope scope) {
        String varName = line.varName.lexeme;
        PhyssProgramFn fn = new PhyssProgramFn(line);
        Scope scopeNew = this.scope;
        if(scope != scopeNew) throw error(line,Translatable.get("runtime.function_inner_scope"));
        scopeNew.defineVariable(varName,fn);
        return null;
    }

    private Object callExpr(CallExpr line, Scope scope) {
        Object callee = evaluate(line.callee,scope);
        List<Object> args = new ArrayList<>();
        for (Expr arg : line.args) args.add(evaluate(arg,scope));

        if(callee instanceof Class<?> klass) {
            Class<?>[] parameters = args.stream().map((k) -> k.getClass()).toArray(Class[]::new);
            try {
                Constructor<?> constructor = klass.getConstructor(parameters);
                Object obj = constructor.newInstance(args.toArray());
                return obj;
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if(!(callee instanceof PhyssFn)) throw error(line,Translatable.get("runtime.not_callable"));
        PhyssFn fn = (PhyssFn) callee;
        if(fn.argCount() >= 0 && args.size() != fn.argCount())
            throw error(line,String.format(Translatable.get("runtime.insufficient_args"),fn.argCount(),args.size()));
        try {
            return fn.methodOrConstructor(this, args);
        } catch (StackOverflowError error) {
            throw error(line,Translatable.get("runtime.call_overflow"));
        } catch (Return ret) {
            return ret.value;
        }
    }

    private Object whileStmt(WhileStmt line, Scope scope) {
        Expr condition = line.condition;
        int evaluateCount = 0;
        double maxCount = (double) Context.getGlobals().getVariable("$$WHILE_MAX_ITERATIONS");
        while (true) {
            if(evaluateCount >= maxCount) throw error(line,Translatable.get("runtime.loop_overflow"));
            Object value = evaluate(condition,scope);
            if(value instanceof Boolean b) {if (!b) break;}
            else break;
            evaluate(line.thenBranch,scope);
            evaluateCount++;
        }
        return null;
    }

    private Object inputExpr(InputExpr line, Scope scope) {
        Object toPrint = evaluate(line.expr,scope);
        if(toPrint == null) toPrint = "";
        PhyssReporter.reportInput(line.line(),line.symbol(),toPrint.toString());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw error(line,String.format(Translatable.get("runtime.failed_to_input"),e.getMessage()));
        }
    }

    private Object ifStmt(IfStmt line, Scope scope) {
        Object condition = evaluate(line.condition,scope);
        if(!(condition instanceof Boolean)) throw error(line.condition,Translatable.getf("runtime.condition_not_boolean",condition.getClass().getSimpleName()));
        boolean condBoolean = (Boolean) condition;
        if(condBoolean) evaluate(line.thenBranch,scope);
        else if(line.elseBranch != null) evaluate(line.elseBranch,scope);
        return null;
    }

    public Object blockStmt(List<Stmt> stmts, Scope scope) {
        Scope thisScope = new Scope(scope);
        Object value = null;
        for (Stmt stmt : stmts) {
            try {
                evaluate(stmt,thisScope);
            } catch(RunException e) {
                throw e;
            } catch (Return e) {
                value = e.value;
                throw e;
            }
        }
        return value;
    }

    private Object assignExpr(AssignmentExpr line, Scope scope) {
        String varName = line.var.lexeme;
        try {
            Object value = evaluate(line.value, scope);
            scope.updateVariable(varName, value);
            return value;
        } catch (RunException e) {
            throw e;
        }
    }

    private Object varExpr(VarGetExpr line, Scope scope) {
        String varName = line.var.lexeme;
        try {
            Object obj = scope.getVariable(varName);
            if(obj instanceof UndefinedValue) throw error(line,Translatable.getf("runtime.variable_not_initialized",varName));
            return obj;
        } catch (RunException e) {
            throw e;
        }
    }

    private Object unaryExpr(UnaryExpr line, Scope scope) {
        Object obj = evaluate(line.expr,scope);
        TokenType operator = line.operator.tokenType;
        switch (operator) {
            case MINUS:
                if(obj instanceof Double oD) return -oD;
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_unary",
                        line.operator.lexeme,getName(obj)));

            case BANG:
                if(obj instanceof Boolean oB) return !oB;
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_unary",
                        line.operator.lexeme,getName(obj)));

            default:
                throw error(line,Translatable.getf("runtime.unx_operator_unary",
                        line.operator.lexeme,getName(obj)));
        }
    }

    private Object binaryExpr(BinaryExpr line, Scope scope) {
        Object left = evaluate(line.left,scope);
        Object right = evaluate(line.right,scope);
        TokenType operator = line.operator.tokenType;
        switch (operator) {
            case MINUS:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD-rD;
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_binary",
                        line.operator.lexeme,getName(left),getName(right)));
            case PLUS:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD+rD;
                else if(left instanceof String lS) return lS+right;
                else if(right instanceof String rS) return left.toString()+rS;
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_binary",
                            line.operator.lexeme,getName(left),getName(right)));
            case MULTIPLIER:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD*rD;
                else if(left instanceof String lS && right instanceof Double rD) return lS.repeat((int) rD.doubleValue());
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_binary",
                            line.operator.lexeme,getName(left),getName(right)));
            case SLASH:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD/rD;
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_binary",
                        line.operator.lexeme,getName(left),getName(right)));

            case MOD:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD%rD;
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_binary",
                        line.operator.lexeme,getName(left),getName(right)));

            case EQUALS_EQUALS:
                return left.equals(right);
            case BANG_EQUALS:
                return !left.equals(right);
            case GREATER:
                if(left instanceof Double lD && right instanceof Double rD) return lD>rD;
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_binary",
                        line.operator.lexeme,getName(left),getName(right)));
            case LESS:
                if(left instanceof Double lD && right instanceof Double rD) return lD<rD;
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_binary",
                        line.operator.lexeme,getName(left),getName(right)));
            case GREATER_EQUALS:
                if(left instanceof Double lD && right instanceof Double rD) return lD>=rD;
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_binary",
                        line.operator.lexeme,getName(left),getName(right)));
            case LESS_EQUALS:
                if(left instanceof Double lD && right instanceof Double rD) return lD<=rD;
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_binary",
                        line.operator.lexeme,getName(left),getName(right)));

            case AND:
                if(left instanceof Boolean lD && right instanceof Boolean rD) return lD && rD;
                else throw error(line,Translatable.getf("runtime.cannot_operator_to_binary",
                        line.operator.lexeme,getName(left),getName(right)));

            case OR:
                if(left instanceof Boolean lD && right instanceof Boolean rD) return lD || rD;
                else return left != null ? left : right;

            default:
                throw error(line,Translatable.getf("runtime.unx_operator_binary",
                    getName(left),line.operator.lexeme,getName(right)));
        }
    }
    
    private static String getName(Object obj) {
        if(obj == null) return "nil";
        return obj.getClass().getSimpleName();
    }

    private Object print(PrintStmt line, Scope scope) {
        Object toPrint = evaluate(line.expr,scope);
        if(toPrint == null) {
            PhyssReporter.reportWarn(line.line(),line.symbol(),Translatable.get("log.runtime"),Translatable.get("runtime.null_print"));
            PhyssReporter.reportInfo(line.line(),line.symbol(),"nil");
        }
        else PhyssReporter.reportInfo(line.line(),line.symbol(),toPrint.toString());
        return null;
    }

    private Object varDeclaration(VarDeclarStmt line,Scope scope) {
        String varName = line.varName.lexeme;
        Object value;
        if(line.initializer == null) value = new UndefinedValue();
        else value = evaluate(line.initializer,scope);
        scope.defineVariable(varName,value);
        return value;
    }

    private RunException error(Stmt stmt, String message) {
        return new RunException(message);
    }
}

package com.skoow.physs.engine;

import com.skoow.physs.engine.component.Translatable;
import com.skoow.physs.engine.global.*;
import com.skoow.physs.engine.global.classes.ListClass;
import com.skoow.physs.engine.global.math.MathClass;
import com.skoow.physs.engine.global.random.RandomClass;
import com.skoow.physs.error.errors.RunException;
import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.Scope;
import com.skoow.physs.runtime.wrap.PhyssClassInstance;
import com.skoow.physs.runtime.wrap.PhyssFn;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlobalScope extends Scope {
    public static List<String> restrictedPackages = Arrays.stream(new String[] {
            "com.skoow.physs","java.lang.reflect"
    }).toList();
    public GlobalScope() {
        super();
        defineVariable("$$WHILE_MAX_ITERATIONS",Math.pow(2,14));

        defineVariable("typeof", new TypeOfFn());
        defineVariable("millis",new MillisFn());
        defineVariable("isNum",new IsNumberFn());

        defineVariable("List",new ListClass());

        defineVariable("importJavaClass", new PhyssFn() {
            @Override
            public int argCount() {
                return 1;
            }

            @Override
            public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
                String st = args.get(0).toString();
                for (String restrictedPackage : restrictedPackages) {
                    if(st.contains(restrictedPackage)) throw new RunException(
                            Translatable.getf("runtime.restricted_java_package",restrictedPackage));
                }
                try {
                    Class<?> klass = Class.forName(st);
                    defineVariable(klass.getSimpleName(),klass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        });

        defineVariable("Math",new MathClass());
        defineVariable("Random",new RandomClass());
    }
}

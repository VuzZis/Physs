package com.skoow.physs.engine;

import com.skoow.physs.engine.global.*;
import com.skoow.physs.engine.global.classes.ListClass;
import com.skoow.physs.engine.global.math.MathClass;
import com.skoow.physs.engine.global.random.RandomClass;
import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.Scope;
import com.skoow.physs.runtime.wrap.PhyssClassInstance;
import com.skoow.physs.runtime.wrap.PhyssFn;

import java.io.File;
import java.util.List;

public class GlobalScope extends Scope {
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

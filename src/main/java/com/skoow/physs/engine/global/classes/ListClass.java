package com.skoow.physs.engine.global.classes;

import com.skoow.physs.ast.literal.Literals;
import com.skoow.physs.engine.global.random.NextFloatFn;
import com.skoow.physs.engine.global.random.NextGaussianFn;
import com.skoow.physs.error.errors.RunException;
import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.wrap.PhyssClass;
import com.skoow.physs.runtime.wrap.PhyssClassFn;
import com.skoow.physs.runtime.wrap.PhyssFn;
import com.skoow.physs.runtime.wrap.PhyssProgramFn;

import java.util.*;

public class ListClass extends PhyssClass {
    private static Map<String,PhyssFn> methods = new HashMap<>();
    static {
        methods.put("instance", new PhyssClassFn() {
            @Override
            public int argCount() {
                return -1;
            }

            @Override
            public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
                List<Object> list = args;
                self.set("$$LIST",list);
                return null;
            }
        });
        methods.put("get", new PhyssClassFn() {
            @Override
            public int argCount() {
                return 1;
            }

            @Override
            public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
                List<Object> list = (List<Object>) self.get("$$LIST");
                int i = (int) Math.floor((Double) Literals.DOUBLE.cast(args.get(0)));
                return list.get(i);
            }
        });
        methods.put("add", new PhyssClassFn() {
            @Override
            public int argCount() {
                return 1;
            }

            @Override
            public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
                List<Object> list = (List<Object>) self.get("$$LIST");
                list.add(args.get(0));
                return args.get(0);
            }
        });
        methods.put("size", new PhyssClassFn() {
            @Override
            public int argCount() {
                return 0;
            }

            @Override
            public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
                List<Object> list = (List<Object>) self.get("$$LIST");
                return list.size();
            }
        });
        methods.put("forEach", new PhyssClassFn() {
            @Override
            public int argCount() {
                return 1;
            }

            @Override
            public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
                List<Object> list = (List<Object>) self.get("$$LIST");
                Object ob = args.get(0);
                if(ob instanceof PhyssFn fn) {
                    list.forEach(e -> {
                        List<Object> arg = new ArrayList<>();
                        arg.add(e);
                        fn.methodOrConstructor(interpreter,arg);
                    });
                    return null;
                }
                throw new RunException("Expected function or lambda expression");
            }
        });

    }
    public ListClass() {
        super("List", methods);
    }
}

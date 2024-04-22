package com.skoow.physs.engine.global.math;

import com.skoow.physs.runtime.wrap.PhyssClass;
import com.skoow.physs.runtime.wrap.PhyssFn;

import java.util.HashMap;
import java.util.Map;

public class MathClass extends PhyssClass {

    private static Map<String,PhyssFn> methods = new HashMap<>();
    static {
        methods.put("sqrt",new SqrtFn());
        methods.put("sin",new SinFn());
        methods.put("cos",new CosFn());
        methods.put("round",new RoundFn());
    }
    public MathClass() {
        super("Math", methods);
    }
}

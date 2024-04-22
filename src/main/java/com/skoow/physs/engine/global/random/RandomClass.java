package com.skoow.physs.engine.global.random;

import com.skoow.physs.engine.global.math.SqrtFn;
import com.skoow.physs.runtime.wrap.PhyssClass;
import com.skoow.physs.runtime.wrap.PhyssFn;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomClass extends PhyssClass {

    public static final Random random = new Random();
    private static Map<String,PhyssFn> methods = new HashMap<>();
    static {
        methods.put("nextFloat",new NextFloatFn());
        methods.put("nextGaussian",new NextGaussianFn());
    }
    public RandomClass() {
        super("Random", methods);
    }
}

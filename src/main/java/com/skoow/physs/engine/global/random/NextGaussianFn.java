package com.skoow.physs.engine.global.random;

import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.wrap.PhyssFn;
import com.skoow.physs.util.TextUtils;

import java.util.List;

public class NextGaussianFn implements PhyssFn {
    @Override
    public int argCount() {
        return 0;
    }

    @Override
    public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
        return RandomClass.random.nextGaussian();
    }

    @Override
    public String toString() {
        return TextUtils.green("<PhyssFn>");
    }
}

package com.skoow.physs.engine.global.random;

import com.skoow.physs.ast.literal.Literals;
import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.wrap.PhyssFn;
import com.skoow.physs.util.TextUtils;

import java.util.List;

public class NextFloatFn implements PhyssFn {
    @Override
    public int argCount() {
        return 0;
    }

    @Override
    public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
        return (double) RandomClass.random.nextFloat();
    }

    @Override
    public String toString() {
        return TextUtils.green("<PhyssFn>");
    }
}

package com.skoow.physs.engine.global.math;

import com.skoow.physs.ast.literal.Literals;
import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.wrap.PhyssFn;
import com.skoow.physs.util.TextUtils;

import java.util.List;

public class SqrtFn implements PhyssFn {
    @Override
    public int argCount() {
        return 1;
    }

    @Override
    public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
        Object obj = args.get(0);
        double d = (double) Literals.DOUBLE.cast(obj);
        return Math.sqrt(d);
    }

    @Override
    public String toString() {
        return TextUtils.green("<PhyssFn>");
    }
}

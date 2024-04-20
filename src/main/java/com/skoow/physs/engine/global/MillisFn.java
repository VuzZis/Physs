package com.skoow.physs.engine.global;

import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.wrap.PhyssFn;

import java.util.List;

public class MillisFn implements PhyssFn {
    @Override
    public int argCount() {
        return 0;
    }

    @Override
    public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
        return (double) System.currentTimeMillis();
    }
}

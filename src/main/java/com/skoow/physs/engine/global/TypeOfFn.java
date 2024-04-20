package com.skoow.physs.engine.global;

import com.skoow.physs.error.PhyssReporter;
import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.wrap.PhyssFn;
import com.skoow.physs.util.TextUtils;

import java.util.List;

public class TypeOfFn implements PhyssFn {
    @Override
    public int argCount() {
        return 1;
    }

    @Override
    public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
        String type = "nil";
        Object obj = args.get(0);
        if(obj != null) type = obj.getClass().getSimpleName();
        return type;
    }

    @Override
    public String toString() {
        return TextUtils.green("<PhyssFn>");
    }
}

package com.skoow.physs.engine.global;

import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.wrap.PhyssFn;
import com.skoow.physs.util.TextUtils;

import java.util.List;

public class IsNumberFn implements PhyssFn {
    @Override
    public int argCount() {
        return 1;
    }

    @Override
    public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
        Object obj = args.get(0);
        try {
            Double.parseDouble((String) obj);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return TextUtils.green("<PhyssFn>");
    }
}

package com.skoow.physs.runtime.wrap;

import com.skoow.physs.runtime.Interpreter;

import java.util.List;

public interface PhyssFn {
    int argCount();
    Object methodOrConstructor(Interpreter interpreter, List<Object> args);

}

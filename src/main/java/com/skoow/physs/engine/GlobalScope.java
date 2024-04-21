package com.skoow.physs.engine;

import com.skoow.physs.engine.global.*;
import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.Scope;
import com.skoow.physs.runtime.wrap.PhyssFn;

import java.util.List;

public class GlobalScope extends Scope {
    public GlobalScope() {
        super();
        defineVariable("typeof", new TypeOfFn());
        defineVariable("millis",new MillisFn());
        defineVariable("isNum",new IsNumberFn());
    }
}

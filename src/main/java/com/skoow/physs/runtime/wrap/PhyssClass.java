package com.skoow.physs.runtime.wrap;

import com.skoow.physs.engine.component.Translatable;
import com.skoow.physs.error.errors.RunException;
import com.skoow.physs.runtime.Interpreter;

import java.util.List;
import java.util.Map;

public class PhyssClass implements PhyssFn {
    public final String name;
    private final Map<String,PhyssFn> methods;

    public PhyssClass(String name, Map<String,PhyssFn> methods) {
        this.name = name;
        this.methods = methods;
    };

    @Override
    public String toString() {
        return "["+name+"]";
    }

    @Override
    public int argCount() {
        if(findMethod("instance") != null) {
            PhyssFn fn = findMethod("instance");
            return fn.argCount();
        }
        return 0;
    }

    @Override
    public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
        PhyssClassInstance instance = new PhyssClassInstance(interpreter,interpreter.classIndex++,this);
        PhyssFn constructor = findMethod("instance");
        if(constructor instanceof PhyssClassFn fn) fn.self = instance;
        if(constructor != null) {
            if(constructor.argCount() != args.size() && constructor.argCount() >= 0)
                throw new RunException(Translatable.getf(
                        "runtime.insufficient_args",""+constructor.argCount(),""+args.size()));
            constructor.methodOrConstructor(interpreter,args);
        }
        return instance;
    }

    private PhyssFn findMethod(String instance) {
        return methods.get(instance);
    }

    public Map<String, PhyssFn> getMethods() {
        return methods;
    }
}

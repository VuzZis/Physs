package com.skoow.physs.runtime.wrap;

import com.skoow.physs.engine.component.Translatable;
import com.skoow.physs.error.errors.RunException;
import com.skoow.physs.runtime.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhyssClassInstance {
    private final PhyssClass inherited;
    private final int instanceId;
    private final Interpreter interpreter;
    private final HashMap<String,Object> instanceFields = new HashMap<>();
    private final HashMap<String,Object> staticFields = new HashMap<>();
    public PhyssClassInstance(Interpreter interpreter, int instanceId,PhyssClass inherited) {
        this.inherited = inherited;
        this.instanceId = instanceId;
        this.interpreter = interpreter;
        instanceFields.put("toString", new PhyssFn() {
            @Override
            public int argCount() {
                return 0;
            }

            @Override
            public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
                return toStringDefault();
            }
        });
        instanceFields.putAll(inherited.getMethods());
        instanceFields.forEach((k,v) -> {
            if(v instanceof PhyssClassFn fn) fn.self = this;
        });
    }

    public String toStringDefault() {
        return inherited.toString()+"#"+instanceId;
    }

    @Override
    public String toString() {
        if(instanceFields.containsKey("toString") && instanceFields.get("toString") instanceof PhyssFn)
            return toStringInner();
        return toStringDefault();
    }

    private String toStringInner() {
        PhyssFn fn = (PhyssFn) instanceFields.get("toString");
        if(fn.argCount() != 0)
            throw new RunException(
                    Translatable.getf("runtime.insufficient_args",""+0,""+fn.argCount()));
        if(fn instanceof PhyssClassFn fnProgram) {
            fnProgram.self = this;
            Object name = fnProgram.methodOrConstructor(interpreter,new ArrayList<>());
            if(name == null) return toStringDefault();
            return name.toString();
        }
        Object name = fn.methodOrConstructor(interpreter,new ArrayList<>());
        if(name == null) return toStringDefault();
        return name.toString();
    }

    public Object get(String lexeme) {
        if(instanceFields.containsKey(lexeme)) return instanceFields.get(lexeme);
        throw new RunException(Translatable.getf("runtime.class_undefined_property",lexeme));
    }

    public void set(String lexeme, Object value) {
        instanceFields.put(lexeme,value);
    }

    public Object getNoError(String lexeme) {
        if(instanceFields.containsKey(lexeme)) return instanceFields.get(lexeme);
        return null;
    }
}

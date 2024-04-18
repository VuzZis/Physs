package com.skoow.physs.runtime;

import com.skoow.physs.error.errors.RunException;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Scope top;
    private final Map<String,Object> values = new HashMap<>();
    public Scope(Scope top) {
        this.top = top;
    }
    public Scope() {
        this.top = null;
    }

    public void defineVariable(String name, Object value) throws RunException {
        if(values.containsKey(name)) throw new RunException("Variable already exists: '"+name+"'");
        values.put(name,value);
    }
    public void updateVariable(String name, Object value) throws RunException {
        if(!values.containsKey(name)) throw new RunException("Undefined variable '"+name+"'");
        values.put(name,value);
    }

    public Object getVariable(String name) throws RunException {
        if(values.containsKey(name)) return values.get(name);
        throw new RunException("Undefined variable '"+name+"'");
    }

}

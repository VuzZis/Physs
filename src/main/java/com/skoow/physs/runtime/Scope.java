package com.skoow.physs.runtime;

import com.skoow.physs.error.errors.RunException;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private Scope top;
    private final Map<String,Object> values = new HashMap<>();
    public Scope(Scope top) {
        this.top = top;
    }
    public Scope() {
        this.top = null;
        defineVariable("$$WHILE_MAX_ITERATIONS",Math.pow(2,14));
    }

    public void defineVariable(String name, Object value) throws RunException {
        if(values.containsKey(name)) throw new RunException("Variable already exists: '"+name+"'");
        values.put(name,value);
    }
    public void updateVariable(String name, Object value) throws RunException {
        if(values.containsKey(name)) values.put(name,value);
        if(top != null) top.updateVariable(name,value);
    }

    public Object getVariable(String name) throws RunException {
        if(values.containsKey(name)) return values.get(name);
        if(top != null) return top.getVariable(name);
        throw new RunException("Undefined variable '"+name+"'");
    }

    public void setChild(Scope scope) {
        scope.top = this;
    }
}

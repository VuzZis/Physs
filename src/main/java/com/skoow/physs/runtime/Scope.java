package com.skoow.physs.runtime;

import com.skoow.physs.engine.component.Translatable;
import com.skoow.physs.error.errors.RunException;
import com.skoow.physs.runtime.wrap.PhyssClass;
import com.skoow.physs.runtime.wrap.PhyssFn;

import java.lang.reflect.Method;
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
    }

    public void defineVariable(String name, Object value) throws RunException {
        if(value instanceof Class<?> clazz) {
            /*Method[] methods = clazz.getMethods();
            Map<String, PhyssFn> funs = new HashMap<>();
            for (Method method : methods) {

            }
            value = new PhyssClass(clazz.getSimpleName(), )*/
        }
        if(values.containsKey(name)) throw new RunException(Translatable.getf("scope.variable_exists",name));
        values.put(name,value);
    }
    public void updateVariable(String name, Object value) throws RunException {
        if(values.containsKey(name)) values.put(name,value);
        else if(top != null) top.updateVariable(name,value);
        else throw new RunException(Translatable.getf("scope.variable_undefined",name));
    }

    public Object getVariable(String name) throws RunException {
        if(values.containsKey(name)) return values.get(name);
        if(top != null) return top.getVariable(name);
        throw new RunException(Translatable.getf("scope.variable_undefined",name));
    }

    public void setChild(Scope scope) {
        scope.top = this;
    }
}

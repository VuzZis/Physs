package com.skoow.physs.runtime.wrap;

public abstract class PhyssClassFn implements PhyssFn {
    public PhyssClassInstance self = null;
    public abstract boolean isStatic();
}

package com.skoow.physs.util;

public class Structs {
    public static <A> A or(A a, A b) {
        return a != null ? a : b;
    }
    public static <A,T> T or(A ac, T a, T b) {
        return ac != null ? a : b;
    }
}

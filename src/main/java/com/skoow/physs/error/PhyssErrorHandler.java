package com.skoow.physs.error;

import com.skoow.physs.error.errors.PhyssException;

public class PhyssErrorHandler {
    public static boolean hadError = false;
    public static void error(int line, int symbol, PhyssException exception) {
        report(line,"at position "+symbol,exception.getMessage());
        hadError = true;
    }

    private static void report(int line, String where, String message) {
        System.out.printf("[Physs][line %s] Exception %s: %s%n",line,where,message);
    }
}

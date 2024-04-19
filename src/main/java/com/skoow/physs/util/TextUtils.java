package com.skoow.physs.util;

public class TextUtils {

    public static String dash(int length) {
        StringBuilder dash = new StringBuilder();
        for (int i = 0; i < length; i++) {
            dash.append(i % 2 == 0 ? "-" : "=");
        }
        return dash.toString();
    }

    public static String purple(String s) {
        return String.format("\u001B[35m%s\u001B[0m",s);
    }
    public static String cyan(String s) {
        return String.format("\u001B[36m%s\u001B[0m",s);
    }
    public static String yellow(String s) {
        return String.format("\u001B[33m%s\u001B[0m",s);
    }
    public static String grey(String s) {
        return String.format("\u001B[37m%s\u001B[0m",s);
    }

    public static String red(String s) {
        return String.format("\u001B[31m%s\u001B[0m",s);
    }
}

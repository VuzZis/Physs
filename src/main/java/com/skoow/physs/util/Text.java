package com.skoow.physs.util;

public class Text {
    public static boolean num(char c) {
        return c >= '0' && c <= '9';
    }
    public static boolean alpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= 'а' && c <= 'я') ||
                (c >= 'А' && c <= 'Я') ||
                c == '_';
    }
    public static boolean alphaNum(char c) {
        return alpha(c) || num(c);
    }
}

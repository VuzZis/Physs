package com.skoow.physs.lexer.scanner;

public class Position {
    public int start = 0;
    public int current = 0;
    public int line = 1;
    public int symbol = 1;
    public Position() {home();}

    public void newLine() {
        line++; symbol=1;
    }

    public void home() {
        start = 0;
        current = 0;
        line = 1;
        symbol = 1;
    }
}

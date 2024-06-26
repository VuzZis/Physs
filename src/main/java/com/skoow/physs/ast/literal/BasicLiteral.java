package com.skoow.physs.ast.literal;

import com.skoow.physs.lexer.scanner.Position;

public class BasicLiteral implements Literal {
    private final int line;
    private final int symbol;
    public final Object value;

    public BasicLiteral(Object value, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int symbol() {
        return symbol;
    }
}

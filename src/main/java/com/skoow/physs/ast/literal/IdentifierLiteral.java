package com.skoow.physs.ast.literal;

import com.skoow.physs.lexer.scanner.Position;

public class IdentifierLiteral implements Literal {
    private final int line;
    private final int symbol;
    private final String value;

    public IdentifierLiteral(String value, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.value = value;
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

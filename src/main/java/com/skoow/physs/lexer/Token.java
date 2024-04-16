package com.skoow.physs.lexer;

import com.skoow.physs.lexer.scanner.Position;

public class Token {
    public final TokenType tokenType;
    public final String lexeme;
    public final Object literal;
    public final int line;
    public final int symbol;

    public Token(TokenType type, String lexeme, Object literal,int line,int symbol) {
        this.tokenType = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.symbol = symbol;
    }
    public Token(TokenType type, String lexeme, Object literal, Position position) {
        this(type,lexeme,literal,position.line,position.symbol);
    }

    @Override
    public String toString() {
        return tokenType+" "+lexeme+" "+literal;
    }
}

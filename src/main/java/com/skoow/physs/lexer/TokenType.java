package com.skoow.physs.lexer;

public enum TokenType {
    EOF,

    LEFT_PAREN,RIGHT_PAREN,LEFT_BRACE,RIGHT_BRACE,LEFT_BRACKET,RIGHT_BRACKET,
    COMMA,DOT,COLON,SEMICOLON, MINUS,PLUS,SLASH,MULTIPLIER,TILDA,QUOTE,MOD,


    ARROW, CAST,

    IDENTIFIER,STRING,NUMBER,

    T_INT,T_STR,T_FLOAT,T_DOUBLE,T_IDENTIFIER,T_BOOL,

    EQUALS,LESS,GREATER,BANG,
    EQUALS_EQUALS,LESS_EQUALS,GREATER_EQUALS,BANG_EQUALS,

    DOLLAR,

    AND,CLASS,ELSE,FALSE,FUNCTION,FOR,IF,NIL,OR,STATIC,
    PRINT,INPUT,RETURN,EXIT,SUPER,THIS,TRUE,VAL, UNX, BACK_SLASH, WHILE
}

package com.skoow.physs.ast.expression;

import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

public class BinaryExpr implements Expr {
    String name = "BinaryExpr";
    private final int line;
    private final int symbol;
    public final Expr left;
    public final Expr right;
    public final Token operator;

    public BinaryExpr(Expr left, Token operator, Expr right, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.left = left;
        this.operator = operator;
        this.right = right;
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

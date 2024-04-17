package com.skoow.physs.ast.expression;

import com.skoow.physs.lexer.scanner.Position;

public class GroupExpr implements Expr {
    private final int line;
    private final int symbol;
    public final Expr expr;

    public GroupExpr(Expr left, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.expr = left;
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

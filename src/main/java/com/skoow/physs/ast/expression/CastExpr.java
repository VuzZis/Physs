package com.skoow.physs.ast.expression;

import com.skoow.physs.ast.literal.Literals;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

public class CastExpr implements Expr {
    String name = "CastExpr";
    private final int line;
    private final int symbol;
    public final Expr expr;
    public final Literals operator;

    public CastExpr(Expr left, Literals operator, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.expr = left;
        this.operator = operator;
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

package com.skoow.physs.ast.expression;

import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

public class GetPropertyExpr implements Expr {
    String name = "GetPropertyExpr";
    private final int line;
    private final int symbol;
    public final Token var;
    public final Expr expr;

    public GetPropertyExpr(Token var, Expr expr, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.var = var;
        this.expr = expr;
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

package com.skoow.physs.ast.statement;

import com.skoow.physs.ast.expression.Expr;
import com.skoow.physs.lexer.scanner.Position;

public class InputExpr implements Expr {
    String name = "InputExpr";
    private final int line;
    private final int symbol;
    public final Expr expr;

    public InputExpr(Expr expr, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
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

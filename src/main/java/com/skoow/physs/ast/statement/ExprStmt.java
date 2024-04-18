package com.skoow.physs.ast.statement;

import com.skoow.physs.ast.expression.Expr;
import com.skoow.physs.lexer.scanner.Position;

public class ExprStmt implements Stmt {
    String name = "ExpressionStmt";
    private final int line;
    private final int symbol;
    private final Expr expr;

    public ExprStmt(Expr expr, Position position) {
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
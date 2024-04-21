package com.skoow.physs.ast.statement;

import com.skoow.physs.ast.expression.Expr;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

public class ReturnStmt implements Stmt {
    String name = "ReturnStmt";
    private final int line;
    private final int symbol;
    public final Token keyword;
    public final Expr expr;

    public ReturnStmt(Token keyword, Expr expr, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.expr = expr;
        this.keyword = keyword;
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

package com.skoow.physs.ast.expression;

import com.skoow.physs.ast.statement.Stmt;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

import java.util.List;

public class LambdaExpr implements Expr {
    String name = "LambdaExpr";
    private final int line;
    private final int symbol;
    public final Stmt body;
    public final List<Token> params;

    public LambdaExpr(List<Token> params, Stmt stmt, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.params = params;
        this.body = stmt;
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

package com.skoow.physs.ast.expression;

import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

import java.util.List;

public class CallExpr implements Expr {
    private final int line;
    private final int symbol;
    public final Expr callee;
    public final Token paren;
    public final List<Expr> args;

    public CallExpr(Expr callee, Token paren, List<Expr> args, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.callee = callee;
        this.paren = paren;
        this.args = args;
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

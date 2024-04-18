package com.skoow.physs.ast.expression;

import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

public class AssignmentExpr implements Expr {
    String name = "AssignmentExpr";
    private final int line;
    private final int symbol;
    public final Token var;
    public final Expr value;

    public AssignmentExpr(Token var,Expr value, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.var = var;
        this.value = value;
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

package com.skoow.physs.ast.expression;

import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

public class PropertyAssignmentExpr implements Expr {
    String name = "PropertyAssignmentExpr";
    private final int line;
    private final int symbol;
    public final Expr object;
    public final Token var;
    public final Expr value;

    public PropertyAssignmentExpr(Expr object, Token var, Expr value, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.var = var;
        this.value = value;
        this.object = object;
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

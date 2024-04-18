package com.skoow.physs.ast.expression;

import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

public class VarGetExpr implements Expr {
    String name = "VarGetExpr";
    private final int line;
    private final int symbol;
    public final Token var;

    public VarGetExpr(Token var, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.var = var;
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

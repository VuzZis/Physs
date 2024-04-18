package com.skoow.physs.ast.statement;

import com.skoow.physs.ast.expression.Expr;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

public class VarDeclarStmt implements Stmt {
    String name = "VarDecStmt";
    private final int line;
    private final int symbol;
    private final Token varName;
    private final Expr initializer;

    public VarDeclarStmt(Token name, Expr expr, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.varName = name;
        this.initializer = expr;
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

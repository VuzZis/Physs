package com.skoow.physs.ast.statement;

import com.skoow.physs.ast.expression.Expr;
import com.skoow.physs.lexer.scanner.Position;

public class WhileStmt implements Stmt {
    String name = "WhileStmt";
    private final int line;
    private final int symbol;
    public final Expr condition;
    public final Stmt thenBranch;

    public WhileStmt(Expr condition, Stmt then, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.condition = condition;
        this.thenBranch = then;
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

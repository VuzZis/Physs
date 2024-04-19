package com.skoow.physs.ast.statement;

import com.skoow.physs.ast.expression.Expr;
import com.skoow.physs.lexer.scanner.Position;

public class IfStmt implements Stmt {
    String name = "IfStmt";
    private final int line;
    private final int symbol;
    public final Expr condition;
    public final Stmt thenBranch;
    public final Stmt elseBranch;

    public IfStmt(Expr condition, Stmt then, Stmt elseBranch, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.condition = condition;
        this.thenBranch = then;
        this.elseBranch = elseBranch;
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

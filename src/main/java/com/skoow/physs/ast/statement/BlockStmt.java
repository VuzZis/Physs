package com.skoow.physs.ast.statement;

import com.skoow.physs.ast.expression.Expr;
import com.skoow.physs.lexer.scanner.Position;

import java.util.List;

public class BlockStmt implements Stmt {
    String name = "BlockStmt";
    private final int line;
    private final int symbol;
    public final List<Stmt> stmts;

    public BlockStmt(List<Stmt> stmts, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.stmts = stmts;
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

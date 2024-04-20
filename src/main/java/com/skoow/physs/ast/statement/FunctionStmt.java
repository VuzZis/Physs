package com.skoow.physs.ast.statement;

import com.skoow.physs.ast.expression.Expr;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

import java.util.List;

public class FunctionStmt implements Stmt {
    String name = "FunctionStmt";
    private final int line;
    private final int symbol;
    public final Token varName;
    public final List<Token> params;
    public final List<Stmt> body;
    public FunctionStmt(Token name, List<Token> params, List<Stmt> body, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.varName = name;
        this.params = params;
        this.body = body;
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

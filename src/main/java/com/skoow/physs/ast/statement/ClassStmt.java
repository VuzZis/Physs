package com.skoow.physs.ast.statement;

import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Position;

import java.util.List;

public class ClassStmt implements Stmt {
    String name = "ClassStmt";
    private final int line;
    private final int symbol;
    public final Token varName;
    public final List<FunctionStmt> methods;
    public ClassStmt(Token name, List<FunctionStmt> methods, Position position) {
        this.line = position.line;
        this.symbol = position.symbol;
        this.varName = name;
        this.methods = methods;
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

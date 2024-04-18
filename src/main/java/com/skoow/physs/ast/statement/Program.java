package com.skoow.physs.ast.statement;

import java.util.List;

public class Program {
    public List<Stmt> lines;

    public Program(List<Stmt> lines) {
        this.lines = lines;
    }
}

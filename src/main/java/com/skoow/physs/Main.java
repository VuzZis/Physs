package com.skoow.physs;

import com.skoow.physs.ast.Parser;
import com.skoow.physs.ast.statement.Program;
import com.skoow.physs.ast.statement.Stmt;
import com.skoow.physs.engine.Context;
import com.skoow.physs.error.PhyssReporter;
import com.skoow.physs.gson.GsonUtil;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Scanner;
import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.Scope;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String sourceCode = """
                val i = 1;
                out $$WHILE_MAX_ITERATIONS;
                """;
        Context context = Context.begin();
        context.evaluateString(sourceCode,"main.phy");
    }
}
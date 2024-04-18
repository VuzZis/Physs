package com.skoow.physs;

import com.skoow.physs.ast.Parser;
import com.skoow.physs.ast.statement.Program;
import com.skoow.physs.ast.statement.Stmt;
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
                val a = 5;
                val b = nil;
                print b;
                print c;
                """;
        Scanner scanner = new Scanner(sourceCode);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> expr = parser.parseStatements();
        String tokenJson = GsonUtil.GSON_PRETTY.toJson(tokens);
        String astJson = GsonUtil.GSON_PRETTY.toJson(expr);
        Program program = new Program(expr);
        PhyssReporter.reportInfo(0,0,"Test info");
        PhyssReporter.reportDebug(0,0,"Test debug");
        Interpreter interpreter = new Interpreter(program,new Scope());
        interpreter.interpreteProgram();
    }
}
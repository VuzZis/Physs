package com.skoow.physs.engine;

import com.skoow.physs.ast.Parser;
import com.skoow.physs.ast.statement.Program;
import com.skoow.physs.error.PhyssReporter;
import com.skoow.physs.lexer.scanner.Scanner;
import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.Scope;

import java.util.Date;

public class Context {

    private final Scope scope;

    public Context(Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }


    public void evaluateString(String code, String fileName) {
        long timeBegin = new Date().getTime();

        Scanner scanner = new Scanner(code);
        Parser astParser = new Parser(scanner.scanTokens());

        Program script = new Program(astParser.parseStatements());
        Interpreter interpreter = new Interpreter(script,scope);
        interpreter.interpreteProgram();
        double timeTook = new Date().getTime()-timeBegin;
        PhyssReporter.reportDebug(0,0,String.format("Took %sms to evaluate %s",timeTook,fileName));
    }

    public static Context begin() {
        Scope scope = new Scope();
        return new Context(scope);
    }




}

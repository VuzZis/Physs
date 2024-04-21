package com.skoow.physs.engine;

import com.skoow.physs.ast.Parser;
import com.skoow.physs.ast.statement.Program;
import com.skoow.physs.error.PhyssReporter;
import com.skoow.physs.lexer.scanner.Scanner;
import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.Scope;

import java.util.Date;

public class Context {


    private static final Scope global = new GlobalScope();
    private final Scope scope;

    public Context(Scope scope) {
        this.scope = scope;
    }

    public static Scope getGlobals() {
        return global;
    }

    public Scope getScope() {
        return scope;
    }


    public void evaluateString(String code, String fileName) {

        Scanner scanner = new Scanner(code);
        Parser astParser = new Parser(scanner.scanTokens());
        if(PhyssReporter.hadError) {PhyssReporter.reportDebug("Failed to lexer "+fileName); return;}
        Program script = new Program(astParser.parseStatements());
        if(PhyssReporter.hadError) {PhyssReporter.reportDebug("Failed to parse "+fileName); return;}

        long timeBegin = new Date().getTime();
        Interpreter interpreter = new Interpreter(script,scope);
        interpreter.interpreteProgram();
        double timeTook = new Date().getTime()-timeBegin;
        PhyssReporter.reportDebug(String.format("Took %sms to evaluate %s",timeTook,fileName));
    }

    public static Context beginWithNative() {
        Scope scope = new Scope(getGlobals());
        return new Context(scope);
    }
    public static Context beginWithCustom(Scope locGlobals) {
        Scope scope = new Scope(locGlobals);
        return new Context(scope);
    }




}

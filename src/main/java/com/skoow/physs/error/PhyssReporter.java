package com.skoow.physs.error;

import com.skoow.physs.ast.statement.Stmt;
import com.skoow.physs.error.errors.PhyssException;
import com.skoow.physs.lexer.Token;

import static com.skoow.physs.lexer.TokenType.EOF;

public class PhyssReporter {
    public static boolean hadError = false;
    public static String name = "";
    public static void error(int line, int symbol, PhyssException exception) {
        reportError(line,symbol,"",exception.getMessage());
        PhyssReporter.hadError = true;
    }
    public static void error(Token token, String message) {
        if(token.tokenType == EOF)
            PhyssReporter.reportError(token.line,token.symbol," at end",message);
        else
            PhyssReporter.reportError(token.line,token.symbol," at '"+token.lexeme+"'", message);
        PhyssReporter.hadError = true;
    }

    public static void error(Stmt token, String message) {
        PhyssReporter.reportError(token.line(),token.symbol()," at runtime",message);
    }

    public static void reportDebug(int line, int symbol, String message) {
        System.out.printf("\u001B[35m[Physs][%s][DEBUG][%s:%s] %s%n\u001B[0m",name,line,symbol,message);
    }
    public static void reportDebug(String message) {
        System.out.printf("\u001B[35m[Physs][DEBUG] %s%n\u001B[0m",message);
    }

    public static void reportInfo(int line, int symbol, String message) {
        System.out.printf("\u001B[39m[Physs][%s][INFO][%s:%s] %s%n\u001B[0m",name,line,symbol,message);
    }

    public static void reportInput(int line, int symbol, String message) {
        System.out.printf("\u001B[36m[Physs][%s][IN][%s:%s] %s\u001B[0m",name,line,symbol,message);
    }

    public static void reportError(String message) {
        System.out.printf("\u001B[31m[Physs][ERROR] %s%n\u001B[0m",message);
    }

    public static void reportError(int line, int symbol, String where, String message) {
        System.out.printf("\u001B[31m[Physs][%s][ERROR][%s:%s] Exception%s: %s%n\u001B[0m",name,line,symbol,where,message);
    }

    public static void reportWarn(int line, int symbol, String where, String message) {
        System.out.printf("\u001B[33m[Physs][%s][WARN][%s:%s] Warning%s: %s%n\u001B[0m",name,line,symbol,where,message);
    }
}

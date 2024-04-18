package com.skoow.physs.error;

import com.skoow.physs.ast.statement.Stmt;
import com.skoow.physs.error.errors.PhyssException;
import com.skoow.physs.lexer.Token;

import static com.skoow.physs.lexer.TokenType.EOF;

public class PhyssReporter {
    public static boolean hadError = false;
    public static void error(int line, int symbol, PhyssException exception) {
        reportError(line,symbol,"",exception.getMessage());
        hadError = true;
    }
    public static void error(Token token, String message) {
        if(token.tokenType == EOF)
            PhyssReporter.reportError(token.line,token.symbol," at end",message);
        else
            PhyssReporter.reportError(token.line,token.symbol," at '"+token.lexeme+"'", message);
    }

    public static void error(Stmt token, String message) {
        PhyssReporter.reportError(token.line(),token.symbol()," at runtime",message);
    }

    public static void reportDebug(int line, int symbol, String message) {
        System.out.printf("\u001B[35m[Physs][DEBUG][%s:%s] %s%n\u001B[0m",line,symbol,message);
    }

    public static void reportInfo(int line, int symbol, String message) {
        System.out.printf("\u001B[39m[Physs][INFO][%s:%s] %s%n\u001B[0m",line,symbol,message);
    }

    public static void reportError(int line, int symbol, String where, String message) {
        System.out.printf("\u001B[31m[Physs][ERROR][%s:%s] Exception%s: %s%n\u001B[0m",line,symbol,where,message);
    }

    public static void reportWarn(int line, int symbol, String where, String message) {
        System.out.printf("\u001B[33m[Physs][WARN][%s:%s] Warning%s: %s%n\u001B[0m",line,symbol,where,message);
    }
}

package com.skoow.physs.error;

import com.skoow.physs.error.errors.PhyssException;
import com.skoow.physs.lexer.Token;

import static com.skoow.physs.lexer.TokenType.EOF;

public class PhyssErrorHandler {
    public static boolean hadError = false;
    public static void error(int line, int symbol, PhyssException exception) {
        report(line,symbol,"",exception.getMessage());
        hadError = true;
    }
    public static void error(Token token, String message) {
        if(token.tokenType == EOF)
            PhyssErrorHandler.report(token.line,token.symbol," at end",message);
        else
            PhyssErrorHandler.report(token.line,token.symbol," at '"+token.lexeme+"'", message);
    }

    public static void report(int line, int symbol, String where, String message) {
        System.out.printf("[Physs][%s:%s] Exception%s: %s%n",line,symbol,where,message);
    }
}

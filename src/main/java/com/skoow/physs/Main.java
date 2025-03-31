package com.skoow.physs;

import com.skoow.physs.parsing.lexer.Lexer;
import com.skoow.physs.parsing.lexer.Token;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer("""
                var a = 5.5;
                var b = a + .5;
                print(a*b);
                """);
        lexer.scan();
        List<Token> toks = lexer.finish();
        for (Token tok : toks) {
            System.out.println(tok.toString());
        }
    }
}

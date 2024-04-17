package com.skoow.physs;

import com.skoow.physs.ast.Parser;
import com.skoow.physs.ast.expression.Expr;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Scanner;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String sourceCode = """
                //Text code
                $someIdentifier == "someIdentifier"
                """;
        Scanner scanner = new Scanner(sourceCode);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        tokens.forEach(System.out::println);
        Expr expr = parser.expr();
        System.out.println(expr);
    }
}
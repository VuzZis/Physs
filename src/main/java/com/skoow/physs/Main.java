package com.skoow.physs;

import com.skoow.physs.ast.Parser;
import com.skoow.physs.ast.expression.Expr;
import com.skoow.physs.ast.statement.Stmt;
import com.skoow.physs.gson.GsonUtil;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Scanner;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String sourceCode = """
                //Text code
                val testValue = 3;
                val a3;
                val testValue2 = testValue+(-5+2);
                print testValue == "someIdentifier";
                """;
        Scanner scanner = new Scanner(sourceCode);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> expr = parser.parseStatements();
        String tokenJson = GsonUtil.GSON_PRETTY.toJson(tokens);
        String astJson = GsonUtil.GSON_PRETTY.toJson(expr);
    }
}
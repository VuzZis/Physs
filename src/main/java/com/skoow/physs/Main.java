package com.skoow.physs;

import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.scanner.Scanner;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String sourceCode = """
                //Text code
                val a = 5
                val b = 3
                val c = 6
                print(a+b+c)
                
                """;
        Scanner scanner = new Scanner(sourceCode);
        List<Token> tokens = scanner.scanTokens();
        tokens.forEach(System.out::println);
    }
}
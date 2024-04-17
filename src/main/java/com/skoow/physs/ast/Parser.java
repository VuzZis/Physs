package com.skoow.physs.ast;

import com.skoow.physs.ast.expression.*;
import com.skoow.physs.ast.literal.BasicLiteral;
import com.skoow.physs.ast.literal.IdentifierLiteral;
import com.skoow.physs.error.PhyssErrorHandler;
import com.skoow.physs.error.errors.AstException;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.TokenType;
import com.skoow.physs.lexer.scanner.Position;

import static com.skoow.physs.lexer.TokenType.*;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private final Position position = new Position();


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expr expr() {
        return equalityExpr();
    }

    private Expr equalityExpr() {
        Position localPos = new Position(position);
        Expr expr = comparison();
        while(match(BANG_EQUALS,EQUALS_EQUALS)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new BinaryExpr(expr,operator,right,localPos);
        }
        return expr;
    }

    private Expr comparison() {
        Position localPos = new Position(position);
        Expr expr = term();
        while (match(GREATER,GREATER_EQUALS,LESS,LESS_EQUALS)) {
            Token operator = previous();
            Expr right = term();
            expr = new BinaryExpr(expr,operator,right,localPos);
        }
        return expr;
    }

    private Expr term() {
        Position localPos = new Position(position);
        Expr expr = factor();
        while(match(MINUS,PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new BinaryExpr(expr,operator,right,localPos);
        }
        return expr;
    }

    private Expr factor() {
        Position localPos = new Position(position);
        Expr expr = unary();
        while(match(MULTIPLIER,SLASH)) {
            Token operator = previous();
            Expr right = unary();
            expr = new BinaryExpr(expr,operator,right,localPos);
        }
        return expr;
    }

    private Expr unary() {
        Position localPos = new Position(position);
        if(match(BANG,MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new UnaryExpr(right,operator,localPos);
        }
        return primary();
    }

    private Expr primary() {
        Position localPos = new Position(position);
        if(match(TRUE)) return new BasicLiteral(true,localPos);
        if(match(FALSE)) return new BasicLiteral(false,localPos);
        if(match(NIL)) return new BasicLiteral(null,localPos);

        if(match(STRING,NUMBER)) return new BasicLiteral(previous().literal,localPos);
        if(match(DOLLAR)) {
            if(match(IDENTIFIER)) return new IdentifierLiteral(previous().lexeme,localPos);
        }
        if(match(LEFT_PAREN)) {
            Expr expr = expr();
            consume(RIGHT_PAREN,"Expected ')' after expression.");
            return new GroupExpr(expr,position);
        }
        PhyssErrorHandler.error(position.line,position.symbol,
                new AstException("Unexpected token found when parsing script: "+peek().lexeme));
        return new BasicLiteral(null,localPos);
    }

    private void consume(TokenType tokenType, String s) {
        if(peek().tokenType != tokenType) PhyssErrorHandler.error(position.line, position.symbol,
                new AstException(s));
        else advance();
    }


    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if(check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if(isAtEnd()) return false;
        return peek().tokenType == type;
    }

    private Token advance() {
        if(!isAtEnd()) position.current++;
        Token token = previous();
        position.line = token.line;
        position.symbol = token.symbol;
        return token;
    }

    private boolean isAtEnd() {
        return peek().tokenType == EOF;
    }
    private Token peek() {
        if(position.current >= tokens.size()) return previous();
        Token token = tokens.get(position.current);
        position.line = token.line;
        position.symbol = token.symbol;
        return token;
    }

    private Token previous() {
        Token token = tokens.get(position.current-1);
        position.line = token.line;
        position.symbol = token.symbol;
        return token;
    }

}

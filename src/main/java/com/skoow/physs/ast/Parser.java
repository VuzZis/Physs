package com.skoow.physs.ast;

import com.skoow.physs.ast.expression.*;
import com.skoow.physs.ast.literal.*;
import com.skoow.physs.ast.statement.*;
import com.skoow.physs.error.PhyssReporter;
import com.skoow.physs.error.errors.AstException;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.TokenType;
import com.skoow.physs.lexer.scanner.Position;

import static com.skoow.physs.lexer.TokenType.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private final Position position = new Position();


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parseStatements() {
        position.home();
        List<Stmt> stmts = new ArrayList<>();
        while (!isAtEnd()) {
            stmts.add(declaration());
        }
        return stmts.stream().toList();
    }

    private Stmt declaration() {
        try {
            if(match(VAL)) return varDeclaration();
            return statement();
        } catch(AstException exception) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER,"Expected variable name.");
        Expr initializer = new BasicLiteral(null,position);
        if(match(EQUALS)) {
            initializer = expr();
        }
        consume(SEMICOLON,"Expected ';' after variable declaration.");
        return new VarDeclarStmt(name,initializer,position);
    }

    private Stmt statement() {
        if(match(PRINT)) return printStatement();
        return exprStatement();
    }

    private Stmt printStatement() {
        Expr value = expr();
        consume(SEMICOLON,"Expected ';' after print statement.");
        return new PrintStmt(value,position);
    }

    private Stmt exprStatement() {
        Expr value = expr();
        consume(SEMICOLON,"Expected ';' after expression.");
        return new ExprStmt(value,position);
    }


    public Expr expr() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = logical();
        if(match(EQUALS)) {
            Token equals = previous();
            Expr value = assignment();
            if(expr instanceof VarGetExpr varExpr) {
                Token name = varExpr.var;
                return new AssignmentExpr(name,value,position);
            }
            throw error(equals,"Invalid variable name needed assign to.");
        }
        return expr;
    }

    private Expr logical() {

        Expr expr = equality();
        while(match(OR,AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new BinaryExpr(expr,operator,right,position);
        }
        return expr;
    }

    private Expr equality() {
        
        Expr expr = comparison();
        while(match(BANG_EQUALS,EQUALS_EQUALS)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new BinaryExpr(expr,operator,right,position);
        }
        return expr;
    }

    private Expr comparison() {
        
        Expr expr = term();
        while (match(GREATER,GREATER_EQUALS,LESS,LESS_EQUALS)) {
            Token operator = previous();
            Expr right = term();
            expr = new BinaryExpr(expr,operator,right,position);
        }
        return expr;
    }

    private Expr term() {
        
        Expr expr = factor();
        while(match(MINUS,PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new BinaryExpr(expr,operator,right,position);
        }
        return expr;
    }

    private Expr factor() {
        
        Expr expr = unary();
        while(match(MULTIPLIER,SLASH)) {
            Token operator = previous();
            Expr right = unary();
            expr = new BinaryExpr(expr,operator,right,position);
        }
        return expr;
    }

    private Expr unary() {
        
        if(match(BANG,MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new UnaryExpr(right,operator,position);
        }
        return primary();
    }

    private Expr primary() {
        
        if(match(TRUE)) return new BasicLiteral(true,position);
        if(match(FALSE)) return new BasicLiteral(false,position);
        if(match(NIL)) return new BasicLiteral(null,position);

        if(match(STRING,NUMBER)) return new BasicLiteral(previous().literal,position);
        if(match(DOLLAR)) {
            return new IdentifierLiteral(consume(IDENTIFIER,"Expected identifier literal").lexeme,position);
        }
        if(match(IDENTIFIER)) return new VarGetExpr(previous(),position);
        if(match(LEFT_PAREN)) {
            Expr expr = expr();
            consume(RIGHT_PAREN,"Expected ')' after expression.");
            return new GroupExpr(expr,position);
        }
        throw error(peek(),"Expected expression, found: '"+peek().lexeme+"'");
    }


    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if(previous().tokenType == SEMICOLON) return;
            switch (peek().tokenType) {
                case CLASS:
                case FUNCTION:
                case VAL:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }

    private Token consume(TokenType tokenType, String s) {
        if(check(tokenType)) return advance();
        throw error(peek(),s);
    }

    private AstException error(Token token, String message) {
        PhyssReporter.error(token,message);
        return new AstException(message);
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

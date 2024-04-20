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
import java.util.Arrays;
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
            if(match(FUNCTION)) return fnDeclaration("function");
            if(match(VAL)) return varDeclaration();
            return statement();
        } catch(AstException exception) {
            synchronize();
            return null;
        }
    }

    private Stmt fnDeclaration(String kind) {
        Token name = consume(IDENTIFIER,"Expected %s name",kind);
        consume(LEFT_PAREN,"Expected '(' after %s name",kind);
        List<Token> params = new ArrayList<>();
        if(!check(RIGHT_PAREN)) {
            do {
                if(params.size() >= 255) throw error(peek(),"More than 255 parameters specified");
                params.add(consume(IDENTIFIER,"Expected parameter name"));
            } while(match(COMMA));
        }
        consume(RIGHT_PAREN,"Expected ')' after %s parameters");
        consume(LEFT_BRACE,"Expected '{' before %s body",kind);
        List<Stmt> body = block();
        return new FunctionStmt(name,params,body,position);
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER,"Expected variable name.");
        Expr initializer = null;
        if(match(EQUALS)) {
            initializer = expr();
        }
        consume(SEMICOLON,"Expected ';' after variable declaration.");
        return new VarDeclarStmt(name,initializer,position);
    }

    private Stmt statement() {
        if(match(FOR)) return forStatement();
        if(match(IF)) return ifStatement();
        if(match(PRINT)) return printStatement();
        if(match(WHILE)) return whileStatement();
        if(match(LEFT_BRACE)) return new BlockStmt(block(),position);
        return exprStatement();
    }

    private Stmt forStatement() {
        consume(LEFT_PAREN,"Expected '(' after 'for'");

        Stmt initializer;
        if(match(SEMICOLON)) initializer = null;
        else if (match(VAL)) initializer = varDeclaration();
        else initializer = exprStatement();

        Expr condition = null;
        if(!check(SEMICOLON)) condition = expr();
        consume(SEMICOLON,"Expected ';' after loop condition");

        Expr increment = null;
        if(!check(RIGHT_PAREN)) increment = expr();

        consume(RIGHT_PAREN,"Expected ')' after 'for' clauses");
        Stmt body = statement();
        if(increment != null)
            body = new BlockStmt(Arrays.asList(
                body,
                new ExprStmt(increment,position)
            ),position);

        if(condition == null) condition = new BasicLiteral(true,position);
        body = new WhileStmt(condition,body,position);

        if(initializer != null)
            body = new BlockStmt(Arrays.asList(
                initializer,
                body
            ),position);

        return body;
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN,"Expected '(' after 'while'");
        Expr condition = expr();
        consume(RIGHT_PAREN,"Expected ')' after 'while' condition");
        Stmt body = statement();
        return new WhileStmt(condition,body,position);
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN,"Expected '(' after 'if'");
        Expr condition = expr();
        consume(RIGHT_PAREN,"Expected ')' after 'if' condition");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if(match(ELSE)) {
            elseBranch = statement();
        }
        return new IfStmt(condition,thenBranch,elseBranch,position);
    }

    private List<Stmt> block() {
        List<Stmt> stmts = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            stmts.add(declaration());
        }
        consume(RIGHT_BRACE,"Expected '}' after program body.");
        return stmts;
    }

    private Stmt printStatement() {
        Expr value = expr();
        consume(SEMICOLON,"Expected ';' after print statement.");
        return new PrintStmt(value,position);
    }
    private Expr inputExpr() {
        Expr value = expr();
        return new InputExpr(value,position);
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
        if(match(INPUT)) return inputExpr();
        return call();
    }

    private Expr call() {
        Expr expr = primary();
        while (true) {
            if(match(LEFT_PAREN)) expr = finishCall(expr);
            else break;
        }
        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> args = new ArrayList<>();
        if(!check(RIGHT_PAREN))
            do {
                if(args.size() >= 255) error(peek(),"More than 255 arguments specified");
                args.add(expr());
            } while(match(COMMA));
        Token paren = consume(RIGHT_PAREN,"Expected ')' after call arguments");
        return new CallExpr(callee,paren,args,position);
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

    private Token consume(TokenType tokenType, String s, Object... args) {
        if(check(tokenType)) return advance();
        throw error(peek(),String.format(s,args));
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

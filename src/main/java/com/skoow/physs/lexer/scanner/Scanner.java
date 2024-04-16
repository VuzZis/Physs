package com.skoow.physs.lexer.scanner;

import com.skoow.physs.error.PhyssErrorHandler;
import com.skoow.physs.error.errors.LexerException;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scanner {

    private final String source;
    private final Position position;

    private static final HashMap<Character,TokenType> CHAR_TOKENS = new HashMap<>();
    private static final HashMap<String,TokenType> DOUBLE_CHAR_TOKENS = new HashMap<>();
    private static final HashMap<String,TokenType> KEYWORDS = new HashMap<>();

    static {
        CHAR_TOKENS.put('(',TokenType.LEFT_PAREN); CHAR_TOKENS.put(')',TokenType.RIGHT_PAREN);
        CHAR_TOKENS.put('[',TokenType.LEFT_BRACKET); CHAR_TOKENS.put(']',TokenType.RIGHT_BRACKET);
        CHAR_TOKENS.put('{',TokenType.LEFT_BRACE); CHAR_TOKENS.put('}',TokenType.RIGHT_BRACE);
        CHAR_TOKENS.put(',',TokenType.COMMA); CHAR_TOKENS.put('.',TokenType.DOT);
        CHAR_TOKENS.put('-',TokenType.MINUS); CHAR_TOKENS.put('+',TokenType.PLUS);
        CHAR_TOKENS.put('*',TokenType.MULTIPLIER); CHAR_TOKENS.put('/',TokenType.SLASH);
        CHAR_TOKENS.put(':',TokenType.COLON); CHAR_TOKENS.put(';',TokenType.SEMICOLON);
        CHAR_TOKENS.put('~',TokenType.TILDA); CHAR_TOKENS.put('=',TokenType.EQUALS);
        CHAR_TOKENS.put('>',TokenType.GREATER); CHAR_TOKENS.put('<',TokenType.LESS);
        CHAR_TOKENS.put('!',TokenType.BANG); CHAR_TOKENS.put('"',TokenType.QUOTE);
        CHAR_TOKENS.put('\0',TokenType.UNX);

        DOUBLE_CHAR_TOKENS.put("!=",TokenType.BANG_EQUALS); DOUBLE_CHAR_TOKENS.put("==",TokenType.EQUALS_EQUALS);
        DOUBLE_CHAR_TOKENS.put(">=",TokenType.GREATER_EQUALS); DOUBLE_CHAR_TOKENS.put("<=",TokenType.LESS_EQUALS);

        KEYWORDS.put("val",TokenType.VAL);


        KEYWORDS.put("str",TokenType.T_STR); KEYWORDS.put("int",TokenType.T_INT);
        KEYWORDS.put("flt",TokenType.T_FLOAT); KEYWORDS.put("bool",TokenType.T_BOOL);
        KEYWORDS.put("dbl",TokenType.T_DOUBLE); KEYWORDS.put("id",TokenType.T_IDENTIFIER);
    }

    public Scanner(String source) {
        this.source = source.trim();
        this.position = new Position();
    }

    public List<Token> scanTokens () {
        position.home();
        List<Token> tokens = new ArrayList<>();
        while(!isAtEnd()) {
            position.start = position.current;
            if(peek() == '\n') {advance(); position.newLine();continue;}
            if(peek() == ' ' || peek() == '\t' || peek() == '\r') {advance();continue;}
            Token token = scanToken();
            if(token == null) continue;
            addToken(tokens,token);
        }
        return tokens;
    }



    private Token scanToken() {
        char c = advance();
        char c2 = peek();
        TokenType cToken = findChar(c);
        TokenType cToken2 = findChar(c2);
        Token token = null;
        if(cToken == TokenType.QUOTE) token = scanString();
        else if(cToken == TokenType.IDENTIFIER) token = scanIdentifier();
        else if(cToken == TokenType.NUMBER) token = scanNumber();
        else if(cToken == TokenType.SLASH && cToken2 == TokenType.SLASH) {
            while (peek() != '\n' && !isAtEnd()) advance();
            return null;
        }
        else {
            String cc = String.valueOf(c)+c2;
            cToken = DOUBLE_CHAR_TOKENS.getOrDefault(cc,cToken);
        }

        return token == null ? new Token(
                cToken,
                source.substring(position.start, position.current),
                null,
                position
        ) : token;
    }

    private Token scanNumber() {
        while (isNumeric(peek())) advance();
        if(peek() == '.' && isNumeric(peekNext())) {
            advance();
            while (isNumeric(peek())) advance();
        }
        String numText = source.substring(position.start, position.current);
        return new Token(TokenType.NUMBER,numText,Double.parseDouble(numText), position);
    }

    private Token scanIdentifier() {
        while(isAlphaNumeric(peek())) advance();
        String identifierValue = source.substring(position.start, position.current);
        return new Token(findKeyword(identifierValue),identifierValue,identifierValue, position);
    }

    private Token scanString() {
        while(!isAtEnd() && peek() != '"') {
            if(peek() == '\n') position.newLine();
            advance();
        }
        if(isAtEnd()) PhyssErrorHandler.error(position.line, position.symbol,
                new LexerException("Unterminated string. Expected '\"'"));
        advance();
        String stringValue = source.substring(position.start+1, position.current-1);
        return new Token(TokenType.STRING,stringValue,stringValue, position);
    }

    private TokenType findChar(char c) {
        TokenType token = findKeyword(String.valueOf(c));
        if(CHAR_TOKENS.containsKey(c)) token = CHAR_TOKENS.get(c);
        if(token == TokenType.IDENTIFIER && isNumeric(c)) token = TokenType.NUMBER;
        if(c == '\n') token = TokenType.UNX;
        if(c == ' ' || c == '\t' || c == '\r') token = TokenType.UNX;
        if(token == TokenType.IDENTIFIER && !isAlpha(c)) PhyssErrorHandler.error(position.line,position.symbol,
                new LexerException("Unexpected character: '"+c+"'"));
        return token;
    }

    private TokenType findKeyword(String c) {
        return KEYWORDS.getOrDefault(c,TokenType.IDENTIFIER);
    }

    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(position.current);
    }
    private char peekNext() {
        if(position.current+1 >= source.length()) return '\0';
        return source.charAt(position.current+1);
    }

    private char advance() {
        position.symbol++;
        return source.charAt(position.current++);
    }

    private void addToken(List<Token> tokens, TokenType type) {
        addToken(tokens,type,null);
    }
    private void addToken(List<Token> tokens, TokenType type, Object literal) {
        String text = source.substring(position.start, position.current);
        addToken(tokens,new Token(type,text,literal, position));
    }
    private void addToken(List<Token> tokens, Token token) {
        tokens.add(token);
    }




    public boolean isAtEnd() {
        return position.current >= source.length();
    }

    public boolean isNumeric(char c) {
        return c >= '0' && c <= '9';
    }
    public boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||  (c >= 'A' && c <= 'Z') || c == '_';
    }
    public boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isNumeric(c);
    }
}

package com.skoow.physs.lexer.scanner;

import com.skoow.physs.engine.component.Translatable;
import com.skoow.physs.error.PhyssReporter;
import com.skoow.physs.error.errors.LexerException;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.TokenType;
import static com.skoow.physs.lexer.TokenType.*;

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
        CHAR_TOKENS.put('(',LEFT_PAREN); CHAR_TOKENS.put(')',RIGHT_PAREN);
        CHAR_TOKENS.put('[',LEFT_BRACKET); CHAR_TOKENS.put(']',RIGHT_BRACKET);
        CHAR_TOKENS.put('{',LEFT_BRACE); CHAR_TOKENS.put('}',RIGHT_BRACE);
        CHAR_TOKENS.put(',',COMMA); CHAR_TOKENS.put('.',DOT);
        CHAR_TOKENS.put('-',MINUS); CHAR_TOKENS.put('+',PLUS);
        CHAR_TOKENS.put('*',MULTIPLIER); CHAR_TOKENS.put('/',SLASH);
        CHAR_TOKENS.put(':',COLON); CHAR_TOKENS.put(';',SEMICOLON);
        CHAR_TOKENS.put('~',TILDA); CHAR_TOKENS.put('=',EQUALS);
        CHAR_TOKENS.put('>',GREATER); CHAR_TOKENS.put('<',LESS);
        CHAR_TOKENS.put('!',BANG); CHAR_TOKENS.put('"',QUOTE);
        CHAR_TOKENS.put('\0',UNX); CHAR_TOKENS.put('$',DOLLAR);
        CHAR_TOKENS.put('&',AND); CHAR_TOKENS.put('|',OR);
        CHAR_TOKENS.put('%',MOD);

        DOUBLE_CHAR_TOKENS.put("!=",BANG_EQUALS); DOUBLE_CHAR_TOKENS.put("==",EQUALS_EQUALS);
        DOUBLE_CHAR_TOKENS.put(">=",GREATER_EQUALS); DOUBLE_CHAR_TOKENS.put("<=",LESS_EQUALS);
        DOUBLE_CHAR_TOKENS.put("||",OR); DOUBLE_CHAR_TOKENS.put("&&",AND);
        DOUBLE_CHAR_TOKENS.put("->",ARROW); DOUBLE_CHAR_TOKENS.put("~~",CAST);
        DOUBLE_CHAR_TOKENS.put("[]",ARRAY);



        KEYWORDS.put("true",TRUE); KEYWORDS.put("false",FALSE);

        KEYWORDS.put("val",VAL); KEYWORDS.put("fn",FUNCTION);
        KEYWORDS.put("object",CLASS); KEYWORDS.put("static",STATIC);

        KEYWORDS.put("out",PRINT); KEYWORDS.put("return",RETURN);
        KEYWORDS.put("in",INPUT); KEYWORDS.put("exit",EXIT);
        KEYWORDS.put("print",PRINT);
        KEYWORDS.put("input",INPUT);

        //KEYWORDS.put("this",THIS);
        KEYWORDS.put("super",SUPER);
        KEYWORDS.put("nil",NIL);

        KEYWORDS.put("or",OR); KEYWORDS.put("and",AND);

        KEYWORDS.put("if",IF); KEYWORDS.put("else",ELSE);
        KEYWORDS.put("for",FOR); KEYWORDS.put("while",WHILE);

        KEYWORDS.put("str",T_STR);
        KEYWORDS.put("bool",T_BOOL);
        KEYWORDS.put("num",T_DOUBLE); KEYWORDS.put("id",T_IDENTIFIER);


    }

    public Scanner(String source) {
        this.source = source;
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
        addToken(tokens,EOF);
        return tokens;
    }



    private Token scanToken() {
        char c = advance();
        char c2 = peek();
        TokenType cToken = findChar(c);
        TokenType cToken2 = findChar(c2);
        Token token = null;
        if(cToken == QUOTE) token = scanString();
        else if(cToken == IDENTIFIER) token = scanIdentifier();
        else if(cToken == NUMBER) token = scanNumber();
        else if(cToken == SLASH && cToken2 == SLASH) {
            while (peek() != '\n' && !isAtEnd()) advance();
            return null;
        }
        else {
            String cc = String.valueOf(c)+c2;
            if(DOUBLE_CHAR_TOKENS.containsKey(cc)) {
                cToken = DOUBLE_CHAR_TOKENS.get(cc);
                advance();
            }
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
        return new Token(NUMBER,numText,Double.parseDouble(numText), position);
    }

    private Token scanIdentifier() {
        while(isAlphaNumeric(peek())) advance();
        String identifierValue = source.substring(position.start, position.current);
        return new Token(findKeyword(identifierValue),identifierValue,identifierValue, position);
    }

    private Token scanString() {
        while(!isAtEnd() && peek() != '"') {
            if(peek() == '\n') position.newLine();
            if(peek() == '\\' && peekNext() == '"') advance();
            advance();
        }
        if(isAtEnd()) PhyssReporter.error(position.line, position.symbol,
                new LexerException(Translatable.get("scanner.string_unterminated")));
        advance();
        String stringValue = source.substring(position.start+1, position.current-1);
        stringValue = stringValue.replace("\\n","\n");
        stringValue = stringValue.replace("\\s", " ");
        stringValue = stringValue.replace("\\t", "\t");
        stringValue = stringValue.replace("\\\\","\\");
        stringValue = stringValue.replace("\\\"","\"");

        return new Token(STRING,stringValue,stringValue, position);
    }

    private TokenType findChar(char c) {
        TokenType token = findKeyword(String.valueOf(c));
        if(CHAR_TOKENS.containsKey(c)) token = CHAR_TOKENS.get(c);
        if(token == IDENTIFIER && isNumeric(c)) token = NUMBER;
        if(c == '\n') token = UNX;
        if(c == ' ' || c == '\t' || c == '\r') token = UNX;
        if(c == '\\') return BACK_SLASH;
        if(token == IDENTIFIER && !isAlpha(c)) PhyssReporter.error(position.line,position.symbol,
                new LexerException(String.format(Translatable.get("scanner.unx_character"),c)));
        return token;
    }

    private TokenType findKeyword(String c) {
        return KEYWORDS.getOrDefault(c,IDENTIFIER);
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
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= 'а' && c <= 'я') ||
                (c >= 'А' && c <= 'Я') ||
                c == '_';
    }
    public boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isNumeric(c);
    }
}

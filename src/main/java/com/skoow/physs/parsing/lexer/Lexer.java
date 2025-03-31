package com.skoow.physs.parsing.lexer;

import com.skoow.physs.util.Structs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.skoow.physs.parsing.SourcePos.*;
import static com.skoow.physs.parsing.lexer.TokenManager.*;
import static com.skoow.physs.util.Text.alpha;

public class Lexer {
    /** Source string for lexing */
    private final String source;
    /** List of tokens generated */
    private final List<Token> tokens = new ArrayList<>();

    public char temp;

    public Lexer(String source) {
        this.source = source;
        begin();
    }

    /** Scans for tokens */
    public void scan() {
        get().reset();
        tokens.clear();
        while (!end()) {
            get().nor();
            if(peek() == '\n') {advance(); get().nextLine(); continue;}
            if(peek() == ' ' || peek() == '\t' || peek() == '\r') {advance(); continue;}
            Token token = scanToken();
            if(token == null) continue;
            tokens.add(token);
        }
        add(all.get("eof"));
    }
    /**
     * Scans token
     *
     * @return
     */
    public Token scanToken() {
        char c = advance();
        char next = peek();

        temp = c;

        for (Map.Entry<String, Function<Lexer, Boolean>> entry : primitiveConditions.entrySet()) {
            boolean cond = entry.getValue().apply(this);
            if(cond)
                return primitiveParsers.get(entry.getKey()).apply(this);
        }
        TokenInfo dual = doubl.get(c+""+next);
        TokenInfo single = tokenOf(c);

        return Structs.or(
                dual,
                new Token(dual,c+""+next,get().cpy()),
                new Token(single,c+"",get().cpy())
        );
    }
    /** Returns token info from a double character or a string */
    public TokenInfo tokenOf(String str) {
        if(keywords.containsKey(str)) return keywords.get(str);
        if(doubl.containsKey(str)) return doubl.get(str);
        return tokenOf(str.charAt(0));
    }
    /** Returns token info from single character */
    private TokenInfo tokenOf(char ch) {
        TokenInfo token = all.get("ident");
        token = singular.getOrDefault(String.valueOf(ch),token);
        if(token.id().equals("ident") && !alpha(ch))
            return null; //todo error
        return token;
    }

    /** Adds token to list */
    private void add(TokenInfo info) {
        String text = source.substring(get().current(), get().currentEnd());
        tokens.add(new Token(info,text,get().cpy()));
    }

    /** Checks if end of string is reached */
    public boolean end() {
        return get().currentEnd() >= source.length();
    }

    /** Checks if there are chars left */
    public boolean ready() {
        return !end();
    }

    /** Matches char and advances position */
    public boolean match(char ch) {
        if(peek() == ch) {
            advance();
            return true;
        }
        return false;
    }

    /** Returns prev char or nil if at the start */
    public char peekPrev() {
        if(get().currentEnd() > 0) return nil();
        return source.charAt(get().currentEnd()-1);
    }
    /** Returns current char or nil if at the end */
    public char peek() {
        if(end()) return nil();
        return source.charAt(get().currentEnd());
    }
    /** Returns next char or nil if at the end */
    public char peekNext() {
        if(get().currentEnd()+1 >= source.length()) return nil();
        return source.charAt(get().currentEnd()+1);
    }

    /** Advances position and returns next char */
    public char advance() {
        return source.charAt(get().expand());
    }

    /** Nil character */
    public static char nil() {
        return '\0';
    }

    public String substring(int i, int i1) {
        return source.substring(i,i1);
    }

    public List<Token> finish() {
        get().reset();
        temp = 0;
        return tokens;
    }
}

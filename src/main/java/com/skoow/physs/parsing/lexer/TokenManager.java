package com.skoow.physs.parsing.lexer;

import com.skoow.physs.gen.tokens.TokenMeta;
import com.skoow.physs.parsing.SourcePos;
import com.skoow.physs.util.Text;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class TokenManager {
    public static Map<String, Function<Lexer,Boolean>> primitiveConditions = new LinkedHashMap<>();
    public static Map<String, Function<Lexer,Token>> primitiveParsers = new LinkedHashMap<>();

    public static Map<String, TokenInfo> all = new LinkedHashMap<>();
    public static Map<String, TokenInfo> singular = new LinkedHashMap<>();
    public static Map<String, TokenInfo> doubl = new LinkedHashMap<>();
    public static Map<String, TokenInfo> keywords = new LinkedHashMap<>();
    public static Map<String, TokenInfo> primitive = new LinkedHashMap<>();

    static {
        TokenMeta.init();

        Map<String, Function<Lexer,Boolean>> prims = primitiveConditions;
        Map<String, Function<Lexer, Token>> pars = primitiveParsers;
        prims.put("string",lexer -> lexer.temp == '"');
        prims.put("ident",lexer -> Text.alpha(lexer.temp));
        prims.put("number",lexer -> (lexer.temp == '.' && Text.num(lexer.peek())) || Text.num(lexer.temp));

        pars.put("string",lexer -> {
            while(!lexer.end() && lexer.peek() != '"') {
                if(lexer.peek() == '\n') SourcePos.get().nextLine();
                if(lexer.peek() == '\\' && lexer.peekNext() == '"') lexer.advance();
                lexer.advance();
            }
            if(lexer.end()) return null; //todo error
            lexer.advance();
            String stringValue = lexer.substring(SourcePos.get().current()+1, SourcePos.get().currentEnd()-1);
            stringValue = stringValue.replace("\\n","\n");
            stringValue = stringValue.replace("\\s", " ");
            stringValue = stringValue.replace("\\t", "\t");
            stringValue = stringValue.replace("\\\\","\\");
            stringValue = stringValue.replace("\\\"","\"");

            return new Token(primitive.get("string"),stringValue,SourcePos.get().cpy());
        });
        pars.put("ident",lexer -> {
            while (Text.alphaNum(lexer.peek())) lexer.advance();
            String ident = lexer.substring(SourcePos.get().current(), SourcePos.get().currentEnd());
            return new Token(lexer.tokenOf(ident),ident,SourcePos.get().cpy());
        });
        pars.put("number",lexer -> {
            while (Text.num(lexer.peek())) lexer.advance();
            if (lexer.peek() == '.' && Text.num(lexer.peekNext())) {
                do lexer.advance();
                while (Text.num(lexer.peek()));
            }
            String numText = lexer.substring(SourcePos.get().current(),SourcePos.get().currentEnd());
            return new Token(primitive.get("number"),numText,SourcePos.get().cpy());
        });
    }
}

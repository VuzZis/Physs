package com.skoow.physs.parsing.lexer;

import com.skoow.physs.parsing.SourcePos;

public class Token {
    public SourcePos source;
    public short tokenType;
    public String lexeme;

    public Token(short tokenId, String tokenBody, SourcePos source) {
        this.tokenType = tokenId;
        this.lexeme = tokenBody;
        this.source = source;
    }
    public Token(int tokenId, String tokenBody, SourcePos source) {
        this((short) tokenId,tokenBody,source);
    }
    public Token(TokenInfo info, String tokenBody, SourcePos source) {
        this(info == null ? 0 : info.idx(),tokenBody,source);
    }

    @Override
    public String toString() {
        return TokenManager.all.values().toArray()[tokenType] + "{" + lexeme + "}";
    }
}

package com.skoow.physs.parsing.lexer;

public record TokenInfo(short idx,String id) {
    public TokenInfo modify(short idx) {
        return new TokenInfo(idx, id);
    }
    public TokenInfo modify(String id) {
        return new TokenInfo(idx,id);
    }

    @Override
    public String toString() {
        return idx+":"+id;
    }
}

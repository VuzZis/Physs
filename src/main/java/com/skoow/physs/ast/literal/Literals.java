package com.skoow.physs.ast.literal;

import com.skoow.physs.error.errors.RunException;
import com.skoow.physs.lexer.TokenType;

import java.util.function.Function;

public enum Literals {
    STR(TokenType.T_STR,(o) -> o.toString()),
    DOUBLE(TokenType.T_DOUBLE,(o) -> {
        if(o instanceof String s) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                throw new RunException("Unexpected number format: \""+s+"\"");
            }
        }
        if(o instanceof Double s) return s;
        if(o instanceof Boolean s) return s ? 1 : 0;
        if(o instanceof Float s) return Math.floor(s);
        return null;
    }),
    IDENTFIIER(TokenType.T_IDENTIFIER,(o) -> "$"+o),
    BOOL(TokenType.T_BOOL,(o) -> o instanceof Boolean ? o : o != null);
    TokenType castToken;
    private Function<Object,Object> cast;
    Literals(TokenType type, Function<Object,Object> f) {
        this.castToken = type;
        this.cast = f;
    }

    public Object cast(Object thi) throws ClassCastException {
        return cast.apply(thi);
    }

    public static Literals get(TokenType type) {
        for (Literals value : values()) {
            if(value.castToken == type) return value;
        }
        return null;
    }
}

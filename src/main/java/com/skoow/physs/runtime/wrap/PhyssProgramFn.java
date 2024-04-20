package com.skoow.physs.runtime.wrap;

import com.skoow.physs.ast.statement.FunctionStmt;
import com.skoow.physs.ast.statement.Stmt;
import com.skoow.physs.engine.Context;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.runtime.Interpreter;
import com.skoow.physs.runtime.Scope;

import java.util.List;

public class PhyssProgramFn implements PhyssFn {

    private final List<Token> params;
    private final List<Stmt> body;
    private int argCount = 0;

    public PhyssProgramFn(FunctionStmt stmt) {
        this.argCount = stmt.params.size();
        this.params = stmt.params;
        this.body = stmt.body;
    }

    @Override
    public int argCount() {
        return argCount;
    }

    @Override
    public Object methodOrConstructor(Interpreter interpreter, List<Object> args) {
        Scope scope = new Scope(interpreter.scope);
        int i = 0;
        for (Token param : params) {
            String paramName = param.lexeme;
            scope.defineVariable(paramName,args.get(i));
            i++;
        }
        interpreter.blockStmt(body,scope);
        return null;
    }

    @Override
    public String toString() {
        return String.format("fn%s",params.stream().map((k) -> k.lexeme).toList().toString());
    }
}

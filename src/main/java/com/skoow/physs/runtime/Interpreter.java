package com.skoow.physs.runtime;

import com.skoow.physs.ast.expression.*;
import com.skoow.physs.ast.literal.BasicLiteral;
import com.skoow.physs.ast.statement.*;
import com.skoow.physs.engine.Context;
import com.skoow.physs.error.PhyssReporter;
import com.skoow.physs.error.errors.RunException;
import com.skoow.physs.lexer.Token;
import com.skoow.physs.lexer.TokenType;
import com.skoow.physs.lexer.scanner.Position;
import com.skoow.physs.runtime.exc.Return;
import com.skoow.physs.runtime.exc.UndefinedValue;
import com.skoow.physs.runtime.wrap.PhyssFn;
import com.skoow.physs.runtime.wrap.PhyssProgramFn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Interpreter {
    private final Program program;
    public final Scope scope;

    public Interpreter(Program program,Scope scope) {
        this.program = program;
        this.scope = scope;
    }

    public void interpreteProgram() {
        for (Stmt line : program.lines) {
            try {
                evaluate(line, scope);
            } catch(RunException e) {
                PhyssReporter.reportError(line.line(),line.symbol()," at runtime",e.getMessage());
                break;
            } catch (Return r) {
                PhyssReporter.reportError(line.line(),line.symbol()," at runtime",
                        "Unexpected return statement outside of a function");
                break;
            }
        }
    }

    private Object evaluate(Stmt line,Scope scope) {
        if(line == null) return null;
        String nodeKind = line.getClass().getSimpleName();
        return switch (nodeKind) {
            case "BlockStmt" -> blockStmt(((BlockStmt) line).stmts,scope);

            case "IfStmt" -> ifStmt((IfStmt) line,scope);
            case "WhileStmt" -> whileStmt((WhileStmt) line, scope);
            case "ReturnStmt" -> returnStmt((ReturnStmt) line,scope);

            case "BasicLiteral" -> ((BasicLiteral) line).value;
            case "IdentifierLiteral" -> line.toString();

            case "VarDeclarStmt" -> varDeclaration((VarDeclarStmt) line,scope);
            case "FunctionStmt" -> fnDeclaration((FunctionStmt) line,scope);
            case "PrintStmt" -> print((PrintStmt) line, scope);
            case "ExprStmt" -> evaluate(((ExprStmt) line).expr,scope);

            case "LambdaExpr" -> lambdaExpr((LambdaExpr) line, scope);
            case "CallExpr" -> callExpr((CallExpr) line,scope);
            case "CastExpr" -> castExpr((CastExpr) line, scope);
            case "InputExpr" -> inputExpr((InputExpr) line, scope);
            case "AssignmentExpr" -> assignExpr((AssignmentExpr) line,scope);
            case "VarGetExpr" -> varExpr((VarGetExpr) line,scope);
            case "BinaryExpr" -> binaryExpr((BinaryExpr) line,scope);
            case "UnaryExpr" -> unaryExpr((UnaryExpr) line,scope);
            case "GroupExpr" -> evaluate(((GroupExpr) line).expr,scope);

            default -> throw error(line,"Unexpected node found when evaluating: "+nodeKind);
        };
    }

    private Object castExpr(CastExpr line, Scope scope) {
        Object val = evaluate(line.expr,scope);
        try {
            return line.operator.cast(val);
        } catch (ClassCastException e) {
            throw error(line,String.format("Cannot cast %s to %s",val.toString(),line.operator.name()));
        }
    }

    private Object lambdaExpr(LambdaExpr line, Scope scope) {
        PhyssProgramFn fn = new PhyssProgramFn(
                new FunctionStmt(
                        new Token(TokenType.IDENTIFIER,"lambda","lambda",0,0),
                        line.params,
                        Arrays.asList(line.body),
                        new Position(line.line(),line.symbol())));
        return fn;
    }

    private Object returnStmt(ReturnStmt line, Scope scope) {
        Object value = null;
        if(line.expr != null) value = evaluate(line.expr,scope);
        throw new Return(value);
    }

    private Object fnDeclaration(FunctionStmt line, Scope scope) {
        String varName = line.varName.lexeme;
        PhyssProgramFn fn = new PhyssProgramFn(line);
        Scope scopeNew = this.scope;
        if(scope != scopeNew) throw error(line,"Cannot define function in inner scopes");
        scopeNew.defineVariable(varName,fn);
        return null;
    }

    private Object callExpr(CallExpr line, Scope scope) {
        Object callee = evaluate(line.callee,scope);
        if(!(callee instanceof PhyssFn)) throw error(line,"Not an callable object");
        List<Object> args = new ArrayList<>();
        for (Expr arg : line.args) args.add(evaluate(arg,scope));
        PhyssFn fn = (PhyssFn) callee;
        if(args.size() != fn.argCount())
            throw error(line,String.format("Expected %s args, found %s",fn.argCount(),args.size()));
        try {
            return fn.methodOrConstructor(this, args);
        } catch (StackOverflowError error) {
            throw error(line,String.format("Call overflow"));
        } catch (Return ret) {
            return ret.value;
        }
    }

    private Object whileStmt(WhileStmt line, Scope scope) {
        Expr condition = line.condition;
        int evaluateCount = 0;
        double maxCount = (double) scope.getVariable("$$WHILE_MAX_ITERATIONS");
        while (true) {
            if(evaluateCount >= maxCount) throw error(line,"Infinite loop");
            Object value = evaluate(condition,scope);
            if(value instanceof Boolean b) {if (!b) break;}
            else break;
            evaluate(line.thenBranch,scope);
            evaluateCount++;
        }
        return null;
    }

    private Object inputExpr(InputExpr line, Scope scope) {
        Object toPrint = evaluate(line.expr,scope);
        if(toPrint == null) toPrint = "";
        PhyssReporter.reportInput(line.line(),line.symbol(),toPrint.toString());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw error(line,"Failed to read input: "+e.getMessage());
        }
    }

    private Object ifStmt(IfStmt line, Scope scope) {
        Object condition = evaluate(line.condition,scope);
        if(!(condition instanceof Boolean)) throw error(line.condition,"The result of condition is "+condition.getClass().getSimpleName()+" expected Boolean");
        boolean condBoolean = (Boolean) condition;
        if(condBoolean) evaluate(line.thenBranch,scope);
        else if(line.elseBranch != null) evaluate(line.elseBranch,scope);
        return null;
    }

    public Object blockStmt(List<Stmt> stmts, Scope scope) {
        Scope thisScope = new Scope(scope);
        for (Stmt stmt : stmts) {
            try {
                evaluate(stmt,thisScope);
            } catch(RunException e) {
                throw e;
            }
        }
        return null;
    }

    private Object assignExpr(AssignmentExpr line, Scope scope) {
        String varName = line.var.lexeme;
        try {
            Object value = evaluate(line.value, scope);
            scope.updateVariable(varName, value);
            return value;
        } catch (RunException e) {
            throw e;
        }
    }

    private Object varExpr(VarGetExpr line, Scope scope) {
        String varName = line.var.lexeme;
        try {
            Object obj = scope.getVariable(varName);
            if(obj instanceof UndefinedValue) throw error(line,"Variable has not been initialized: "+varName);
            return obj;
        } catch (RunException e) {
            throw e;
        }
    }

    private Object unaryExpr(UnaryExpr line, Scope scope) {
        Object obj = evaluate(line.expr,scope);
        TokenType operator = line.operator.tokenType;
        switch (operator) {
            case MINUS:
                if(obj instanceof Double oD) return -oD;
                else throw error(line,String.format("Cannot apply operator '-' for type %s",
                        getName(obj)));

            case BANG:
                if(obj instanceof Boolean oB) return !oB;
                else throw error(line,String.format("Cannot apply operator '!' for type %s",
                        getName(obj)));

            default:
                throw error(line,String.format("Unexpected operator found for expression: %s%s",
                        line.operator.lexeme,getName(obj)));
        }
    }

    private Object binaryExpr(BinaryExpr line, Scope scope) {
        Object left = evaluate(line.left,scope);
        Object right = evaluate(line.right,scope);
        TokenType operator = line.operator.tokenType;
        switch (operator) {
            case MINUS:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD-rD;
                else throw error(line,String.format("Cannot apply operator '-' for type %s, %s",
                        getName(left),getName(right)));
            case PLUS:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD+rD;
                else if(left instanceof String lS) return lS+right;
                else if(right instanceof String rS) return left.toString()+rS;
                else throw error(line,String.format("Cannot apply operator '+' for type %s, %s",
                        getName(left),getName(right)));
            case MULTIPLIER:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD*rD;
                else if(left instanceof String lS && right instanceof Double rD) return lS.repeat((int) rD.doubleValue());
                else throw error(line,String.format("Cannot apply operator '*' for type %s, %s",
                            getName(left),getName(right)));
            case SLASH:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD/rD;
                else throw error(line,String.format("Cannot apply operator '/' for type %s, %s",
                        getName(left),getName(right)));

            case MOD:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD%rD;
                else throw error(line,String.format("Cannot apply operator '%%' for type %s, %s",
                        getName(left),getName(right)));

            case EQUALS_EQUALS:
                return left.equals(right);
            case BANG_EQUALS:
                return !left.equals(right);
            case GREATER:
                if(left instanceof Double lD && right instanceof Double rD) return lD>rD;
                else throw error(line,String.format("Cannot apply operator '>' for type %s, %s",
                        getName(left),getName(right)));
            case LESS:
                if(left instanceof Double lD && right instanceof Double rD) return lD<rD;
                else throw error(line,String.format("Cannot apply operator '<' for type %s, %s",
                        getName(left),getName(right)));
            case GREATER_EQUALS:
                if(left instanceof Double lD && right instanceof Double rD) return lD>=rD;
                else throw error(line,String.format("Cannot apply operator '>=' for type %s, %s",
                        getName(left),getName(right)));
            case LESS_EQUALS:
                if(left instanceof Double lD && right instanceof Double rD) return lD<=rD;
                else throw error(line,String.format("Cannot apply operator '<=' for type %s, %s",
                        getName(left),getName(right)));

            case AND:
                if(left instanceof Boolean lD && right instanceof Boolean rD) return lD && rD;
                else throw error(line,String.format("Cannot apply operator 'and &&' for type %s, %s",
                        getName(left),getName(right)));

            case OR:
                if(left instanceof Boolean lD && right instanceof Boolean rD) return lD || rD;
                else return left != null ? left : right;

            default:
                throw error(line,String.format("Unexpected operator found for expression: %s %s %s",
                        getName(left),line.operator.lexeme,getName(right)));
        }
    }
    
    private static String getName(Object obj) {
        if(obj == null) return "nil";
        return obj.getClass().getSimpleName();
    }

    private Object print(PrintStmt line, Scope scope) {
        Object toPrint = evaluate(line.expr,scope);
        if(toPrint == null) {
            PhyssReporter.reportWarn(line.line(),line.symbol()," at runtime","Null print.");
            PhyssReporter.reportInfo(line.line(),line.symbol(),"nil");
        }
        else PhyssReporter.reportInfo(line.line(),line.symbol(),toPrint.toString());
        return null;
    }

    private Object varDeclaration(VarDeclarStmt line,Scope scope) {
        String varName = line.varName.lexeme;
        Object value;
        if(line.initializer == null) value = new UndefinedValue();
        else value = evaluate(line.initializer,scope);
        scope.defineVariable(varName,value);
        return value;
    }

    private RunException error(Stmt stmt, String message) {
        return new RunException(message);
    }
}

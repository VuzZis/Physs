package com.skoow.physs.runtime;

import com.skoow.physs.ast.expression.*;
import com.skoow.physs.ast.literal.BasicLiteral;
import com.skoow.physs.ast.statement.*;
import com.skoow.physs.error.PhyssReporter;
import com.skoow.physs.error.errors.RunException;
import com.skoow.physs.lexer.TokenType;
import com.skoow.physs.runtime.exc.UndefinedValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Interpreter {
    private final Program program;
    private final Scope scope;

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
            }
        }
    }

    private Object evaluate(Stmt line,Scope scope) {
        if(line == null) return null;
        String nodeKind = line.getClass().getSimpleName();
        return switch (nodeKind) {
            case "BlockStmt" -> blockStmt((BlockStmt) line,scope);

            case "IfStmt" -> ifStmt((IfStmt) line,scope);
            case "WhileStmt" -> whileStmt((WhileStmt) line, scope);
            
            case "BasicLiteral" -> ((BasicLiteral) line).value;
            case "IdentifierLiteral" -> line.toString();

            case "VarDeclarStmt" -> varDeclaration((VarDeclarStmt) line,scope);
            case "PrintStmt" -> print((PrintStmt) line, scope);
            case "ExprStmt" -> evaluate(((ExprStmt) line).expr,scope);

            case "InputExpr" -> inputExpr((InputExpr) line, scope);
            case "AssignmentExpr" -> assignExpr((AssignmentExpr) line,scope);
            case "VarGetExpr" -> varExpr((VarGetExpr) line,scope);
            case "BinaryExpr" -> binaryExpr((BinaryExpr) line,scope);
            case "UnaryExpr" -> unaryExpr((UnaryExpr) line,scope);
            case "GroupExpr" -> evaluate(((GroupExpr) line).expr,scope);

            default -> throw error(line,"Unexpected node found when evaluating: "+nodeKind);
        };
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

    private Object blockStmt(BlockStmt line, Scope scope) {
        Scope thisScope = new Scope(scope);
        for (Stmt stmt : line.stmts) {
            try {
                evaluate(stmt,thisScope);
            } catch(RunException e) {
                PhyssReporter.reportError(stmt.line(),stmt.symbol()," at runtime",e.getMessage());
                break;
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
                        obj.getClass().getSimpleName()));

            case BANG:
                if(obj instanceof Boolean oB) return !oB;
                else throw error(line,String.format("Cannot apply operator '!' for type %s",
                        obj.getClass().getSimpleName()));

            default:
                throw error(line,String.format("Unexpected operator found for expression: %s%s",
                        line.operator.lexeme,obj.getClass().getSimpleName()));
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
                        left.getClass().getSimpleName(),right.getClass().getSimpleName()));
            case PLUS:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD+rD;
                else if(left instanceof String lS) return lS+right;
                else if(right instanceof String rS) return left.toString()+rS;
                else throw error(line,String.format("Cannot apply operator '+' for type %s, %s",
                        left.getClass().getSimpleName(),right.getClass().getSimpleName()));
            case MULTIPLIER:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD*rD;
                else if(left instanceof String lS && right instanceof Double rD) return lS.repeat((int) rD.doubleValue());
                else throw error(line,String.format("Cannot apply operator '*' for type %s, %s",
                            left.getClass().getSimpleName(),right.getClass().getSimpleName()));
            case SLASH:
                if(left instanceof Boolean bl) left = bl ? 1d : 0d;
                if(right instanceof Boolean br) right = br ? 1d : 0d;

                if(left instanceof Double lD && right instanceof Double rD) return lD/rD;
                else throw error(line,String.format("Cannot apply operator '-' for type %s, %s",
                        left.getClass().getSimpleName(),right.getClass().getSimpleName()));

            case EQUALS_EQUALS:
                return left.equals(right);
            case BANG_EQUALS:
                return !left.equals(right);
            case GREATER:
                if(left instanceof Double lD && right instanceof Double rD) return lD>rD;
                else throw error(line,String.format("Cannot apply operator '>' for type %s, %s",
                        left.getClass().getSimpleName(),right.getClass().getSimpleName()));
            case LESS:
                if(left instanceof Double lD && right instanceof Double rD) return lD<rD;
                else throw error(line,String.format("Cannot apply operator '<' for type %s, %s",
                        left.getClass().getSimpleName(),right.getClass().getSimpleName()));
            case GREATER_EQUALS:
                if(left instanceof Double lD && right instanceof Double rD) return lD>=rD;
                else throw error(line,String.format("Cannot apply operator '>=' for type %s, %s",
                        left.getClass().getSimpleName(),right.getClass().getSimpleName()));
            case LESS_EQUALS:
                if(left instanceof Double lD && right instanceof Double rD) return lD<=rD;
                else throw error(line,String.format("Cannot apply operator '<=' for type %s, %s",
                        left.getClass().getSimpleName(),right.getClass().getSimpleName()));

            case AND:
                if(left instanceof Boolean lD && right instanceof Boolean rD) return lD && rD;
                else throw error(line,String.format("Cannot apply operator 'and &&' for type %s, %s",
                        left.getClass().getSimpleName(),right.getClass().getSimpleName()));

            case OR:
                if(left instanceof Boolean lD && right instanceof Boolean rD) return lD || rD;
                else return left != null ? left : right;

            default:
                throw error(line,String.format("Unexpected operator found for expression: %s %s %s",
                        left.getClass().getSimpleName(),line.operator.lexeme,right.getClass().getSimpleName()));
        }
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

package com.skoow.physs.runtime;

import com.skoow.physs.ast.expression.*;
import com.skoow.physs.ast.literal.BasicLiteral;
import com.skoow.physs.ast.statement.*;
import com.skoow.physs.error.PhyssReporter;
import com.skoow.physs.error.errors.RunException;
import com.skoow.physs.lexer.TokenType;

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
            case "BasicLiteral" -> ((BasicLiteral) line).value;
            case "IdentifierLiteral" -> line.toString();

            case "VarDeclarStmt" -> varDeclaration((VarDeclarStmt) line,scope);
            case "PrintStmt" -> print((PrintStmt) line, scope);
            case "ExprStmt" -> evaluate(((ExprStmt) line).expr,scope);

            case "AssignmentExpr" -> assignExpr((AssignmentExpr) line,scope);
            case "VarGetExpr" -> varExpr((VarGetExpr) line,scope);
            case "BinaryExpr" -> binaryExpr((BinaryExpr) line,scope);
            case "UnaryExpr" -> unaryExpr((UnaryExpr) line,scope);
            case "GroupExpr" -> evaluate(((GroupExpr) line).expr,scope);

            default -> throw error(line,"Unexpected node found when evaluating: "+nodeKind);
        };
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
            return scope.getVariable(varName);
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
        if(toPrint == null) PhyssReporter.reportWarn(line.line(),line.symbol()," at runtime","Null print.");
        else PhyssReporter.reportInfo(line.line(),line.symbol(),toPrint.toString());
        return null;
    }

    private Object varDeclaration(VarDeclarStmt line,Scope scope) {
        String varName = line.varName.lexeme;
        Object value = evaluate(line.initializer,scope);
        scope.defineVariable(varName,value);
        return value;
    }

    private RunException error(Stmt stmt, String message) {
        PhyssReporter.error(stmt,message);
        return new RunException(message);
    }
}

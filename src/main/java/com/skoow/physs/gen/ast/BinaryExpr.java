package com.skoow.physs.gen.ast;

import com.skoow.physs.parsing.SourcePos;
import com.skoow.physs.parsing.lexer.Token;
import javax.annotation.processing.Generated;

@Generated("com.skoow.physs.gen.MetaGenerator")
public class BinaryExpr extends Expr {
  public Expr left;

  public Expr right;

  public Token operator;

  public BinaryExpr(SourcePos pos, Expr left, Expr right, Token operator) {
    super(pos);
    this.name = "BinaryExpr";
    this.left = left;
    this.right = right;
    this.operator = operator;
  }
}

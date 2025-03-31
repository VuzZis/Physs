package com.skoow.physs.gen.ast;

import com.skoow.physs.parsing.SourcePos;
import java.lang.String;
import javax.annotation.processing.Generated;

@Generated("com.skoow.physs.gen.MetaGenerator")
public class Stmt {
  public SourcePos pos;

  public String name;

  public Stmt(SourcePos pos) {
    this.name = "Stmt";
    this.pos = pos;
  }
}

package com.skoow.physs.gen.tokens;

import com.skoow.physs.parsing.lexer.TokenInfo;
import com.skoow.physs.parsing.lexer.TokenManager;
import java.lang.String;
import java.util.LinkedHashMap;
import javax.annotation.processing.Generated;

@Generated("com.skoow.physs.gen.MetaGenerator")
public class TokenMeta {
  public static final LinkedHashMap<String, TokenInfo> all = new LinkedHashMap<>();

  public static final LinkedHashMap<String, TokenInfo> singular = new LinkedHashMap<>();

  public static final LinkedHashMap<String, TokenInfo> doubl = new LinkedHashMap<>();

  public static final LinkedHashMap<String, TokenInfo> keywords = new LinkedHashMap<>();

  public static final LinkedHashMap<String, TokenInfo> primitive = new LinkedHashMap<>();

  static {
    primitive.put("number",new TokenInfo((short) 0,"number"));
    all.put("number",new TokenInfo((short) 0,"number"));
    primitive.put("string",new TokenInfo((short) 1,"string"));
    all.put("string",new TokenInfo((short) 1,"string"));
    primitive.put("eof",new TokenInfo((short) 2,"eof"));
    all.put("eof",new TokenInfo((short) 2,"eof"));
    primitive.put("ident",new TokenInfo((short) 3,"ident"));
    all.put("ident",new TokenInfo((short) 3,"ident"));
    doubl.put("<=",new TokenInfo((short) 4,"logic_less_or_equal"));
    all.put("<=",new TokenInfo((short) 4,"logic_less_or_equal"));
    doubl.put("->",new TokenInfo((short) 5,"arrow"));
    all.put("->",new TokenInfo((short) 5,"arrow"));
    doubl.put(">=",new TokenInfo((short) 6,"logic_greater_or_equal"));
    all.put(">=",new TokenInfo((short) 6,"logic_greater_or_equal"));
    doubl.put("==",new TokenInfo((short) 7,"logic_equals"));
    all.put("==",new TokenInfo((short) 7,"logic_equals"));
    doubl.put("&&",new TokenInfo((short) 8,"logic_and"));
    all.put("&&",new TokenInfo((short) 8,"logic_and"));
    doubl.put("!=",new TokenInfo((short) 9,"logic_not_equals"));
    all.put("!=",new TokenInfo((short) 9,"logic_not_equals"));
    doubl.put("||",new TokenInfo((short) 10,"logic_or"));
    all.put("||",new TokenInfo((short) 10,"logic_or"));
    singular.put("-",new TokenInfo((short) 11,"binary_minus"));
    all.put("-",new TokenInfo((short) 11,"binary_minus"));
    singular.put("[",new TokenInfo((short) 12,"group_bracket_left"));
    all.put("[",new TokenInfo((short) 12,"group_bracket_left"));
    singular.put(")",new TokenInfo((short) 13,"group_paren_right"));
    all.put(")",new TokenInfo((short) 13,"group_paren_right"));
    singular.put("(",new TokenInfo((short) 14,"group_paren_left"));
    all.put("(",new TokenInfo((short) 14,"group_paren_left"));
    singular.put("}",new TokenInfo((short) 15,"group_brace_right"));
    all.put("}",new TokenInfo((short) 15,"group_brace_right"));
    singular.put("]",new TokenInfo((short) 16,"group_bracket_right"));
    all.put("]",new TokenInfo((short) 16,"group_bracket_right"));
    singular.put("+",new TokenInfo((short) 17,"binary_plus"));
    all.put("+",new TokenInfo((short) 17,"binary_plus"));
    singular.put("$",new TokenInfo((short) 18,"other_dollar"));
    all.put("$",new TokenInfo((short) 18,"other_dollar"));
    singular.put("%",new TokenInfo((short) 19,"binary_mod"));
    all.put("%",new TokenInfo((short) 19,"binary_mod"));
    singular.put(":",new TokenInfo((short) 20,"other_colon"));
    all.put(":",new TokenInfo((short) 20,"other_colon"));
    singular.put("~",new TokenInfo((short) 21,"other_tilda"));
    all.put("~",new TokenInfo((short) 21,"other_tilda"));
    singular.put("/",new TokenInfo((short) 22,"other_slash"));
    all.put("/",new TokenInfo((short) 22,"other_slash"));
    singular.put("\\",new TokenInfo((short) 23,"other_backslash"));
    all.put("\\",new TokenInfo((short) 23,"other_backslash"));
    singular.put(">",new TokenInfo((short) 24,"logic_greater_than"));
    all.put(">",new TokenInfo((short) 24,"logic_greater_than"));
    singular.put("{",new TokenInfo((short) 25,"group_brace_left"));
    all.put("{",new TokenInfo((short) 25,"group_brace_left"));
    singular.put(".",new TokenInfo((short) 26,"other_dot"));
    all.put(".",new TokenInfo((short) 26,"other_dot"));
    singular.put("<",new TokenInfo((short) 27,"logic_less_than"));
    all.put("<",new TokenInfo((short) 27,"logic_less_than"));
    singular.put(";",new TokenInfo((short) 28,"other_semicolon"));
    all.put(";",new TokenInfo((short) 28,"other_semicolon"));
    singular.put("!",new TokenInfo((short) 29,"other_bang"));
    all.put("!",new TokenInfo((short) 29,"other_bang"));
    singular.put("*",new TokenInfo((short) 30,"other_asterisk"));
    all.put("*",new TokenInfo((short) 30,"other_asterisk"));
    singular.put("=",new TokenInfo((short) 31,"assign"));
    all.put("=",new TokenInfo((short) 31,"assign"));
    keywords.put("final",new TokenInfo((short) 32,"modifier_final"));
    all.put("final",new TokenInfo((short) 32,"modifier_final"));
    keywords.put("private",new TokenInfo((short) 33,"modifier_private"));
    all.put("private",new TokenInfo((short) 33,"modifier_private"));
    keywords.put("false",new TokenInfo((short) 34,"logic_false"));
    all.put("false",new TokenInfo((short) 34,"logic_false"));
    keywords.put("var",new TokenInfo((short) 35,"var"));
    all.put("var",new TokenInfo((short) 35,"var"));
    keywords.put("override",new TokenInfo((short) 36,"modifier_override"));
    all.put("override",new TokenInfo((short) 36,"modifier_override"));
    keywords.put("while",new TokenInfo((short) 37,"loop_while"));
    all.put("while",new TokenInfo((short) 37,"loop_while"));
    keywords.put("else",new TokenInfo((short) 38,"word_else"));
    all.put("else",new TokenInfo((short) 38,"word_else"));
    keywords.put("package",new TokenInfo((short) 39,"class_package"));
    all.put("package",new TokenInfo((short) 39,"class_package"));
    keywords.put("extends",new TokenInfo((short) 40,"class_extends"));
    all.put("extends",new TokenInfo((short) 40,"class_extends"));
    keywords.put("return",new TokenInfo((short) 41,"word_return"));
    all.put("return",new TokenInfo((short) 41,"word_return"));
    keywords.put("static",new TokenInfo((short) 42,"modifier_static"));
    all.put("static",new TokenInfo((short) 42,"modifier_static"));
    keywords.put("null",new TokenInfo((short) 43,"null"));
    all.put("null",new TokenInfo((short) 43,"null"));
    keywords.put("import",new TokenInfo((short) 44,"class_import"));
    all.put("import",new TokenInfo((short) 44,"class_import"));
    keywords.put("exit",new TokenInfo((short) 45,"word_exit"));
    all.put("exit",new TokenInfo((short) 45,"word_exit"));
    keywords.put("if",new TokenInfo((short) 46,"word_if"));
    all.put("if",new TokenInfo((short) 46,"word_if"));
    keywords.put("map",new TokenInfo((short) 47,"var_map"));
    all.put("map",new TokenInfo((short) 47,"var_map"));
    keywords.put("for",new TokenInfo((short) 48,"loop_for"));
    all.put("for",new TokenInfo((short) 48,"loop_for"));
    keywords.put("true",new TokenInfo((short) 49,"logic_true"));
    all.put("true",new TokenInfo((short) 49,"logic_true"));
    TokenManager.all = all;
    TokenManager.singular = singular;
    TokenManager.doubl = doubl;
    TokenManager.keywords = keywords;
    TokenManager.primitive = primitive;
  }

  public static void init() {
  }
}

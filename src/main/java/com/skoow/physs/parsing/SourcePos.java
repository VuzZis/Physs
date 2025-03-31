package com.skoow.physs.parsing;

public class SourcePos {

    static SourcePos current;

    /** Creates new static sourcepos */
    public static void begin() {
        current = new SourcePos();
    }
    /** Returns static sourcepos */
    public static SourcePos get() {
        if(current == null) begin();
        return current;
    }

    int line;
    int start,end;
    int visStart,visEnd;

    public SourcePos() {
        reset();
    }
    /** Copies this source pos */
    public SourcePos cpy() {
        SourcePos pos = new SourcePos();
        pos.set(line,start,end);
        return pos;
    }
    public void reset() {
        set(1,0,0);
    }
    /** Sets sourcepos' line, starting and ending symbol index */
    public void set(int line, int start, int end) {
        this.line = line;
        this.start = start;
        this.end = end;
    }
    /** Returns current line */
    public int line() {
        return line;
    }
    /** Returns current symbol */
    public int current() {
        return start;
    }
    /** Returns current symbol ending */
    public int currentEnd() {
        return end;
    }
    /** Advances position and returns the next index */
    public int next() {
        expand();
        ++visStart;
        return ++start;
    }
    /** Expands selected index length and returns the last index */
    public int expand() {
        ++visEnd;
        return end++;
    }
    /** Regresses position and returns the previous index */
    public int prev() {
        shrink();
        --visStart;
        return --start;
    }
    /** Shrinks selected index length and returns the last index */
    public int shrink() {
        --visEnd;
        return end--;
    }
    /** Sets starting index to end index */
    public int nor() {
        visStart = visEnd;
        return start = end;
    }
    /** Sets end index to start index */
    public int norShrink() {
        visEnd = visStart;
        return end = start;
    }
    /** Advances line position and returns next line index */
    public int nextLine() {
        line++;
        visStart = 0; visEnd = 0;
        return line;
    }
    /** Regresses line position and returns previous line index */
    public int prevLine() {
        return --line;
    }
}

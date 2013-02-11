package antelope;
import java.util.HashSet;
import java.util.LinkedList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.File;

public final class Preprocessor implements TokenSource {
    public final String startingPath;
    public final LinkedList<String> directories;
    public final LinkedList<Token> allocations; // Will eventually replace these lists
    public final LinkedList<Token> assemblies;  // with proper syntax-tree constructs.
    public final HashSet<Token> defined;
    public final HashSet<File> included;
    public final ErrorHandler handler;
    public final TokenSource source;
    private Preprocessor include;
    private boolean first;
    private int ifDepth;

    public Preprocessor(TokenSource source, LinkedList<String> directories, ErrorHandler handler) {
        this.source = source; this.directories = directories; this.handler = handler;
        include = null; first = true; startingPath = null; ifDepth = 0;
        allocations = new LinkedList<Token>();
        assemblies = new LinkedList<Token>();
        included = new HashSet<File>();
        defined = new HashSet<Token>();
    }

    public Preprocessor(String file, LinkedList<String> directories, ErrorHandler handler) throws IOException {
        this.directories = directories;
        File f = getFile(file);
        if(f == null) {
            throw new FileNotFoundException("File not found: "+file);
        }

        startingPath = f.getAbsoluteFile().getParent();
        source = new TokenSource.Default(f.getPath(), handler);
        allocations = new LinkedList<Token>();
        assemblies = new LinkedList<Token>();
        included = new HashSet<File>();
        defined = new HashSet<Token>();
        this.handler = handler;
        included.add(f);
        include = null;
        first = true;
        ifDepth = 0;
    }

    private Preprocessor(File file, Preprocessor parent) throws IOException {
        handler = parent.handler;
        directories = parent.directories;
        startingPath = file.getAbsoluteFile().getParent();
        source = new TokenSource.Default(file.getPath(), handler);
        allocations = parent.allocations;
        assemblies = parent.assemblies;
        included = parent.included;
        defined = parent.defined;
        included.add(file);
        include = null;
        first = true;
        ifDepth = 0;
    }

    public int getLine() { return (include != null)? include.getLine() : source.getLine(); }
    public String getName() { return (include != null)? include.getName() : source.getName(); }

    public File getFile(String file) {
        File f = new File(startingPath, file);
        if(f.exists()) return f;
        for(String dir : directories) {
            f = new File(dir, file);
            if(f.exists()) return f;
        }
        return null;
    }

    private void error(String message, int line) {
        handler.handle(getName(), line, message);
    }

    private Token clearLine(String errorMessage, int line) {
        handler.handle(getName(), line, errorMessage);
        line = getLine(); Token t;
        do { t = source.nextToken(); }
        while(line == getLine() && !t.isNewLine() && !t.isEOF());
        return t;
    }

    public Token nextToken() {
        if(include != null) {
            Token t = include.nextToken();
            if(!t.isEOF()) return t;
            include = null;
        }
        int prevLine = getLine();
        Token t = source.nextToken();
        while(t == Token.POUND) {
            int line = getLine();
            t = source.nextToken();
            if(line != getLine() || t.isEOF())
                t = clearLine("Missing directive after '#'", line);
            else {
                if(line == prevLine && !first) {
                    error("#"+t+" is not on its own line", prevLine);
                    first = false;
                }
                if(t == Token.DEFINE || t == Token.UNDEF) {
                    Token kind = t;
                    String message = "Missing arguments after #"+t;
                    do {
                        t = source.nextToken();
                        if(line != getLine() || t.isEOF())
                            error(message, line);
                        else if(!t.isIdentifier())
                            t = clearLine("Identifier expected following #"+kind+". Found: "+t, line);
                        else {
                            if(kind == Token.DEFINE) { defined.add(t); }
                            else { defined.remove(t); }
                            t = source.nextToken();
                        }
                        message = "Missing identifier after #"+kind+" ... ,";
                    } while(t == Token.COMMA);
                    if(line == getLine() && !t.isEOF()) {
                        t = clearLine("Unexpected symbol following #"+kind+": "+t, line);
                    }
                }
                else if(t == Token.ASSEMBLY) {
                    t = source.nextToken();
                    if(line != getLine() || t.isEOF())
                        error("Missing arguments after #assembly", line);
                    else if(!t.isString())
                        t = clearLine("String literal expected after #assembly. Found: "+t, line);
                    else {
                        allocations.add(t);
                        t = source.nextToken();
                        if(line == getLine() && !t.isEOF())
                            t = clearLine("Unexpected symbol following #assembly: "+t, line);
                    }
                }
                else if(t == Token.ALLOCATE) {
                    t = source.nextToken();
                    if(line != getLine() || t.isEOF())
                        error("Missing arguments after #allocate", line);
                    else if(!t.isString())
                        t = clearLine("String literal expected after #allocate. Found: "+t, line);
                    else {
                        int limit = 0;
                        Token addr = t;
                        t = source.nextToken();
                        boolean minus = false;

                        if(line == getLine() && !t.isEOF()) {
                            minus = (t == Token.MINUS);
                            if(t.isNumber()) {
                                limit = t.number;
                                t = source.nextToken();
                                if(line == getLine() && !t.isEOF())
                                    t = clearLine("Unexpected symbol following #allocate: "+t, line);
                            }
                            else {
                                if(!minus && t != Token.PLUS)
                                    t = clearLine("Unexpected symbol following #allocate: "+t, line);
                                else {
                                    t = source.nextToken();
                                    if(line == getLine() && !t.isEOF()) {
                                        if(!t.isNumber())
                                            t = clearLine("Unexpected symbol following #allocate: "+t, line);
                                        else {
                                            limit = t.number;
                                            t = source.nextToken();
                                            if(line == getLine() && !t.isEOF())
                                                t = clearLine("Unexpected symbol following #allocate: "+t, line);
                                        }
                                    }
                                }
                            }
                        }

                        allocations.add(addr);
                        if(minus) allocations.add(Token.MINUS);
                        allocations.add(Token.makeNumber(limit));
                    }
                }
                else if(t == Token.INCLUDE) {
                    String message = "Missing arguments after #include";
                    do {
                        t = source.nextToken();
                        if(line != getLine() || t.isEOF())
                            error(message, line);
                        else if(t.isString()) {
                            try {
                                String file = t.format();
                                File f = getFile(file);
                                if(f == null)
                                    error("File not found: \""+t.format()+'\"', line);
                                else if(included.add(f)) { // Skip re-included files.
                                    include = new Preprocessor(f, this);
                                    Token tok = include.nextToken();
                                    if(!tok.isEOF()) return tok;
                                    else include = null;
                                }
                            }
                            catch(FileNotFoundException ioe) {
                                error("File not found: \""+t.format()+'\"', line);
                            }
                            catch(IOException ioe) {
                                error("Unable to read from \""+t.format()+'\"', line);
                            }
                            t = source.nextToken();
                        }
                        else {
                            t = clearLine("String literal expected after #include. Found: "+t, line);
                        }
                        message = "Missing identifier following #include ... ,";
                    } while(t == Token.COMMA);
                    if(line == getLine() && !t.isEOF()) {
                        t = clearLine("Unexpected symbol following #include: "+t, line);
                    }
                }
                else if(t == Token.ERROR) {
                    t = source.nextToken();
                    if(line != getLine() || t.isEOF())
                        error("<INSERT ERROR MESSAGE>", line);
                    else if(t.isString()) {
                        error(t.format(), line);
                        t = source.nextToken();
                        if(line == getLine() && !t.isEOF())
                            t = clearLine("Unexpected symbol following #error: "+t, line);
                    }
                    else
                        t = clearLine("Error message must be a string literal", line);
                }
                else if(t == Token.ENDIF) {
                    t = source.nextToken();
                    if(line == getLine() && !t.isEOF())
                        t = clearLine("Unexpected symbol after #endif: "+t, line);
                    if(ifDepth < 1)
                        error("Unexpected #endif", line);
                    else ifDepth--;
                }
                else if(t == Token.IF || t == Token.ELSE || t == Token.ELIF) {
                    boolean isIf = (t == Token.IF);
                    if(ifDepth < 1 && !isIf)
                        t = clearLine("Unexpected #"+t, line);
                    else
                        t = doCondition(t, line, isIf);
                }
                else {
                    t = clearLine("Invalid directive: #"+t, line);
                }
            }
            prevLine = line;
        }
        first = false;
        if(t.isEOF() && ifDepth > 0) {
            if(ifDepth == 1) { error("Missing #endif", getLine()); }
            else { error("Missing "+ifDepth+" #endifs", getLine()); }
        }
        return t;
    }

    private Token doCondition(Token kind, int line, boolean keepLooking) {
        Token t = source.nextToken();
        if(kind == Token.IF) ifDepth++;
        boolean[] state = {false};

        if(line == getLine() && !t.isEOF()) {
            if(kind == Token.ELSE)
                t = clearLine("Unexpected symbol after #"+kind+": "+t, line);
            else { t = doOr(t, state, line); }
            if(line == getLine() && !t.isEOF())
                t = clearLine("Unexpected symbol following #"+kind+": "+t, line);
        }
        else if(kind != Token.ELSE) {
            error("Missing condition after #"+kind, line);
            state[0] = (kind == Token.IF);
        }

        if(!state[0] || !keepLooking) {
            int levels = 1;
            while(levels > 0) {
                while(t != Token.POUND && !t.isEOF()) {
                    line = getLine();
                    t = source.nextToken();
                }
                t = source.nextToken();
                int current = getLine();
                if(t.isEOF()) { return t; }
                else if(line == current) {
                    error("Directive is not on its own line", line);
                }
                else {
                    if(t == Token.IF) levels++;
                    else if(t == Token.ENDIF) levels--;
                    else if(keepLooking && levels == 1) {
                        if(t == Token.ELSE)
                            return source.nextToken();
                        if(t == Token.ELIF)
                            return doCondition(t, current, true);
                    }
                }
            }
            if(t == Token.ENDIF) {
            	ifDepth--;
            	return source.nextToken();
            }
        }
        return t;
    }

    private Token doOr(Token t, boolean[] state, int line) {
        t = doAnd(t, state, line);
        while(t == Token.OR && line == getLine()) {
            boolean other = state[0];
            t = source.nextToken();
            if(line != getLine() || t.isEOF()) {
                error("Missing value after '||' (must all be on same line)", line);
                return t;
            }
            t = doAnd(t, state, line);
            state[0] |= other;
        }
        return t;
    }

    private Token doAnd(Token t, boolean[] state, int line) {
        t = doValue(t, state, line);
        while(t == Token.AND && line == getLine()) {
            boolean other = state[0];
            t = source.nextToken();
            if(line != getLine() || t.isEOF()) {
                error("Missing value after '&&' (must all be on same line)", line);
                return t;
            }
            t = doValue(t, state, line);
            state[0] &= other;
        }
        return t;
    }

    private Token doValue(Token t, boolean[] state, int line) {
        boolean invert = (t == Token.NOT);
        if(invert) {
            t = source.nextToken();
            if(line != getLine() || t.isEOF()) {
                error("Missing value after '!' (must all be on same line)", line);
                return t;
            }
        }
        if(t == Token.L_PAREN) {
            t = source.nextToken();
            if(line != getLine() || t.isEOF()) {
                error("Missing value after '(' (must all be on same line)", line);
                return t;
            }
            t = doOr(t, state, line);
            if(line != getLine() || t.isEOF()) {
                error("Missing value after '(' (must all be on same line)", line);
                return t;
            }
            if(t != Token.R_PAREN) {
                return clearLine("Missing ')'", line);
            }
            state[0] ^= invert;
            return source.nextToken();
        }
        if(!t.isIdentifier())
            return clearLine("Idendifier expected. Found: "+t, line);
        state[0] = (defined.contains(t) ^ invert);
        return source.nextToken();
    }
}
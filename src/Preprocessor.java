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
        defined = new HashSet<Token>();
        this.handler = handler;
        include = null;
        first = true;
        ifDepth = 0;
    }

    public Preprocessor(String file, Preprocessor parent) throws IOException {
        File f = parent.getFile(file);
        if(f == null) {
            throw new FileNotFoundException("File not found: "+file);
        }

        handler = parent.handler;
        directories = parent.directories;
        startingPath = f.getAbsoluteFile().getParent();
        source = new TokenSource.Default(f.getPath(), handler);
        allocations = parent.allocations;
        assemblies = parent.assemblies;
        defined = parent.defined;
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
        while(line == getLine() && !t.isNewLine());
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
            if(line == prevLine && first) {
                error("Directive is not on its own line", prevLine);
                first = false;
            }
            else if(line != getLine()) {
                t = clearLine("Missing directive after '#'", line);
            }
            else if(t == Token.DEFINE) {
                String message = "Missing arguments after #define";
                do {
                    t = source.nextToken();
                    if(line != getLine())
                        error(message, line);
                    else if(t.isIdentifier())
                        defined.add(t);
                    else
                        t = clearLine("Identifier expected after #define. Found: "+t, line);
                    message = "Missing identifier after comma";
                } while(t == Token.COMMA && line == getLine());
            }
            else if(t == Token.ASSEMBLY) {
                t = nextToken();
                if(line != getLine())
                    error("Missing arguments after #assembly", line);
                else if(!t.isString())
                    t = clearLine("Code for #assembly must be a string literal", line);
                else
                    allocations.add(t);
            }
            else if(t == Token.ALLOCATE) {
                t = nextToken();
                if(line != getLine())
                    error("Missing arguments after #allocate", line);
                else if(!t.isString())
                    t = clearLine("Address for #allocate must be a string literal", line);
                else {
                    int limit = 0;
                    Token addr = t;
                    t = source.nextToken();

                    if(line == getLine()) {
                        boolean minus = (t == Token.MINUS);
                        if(!minus && t != Token.PLUS)
                            t = clearLine("Unexpected symbol after #allocate: "+t, line);
                        else {
                            t = source.nextToken();
                            if(line == getLine() && !t.isNumber())
                                t = clearLine("Unexpected symbol after #allocate: "+t, line);
                            limit = (minus ? -t.number : t.number);
                        }
                    }

                    allocations.add(addr);
                    allocations.add(Token.makeNumber(limit));
                }
            }
            else if(t == Token.INCLUDE) {
                String message = "Missing arguments after #include";
                do {
                    t = source.nextToken();
                    if(line != getLine())
                        error(message, line);
                    else if(t.isString()) {
                        try {
                            include = new Preprocessor(t.value, this);
                            Token tok = include.nextToken();
                            if(!tok.isEOF()) return tok;
                            else include = null;
                        }
                        catch(FileNotFoundException ioe) {
                            error("File not found: \""+t.value+'\"', line);
                        }
                        catch(IOException ioe) {
                            error("Unable to read from \""+t.value+'\"', line);
                        }
                        t = source.nextToken();
                    }
                    else
                        t = clearLine("String literal expected after #include. Found: "+t, line);
                    message = "Missing identifier after comma";
                } while(t == Token.COMMA && line == getLine());
            }
            else if(t == Token.ERROR) {
                t = source.nextToken();
                if(line != getLine())
                    error("<INSERT ERROR MESSAGE>", line);
                else if(t.isString()) {
                    error(t.toString(), line);
                    t = source.nextToken();
                    if(line == getLine())
                        t = clearLine("Unexpected symbol after #error message: "+t, line);
                }
                else
                    t = clearLine("Error message must be a string literal", line);
            }
            else if(t == Token.ENDIF) {
                t = source.nextToken();
                if(line == getLine())
                    t = clearLine("Unexpected symbol after #endif: "+t, line);
                if(ifDepth < 1)
                    error("Unexpected #endif", line);
                else ifDepth--;
            }
            else if(t == Token.IF || t == Token.ELSE || t == Token.ELIF) {
                if(ifDepth < 1 && t != Token.IF)
                    t = clearLine("Unexpected #"+t, line);
                else
                    t = doCondition(t, line);
            }
            else {
                t = clearLine("Invalid directive: #"+t, line);
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

    private Token doCondition(Token kind, int line) {
        Token t = source.nextToken();
        if(kind == Token.IF) ifDepth++;
        boolean[] state = {false};

        if(line == getLine()) {
            if(kind == Token.ELSE)
                t = clearLine("Unexpected symbol after #"+kind+": "+t, line);
            else { t = doOr(t, state, line); }
            if(line == getLine())
                t = clearLine("Unexpected symbol after #"+kind+" condition: "+t, line);
        }
        else if(kind != Token.ELSE) {
            error("Missing condition after #"+kind, line);
            state[0] = (kind == Token.IF);
        }

        if(!state[0]) {
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
                    else if(levels == 1) {
                        if(t == Token.ELSE)
                            return source.nextToken();
                        if(t == Token.ELIF)
                            return doCondition(t, current);
                    }
                }
            }
        }
        return t;
    }

    private Token doOr(Token t, boolean[] state, int line) {
        t = doAnd(t, state, line);
        while(t == Token.OR && line == getLine()) {
            boolean other = state[0];
            t = source.nextToken();
            if(line != getLine()) {
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
            if(line != getLine()) {
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
            if(line != getLine()) {
                error("Missing value after '!' (must all be on same line)", line);
                return t;
            }
        }
        if(t == Token.L_PAREN) {
            t = source.nextToken();
            if(line != getLine()) {
                error("Missing value after '(' (must all be on same line)", line);
                return t;
            }
            t = doOr(t, state, line);
            if(line != getLine()) {
                error("Missing value after '(' (must all be on same line)", line);
                return t;
            }
            if(t != Token.R_PAREN) {
                return clearLine("Missing ')'", line);
            }
            return source.nextToken();
        }
        if(!t.isIdentifier())
            return clearLine("Idendifier expected. Found: "+t, line);
        state[0] = (defined.contains(t) ^ invert);
        return source.nextToken();
    }
}
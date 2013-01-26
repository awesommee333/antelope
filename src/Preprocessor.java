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
        String srcName = getName();
        while(t == Token.POUND) {
            int line = getLine();
            t = source.nextToken();
            if(line == prevLine && first) {
                handler.handle(srcName, prevLine, "Directive not on its own line");
                first = false;
            }
            else if(line != getLine()) {
                t = clearLine("Missing directive after '#'", line);
            }
            else if(t == Token.DEFINE) {
                String msg = "Missing arguments after #define";
                do {
                    t = source.nextToken();
                    if(line != getLine())
                        handler.handle(srcName, line, msg);
                    else if(t.isIdentifier())
                        defined.add(t);
                    else
                        t = clearLine("Identifier expected after #define. Found: "+t, line);
                    msg = "Missing identifier after comma";
                } while(t == Token.COMMA && line == getLine());
            }
            else if(t == Token.ASSEMBLY) {
                t = nextToken();
                if(line != getLine())
                    handler.handle(srcName, line, "Missing arguments after #assembly");
                else if(!t.isString())
                    t = clearLine("Code for #assembly must be a string literal", line);
                else
                    allocations.add(t);
            }
            else if(t == Token.ALLOCATE) {
                t = nextToken();
                if(line != getLine())
                    handler.handle(srcName, line, "Missing arguments after #allocate");
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
                String msg = "Missing arguments after #include";
                do {
                    t = source.nextToken();
                    if(line != getLine())
                        handler.handle(srcName, line, msg);
                    else if(t.isString()) {
                        try {
                            include = new Preprocessor(t.value, this);
                            Token tok = include.nextToken();
                            if(!tok.isEOF()) return tok;
                            else include = null;
                        }
                        catch(FileNotFoundException ioe) {
                            handler.handle(srcName, line, "File not found: \""+t.value+'\"');
                        }
                        catch(IOException ioe) {
                            handler.handle(srcName, line, "Unable to read from \""+t.value+'\"');
                        }
                    }
                    else
                        t = clearLine("String literal expected after #include. Found: "+t, line);
                    msg = "Missing identifier after comma";
                } while(t == Token.COMMA && line == getLine());
            }
            else if(t == Token.ERROR) {
                t = source.nextToken();
                if(line != getLine())
                    handler.handle(srcName, line, "<INSERT ERROR MESSAGE>");
                else if(t.isString())
                    handler.handle(srcName, line, t.toString());
                else
                    t = clearLine("Error message must be a string literal", line);
            }
            else if(t == Token.ENDIF) {
                t = source.nextToken();
                if(line == getLine())
                    t = clearLine("Unexpected symbol after #endif: "+t, line);
                if(ifDepth < 1)
                    handler.handle(srcName, line, "Unexpected #endif");
                else ifDepth--;
            }
            else {
                boolean isElse = (t == Token.ELSE);
                if(isElse && ifDepth < 1)
                    handler.handle(srcName, line, "Unexpected #else");
                else if(t == Token.IF || isElse) {
                    if(isElse) { ifDepth--; }
                    t = doCondition(isElse, line);
                }
                else {
                    t = clearLine("Invalid directive: #"+t, line);
                }
            }
            prevLine = line;
        }
        first = false;
        if(t.isEOF() && ifDepth > 0) {
            if(ifDepth == 1) { handler.handle(srcName, getLine(), "Missing #endif"); }
            else { handler.handle(srcName, getLine(), "Missing "+ifDepth+" #endifs"); }
        }
        return t;
    }

    private Token doCondition(boolean isElse, int line) {
        return clearLine("Not implemented yet "+(isElse?"(#else)":"(#if)"), line); // For now
    }
}
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

    public Preprocessor(TokenSource source, ErrorHandler handler) {
        this.source = source; this.handler = handler; include = null; first = true;
        directories = new LinkedList<String>();
        allocations = new LinkedList<Token>();
        assemblies = new LinkedList<Token>();
        defined = new HashSet<Token>();
        startingPath = null;
    }

    public Preprocessor(String file, ErrorHandler handler) throws IOException {
        this(new TokenSource.Default(file, handler), handler);
    }

    public Preprocessor(Reader in, String name, ErrorHandler handler) throws IOException {
        this(new TokenSource.Default(in, name, handler), handler);
    }

    private Preprocessor(Preprocessor includer, String file) throws IOException {
        File f = new File(file);
        if(!f.isAbsolute()) {
            f = new File(includer.startingPath, file);
            boolean exists = f.exists();
            if(!exists) {
                for(String dir : directories) {
                    f = new File(dir, file);
                    exists = f.exists();
                    if(exists) break;
                }
            }
            if(!exists)
                throw new FileNotFoundException("File not found: "+file);
            file = f.getAbsolutePath();
        }

        handler = includer.handler;
        startingPath = f.getAbsoluteFile().getParent();
        source = new TokenSource.Default(file, handler);
        directories = includer.directories;
        allocations = includer.allocations;
        assemblies = include.assemblies;
        defined = includer.defined;
        include = null;
        first = true;
    }

    public int getLine() { return (include != null)? include.getLine() : source.getLine(); }
    public String getName() { return (include != null)? include.getName() : source.getName(); }

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
                            include = new Preprocessor(this, t.value);
                            Token tok = include.nextToken();
                            if(!tok.isEOF()) return tok;
                            else include = null;
                        }
                        catch(FileNotFoundException ioe) {
                            handler.handle(srcName, line, "File not found: \""+t.value+'\"');
                            include = null;
                        }
                        catch(IOException ioe) {
                            handler.handle(srcName, line, "Unable to read from \""+t.value+'\"');
                            include = null;
                        }
                    }
                    else
                        t = clearLine("String literal expected after #include. Found: "+t, line);
                    msg = "Missing identifier after comma";
                } while(t == Token.COMMA && line == getLine());
            }
            else if(t == Token.IF) {
                // INSERT CODE!
            }
            else if(t == Token.ERROR) {
                t = nextToken();
                if(line != getLine())
                    handler.handle(srcName, line, "<INSERT ERROR MESSAGE>");
                else if(t.isString())
                    handler.handle(srcName, line, t.toString());
                else
                    t = clearLine("Error message must be a string literal", line);
            }
            else {
                t = clearLine("Invalid directive: #"+t, line);
            }
            prevLine = line;
        }
        first = false;
        return t;
    }
}
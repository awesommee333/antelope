package antelope;
import java.util.LinkedList;

public final class Preprocessor implements TokenSource {
    public final java.util.TreeSet<Token> defined;
    public final LinkedList<Token> allocations; // Will eventually replace these lists
    public final LinkedList<Token> assemblies;  // with proper syntax-tree constructs.
    public final ErrorHandler handler;
    public final TokenSource source;
    private Preprocessor include;
    private boolean first;

    public Preprocessor(TokenSource source, ErrorHandler handler) {
        if(source == null) throw new NullPointerException("TokenSource must not be null");
        this.source = source; this.handler = handler; include = null; first = true;
        defined = new java.util.TreeSet<Token>();
        allocations = new LinkedList<Token>();
        assemblies = new LinkedList<Token>();
    }

    public Preprocessor(String file, ErrorHandler handler) {
        this(TokenSource.Default.Get(file, handler), handler);
    }

    private Preprocessor(Preprocessor includer, String file) {
        handler = includer.handler;
        source = TokenSource.Default.Get(file, handler);
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
                        String file = t.format();
                        if(file != null) {
                            include = new Preprocessor(this, t.format());
                            Token tok = include.nextToken();
                            if(!tok.isEOF()) return tok;
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
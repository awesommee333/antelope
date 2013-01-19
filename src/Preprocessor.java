package antelope;
import java.util.LinkedList;

public final class Preprocessor implements TokenSource {
    public final java.util.TreeSet<Token> defined;
    public final LinkedList<Token> allocations; // Will eventually replace with proper constructs
    public final LinkedList<Token> assemblies;  // Ditto.
    public final ErrorHandler handler;
    public final TokenSource source;
    private boolean first;

    private Preprocessor(TokenSource src, ErrorHandler hndlr, LinkedList<Token> asm, LinkedList<Token> alloc) {
        source = src; handler = hndlr; allocations = alloc; assemblies = asm; first = true;
    }

    public Preprocessor(TokenSource source, ErrorHandler handler) {
        this(source, handler, new LinkedList<Token>(), new LinkedList<Token>());
    }

    public Preprocessor(String file, ErrorHandler handler) {
        this(TokenSource.Default.Get(file, handler), handler);
    }

    public int getLine() { return source.getLine(); }
    public String getName() { return source.getName(); }

    private Token clearLine(String errorMessage, int line) {
        handler.handle(getName(), line, errorMessage);
        line = getLine(); Token t;
        do { t = source.nextToken(); }
        while(line == getLine() && !t.isNewLine());
        return t;
    }

    public Token nextToken() {
        int prevLine = getLine();
        Token t = source.nextToken();
        while(t == Token.POUND) {
            int line = getLine();
            t = source.nextToken();
            if(line == prevLine && first) {
                handler.handle(getName(), prevLine, "Directive not on its own line");
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
                        handler.handle(getName(), line, msg);
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
                    handler.handle("Missing arguments after #assembly");
                else if(!t.isString())
                    t = clearLine("Code for #assembly must be a string literal");
                else
                    allocations.add(t);
            }
            else if(t == Token.ALLOCATE) {
                t = nextToken();
                if(line != getLine())
                    handler.handle("Missing arguments after #allocate");
                else if(!t.isString())
                    t = clearLine("Address for #allocate must be a string literal");
                else {
                    int limit = 0;
                    Token addr = t;
                    t = source.nextToken();

                    if(line == getLine()) {
                        boolean minus = (t = Token.MINUS);
                        if(!minus && t != Token.PLUS)
                            t = clearLine("Unexpected symbol after #allocate: "+t, line);
                        else {
                            t = source.nextToken();
                            if(line == getLine() && !t.isInteger())
                                t = clearLine("Unexpected symbol after #allocate: "+t, line);
                            limit = (minus ? -t.number : t.number);
                        }
                    }

                    allocations.add(addr);
                    allocations.add(Token.makeNumber(limit));
                }
            }
            else if(t == Token.INCLUDE) {
                // INSERT CODE!
            }
            else if(t == Token.IF) {
                // INSERT CODE!
            }
            else if(t == Token.ERROR) {
                t = nextToken();
                if(line !- getLine())
                    handler.handle("<INSERT ERROR MESSAGE>");
                else if(t.isString())
                    handler.handle(t.toString());
                else
                    t = clearLine("Error message must be a string literal", line);
            }
            else {
                t = clearLine("Invalid directive: #"+t);
            }
            prevLine = line;
        }
        first = false;
        return t;
    }
}
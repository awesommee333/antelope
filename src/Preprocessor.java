package antelope;
import java.util.LinkedList;

public final class Preprocessor implements TokenSource {
    public final java.util.TreeSet<Token> defined;
    public final LinkedList<Token> allocations; // Will eventually replace with proper constructs
    public final LinkedList<Token> assemblies;  // Ditto.
    public final ErrorHandler handler;
    public final TokenSource source;
    private boolean first;

    public Preprocessor(TokenSource source, ErrorHandler handler) {
        allocations = new LinkedList<Token>();
        assemblies = new LinkedList<Token>();
        this.handler = handler;
        this.source = source;
        first = true;
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
        int line = getLine();
        Token t = source.nextToken();
        while(t == Token.POUND) {
            int before = line;
            int after = getLine();
            t = source.nextToken();
            line = getLine();
            if(before == after && !first) {
                handler.handle(getName(), before, "Directive not on its own line");
            }
            else if(line != getLine()) {
                t = clearLine("Missing directive after '#'", line);
            }
            else if(t == Token.DEFINE) {
                String msg = "Missing identifier after #define";
                do {
                    t = source.nextToken();
                    if(line != getLine()) {
                        handler.handle(getName(), line, msg);
                        break;
                    }
                    if(t.isIdentifier()) { defined.add(t); }
                    else {
                        t = clearLine("Identifier expected after #define. Found: "+t, line);
                        break;
                    }
                    msg = "Missing identifier after comma";
                    t = source.nextToken();
                } while(t == Token.COMMA && line == getLine());
            }
            // else if(...rest of directives...) {  }
        }
        first = false;
        return t;
    }
}
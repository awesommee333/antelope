package antelope;
import java.util.LinkedList;

public final class Preprocessor implements TokenSource {
    private final java.util.TreeSet<Token> defined;
    private final LinkedList<Token> allocations; // Will eventually replace with proper constructs
    private final LinkedList<Token> assemblies;  // Ditto.
    private final TokenSource source;
    private boolean first;

    public Preprocessor(TokenSource source) {
        allocations = new LinkeList<Token>();
        assemblies = new LinkeList<Token>();
        this.source = source;
        first = true;
    }

    public Preprocessor(String file, ErrorHandler handler) {
        super(new TokenSource.Default.Get(file, handler));
    }

    public int getLine() { return source.getLine(); }
    public String getName() { return source.getName(); }

    private Token clearLine(String errorMessage, int line) {
        source.handler.handle(getName(), line, errorMessage);
        line = getLine(); Token t;
        do { t = source.nextToken(); }
        while(line == getLine() && !t.isNewLine());
        return t;
    }

    public Token nextToken() {
        while(true) {
            int line = getLine();
            Token t = source.nextToken();
            if(t != Token.POUND) break;

            if(!first) {
                int nextLine = getLine();
                if(line == getLine())
                    handler.handle(getName(), line, "Directive not on its own line");
                continue;
            }

            t = source.nextToken();

            if(line != getLine())
                return retry("Missing directive after '#'", line);

            if(t == Token.DEFINE) {
                String msg = "Missing identifier after #define";
                do {
                    t = source.nextToken();
                    if(line != getLine()) {
                        handler.handle(getName(), line, msg);
                        return t;
                    }
                    if(t.isIdentifier()) { defined.add(t); }
                    else { return retry("Identifier expected after #define. Found: "+t); }
                    msg = "Missing identifier after comma";
                    t = source.nextToken();
                } while(t == Token.COMMA && line == getLine());
                return nextToken();
            }


        }
        first = false;
        return t;
    }
}
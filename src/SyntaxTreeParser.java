package antelope;
import antelope.tree.TParam;
import antelope.tree.Namespace;
import java.util.LinkedList;

public final class SyntaxTreeParser implements TokenSource, ErrorHandler {
    private final LinkedList<Token> stack;
    private final ErrorHandler handler;
    private final TokenSource source;
    private final Namespace globalNS;

    public SyntaxTreeParser(TokenSource source, ErrorHandler handler) {
        globalNS = new Namespace(null, TParam.ZERO, null);
        stack = new LinkedList<Token>();
        this.handler = handler;
        this.source = source;
    }

    public int getLine() { return source.getLine(); }
    public String getName() { return source.getName(); }
    public void push(Token token) { stack.push(token); }

    public void error(String src, int line, String msg)
        { handler.error(src, line, msg); }

    public void error(String message, int line)
        { handler.error(source.getName(), line, message); }

    public void error(String message)
        { handler.error(source.getName(), source.getLine(), message); }

    public Token nextToken() {
        while(true) {
            Token t = (stack.size() > 0 ? stack.pop() : source.nextToken());
            if(t != Token.NEW_FILE) { return t; }
            globalNS.parse(this);
        }
    }

    public Namespace parse() {
        globalNS.parse(this);
        return globalNS;
    }
}
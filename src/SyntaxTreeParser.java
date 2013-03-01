package antelope;

public final class SyntaxTreeParser implements TokenSource, ErrorHandler {
    private final ErrorHandler handler;
    private final TokenSource source;
    private final Namespace globalNS;

    public SyntaxTreeParser(TokenSource source, ErrorHandler handler) {
        globalNS = new Namespace(null, TParam.ZERO, null);
        this.handler = handler;
        this.source = source;
    }

    public int getLine() { return source.getLine(); }
    public String getName() { return source.getName(); }

    public void error(String src, int line, String msg) {
        handler.error(src, line, msg);
    }

    public void error(String message, int line) {
        handler.error(source.getName(), line, message);
    }

    public void error(String message) {
        handler.error(source.getName(), source.getLine(), message);
    }

    public Token nextToken() {
        Token t = source.nextToken();
        while(t == Token.NEW_FILE) {
            globalNS.parse(this);
            t = source.nextToken();
        }
        return t;
    }

    public Namespace parse() {
        globalNS.parse(this);
        return globalNS;
    }
}
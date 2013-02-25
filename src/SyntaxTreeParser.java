package antelope;

public final class SyntaxTreeParser implements TokenSource {
    private final ErrorHandler handler;
    private final TokenSource source;
    private final Namespace globalNS;

    private SyntaxTreeParser(TokenSource source, ErrorHandler handler) {
        globalNS = new Namespace(null, TParam.ZERO, null);
        this.handler = handler;
        this.source = source;
    }

    private SyntaxTreeParser(SyntaxTreeParser other) {
        globalNS = other.globalNS;
        handler = other.handler;
        source = other.source;
    }

    public int getLine() { return source.getLine(); }
    public String getName() { return source.getName(); }

    public Token nextToken() {
        Token t = source.nextToken();
        while(t == Token.NEW_FILE) {
            globalNS.parse(new SyntaxTreeParser(this), handler);
            t = source.nextToken();
        }
        return t;
    }

    public static Namespace parse(TokenSource source, ErrorHandler handler) {
        SyntaxTreeParser parser = new SyntaxTreeParser(source, handler);
        parser.globalNS.parse(parser, handler);
        return parser.globalNS;
    }
}
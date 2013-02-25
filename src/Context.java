package antelope;

public final class Context {
    public final Namespace namespace;
    public final Using[] usings;
    public final String source;
    public final int line;

    public Context(Namespace namespace, Using[] usings, String source, int line) {
        this.namespace = namespace;
        this.usings = usings;
        this.source = source;
        this.line = line;
    }

    public Context atLine(int line) {
        return new Context(namespace, usings, source, line);
    }
}
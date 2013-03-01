package antelope;

public final class Context {
    public final Namespace namespace;
    public final Using[] usings;
    public final String source;

    public Context(Namespace namespace, Using[] usings, String source) {
        this.namespace = namespace;
        this.usings = usings;
        this.source = source;
    }
}
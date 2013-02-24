package antelope;

public final class Context {
    public final Namespace namespace;
    public final Using[] usings;
    
    public Context(Namespace namespace, Using[] usings) {
        this.namespace = namespace;
        this.usings = usings;
    }
}
package antelope;

public final class Cast extends Expression {
    public final Expression value;
    public final Type[] types;
    
    public Cast(Expression value, Type type) {
        types = new Type[] { type };
        this.value = value;
    }
    
    public Cast(Expression value, Type[] types) {
        this.value = value;
        this.types = types;
    }
}
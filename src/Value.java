package antelope;

public final class Value extends Expression {
    public final Token value;
    
    public Value(Token value) {
        this.value = value;
    }
}
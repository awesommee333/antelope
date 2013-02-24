package antelope;

public abstract class Expression {
    public boolean interpret = false;
    
    public static final Expression UNKNOWN = Value.UNKNOWN;
    public static final Expression[] ZERO = new Expression[0];
}
package antelope;

public final class Variable extends Member {
    public final Type type;
    public final Expression init;
    public final Expression alias;
    public boolean interpret = false;
    
    public Variable(Context context, Token name, Type type) {
        this(context, name, type, null, null);
    }
    
    public Variable(Context context, Token name, Type type, Expression alias, Expression init) {
        super(context, name);
        this.type = type;
        this.alias = alias;
        this.init = init;
    }
    
    public static final Variable[] ZERO = new Variable[0];
}
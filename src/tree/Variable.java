package antelope.tree;

import antelope.Token;

public final class Variable extends Member {
    public final Type type;
    public final Expression init;
    public final Expression alias;
    public boolean interpret = false;

    public Variable(Context context, int line, Token name, Type type) {
        super(context, line, name);
        this.type = type;
        alias = null;
        init = null;
    }

    public Variable(Context context, int line, Token name, Type type, Expression alias, Expression init) {
        super(context, line, name);
        this.type = type;
        this.alias = alias;
        this.init = init;
    }

    public static final Variable[] ZERO = new Variable[0];
}
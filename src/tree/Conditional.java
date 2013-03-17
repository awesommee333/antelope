package antelope.tree;

import antelope.Token;

public abstract class Conditional extends Construct {
    public final Token kind;
    public final Expression condition;
    
    public Conditional(Token kind, Expression condition) {
        this.kind = kind; this.condition = condition;
    }
}
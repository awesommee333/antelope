package antelope.tree;
import antelope.*;

public final class Call extends Expression {
    public boolean isNew;
    public final Expression[] args;
    public final Expression[] inits;
    
    public Call(boolean isNew, Expression[] args) {
        this.isNew = isNew; this.args = args; inits = Expression.ZERO;
    }
    
    public Call(boolean isNew, Expression[] args, Expression[] inits) {
        this.isNew = isNew; this.args = args; this.inits = inits;
    }
}
package antelope.tree;

import antelope.Token;

public final class Ternary extends Operation {
    public final Expression condition;
    
    public Ternary(Expression condition, Expression left, Expression right) {
        super(left, Token.QUESTION, right);
        this.condition = condition;
    }
}
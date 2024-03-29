package antelope.tree;
import antelope.*;

public final class Ternary extends Operation {
    public final Expression condition;

    public Ternary(Expression condition, Expression left, Expression right) {
        super(left, Token.COLON, right);
        this.condition = condition;
    }
}
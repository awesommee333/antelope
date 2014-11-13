package antelope.tree;
import antelope.*;

public class Operation extends Expression {
    public final Token kind;
    public final Expression left;
    public final Expression right;
    
    public Operation(Expression left, Token kind, Expression right) {
        this.left = left;
        this.kind = kind;
        this.right = right;
    }
    
    public Operation(Expression value, Token kind) {
        left = value;
        this.kind = kind;
        right = null;
    }
    
    public Operation(Token kind, Expression value) {
        left = null;
        this.kind = kind;
        right = value;
    }
}
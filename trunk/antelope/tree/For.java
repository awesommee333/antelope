package antelope.tree;
import antelope.*;

public final class For extends Conditional {
    public final Command init;
    public final Command step;
    
    public For(Command init, Expression condition, Command step) {
        super(Token.FOR, condition);
        code = new Block(true);
        this.init = init;
        this.step = step;
    }
    
    public For(Command init, Expression condition, Command step, Block code) {
        super(Token.FOR, condition);
        this.code = code;
        this.init = init;
        this.step = step;
    }
}
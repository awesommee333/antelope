package antelope.tree;

import antelope.Token;

public final class Do extends Conditional {
    public final Token logic;
    public int conditionIndex = -1;
    
    public Do(Token logic, Expression condition) {
        super(Token.DO, condition);
        this.code = new Block(true);
        this.logic = logic;
    }
    
    public Do(Token logic, Expression condition, Block code) {
        super(Token.DO, condition);
        this.logic = logic;
        this.code = code;
    }
}
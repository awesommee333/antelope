package antelope.tree;

import antelope.Token;

public final class If extends Conditional {
    public Block elseCode;
    
    public If(Expression condition) {
        super(Token.IF, condition);
        elseCode = new Block(true);
        code = new Block(true);
    }
    
    public If(Expression condition, Block code) {
        super(Token.IF, condition);
        elseCode = new Block(true);
        this.code = code;
    }
    
    public If(Expression condition, Block code, Block elseCode) {
        super(Token.IF, condition);
        this.elseCode = elseCode;
        this.code = code;
    }
}
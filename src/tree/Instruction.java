package antelope.tree;

import antelope.Token;

public final class Instruction extends Command {
    public final Token kind;
    public final Expression value;
    
    public Instruction(Token kind, Expression value) {
        this.kind = kind;
        this.value = value;
    }
}
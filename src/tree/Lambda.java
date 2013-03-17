package antelope.tree;

public final class Lambda extends Expression {
    public final FuncPtr signature;
    public final Block code;
    
    public Lambda(FuncPtr signature) {
        this.signature = signature;
        code = new Block(true);
    }
    
    public Lambda(FuncPtr signature, Block code) {
        this.signature = signature;
        this.code = code;
    }
}
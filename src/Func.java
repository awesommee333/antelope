package antelope;

public final class Func extends Member {
    public final TParam[] params;
    public final FuncPtr signature;
    public boolean interpret = false;
    public Block code;
    
    public Func(Context context, Token name, FuncPtr signature) {
        super(context, name);  this.signature = signature;
        params = TParam.ZERO;  code = new Block(true);
    }
    
    public Func(Context context, Token name, FuncPtr signature, Block code) {
        super(context, name);  this.signature = signature;
        params = TParam.ZERO;  this.code = code;
    }
    
    public Func(Context context, Token name, TParam[] params, FuncPtr signature) {
        super(context, name);  this.signature = signature;
        this.params = params;  code = new Block(true);
    }
    
    public Func(Context context, Token name, TParam[] params, FuncPtr signature, Block code) {
        super(context, name);  this.signature = signature;
        this.params = params;  this.code = code;
    }
}
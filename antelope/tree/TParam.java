package antelope.tree;
import antelope.*;

public final class TParam extends SyntaxTreeItem {
    public final Token name;
    public final BaseType[] constraints;
    
    public TParam(Token name) {
        this.name = name;
        constraints = BaseType.ZERO;
    }
    
    public TParam(Token name, BaseType[] constraints) {
        this.name = name;
        this.constraints = constraints;
    }
    
    public static final TParam[] ZERO = new TParam[0];
}
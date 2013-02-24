package antelope;

public final class Type {
    public final BaseType base;
    public final Expression[] dims;
    public boolean isVolatile = false;
    public boolean isConst = false;
    public boolean isRef = false;
    
    public Type(BaseType base) {
        this.base = base;
        dims = Expression.ZERO;
    }
        
    public Type(BaseType base, Expression[] dims) {
        this.base = base;
        this.dims = dims;
    }
        
    public Type(BaseType base, Expression[] dims, boolean isConst, boolean isVolatile, boolean isRef) {
        this.base = base;
        this.dims = dims;
        this.isConst = isConst;
        this.isVolatile = isVolatile;
        this.isRef = isRef;
    }
    
    public static final Type UNKNOWN = new Type(Entity.UNKNOWN, Expression.ZERO, false, false, false);
    public static final Type[] ZERO = new Type[0];
}
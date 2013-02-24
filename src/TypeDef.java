package antelope;

public final class TypeDef extends Global {
    public final TParam[] params;
    public final boolean isNew;
    public final Type type;
    
    public TypeDef(Context context, Token name, TParam[] params, Type type, boolean isNew) {
        super(context, name);
        this.params = params;
        this.isNew = isNew;
        this.type = type;
    }
}
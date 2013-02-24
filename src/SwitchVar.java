package antelope;

public final class SwitchVar extends Member {
    public final Token[] values;
    
    public SwitchVar(Context context, Token name, Token[] values) {
        super(context, name);
        this.values = values;
    }
}
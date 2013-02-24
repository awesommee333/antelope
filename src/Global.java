package antelope;

public abstract class Global {
    public final Context context;
    public final Token name;
    
    public Global(Context context, Token name) {
        this.context = context;
        this.name = name;
    }
}
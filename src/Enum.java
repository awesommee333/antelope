package antelope;

public final class Enum extends Global {
    public final Token[] values;

    public Enum(Context context, Token name, Token[] values) {
        super(context, name);
        this.values = values;
    }
}
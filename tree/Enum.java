package antelope.tree;
import antelope.*;

public final class Enum extends Global {
    public final Token[] values;

    public Enum(Context context, int line, Token name, Token[] values) {
        super(context, line, name);
        this.values = values;
    }
}
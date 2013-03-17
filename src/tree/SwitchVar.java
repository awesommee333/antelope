package antelope.tree;

import antelope.Token;

public final class SwitchVar extends Member {
    public final Token[] values;

    public SwitchVar(Context context, int line, Token name, Token[] values) {
        super(context, line, name);
        this.values = values;
    }
}
package antelope.tree;
import antelope.*;

public abstract class Global extends SyntaxTreeItem {
    public final Context context;
    public final Token name;
    public final int line;

    public Global(Context context, int line, Token name) {
        this.context = context;
        this.name = name;
        this.line = line;
    }
}
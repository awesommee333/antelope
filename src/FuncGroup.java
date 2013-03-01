package antelope;
import java.util.LinkedList;

public final class FuncGroup extends Member {
    public final LinkedList<Func> funcs;

    public FuncGroup(Context context, int line, Token name) {
        super(context, line, name);
        funcs = new LinkedList<Func>();
    }

    public FuncGroup(LinkedList<Func> funcs) throws NullPointerException {
        super(funcs.getFirst().context, funcs.getFirst().line, funcs.getFirst().name);
        this.funcs = funcs;
    }

    public FuncGroup(Func func) {
        super(func.context, func.line, func.name);
        funcs = new LinkedList<Func>();
        funcs.add(func);
    }

    public boolean add(Func func) {
        if(!func.name.equals(name))
            return false;
        funcs.add(func);
        return true;
    }
}
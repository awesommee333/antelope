package antelope;
import java.util.LinkedList;

public final class VarDec extends Command {
    public final Type type;
    public final LinkedList<Variable> vars;

    public VarDec(Type type) {
        this.type = type;
        vars = new LinkedList<Variable>();
    }

    public VarDec(LinkedList<Variable> vars) {
        type = vars.getFirst().type;
        this.vars = vars;
    }

    public Variable add(Context context, Token name) {
        Variable var = new Variable(context, name, type);
        vars.add(var);
        return var;
    }

    public Variable add(Context context, Token name, Expression alias, Expression init) {
        Variable var = new Variable(context, name, type, alias, init);
        vars.add(var);
        return var;
    }
}
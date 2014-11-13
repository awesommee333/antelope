package antelope.tree;
import antelope.*;

public final class FuncPtr extends BaseType {
    public final Entity receiver;
    public final Variable[] args;
    public final Type[] returns;

    public FuncPtr(Entity receiver, Variable[] args)
        { this(receiver, args, Type.ZERO); }

    public FuncPtr(Entity receiver, Variable[] args, Type ret)
        { this(receiver, args, new Type[] { ret }); }

    public FuncPtr(Entity receiver, Variable[] args, Type[] returns)
        { this.receiver = receiver; this.returns = returns; this.args = args; }
}
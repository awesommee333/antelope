package antelope;

public final class FuncPtr extends BaseType {
    public final Entity receiver;
    public final Variable[] args;
    public final Variable[] returns;

    public FuncPtr(Entity receiver, Variable[] args) {
        this(receiver, args, Variable.ZERO);
    }

    public FuncPtr(Entity receiver, Variable[] args, Type ret) {
        this(receiver, args, new Variable[] { new Variable(null, null, ret) });
    }

    public FuncPtr(Entity receiver, Variable[] args, Variable ret) {
        this(receiver, args, new Variable[] { ret });
    }

    public FuncPtr(Entity receiver, Variable[] args, Variable[] returns) {
        this.receiver = receiver; this.returns = returns; this.args = args;
    }
}
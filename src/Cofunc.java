package antelope;

public final class Cofunc extends Member {
    public final Func func;
    public final Struct struct;

    public Cofunc(Func func) {
        super(func.context, func.name);
        struct = new Struct(context, name, TParam.ZERO);
        this.func = func;
    }

    public Cofunc(Func func, Struct struct) {
        super(func.context, func.name);
        this.struct = struct;
        this.func = func;
    }

    public void add(Statement statement) {
        func.code.add(statement);
    }
}
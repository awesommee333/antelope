package antelope;
import java.util.LinkedHashMap;

public final class Iface extends Global {
    public final TParam[] params;
    public final Entity[] parents;
    public final LinkedHashMap<Token,Member> members;

    public Iface(Context context, int line, Token name, TParam[] params, Entity[] parents) {
        super(context, line, name);
        this.params = params;
        this.parents = parents;
        this.members = new LinkedHashMap<Token,Member>();
    }

    public Iface(Context context, int line, Token name, TParam[] params, Entity[] parents,
            LinkedHashMap<Token,Member> members) {
        super(context, line, name);
        this.params = params;
        this.parents = parents;
        this.members = members;
    }

    // Returns null if the add succeeded, otherwise returns
    // the Member that the argument conflicts with
    public Member add(Member member) {
        Member m = get(member.name);
        if(m != null) {
            if(!(m instanceof FuncGroup))
                return m;
            FuncGroup fg = (FuncGroup)m;
            if(!(member instanceof Func))
                return fg.funcs.getFirst();
            fg.add((Func)member);
            return member;
        }
        members.put(member.name, member);
        return member;
    }

    public Member get(Token name) { return members.get(name); }
}
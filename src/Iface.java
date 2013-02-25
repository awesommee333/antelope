package antelope;
import java.util.LinkedHashMap;

public final class Iface extends Global {
    public final TParam[] params;
    public final Entity[] parents;
    public final LinkedHashMap<Token,Member> members;

    public Iface(Context context, Token name, TParam[] params, Entity[] parents) {
        super(context, name);
        this.params = params;
        this.parents = parents;
        this.members = new LinkedHashMap<Token,Member>();
    }

    public Iface(Context context, Token name, TParam[] params, Entity[] parents, LinkedHashMap<Token,Member> members) {
        super(context, name);
        this.params = params;
        this.parents = parents;
        this.members = members;
    }

    public Member add(Member member) {
        if(!(member instanceof Func))
            return members.put(member.name, member);
        Member group = members.get(member.name);
        if(!(group instanceof FuncGroup))
            return members.put(member.name, new FuncGroup((Func)member));
        ((FuncGroup)group).add((Func)member);
        return null;
    }

    public Member get(Token name) { return members.get(name); }
}
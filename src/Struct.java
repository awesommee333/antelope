package antelope;
import java.util.LinkedHashMap;

public final class Struct extends Global {
    public final TParam[] params;
    public final LinkedHashMap<Token,Member> members;

    public Struct(Context context, Token name, TParam[] params) {
        super(context, name);
        this.params = params;
        members = new LinkedHashMap<Token,Member>();
    }

    public Struct(Context context, Token name, TParam[] params, LinkedHashMap<Token,Member> members) {
        super(context, name);
        this.params = params;
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
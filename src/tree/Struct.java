package antelope.tree;
import antelope.*;
import java.util.LinkedHashMap;

public final class Struct extends Global {
    public final TParam[] params;
    public final LinkedHashMap<Token,Member> members;

    public Struct(Context context, int line, Token name, TParam[] params) {
        super(context, line, name);
        this.params = params;
        members = new LinkedHashMap<Token,Member>();
    }

    public Struct(Context context, int line, Token name, TParam[] params,
            LinkedHashMap<Token,Member> members) {
        super(context, line, name);
        this.params = params;
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
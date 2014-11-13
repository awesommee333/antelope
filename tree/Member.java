package antelope.tree;
import antelope.*;

public abstract class Member extends Global {
    public Member(Context context, int line, Token name) {
        super(context, line, name);
    }
}
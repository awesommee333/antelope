package antelope.tree;
import antelope.*;

public final class Value extends Expression {
    public final Token value;

    public Value(Token value) {
        this.value = value;
    }

    public static Expression parse(SyntaxTreeParser parser) {
        Token t = parser.nextToken();
        if(t.isValue()) return new Value(t);
        return null;
    }
}
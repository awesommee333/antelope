package antelope.tree;
import antelope.*;

public abstract class Expression extends SyntaxTreeItem {
    public boolean interpret = false;
    
    public static Expression parse(SyntaxTreeParser parser) {
        return parse(parser, null);
    }
    
    public static Expression parse(SyntaxTreeParser parser, Token first) {
        Token t = (first != null ? first : parser.nextToken());
        return null;
    }
    
    public static final Expression UNKNOWN = Value.UNKNOWN;
    public static final Expression[] ZERO = new Expression[0];
}
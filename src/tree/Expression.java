package antelope.tree;

public abstract class Expression extends SyntaxTreeItem {
    public boolean interpret = false;
    
    public static final Expression UNKNOWN = Value.UNKNOWN;
    public static final Expression[] ZERO = new Expression[0];
}
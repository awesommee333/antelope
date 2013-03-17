package antelope.tree;
import java.util.ArrayList;

public final class Block extends SyntaxTreeItem {
    public boolean braces;
    public final ArrayList<Statement> code;
    
    public Block(boolean braces) {
        code = new ArrayList<Statement>();
        this.braces = braces;
    }
    
    public Block(ArrayList<Statement> code, boolean braces) {
        this.braces = braces || code.size() > 1;
        this.code = code;
    }
    
    public void add(Statement lineOfCode) {
        code.add(lineOfCode);
        braces |= (code.size() > 1);
    }
}
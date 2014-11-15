package antelope.tree;
import antelope.*;

public final class Using extends SyntaxTreeItem {
    public final Entity entity;
    public final Token alias;
    
    public Using(Entity entity) {
        this.entity = entity;
        alias = null;
    }
    
    public Using(Entity entity, Token alias) {
        this.entity = entity;
        this.alias = alias;
    }
}
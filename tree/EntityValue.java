package antelope.tree;
import antelope.*;

public final class EntityValue extends Expression {
    public final Entity value;
    
    public EntityValue(Entity value)
        { this.value = value; }

    public EntityValue(Token name)
        { value = new Entity(name); }

    public EntityValue(Token name, Entity next)
        { value = new Entity(name, next); }

    public EntityValue(Token name, Type[] params)
        { value = new Entity(name, params); }

    public EntityValue(Token name, Type[] params, Entity next)
        { value = new Entity(name, params, next); }

    public static final EntityValue UNKNOWN = new EntityValue(Entity.UNKNOWN);
}
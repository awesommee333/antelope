package antelope;

public final class Entity extends BaseType {
    public final Token name;
    public Type[] params;
    public Entity next;

    public Entity(Token name)
        { this.name = name; params = Type.ZERO; next = null; }

    public Entity(Token name, Entity next)
        { this.name = name; params = Type.ZERO; this.next = next; }

    public Entity(Token name, Type[] params)
        { this.name = name; this.params = params; next = null; }

    public Entity(Token name, Type[] params, Entity next)
        { this.name = name; this.params = params; this.next = next; }

    public static final Entity UNKNOWN = new Entity(Token.UNKNOWN, Type.ZERO, null);
}
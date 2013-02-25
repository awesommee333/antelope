package antelope;
import java.util.Map;
import java.util.LinkedHashMap;

public final class Namespace {
    public final Token name;
    public final TParam[] params;
    public final Namespace parent;
    public final LinkedHashMap<Token,Global> contents;
    public final LinkedHashMap<Token,Namespace> children;

    public Namespace(Token name, TParam[] params, Namespace parent) {
        this.name = name;
        this.params = params;
        this.parent = parent;
        contents = new LinkedHashMap<Token,Global>();
        children = new LinkedHashMap<Token,Namespace>();
    }

    public Namespace(Token name, TParam[] params, Namespace parent,
            LinkedHashMap<Token,Namespace> children, LinkedHashMap<Token,Global> contents) {
        this.name = name;
        this.params = params;
        this.parent = parent;
        this.contents = contents;
        this.children = children;
    }

    public Global add(Global global) {
        if(!(global instanceof Func))
            return contents.put(global.name, global);
        Global group = contents.get(global.name);
        if(!(group instanceof FuncGroup))
            return contents.put(global.name, new FuncGroup((Func)global));
        ((FuncGroup)group).add((Func)global);
        return null;
    }

    public Namespace add(Namespace namespace) {
        return children.put(namespace.name, namespace);
    }

    public Object get(Token name) {
        Object o = contents.get(name);
        return (o != null ? o : children.get(name));
    }

    public void parse(TokenSource source, ErrorHandler handler) {
        // INSERT CODE TO PARSE CODE FROM SOURCE
    }
}
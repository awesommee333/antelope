package antelope;
import java.util.ArrayList;
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

    // Returns null if the add succeeded, otherwise returns
    // the Global or Namespace that the argument conflicts with
    public Object add(Global global) {
        Object o = get(global.name);
        if(o != null) {
            if(!(o instanceof FuncGroup))
                return o;
            FuncGroup fg = (FuncGroup)o;
            if(!(global instanceof Func))
                return fg.funcs.getFirst();
            fg.add((Func)global);
            return null;
        }
        contents.put(global.name, global);
        return null;
    }

    // Returns null if the add succeeded, otherwise returns
    // the Global or Namespace that the argument conflicts with
    public Object add(Namespace namespace) {
        Object o = get(namespace.name);
        if(o != null)
            return (o instanceof FuncGroup ? ((FuncGroup)o).funcs.getFirst() : o);
        children.put(namespace.name, namespace);
        return namespace;
    }

    public Global getGlobal(Token name) { return contents.get(name); }
    public Namespace getNamespace(Token name) { return children.get(name); }

    public Object get(Token name) {
        Object o = contents.get(name);
        return (o != null ? o : children.get(name));
    }

    public void parse(SyntaxTreeParser parser) {
        ArrayList<Using> usings = new ArrayList<Using>();
        Token t = parser.nextToken();
        Namespace ns = null;

        while(t == Token.NAMESPACE || t == Token.USING) {
            if(t == Token.NAMESPACE) {
                // Parse namespace values; if ns not null, error; else ns = new.
            }
        }
        // INSERT CODE TO PARSE CONTENTS
    }
}
package antelope.tree;
import antelope.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public final class Namespace extends SyntaxTreeItem {
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
    public SyntaxTreeItem add(Global global) {
        SyntaxTreeItem i = get(global.name);
        if(i != null) {
            if(!(i instanceof FuncGroup))
                return i;
            FuncGroup fg = (FuncGroup)i;
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
    public SyntaxTreeItem add(Namespace namespace) {
        SyntaxTreeItem i = get(namespace.name);
        if(i != null)
            return (i instanceof FuncGroup ? ((FuncGroup)i).funcs.getFirst() : i);
        children.put(namespace.name, namespace);
        return namespace;
    }

    public Global getGlobal(Token name) { return contents.get(name); }
    public Namespace getNamespace(Token name) { return children.get(name); }

    public SyntaxTreeItem get(Token name) {
        SyntaxTreeItem i = contents.get(name);
        return (i != null ? i : children.get(name));
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
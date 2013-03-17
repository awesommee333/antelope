package antelope.tree;

public final class ForEach extends Construct {
    public final Variable[] vars;
    public final boolean inferred;
    public final Expression start, end, inc;
    
    public ForEach(Variable[] vars, boolean inferred, Expression start) {
        this.vars = vars; this.inferred = inferred; code = new Block(true);
        this.start = start; end = null; inc = null;
    }
    
    public ForEach(Variable[] vars, boolean inferred, Expression start, Block code) {
        this.vars = vars; this.inferred = inferred; this.code = code;
        this.start = start; end = null; inc = null;
    }
    
    public ForEach(Variable[] vars, boolean inferred, Expression start, Expression end, Expression inc) {
        this.vars = vars; this.inferred = inferred; code = new Block(true);
        this.start = start; this.end = end; this.inc = inc;
    }
    
    public ForEach(Variable[] vars, boolean inferred, Expression start, Expression end, Expression inc, Block code) {
        this.vars = vars; this.inferred = inferred; this.code = code;
        this.start = start; this.end = end; this.inc = inc;
    }
}
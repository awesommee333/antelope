package antelope;

public final class Assembly extends Command {
    public Expression[] values;
    
    public Assembly(Expression[] values) {
        this.values = values;
    }
}
package antelope;
import java.util.ArrayList;

public final class Init extends Global {
    public final Block code;
    
    public Init(Context context) {
        super(context, Token.INIT);
        code = new Block(true);
    }
    
    public Init(Context context, Block code) {
        super(context, Token.INIT);
        code.braces = true;
        this.code = code;
    }

    public Init(Context context, ArrayList<Statement> code) {
        super(context, Token.INIT);
        this.code = new Block(code, true);
    }
}
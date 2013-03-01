package antelope;
import java.util.ArrayList;

public final class Init extends Global {
    public final Block code;

    public Init(Context context,int line) {
        super(context, line, Token.INIT);
        code = new Block(true);
    }

    public Init(Context context, int line, Block code) {
        super(context, line, Token.INIT);
        code.braces = true;
        this.code = code;
    }

    public Init(Context context, int line, ArrayList<Statement> code) {
        super(context, line, Token.INIT);
        this.code = new Block(code, true);
    }
}
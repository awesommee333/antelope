package antelope.tree;
import antelope.*;

public abstract class Expression extends SyntaxTreeItem {
    public boolean interpret = false;

    public static Expression parse(SyntaxTreeParser parser) {
        line = parser.getLine();
        Expression e = parse(parser, Value.parse(parser), MAX_BINARY_PRECEDENCE, false);
        parser.push(operator);
        return e;
    }

    private static Token operator;
    private static int line;

    private static Expression parse(SyntaxTreeParser parser, Expression left, int precedence, boolean fromQuestion) {
        while(true) {
            operator = parser.nextToken(); // check precedence
            int nextPrec = operator.precedence();
            if(nextPrec < MIN_BINARY_PRECEDENCE || nextPrec > precedence || (operator == Token.COLON && !fromQuestion))
                return left;
            if(nextPrec < precedence)
                left = parse(parser, left, nextPrec, false);
            else if(operator == Token.QUESTION) {
                Expression right = parse(parser, left, nextPrec, true);
                if(!(right instanceof Operation) || ((Operation)right).kind != Token.COLON) {
                    parser.error("Missing colon (':') after conditional operator ('?')", line);
                    left = new Ternary(left, right, UNKNOWN);
                }
                else {
                    Operation colon = (Operation)right;
                    left = new Ternary(left, colon.left, colon.right);
                }
            }
            else
                left = new Operation(left, operator, Value.parse(parser));
        }
    }

    public static final Expression UNKNOWN = Value.UNKNOWN;
    public static final Expression[] ZERO = new Expression[0];

    public static final int TERNAY_PRECEDENCE = Token.QUESTION.precedence();
    public static final int MIN_BINARY_PRECEDENCE = Token.CAST.precedence();
    public static final int MAX_BINARY_PRECEDENCE = Token.QUESTION2.precedence();
}
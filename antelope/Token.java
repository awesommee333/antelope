package antelope;

public final class Token implements Comparable<Token> {
    public final String value; // String representation of Token
    public final int number;   // Numeric value (usage depends on type)
    private final short type;  // Type of token

    // Private Constructors; Use static methods instead
    private Token(short t, String v) { this(t,v,0); }
    private Token(short t, String v, int n) {
        type = t; value = v.intern(); number = n;
        if((t & ~T_VALUE) > T_ANNOTATION) {
            PRE_DEFINED.put(value, this);
        }
    }

    // Private values for Token.type (LSB is operator precedence)
    private static final short T_ANNOTATION   = 0x0000;
    private static final short T_ERROR        = 0x0100;
    private static final short T_COMMENT      = 0x0200;
    private static final short T_OPERATOR     = 0x1000;
    private static final short T_UNARY_OP     = 0x1100; // + - ! ~ * & $ ++ --
    private static final short T_PRIMARY_OP   = 0x1001; // . ( ) [ ] => (++ --)
    private static final short T_INCDEC_OP    = 0x1101; // ++ --
    private static final short T_SECONDARY_OP = 0x1102; // ! ~ $
    private static final short T_CAST_OP      = 0x1003; // ->
    private static final short T_MULTDIV_OP   = 0x1004; // / % (*)
    private static final short T_MULT_OP      = 0x1104; // *
    private static final short T_ADDSUB_OP    = 0x1105; // + -
    private static final short T_SHIFT_OP     = 0x1006; // << >>
    private static final short T_COMPARE_OP   = 0x1007; // < > <= >=
    private static final short T_EQUALITY_OP  = 0x1008; // == !=
    private static final short T_BIT_AND_OP   = 0x1109; // &
    private static final short T_BIT_XOR_OP   = 0x100A; // ^
    private static final short T_BIT_OR_OP    = 0x100B; // |
    private static final short T_AND_OP       = 0x100C; // &&
    private static final short T_OR_OP        = 0x100D; // ||
    private static final short T_TERNARY_OP   = 0x100E; // ? :
    private static final short T_BIN_COND_OP  = 0x100F; // ??
    private static final short T_ASSIGN_OP    = 0x1010; // = := *= += (etc.)
    private static final short T_COMMA_OP     = 0x1011; // ,
    private static final short T_IDENT        = 0x2000;
    private static final short T_KEYWORD      = 0x2100;
    private static final short T_PRIMITIVE    = 0x2300;
    private static final short T_VALUE        = 0x4000;
    private static final short T_STRING       = 0x4100;
    private static final short T_CHAR         = 0x4200;
    private static final short T_NUMBER       = 0x4400;
    private static final short T_BIN_NUMBER   = 0x4500;
    private static final short T_DEC_NUMBER   = 0x4600;
    private static final short T_HEX_NUMBER   = 0x4700;
    private static final short T_USER_IDENT   = T_IDENT | T_VALUE;
    private static final short T_KEY_VALUE    = T_KEYWORD | T_VALUE;

    // Keep a SINGLE entry for each token
    private static final java.util.HashMap<String,Token> USER_IDENTS
            = new java.util.HashMap<String,Token>();
    private static final java.util.HashMap<String,Token> PRE_DEFINED
            = new java.util.HashMap<String,Token>();

    // For use with formatting strings and parsing tokens:
    private static final String CTRL_CHARS = "\b\n\r\t\\\'\"\0";
    private static final String ESC_CHARS = "bnrt\\\'\"0";
    private static final String DOUBLE_OPS = "&|=+-><?";
    private static final String EQ_OPS = "<>!+-*%/&|^";

    // Predefined Tokens (sorted by Annotation, AssignOp, Operator, and Ident)
    public static final Token EOF      = new Token(T_ANNOTATION, "END_OF_FILE");
    public static final Token NEW_FILE = new Token(T_ANNOTATION, "NEW_FILE"   );
    public static final Token NEW_LINE = new Token(T_ANNOTATION, "NEW_LINE"   );
    public static final Token UNKNOWN  = new Token(T_ANNOTATION, "UNKNOWN"    );
    public static final Token ASSIGN    = new Token(T_ASSIGN_OP, "="  );
    public static final Token AND_EQ    = new Token(T_ASSIGN_OP, "&=" );
    public static final Token DEF_EQ    = new Token(T_ASSIGN_OP, ":=" );
    public static final Token DIV_EQ    = new Token(T_ASSIGN_OP, "/=" );
    public static final Token L_EQ      = new Token(T_ASSIGN_OP, "<<=");
    public static final Token MINUS_EQ  = new Token(T_ASSIGN_OP, "-=" );
    public static final Token MOD_EQ    = new Token(T_ASSIGN_OP, "%=" );
    public static final Token OR_EQ     = new Token(T_ASSIGN_OP, "|=" );
    public static final Token PLUS_EQ   = new Token(T_ASSIGN_OP, "+=" );
    public static final Token R_EQ      = new Token(T_ASSIGN_OP, ">>=");
    public static final Token TIMES_EQ  = new Token(T_ASSIGN_OP, "*=" );
    public static final Token XOR_EQ    = new Token(T_ASSIGN_OP, "^=" );
    public static final Token AMP       = new Token(T_BIT_AND_OP, "&" );
    public static final Token AND       = new Token(T_AND_OP,       "&&");
    public static final Token AT        = new Token(T_OPERATOR,     "@" );
    public static final Token BAR       = new Token(T_BIT_OR_OP,    "|" );
    public static final Token CAST      = new Token(T_CAST_OP,      "->");
    public static final Token COLON     = new Token(T_TERNARY_OP,   ":" );
    public static final Token COMMA     = new Token(T_COMMA_OP,     "," );
    public static final Token COMPL     = new Token(T_SECONDARY_OP, "~" );
    public static final Token DEC       = new Token(T_INCDEC_OP,    "--");
    public static final Token DOLLAR    = new Token(T_SECONDARY_OP, "$" );
    public static final Token DOT       = new Token(T_PRIMARY_OP,   "." );
    public static final Token EQ_TO     = new Token(T_EQUALITY_OP,  "==");
    public static final Token GREATER   = new Token(T_COMPARE_OP,   ">" );
    public static final Token GREATER_EQ= new Token(T_COMPARE_OP,   ">=");
    public static final Token INC       = new Token(T_INCDEC_OP,    "++");
    public static final Token L_BRACE   = new Token(T_OPERATOR,     "{" );
    public static final Token L_BRACKET = new Token(T_PRIMARY_OP,   "[" );
    public static final Token L_PAREN   = new Token(T_PRIMARY_OP,   "(" );
    public static final Token L_SHIFT   = new Token(T_SHIFT_OP,     "<<");
    public static final Token LESS      = new Token(T_COMPARE_OP,   "<" );
    public static final Token LESS_EQ   = new Token(T_COMPARE_OP,   "<=");
    public static final Token LAMBDA    = new Token(T_PRIMARY_OP,   "=>");
    public static final Token MINUS     = new Token(T_ADDSUB_OP,    "-" );
    public static final Token MOD       = new Token(T_MULTDIV_OP,   "%" );
    public static final Token NOT       = new Token(T_SECONDARY_OP, "!" );
    public static final Token NOT_EQ    = new Token(T_EQUALITY_OP,  "!=");
    public static final Token OR        = new Token(T_OR_OP,        "||");
    public static final Token PLUS      = new Token(T_ADDSUB_OP,    "+" );
    public static final Token POUND     = new Token(T_OPERATOR,     "#" );
    public static final Token QUESTION  = new Token(T_TERNARY_OP,   "?" );
    public static final Token QUESTION2 = new Token(T_BIN_COND_OP,  "??");
    public static final Token R_BRACE   = new Token(T_OPERATOR,     "}" );
    public static final Token R_BRACKET = new Token(T_PRIMARY_OP,   "]" );
    public static final Token R_PAREN   = new Token(T_PRIMARY_OP,   ")" );
    public static final Token R_SHIFT   = new Token(T_SHIFT_OP,     ">>");
    public static final Token SEMICOLON = new Token(T_OPERATOR,     ";" );
    public static final Token SLASH     = new Token(T_MULTDIV_OP,   "/" );
    public static final Token STAR      = new Token(T_MULT_OP,      "*" );
    public static final Token XOR       = new Token(T_BIT_XOR_OP,   "^" );
    public static final Token ASSEMBLY  = new Token(T_KEYWORD, "assembly" );
    public static final Token BREAK     = new Token(T_KEYWORD, "break"    );
    public static final Token CASE      = new Token(T_KEYWORD, "case"     );
    public static final Token CONST     = new Token(T_KEYWORD, "const"    );
    public static final Token CONTINUE  = new Token(T_KEYWORD, "continue" );
    public static final Token COFUNC    = new Token(T_KEYWORD, "cofunc"   );
    public static final Token DEFAULT   = new Token(T_KEYWORD, "default"  );
    public static final Token DELETE    = new Token(T_KEYWORD, "delete"   );
    public static final Token DO        = new Token(T_KEYWORD, "do"       );
    public static final Token ELSE      = new Token(T_KEYWORD, "else"     );
    public static final Token ENUM      = new Token(T_KEYWORD, "enum"     );
    public static final Token FOR       = new Token(T_KEYWORD, "for"      );
    public static final Token FUNC      = new Token(T_KEYWORD, "func"     );
    public static final Token IF        = new Token(T_KEYWORD, "if"       );
    public static final Token INIT      = new Token(T_KEYWORD, "init"     );
    public static final Token INTERFACE = new Token(T_KEYWORD, "interface");
    public static final Token NAMESPACE = new Token(T_KEYWORD, "namespace");
    public static final Token NEW       = new Token(T_KEYWORD, "new"      );
    public static final Token RETURN    = new Token(T_KEYWORD, "return"   );
    public static final Token SIZEOF    = new Token(T_KEYWORD, "sizeof"   );
    public static final Token STRUCT    = new Token(T_KEYWORD, "struct"   );
    public static final Token SWITCH    = new Token(T_KEYWORD, "switch"   );
    public static final Token UNTIL     = new Token(T_KEYWORD, "until"    );
    public static final Token USING     = new Token(T_KEYWORD, "using"    );
    public static final Token VOLITALE  = new Token(T_KEYWORD, "volitale" );
    public static final Token WHILE     = new Token(T_KEYWORD, "while"    );
    public static final Token YIELD     = new Token(T_KEYWORD, "yield"    );
    public static final Token BYTE   = new Token(T_PRIMITIVE, "byte" );
    public static final Token WORD   = new Token(T_PRIMITIVE, "word" );
    public static final Token CHAR   = new Token(T_PRIMITIVE, "char" );
    public static final Token BOOL   = new Token(T_PRIMITIVE, "bool" );
    public static final Token UBYTE  = new Token(T_PRIMITIVE, "ubyte");
    public static final Token UWORD  = new Token(T_PRIMITIVE, "uword");
    public static final Token FALSE  = new Token(T_KEY_VALUE, "false");
    public static final Token NULL   = new Token(T_KEY_VALUE, "null" );
    public static final Token THIS   = new Token(T_KEY_VALUE, "this" );
    public static final Token TRUE   = new Token(T_KEY_VALUE, "true" );
    public static final Token ALLOCATE = new Token(T_USER_IDENT, "allocate");
    public static final Token ELIF     = new Token(T_USER_IDENT, "elif"    );
    public static final Token DEFINE   = new Token(T_USER_IDENT, "define"  );
    public static final Token ENDIF    = new Token(T_USER_IDENT, "endif"   );
    public static final Token ERROR    = new Token(T_USER_IDENT, "error"   );
    public static final Token INCLUDE  = new Token(T_USER_IDENT, "include" );
    public static final Token UNDEF    = new Token(T_USER_IDENT, "undef"   );

    // Instance methods

    private boolean typeIsIn(short t) { return (type & t) == t;  }
    public boolean isAnnotation() { return type <  0x1000;       }
    public boolean isAssignment() { return type == T_ASSIGN_OP;  }
    public boolean isBinNumber( ) { return type == T_BIN_NUMBER; }
    public boolean isChar(      ) { return type == T_CHAR;       }
    public boolean isComment(   ) { return type == T_COMMENT;    }
    public boolean isDecNumber( ) { return type == T_DEC_NUMBER; }
    public boolean isEOF(       ) { return this == EOF;          }
    public boolean isError(     ) { return type == T_ERROR;      }
    public boolean isHexNumber( ) { return type == T_HEX_NUMBER; }
    public boolean isIdent(     ) { return typeIsIn(T_IDENT);    }
    public boolean isKeyword(   ) { return typeIsIn(T_KEYWORD);  }
    public boolean isNewLine(   ) { return this == NEW_LINE;     }
    public boolean isNumber(    ) { return typeIsIn(T_NUMBER);   }
    public boolean isOperator(  ) { return typeIsIn(T_OPERATOR); }
    public boolean isPrimitive( ) { return type == T_PRIMITIVE;  }
    public boolean isString(    ) { return type == T_STRING;     }
    public boolean isUnaryOp(   ) { return type == T_UNARY_OP;   }
    public boolean isUserIdent( ) { return type == T_USER_IDENT; }
    public boolean isValue(     ) { return typeIsIn(T_VALUE);    }
    public int precedence(      ) { return (type & 0x00FF);      }
    public int unaryPrecedence( )
        { return typeIsIn(T_UNARY_OP) ? 2 : precedence(); }

    public int compareTo(Token t) {
        if(this == t) { return 0; } // This is the most common "==" case.
        if(type != t.type) { return (type < t.type)? -1 : 1; }
        if(number != t.number) { return (number < t.number)? -1 : 1; }
        return value.compareTo(t.value);
    }

    @Override
    public String toString() { return value; }

    @Override
    public boolean equals(Object o) {
        if(this == o) { return true; } // Most common case.
        if(o instanceof Token) {
           Token t = (Token)o;
           return (t.type == type) && (t.number == number)
               && (t.value.equals(value));
        }
        return false;
    }

    public String format() { return format(true); }
    public String format(boolean allowCtrlChars) { // returns null if not a legally formatted char or string
        if(type != T_CHAR && type != T_STRING) { return null; }
        StringBuilder str = new StringBuilder();
        for(int i=1; i+1 < value.length(); i++) {
            char c = value.charAt(i);
            if(c == '\\') {
                i++;
                if(!allowCtrlChars) { return null; }
                c = CTRL_CHARS.charAt(ESC_CHARS.indexOf(value.charAt(i)));
            } str.append(c);
        }
        return str.toString();
    }

    // Static Token making methods

    public static Token makeError(String message)
        { return new Token(T_ERROR, message); }

    public static Token makeComment(String comment)
        { return new Token(T_COMMENT, comment); }

    public static Token makeChar(char ch)
        { return makeChar(Character.toString(ch)); }

    public static Token makeBinNum(int num)
        { return new Token(T_BIN_NUMBER, "0b"+Integer.toString(num,2), num); }

    public static Token makeHexNum(int num)
        { return new Token(T_HEX_NUMBER, "0x"+Integer.toString(num,16), num); }

    public static Token makeNumber(int num)
        { return new Token(T_DEC_NUMBER, Integer.toString(num), num); }

    public static Token makeIdent(String ident) {
        for(int i = 0; i < ident.length(); i++) {
            if(!isIdentChar(ident.charAt(i))) {
                return new Token(T_ERROR, "Invalid identifier: "+ident);
            }
        }
        return getIdentToken(ident);
    }

    public static Token makeNumber(String num) {
        try {
            if(num.length() > 1 && num.charAt(0) == '0') {
                char ch = num.charAt(1);
                if(ch == 'x' || ch == 'h') { return new Token(T_HEX_NUMBER, num, Integer.parseInt(num.substring(2), 16)); }
                if(ch == 'b') { return new Token(T_BIN_NUMBER, num, Integer.parseInt(num.substring(2), 2)); }
                if(ch == 'd') { return new Token(T_DEC_NUMBER, num, Integer.parseInt(num.substring(2))); }
            }
            return new Token(T_DEC_NUMBER, num, Integer.parseInt(num));
        }
        catch(NumberFormatException nfe) {
            return new Token(T_ERROR, nfe.getMessage());
        }
    }

    public static Token makeChar(String ch) {
        if(!isValidChar(ch))
            return new Token(T_ERROR, "Invalid character format: \'"+ch+'\'');
        return new Token(T_CHAR, "\'"+ch+'\'');
    }

    public static Token makeString(String str) {
        if(!isValidString(str))
            return new Token(T_ERROR, "Invalid string format: \""+str+'\"');
        return new Token(T_STRING, "\""+str+'\"');
    }

    // Static Token parsing methods

    public static boolean isIdentChar(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }

    public static Token nextToken(StringBuilder source) {
        int idx, len = source.length();

        // Remove whitespace
        for(idx = 0; idx < len && Character.isWhitespace(source.charAt(idx)); idx++) {
            char ch = source.charAt(idx);
            if(ch == '\r' || ch == '\n') {
                source.delete(0,idx+1);
                return NEW_LINE;
            }
        }

        // Remove comments
        if(idx < len-1 && source.charAt(idx) == '/') {
            if(source.charAt(idx+1) == '/') {
                int i = idx+2;
                char ch = source.charAt(i++);
                while(i < len && ch != '\r' && ch != '\n') {
                    ch = source.charAt(i++);
                }
                if(i < len) { i--; }
                String value = source.substring(idx,i);
                source.delete(idx,i);
                return makeComment(value);
            }
            if(source.charAt(idx+1) == '*') {
                int i = idx+2;
                char last = 0, ch = source.charAt(i++);
                while(i < len && (last != '*' || ch != '/')) {
                    last = ch; ch = source.charAt(i++);
                }
                String value = source.substring(idx,i);
                source.delete(idx,i);
                return makeComment(value);
            }
        }

        // Signal EOF (end of file)
        if(idx >= len) {
            source.delete(0,idx);
            return EOF;
        }

        int begin = idx;
        char start = source.charAt(idx);

        // Handle keywords and directives
        if(isIdentChar(start)) {
            while(idx < len && isIdentChar(source.charAt(idx))) { idx++; }
            String value = source.substring(begin, idx);
            source.delete(0,idx);
            Token t = PRE_DEFINED.get(value);
            if(t != null) { return t; }
            if(Character.isDigit(value.charAt(0))) { return makeNumber(value); }
            return getIdentToken(value);
        }

        // Handle string and character literals
        if(start == '\"' || start == '\'') {
            while(++idx < len) {
                char ch = source.charAt(idx);
                if(ch == '\\') { idx++; continue; }
                if(ch == '\r' || ch == '\n') { break; }
                if(ch == start) {
                    String value = source.substring(begin+1, idx);
                    source.delete(0, idx+1);
                    return (start == '\"' ? makeString(value) : makeChar(value));
                }
            }
            String value = source.substring(begin, idx-1);
            source.delete(0,idx);
            return makeError("Missing closing quote on literal: "+start+value);
        }

        // Handle operators
        char next = (idx < len-1)? source.charAt(idx+1) : 0;
        if(start == next && DOUBLE_OPS.indexOf(start) >= 0) {
            idx++;
            if((start == '>' || start == '<') && source.charAt(idx+1) == '=') { idx++; }
        }
        else if((next == '=' && EQ_OPS.indexOf(start) >= 0) || (next == '>' && (start == '=' || start == '-'))) {
            idx++;
        }

        String value = source.substring(begin, idx+1);
        source.delete(0,idx+1);
        Token t = PRE_DEFINED.get(value);
        if(t != null) { return t; }
        return new Token(T_OPERATOR, value);
    }

    private static Token getIdentToken(String value) {
        Token t = USER_IDENTS.get(value);
        if(t == null) {
            t = new Token(T_USER_IDENT, value);
            USER_IDENTS.put(value,t);
        }
        return t;
    }

    private static boolean isValidString(String str) {
        boolean last = false;
        for(int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if(last && ESC_CHARS.indexOf(ch) < 0) { return false; }
            if(Character.isWhitespace(ch) && ch != ' ' && ch != '\t') { return false; }
            last = (!last && ch == '\\');
        }
        return !last;
    }

    private static boolean isValidChar(String str) {
        int len = str.length();
        if(len < 1) { return false; }
        char ch = str.charAt(0);
        if(ch == '\\') { return (len == 2 && ESC_CHARS.indexOf(str.charAt(1)) >= 0); }
        return (len == 1 && (!Character.isWhitespace(ch) || ch == '\t' || ch == ' '));
    }
}
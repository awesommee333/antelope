package antelope;

public interface TokenSource {
    public Token nextToken();
    public String getName();
    public int getLine();

    public static final class Default implements TokenSource {
        private int line;
        public final String name;
        public final StringBuilder source;
        public final ErrorHandler handler;
        public int getLine() { return line; }
        public String getName() { return name; }
        public Default(StringBuilder source, String name, ErrorHandler handler) {
            this.source = source; this.name = name;
            this.handler = handler; line = 1;
        }
        public Default(String file, ErrorHandler handler) throws java.io.IOException {
            this(new java.io.BufferedReader(new java.io.FileReader(file)), file, handler);
        }
        public Default(java.io.Reader in, String name, ErrorHandler handler) throws java.io.IOException {
            javax.swing.JTextArea jta = new javax.swing.JTextArea(); jta.read(in, name);
            source = new StringBuilder(jta.getText()); this.name = name;
            this.handler = handler; line = 1;
        }
        public Token nextToken() {
            Token t;
            while(true) {
                t = Token.nextToken(source);
                if(t.isError()) { handler.handle(name, line, t.value); }
                else if(t.isNewLine()) { line++; }
                else if(!t.isComment()) { break; }
            }
            return t;
        }
    }
}
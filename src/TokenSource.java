package antelope;

public interface TokenSource {
    public void clearLine();
    public Token nextToken();
    public String getName();
    public int getLine();
    
    public static final class Default implements TokenSource {
        private int line;
        private Token next;
        public final String name;
        public final StringBuilder source;
        public final ErrorHandler handler;
        public int getLine() { return line; }
        public String getName() { return name; }
        public Default(StringBuilder source, String name, ErrorHandler handler) {
            this.source = source; this.name = name;
            this.handler = handler; line = 1;
            next = Token.nextToken();
        }
        public Token nextToken() {
            do {
                if(next.isError()) { handler.handle(name, line, next.value); }
                else if(next.isNewLine()) { line++; }
                else if(!next.isComment()) { break; }
                next = Token.nextToken(source);
            } while(true);
            return next;
        }
        public void clearLine() {
            while(!next.isEOF() && !next.isNewLine()) {
                next = Token.nextToken(source);
            }
        }
        public static Default Get(String file, ErrorHandler handler) {
            javax.swing.JTextArea jta = new javax.swing.JTextArea();
            try { jta.read(new java.io.BufferedReader(new java.io.FileReader(file)), file); }
            catch(java.io.IOException ioe) { return null; }
            return new Default(new StringBuilder(jta.getText()), file, handler);
        }
    }
}
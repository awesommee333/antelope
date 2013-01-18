package antelope;

public interface TokenSource {
    public Token nextToken();
    public String getName();
    public int getLine();
    
    public static class Default implements TokenSource {
        protected int line;
        public final String name;
        public final StringBuilder source;
        public final ErrorHandler handler;
        public int getLine() { return line; }
        public String getName() { return name; }
        public Default(StringBuilder source, String name, ErrorHandler handler) {
            this.source = source; this.name = name;
            this.handler = handler; line = 1;
        }
        public Token nextToken() {
            Token t;
            do {
                t = Token.nextToken(source);
                if(t.isError()) { handler.handle(name, line, t.value); }
                else if(t.isNewLine()) { line++; }
                else if(!t.isComment()) { break; }
            } while(true);
            return t;
        }
        public static Default Get(String file, ErrorHandler handler) {
            javax.swing.JTextArea jta = new javax.swing.JTextArea();
            try { jta.read(new java.io.BufferedReader(new java.io.FileReader(file)), file); }
            catch(java.io.IOException ioe) { return null; }
            return new Default(new StringBuilder(jta.getText()), file, handler);
        }
    }
}
package antelope;
import java.io.*;

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

        public Default(String file, ErrorHandler handler) {
            this.handler = handler; line = 1;
            name = new File(file).getName();
            StringBuilder src;
            try {
                javax.swing.JTextArea jta = new javax.swing.JTextArea();
                jta.read(new FileReader(file), name);
                src = new StringBuilder(jta.getText());
            }
            catch(FileNotFoundException fnfe) {
                handler.handle(null, 0, "File not found: "+name);
                src = new StringBuilder();
            }
            catch(IOException ioe) {
                handler.handle(null, 0, "Unable to read from "+name);
                src = new StringBuilder();
            }
            source = src;
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

    public static final class Empty implements TokenSource {
        public static final Empty Instance = new Empty();
        public String getName() { return "<NONEXISTENT_FILE>"; }
        public Token nextToken() { return Token.EOF; }
        public int getLine() { return 1; }
        private Empty() { }
    }
}
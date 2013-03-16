package antelope;
import java.util.TreeSet;
import java.util.LinkedList;
import java.io.PrintStream;

public interface ErrorHandler {
    public void error(String file, int line, String message);

    public static final class Default implements ErrorHandler {
        public final PrintStream printStream;
        private Default() { printStream = System.out; }
        public Default(PrintStream printStream)
            { this.printStream = printStream; }
        public void error(String file, int line, String message) {
            if(file != null && file.length() > 0) {
                printStream.println(file+':'+line+": "+message);
            } else { printStream.println(message); }
        }
        public static final Default INSTANCE = new Default();
    }

    public static final class Empty implements ErrorHandler {
        private Empty() { }
        public void error(String file, int line, String message) { }
        public static final Empty INSTANCE = new Empty();
    }

    public static final class Buffer implements ErrorHandler {
        public final LinkedList<Message> messages;
        public Buffer() { messages = new LinkedList<Message>(); }
        public void error(String file, int line, String message)
            { messages.add(new Message(file, line, message)); }
        public void flush() { flush(Default.INSTANCE); }
        public void flush(ErrorHandler handler) {
            for(Message m : messages) {
                handler.error(m.file, m.line, m.message);
            }
        }
    }

    public static final class Sorter implements ErrorHandler {
        public final TreeSet<Message> messages;
        public Sorter() { messages = new TreeSet<Message>(); }
        public void error(String file, int line, String message)
            { messages.add(new Message(file, line, message)); }
        public void flush() { flush(Default.INSTANCE); }
        public void flush(ErrorHandler handler) {
            for(Message m : messages) {
                handler.error(m.file, m.line, m.message);
            }
        }
    }

    public static final class Message implements Comparable<Message> {
        private static int ID = 0;
        public final String file, message;
        public final int line, id;

        public Message(String file, int line, String message)
            { this.file = file; this.line = line; this.message = message; id = ID++; }

        public int compareTo(Message other) {
            int value = 0;
            boolean ofn = (other.file == null);
            if(file == null) { if(!ofn) return -1; }
            else if(ofn) { return 1; }
            else { value = file.compareTo(other.file); }
            return (value != 0)? value :
                (line != other.line)? (line < other.line ? -1 : 1) :
                (message.equals(other.message))? 0 :
                (id < other.id ? -1 : 1);
        }
    }
}
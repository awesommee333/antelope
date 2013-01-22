package antelope;

public interface ErrorHandler {
    public void handle(String file, int line, String message);

    public static class Default implements ErrorHandler {
        public java.io.PrintStream printStream;
        public Default() { printStream = System.out; }
        public Default(java.io.PrintStream printStream) {
            this.printStream = printStream;
        }
        public void handle(String file, int line, String message) {
            if(file != null && file.length() > 0) {
                printStream.println(file+':'+line+": "+message);
            } else { printStream.println(message); }
        }
    }

    public static class Sorter implements ErrorHandler {
        public final java.util.TreeSet<Message> messages;
        public Sorter() { messages = new java.util.TreeSet<Message>(); }
        public void handle(String file, int line, String message) {
            messages.add(new Message(file, line, message));
        }
        public void flush() { flush(new Default()); }
        public void flush(ErrorHandler handler) {
            for(Message m : messages) {
                handler.handle(m.file, m.line, m.message);
            }
        }
    }

    public static class Message implements Comparable<Message> {
        private static int ID = 0;
        public final String file, message;
        public final int line, id;
        public Message(String file, int line, String message) {
            this.file = file; this.line = line; this.message = message; id = ID++;
        }
        public int compareTo(Message other) {
            int value = file.compareTo(other.file);
            return (value != 0)? value :
                (line != other.line)? (line < other.line ? -1 : 1) :
                (id < other.id ? -1 : 1);
        }
    }
}
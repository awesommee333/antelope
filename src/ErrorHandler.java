package antelope;

public interface ErrorHandler {
    public void handle(String file, int line, String message);
    
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

    public static class Printer implements ErrorHandler {
        protected java.io.PrintStream printStream;
        public Printer(java.io.PrintStream printStream) {
            this.printStream = printStream;
        }
        public void handle(String file, int line, String message) {
            printStream.println(file+": "+line+": "+message);
        }
    }

    public static class Sorter implements ErrorHandler {
        public final java.util.TreeSet<Message> messages;
        public Sorter() { messages = new java.util.TreeSet<Message>(); }
        public void handle(String file, int line, String message) {
            messages.add(new Message(file, line, message));
        }
    }
}
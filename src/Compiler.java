package antelope;

public class Compiler {
    public static void main(String[] args) {
        int errors = 0;
        boolean showTokens = true;  // TOGGLE THESE FOR DEBUGGING OUTPUT
        boolean showErrors = true;  // TOGGLE THESE FOR DEBUGGING OUTPUT
        java.util.LinkedList<Token> src = Preprocessor.process("Test.antler", null);

        for(Token t : src) {
            if(t.isError()) {
                errors++;
                if(showErrors) { System.out.println(t); }
            } else if(showTokens) { System.out.println(t); }
        }

        System.out.println("\nFinished with "+errors+" errors.");
    }
}
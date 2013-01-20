package antelope;

public class Compiler {
    public static void main(String[] args) {
        boolean showTokens = true;  // TOGGLE THESE FOR DEBUGGING OUTPUT
        boolean showErrors = true;  // TOGGLE THESE FOR DEBUGGING OUTPUT
        ErrorHandler.Sorter errors = new ErrorHandler.Sorter();
        Preprocessor src = new Preprocessor("Test.antler", errors);

        int prevLine = 1;
        Token t = src.nextToken();
        while(!t.isEOF()) {
            if(showTokens) {
                int line = src.getLine();
                if(line != prevLine) {
                    System.out.println();
                    for(int l = line; l < 1000; l *= 10)
                        System.out.print(' ');
                    System.out.print(line);
                    System.out.print(": ");
                }
                System.out.print(t);
                prevLine = line;
            }
            t = src.nextToken();
        }

        if(showErrors) {
            errors.flush();
        }
    }
}
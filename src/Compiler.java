package antelope;

public class Compiler {
    public static void main(String[] args) {
        boolean showTokens = true;  // TOGGLE THESE FOR DEBUGGING OUTPUT
        boolean showErrors = true;  // TOGGLE THESE FOR DEBUGGING OUTPUT
        String directory = "C:\\Users\\Dan\\Documents\\JCreator LE\\MyProjects\\Antelope\\src";
        String source = "Compiler.java";
        ErrorHandler.Sorter errors = new ErrorHandler.Sorter();

        try {
            java.util.LinkedList<String> dirs = new java.util.LinkedList<String>();
            dirs.add(directory);
            Preprocessor src = new Preprocessor(source, dirs, errors);
            src.directories.add(directory);

            int prevLine = 0;
            Token t = src.nextToken();
            while(!t.isEOF()) {
                if(showTokens) {
                    int line = src.getLine();
                    if(line != prevLine) {
                        System.out.println();
                        for(int l = line; l < 1000; l *= 10)
                            System.out.print('0');
                        System.out.print(line);
                        System.out.print(':');
                    }
                    System.out.print(' ');
                    System.out.print(t);
                    prevLine = line;
                }
                t = src.nextToken();
            }
        }
        catch(java.io.IOException ioe) {
            errors.handle(null,1,"Unable to read from \""+source+'\"');
        }

        if(showErrors) {
            errors.flush();
        }
    }
}
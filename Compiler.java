package antelope;
import java.util.LinkedList;
import java.util.HashSet;

public final class Compiler {
    private static void showUsage() {
        System.out.println("\nUSAGE: Antelope [Options] [Files]");
        System.out.println("  Options:");
        System.out.println("    -d value1;value2;...   Define values as with #define.");
        System.out.println("    -l path1;path2;...     Path(s) of library directory(s).");
        System.out.println("  Files:");
        System.out.println("    FILE1 FILE2 FILE3...   Path(s) of file(s) to compile.\n");
        System.exit(0);
    }

    public static void main(String[] args) {
        LinkedList<String> directories = new LinkedList<String>();
        LinkedList<String> sources = new LinkedList<String>();
        LinkedList<Token> defined = new LinkedList<Token>();
        char last = 0;
        for(String arg : args) {
            if(arg.length() < 1)
                continue;
            if(arg.charAt(0) == '-') {
                if(arg.length() != 2) { showUsage(); }
                last = Character.toLowerCase(arg.charAt(1));
                if(last != 'd' && last != 'l') { showUsage(); }
            }
            else if(last == 0) {
                sources.add(arg);
            }
            else {
                for(String value : arg.split(";")) {
                    if(value.length() > 0) {
                        if(last == 'd') {
                            try { defined.add(Token.makeIdent(value)); }
                            catch(Exception e) { showUsage(); }
                        }
                        else { directories.add(value); }
                    }
                }
                last = 0;
            }
        }

        if(sources.size() < 1) { showUsage(); }

        ErrorHandler.Sorter errors = new ErrorHandler.Sorter();

        Preprocessor src = new Preprocessor(errors, directories.toArray(new String[0]), sources.toArray(new String[sources.size()]));
        for(Token def : defined) { src.define(def); }

        int prevLine = 0;
        boolean wasEOF = false;
        while(true) {
            Token t = src.nextToken();
            if(wasEOF && t.isEOF()) break;
            wasEOF = t.isEOF();
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

        Token[] assemblies = src.getAssemblies();
        Token[] allocations = src.getAllocations();

        if(allocations.length > 0) {
            System.out.println("\nALLOCATIONS:");
            for(Token tok : allocations) System.out.print(" "+tok);
        }

        if(assemblies.length > 0) {
            System.out.println("\nASSEMBLIES:");
            for(Token tok : assemblies) System.out.print(" "+tok);
        }

        System.out.println();
        errors.flush();
    }
}
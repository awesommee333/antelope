package antelope;
import java.util.LinkedList;

public class Preprocessor {
    // THIS IS THE ONLY PUBLIC ITEM:
    public static LinkedList<Token> process(String file, Token environment) {
        StringBuilder src = getSource(file);

        if(src == null) {
            LinkedList<Token> err = new LinkedList<Token>();
            err.add(Token.makeError("Unable to process file: "+file));
            return err;
        }

        Preprocessor t = new Preprocessor(src, file, environment, false);
        t.tokens.add(Token.EOF); // tokens + EOS
        t.assemblies.consume(t.tokens); // assemblies + (tokens + EOS)
        t.allocations.consume(t.assemblies); // allocations + (assemblies + tokens + EOS)
        t.errors.consume(t.allocations); // errors + (allocations + assemblies + tokens + EOS)
        return t.errors; // In order: errors, allocations, assemblies, tokens, EOS
    }

    private final String file;
    private final LinkedList<Token> allocations;
    private final LinkedList<Token> assemblies;
    private final LinkedList<Token> errors;
    private final LinkedList<Token> tokens;
    private boolean legalEnvironment;
    private boolean environmentUsed;
    private StringBuilder source;
    private int line, prevLine;
    private Token environment;
    private Token pushBack;

    private Preprocessor() { throw new Error("Do not use this constructor!"); }
    private Preprocessor(StringBuilder src, String file, Token env, boolean envUsed) {
        source = getSource(file);
        allocations = new LinkedList<Token>();
        assemblies = new LinkedList<Token>();
        errors = new LinkedList<Token>();
        tokens = new LinkedList<Token>();
        environmentUsed = envUsed;
        legalEnvironment = true;
        environment = env;
        this.file = file;
        pushBack = null;
        source = src;
        prevLine = 0;
        line = 1;

        if(tokenize(false) == Token.DIR_END) {
            error("Mismatched #end directive");
        }
    }

    private static StringBuilder getSource(String file) {
        javax.swing.JTextArea jta = new javax.swing.JTextArea();
        try { jta.read(new java.io.BufferedReader(new java.io.FileReader(file)), file); }
        catch(java.io.IOException ioe) { return null; }
        return new StringBuilder(jta.getText());
    }

    private Token tokenize(boolean inEnv) { // returns terminating Token
        Token t;
        for(t = nextToken(); t != Token.EOS && t != Token.DIR_END; t = nextToken()) {
            int l = line;
            if(t == Token.ENVIRONMENT) {
                if(nextLineToken() != Token.ASSIGN) { error("Missing '=' after 'environment'"); }
                else {
                    Token e = nextToken();
                    if(e != null && (e.isIdent() || e.isKeyword())) {
                        if(nextToken() != Token.SEMI) { error("Missing semicolon after environment", l); }
                        if(environment != null && environment != e) {
                            error("Duplicate environment selection (already set to '"+environment+"')");
                        } else { environment = e; }
                    } else { error("Identifier expected after 'environment ='. Found: "+e); }
                }
            }
            else if(t.isDirective()) {
                Token next = nextLineToken();
                if(next == null) {
                    error("Missing arguments after "+t);
                    if(t != Token.DIR_ENVIRONMENT && t != Token.DIR_FOR) {
                        continue;
                    }
                }

                boolean lineErr = true;

                if(t == Token.DIR_INCLUDE) {
                    String incFile = next.format(false);
                    if(incFile == null) { error("Invalid file name: "+next); }
                    else if(legalEnvironment) {
                        StringBuilder src = getSource(incFile);
                        if(src == null) { error("Unable to process file: "+incFile); }
                        else {
                            Preprocessor p = new Preprocessor(src, incFile, environment, environmentUsed);
                            if(p.tokens.size()+p.allocations.size()+p.assemblies.size() > 0) {
                                allocations.consume(p.allocations);
                                assemblies.consume(p.assemblies);
                                errors.consume(p.errors);
                                tokens.consume(p.tokens);
                                prevLine = 0; // triggers a fileMarker
                            }
                        }
                    }
                }
                else if(t == Token.DIR_FOR || t == Token.DIR_ENVIRONMENT) {
                    boolean isEnv = (t == Token.DIR_ENVIRONMENT);
                    boolean pass = false;
                    do {
                        pass = (pass || next == environment);
                        if(!next.isIdent() && !next.isKeyword()) {
                            error("Identifier expected after "+t+". Found: "+next);
                        } else if(t == Token.DIR_ENVIRONMENT) { break; }
                        next = nextLineToken();
                    } while(next != null);

                    boolean wasLegal = legalEnvironment;
                    legalEnvironment = (pass && legalEnvironment && !inEnv);
                    if(isEnv && pass) {
                        if(environmentUsed)
                            error("Duplicate environment definition for "+environment, l);
                        environmentUsed = true;
                    }
                    if(tokenize(isEnv || inEnv) != Token.DIR_END) {
                        error("Missing #end to match "+t+" on line "+l);
                    } else { t = Token.DIR_END; }
                    legalEnvironment = wasLegal;
                }
                else { // By default, t is #allocate or #assembly
                    boolean isAlloc = (t == Token.DIR_ALLOCATE);
                    if(!inEnv) {
                        error("Illegal use of "+t+" without #environment");
                        lineErr = false;
                    } else if(!next.isString()) {
                        error("String value expected after "+t+". Found: "+next);
                        lineErr = false;
                    } else {
                        Token n = ((isAlloc)? nextLineToken() : null);
                        boolean minus = (n == Token.MINUS);
                        if(minus) { n = nextLineToken(); }

                        if(isAlloc && (n == null || !n.isNumber())) {
                            error("Number expected after #allocate "+next);
                            lineErr = false;
                        } else if(legalEnvironment) {
                            allocations.add(t);
                            allocations.add(next);
                            if(minus) { allocations.add(Token.makeNumber(-n.number)); }
                            else if(isAlloc) { allocations.add(n); }
                        }
                    }
                }

                // Trim the rest of the line:

                Token n = nextLineToken();
                if(n != null) {
                    StringBuilder err = new StringBuilder("Excess code found after "+t+":");
                    while(n != null) { err.append(" "+n); n = nextLineToken(); }
                    if(lineErr) { error(err.toString(), l); }
                }
            }
            else if(legalEnvironment) {
                if(line != prevLine) {
                    prevLine = line;
                    tokens.add(Token.makeFileMarker(file,line));
                }
                tokens.add(t);
            }
        }
        return t;
    }

    private Token nextToken() { // Get next non-line, non-comment, non-error token
        Token t;
        if(pushBack != null) { t = pushBack; pushBack = null; return t; }
        do { // count lines, dump comments
            t = Token.parseNext(source);
            if(t == Token.NEW_LINE) { line++; }
            if(t.isError()) { error(file+":"+line+":"+t.value); }
        } while(t == Token.NEW_LINE || t.isComment() || t.isError());
        return t;
    }

    private Token nextLineToken() { // Get next token on same line (null if none)
        if(pushBack != null) { return null; }
        int prev = line;
        Token t = nextToken();
        if(line == prev && t != Token.EOS) { return t; }
        pushBack = t; return null;
    }

    private void error(String message) { error(message,line); }
    private void error(String message, int l) {
        errors.add(Token.makeError(file+":"+l+": "+message));
    }
}
package antelope;
import java.util.LinkedList;

public final class Preprocessor implements TokenSource {
    private final java.util.TreeSet<Token> defined;
    private final LinkedList<Token> allocations;
    private final LinkedList<Token> assemblies;
    private final TokenSource source;
    private int lastLine;
    
    public Preprocessor(TokenSource source) {
        this.source = source;
        lastLine = getLine() - 1;
        assemblies = new LinkeList<Token>();
        allocations = new LinkeList<Token>();
    }
    
    public Preprocessor(String file, ErrorHandler handler) {
        super(new TokenSource.Default.Get(file, handler));
    }
    
    public int getLine() { return source.getLine(); }
    public String getName() { return source.getName(); }
    
    private void error(String message) {
        Token t;
        int line = getLine();
        do { Token t = source.nextToken(); }
        while(line == getLine() && !t.isEOF());
        handler.handle(getName(), line, message);
    }

    public Token nextToken() {
        Token t = source.nextToken();
        if(t != Token.POUND) return t;
        t = source.nextToken();
        int line = getLine();
        
        if(t == Token.DEFINE) {
            do {
                t = source.nextToken();
                if(t.isIdentifier()) { defined.add(t); }
                else { error("Identifier expected after #define. Found: "+t); }
            } while(line == getLine());
        }
        
        
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
}
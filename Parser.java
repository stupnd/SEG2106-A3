import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

    private BufferedReader reader;
    private String currentToken;

    public Parser(BufferedReader reader) {
        this.reader = reader;
        this.currentToken = null;
    }

    private String getNextToken() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean parseProgram() {
        if (match("{")) {
            advance();
            if (parseStatementList()) {
                if (match("}")) {
                    advance();
                    return match("$");
                }
            }
        }
        return false;
    }

    private boolean parseStatementList() {
        if (parseStatement()) {
            if (match("id") && match(";")) {
                advance();
                return parseStatementList();
            }
            if (match(";")) {
                advance();
                return parseStatementList();
            }
            return match("}");
        }
        return false;
    }

    private boolean parseStatement() {
        if (match("call")) {
            advance();
            if (match(":")) {
                advance();
                return parseProcedureCall();
            }
        } else if (match("compute")) {
            advance();
            if (match(":")) {
                advance();
                return parseExpression();
            }
        }
        return false;
    }

    private boolean parseProcedureCall() {
        if (match("id")) {
            advance();
            if (match("(")) {
                advance();
                if (parseParameters()) {
                    advance();
                    return match(")");
                } else if (match("}")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean parseParameters() {
        if (match("num") || match("id")) {
            advance();
            return parseParametersPrime();
        }
        return false;
    }

    private boolean parseParametersPrime() {
        if (match(",")) {
            advance();
            return parseParameters();
        } else if (match(")")) {
            advance();
            return parseParameters();
        }
        return match(";");
    }

    private boolean parseExpression() {
        if (match("id")) {
            advance();
            if (match("=")) {
                advance();
                if (match("num") || match("id")) {
                    advance();
                    return parseFactorPrime();
                }
            }
        }
        return false;
    }

    private boolean parseFactorPrime() {
        if (match("+") || match("-")) {
            advance();
            return parseFactor();
        }
        return match(";");
    }

    private boolean parseFactor() {
        return match("num") || match("id");
    }

    private void advance() {
        currentToken = getNextToken();
    }

    private boolean match(String expectedToken) {
        return currentToken != null && currentToken.equals(expectedToken);
    }

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[0]));
            Parser parser = new Parser(reader);
            parser.advance();
            if (parser.parseProgram()) {
                System.out.println("SUCCESS");
            } else {
                System.out.println("ERROR");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

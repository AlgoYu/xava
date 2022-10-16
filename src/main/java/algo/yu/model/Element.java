package algo.yu.model;

public class Element {
    private int line;
    private Token token;
    private String value;

    public Element(int line, Token token, String value) {
        this.line = line;
        this.token = token;
        this.value = value;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

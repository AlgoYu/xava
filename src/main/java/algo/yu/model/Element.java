package algo.yu.model;

import algo.yu.enums.TokenEnum;

public class Element {
    private int line;
    private TokenEnum tokenEnum;
    private String value;

    public Element(int line, TokenEnum tokenEnum, String value) {
        this.line = line;
        this.tokenEnum = tokenEnum;
        this.value = value;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public TokenEnum getToken() {
        return tokenEnum;
    }

    public void setToken(TokenEnum tokenEnum) {
        this.tokenEnum = tokenEnum;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

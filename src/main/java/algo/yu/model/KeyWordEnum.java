package algo.yu.model;


import java.util.HashMap;
import java.util.Map;

/**
 * XAVA关键字
 */
public enum KeyWordEnum {
    BOOL("bool"),
    BYTE("byte"), SHORT("short"), INT("int"), LONG("long"),
    FLOT("flot"), DOUBLE("double"),
    STRING("string"),
    IF("if"), ELSE("else"),
    FOR("for");

    public final String symbol;

    KeyWordEnum(String symbol) {
        this.symbol = symbol;
    }

    public static Map<String, Token> getKeyWordTokenMap() {
        Map<String, Token> map = new HashMap<>();
        for (KeyWordEnum keyWordEnum : values()) {
            map.put(keyWordEnum.symbol, Token.KEYWORD);
        }
        return map;
    }
}

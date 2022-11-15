package algo.yu.enums;


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
    IF("if"), ELSE("else"), SWITCH("switch"), CASE("case"),
    FOR("for"), BREAK("break"),
    MAIN("main"), VOID("void"), FUNCTION("func"), RETURN("return");

    private final String symbol;

    KeyWordEnum(String symbol) {
        this.symbol = symbol;
    }

    public static Map<String, TokenEnum> getKeyWordTokenMap() {
        Map<String, TokenEnum> map = new HashMap<>();
        for (KeyWordEnum keyWordEnum : values()) {
            map.put(keyWordEnum.symbol, TokenEnum.KEYWORD);
        }
        return map;
    }
}

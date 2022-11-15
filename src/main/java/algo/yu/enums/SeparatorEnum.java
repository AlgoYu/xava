package algo.yu.enums;

import java.util.HashMap;
import java.util.Map;

public enum SeparatorEnum {
    LEFT_PARENTHESES("("), RIGHT_PARENTHESES(")"),
    LEFT_CURLY_BRACKETS("{"), RIGHT_CURLY_BRACKETS("}"),
    LEFT_SQUARE_BRACKETS("["), RIGHT_SQUARE_BRACKETS("]"),
    SEMICOLON(";"), DOT(".");

    private final String symbol;

    SeparatorEnum(String symbol) {
        this.symbol = symbol;
    }

    public static Map<String, TokenEnum> getSeparatorTokenMap() {
        Map<String, TokenEnum> map = new HashMap<>();
        for (SeparatorEnum separatorEnum : values()) {
            map.put(separatorEnum.symbol, TokenEnum.SEPARATOR);
        }
        return map;
    }
}

package algo.yu.enums;

import java.util.HashMap;
import java.util.Map;

public enum SeparatorEnum {
    LEFT_BRACKET("("), RIGHT_BRACKET(")");

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

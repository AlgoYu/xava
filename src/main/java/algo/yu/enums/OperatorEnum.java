package algo.yu.enums;

import java.util.HashMap;
import java.util.Map;

public enum OperatorEnum {
    ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/"),
    ASSIGN("="), EQUALS("==");

    private final String symbol;

    OperatorEnum(String symbol) {
        this.symbol = symbol;
    }

    public static Map<String, TokenEnum> getSeparatorTokenMap() {
        Map<String, TokenEnum> map = new HashMap<>();
        for (OperatorEnum operatorEnum : values()) {
            map.put(operatorEnum.symbol, TokenEnum.SEPARATOR);
        }
        return map;
    }
}

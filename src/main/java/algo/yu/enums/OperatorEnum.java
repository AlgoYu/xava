package algo.yu.enums;

public enum OperatorEnum {
    ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/"),
    EQUALS("==");

    private final String symbol;

    OperatorEnum(String symbol) {
        this.symbol = symbol;
    }
}

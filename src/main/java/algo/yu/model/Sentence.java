package algo.yu.model;

public class Sentence {
    private int row;
    private String value;

    public Sentence(int row, String value) {
        this.row = row;
        this.value = value;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

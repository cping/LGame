package loon;

public class Counter {

    private int value;

    public int getValue() {
        return value;
    }

    public int increment() {
        value++;
        return value;
    }

    public void clear() {
        value = 0;
    }
    
}

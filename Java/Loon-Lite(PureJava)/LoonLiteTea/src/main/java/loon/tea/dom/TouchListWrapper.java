package loon.tea.dom;

public interface TouchListWrapper extends EventWrapper {

    public int getLength();

    public TouchWrapper item(int index);
}

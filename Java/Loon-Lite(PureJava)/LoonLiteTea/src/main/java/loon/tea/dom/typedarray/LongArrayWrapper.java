package loon.tea.dom.typedarray;

public interface LongArrayWrapper {

    public int getLength();

    public void setLength(int length);

    public int getElement(int index);

    public void setElement(int index, int value);
}

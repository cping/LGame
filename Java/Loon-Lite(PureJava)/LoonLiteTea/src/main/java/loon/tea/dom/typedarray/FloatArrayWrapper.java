package loon.tea.dom.typedarray;

public interface FloatArrayWrapper {

    public int getLength();

    public void setLength(int length);

    public float getElement(int index);

    public void setElement(int index, float value);
}

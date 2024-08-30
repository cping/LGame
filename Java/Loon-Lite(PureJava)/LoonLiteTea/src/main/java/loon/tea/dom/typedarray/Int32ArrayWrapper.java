package loon.tea.dom.typedarray;

import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSProperty;

public interface Int32ArrayWrapper extends ArrayBufferViewWrapper {

    static final int BYTES_PER_ELEMENT = 4;

    @JSProperty
    int getLength();

    @JSIndexer
    int get(int index);

    @JSIndexer
    void set(int index, int value);

    void set(Int32ArrayWrapper array);

    void set(Int32ArrayWrapper array, int offset);

    void set(LongArrayWrapper array);

    void set(LongArrayWrapper array, int offset);

    Int32ArrayWrapper subarray(int start, int end);
}
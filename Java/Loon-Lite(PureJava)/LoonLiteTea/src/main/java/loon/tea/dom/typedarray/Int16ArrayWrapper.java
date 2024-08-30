package loon.tea.dom.typedarray;

import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSProperty;

public interface Int16ArrayWrapper extends ArrayBufferViewWrapper {

    static final int BYTES_PER_ELEMENT = 2;

    @JSProperty
    int getLength();

    @JSIndexer
    short get(int index);

    @JSIndexer
    void set(int index, short value);

    void set(int index, int value);

    void set(Int16ArrayWrapper array);

    void set(Int16ArrayWrapper array, int offset);

    Int16ArrayWrapper subarray(int start, int end);
}
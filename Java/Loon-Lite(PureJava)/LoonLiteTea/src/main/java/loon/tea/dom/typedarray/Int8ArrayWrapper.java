package loon.tea.dom.typedarray;

import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSProperty;

public interface Int8ArrayWrapper extends ArrayBufferViewWrapper {

    static final int BYTES_PER_ELEMENT = 1;

    @JSProperty
    int getLength();

    @JSIndexer
    byte get(int index);

    @JSIndexer
    void set(int index, byte value);

    void set(Int8ArrayWrapper array);

    void set(Int8ArrayWrapper array, int offset);

    Int8ArrayWrapper subarray(int start, int end);
}
package loon.tea.dom.typedarray;

import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSProperty;

public interface Uint16ArrayWrapper extends ArrayBufferViewWrapper {

    static final int BYTES_PER_ELEMENT = 2;

    @JSProperty
    int getLength();

    @JSIndexer
    byte get(int index);

    @JSIndexer
    void set(int index, byte value);

    void set(Uint16ArrayWrapper array);

    void set(Uint16ArrayWrapper array, int offset);

    Uint16ArrayWrapper subarray(int start, int end);
}
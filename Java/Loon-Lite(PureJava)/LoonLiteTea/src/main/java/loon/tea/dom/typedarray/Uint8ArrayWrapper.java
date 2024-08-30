package loon.tea.dom.typedarray;

import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSProperty;

public interface Uint8ArrayWrapper extends ArrayBufferViewWrapper {

    static final int BYTES_PER_ELEMENT = 1;

    @JSProperty
    int getLength();

    @JSIndexer
    byte get(int index);

    @JSIndexer
    void set(int index, byte value);

    void set(Uint8ArrayWrapper array);

    void set(Uint8ArrayWrapper array, int offset);
    
    Uint8ArrayWrapper subarray(int start, int end);
}
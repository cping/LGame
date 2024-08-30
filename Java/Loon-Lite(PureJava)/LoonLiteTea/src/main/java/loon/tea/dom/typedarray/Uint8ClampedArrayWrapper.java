package loon.tea.dom.typedarray;

import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSProperty;

public interface Uint8ClampedArrayWrapper extends ArrayBufferViewWrapper {
	
    @JSProperty
    int getLength();

    @JSIndexer
    byte get(int index);

    @JSIndexer
    void set(int index, byte value);

    void set(Uint8ClampedArrayWrapper array);

    void set(Uint8ClampedArrayWrapper array, int offset);

    Uint8ClampedArrayWrapper subarray(int start, int end);
    
}
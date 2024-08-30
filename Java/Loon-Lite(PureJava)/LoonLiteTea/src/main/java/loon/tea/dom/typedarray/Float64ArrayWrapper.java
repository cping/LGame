package loon.tea.dom.typedarray;

import org.teavm.jso.JSIndexer;

public interface Float64ArrayWrapper extends ArrayBufferViewWrapper {

    @JSIndexer
    void set(int index, double value);
    
    @JSIndexer
    double get(int index);
}
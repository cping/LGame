package loon.tea.dom;

import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface DataTransferItemArrayWrapper extends JSObject {

    @JSProperty
    int getLength();

    @JSIndexer
    DataTransferItemWrapper get(int index);
}
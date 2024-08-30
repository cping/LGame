package loon.tea.dom.typedarray;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface ArrayBufferViewWrapper extends JSObject {

    @JSProperty
    ArrayBufferWrapper getBuffer();

    @JSProperty
    int getByteOffset();

    @JSProperty
    int getByteLength();
}
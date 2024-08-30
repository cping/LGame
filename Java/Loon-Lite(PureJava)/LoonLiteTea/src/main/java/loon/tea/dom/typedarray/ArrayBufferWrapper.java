package loon.tea.dom.typedarray;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface ArrayBufferWrapper extends JSObject {
    @JSProperty
    int getByteLength();
    @JSProperty
    boolean isDetached();
}
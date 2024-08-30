package loon.tea.gl;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface WebGLActiveInfoWrapper extends JSObject {
    @JSProperty()
    int getSize();

    @JSProperty()
    int getType();

    @JSProperty()
    String getName();
}
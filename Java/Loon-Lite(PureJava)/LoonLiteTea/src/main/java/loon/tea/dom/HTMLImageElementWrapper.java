package loon.tea.dom;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface HTMLImageElementWrapper extends HTMLElementWrapper, JSObject {
    @JSProperty
    void setSrc(String src);

    @JSProperty
    String getSrc();

    @JSProperty
    int getWidth();

    @JSProperty
    int getHeight();
}
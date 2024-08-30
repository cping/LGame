package loon.tea.dom;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface FileWrapper extends JSObject {

    @JSProperty
    String getName();

    @JSProperty
    int getSize();

    @JSProperty
    String getType();

    @JSProperty
    String getWebkitRelativePath();
}
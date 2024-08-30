package loon.tea.dom;

import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface FileListWrapper extends JSObject {

    @JSProperty
    int getLength();

    @JSIndexer
    FileWrapper get(int index);
}
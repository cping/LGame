package loon.tea.dom;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface FileSystemEntryWrapper extends JSObject {

    @JSProperty
    String getFullPath();
}
package loon.tea.dom;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface DataTransferItemWrapper extends JSObject {

    @JSProperty
    String getKind();

    @JSMethod
    FileSystemEntryWrapper webkitGetAsEntry();

    @JSMethod
    FileWrapper getAsFile();
}
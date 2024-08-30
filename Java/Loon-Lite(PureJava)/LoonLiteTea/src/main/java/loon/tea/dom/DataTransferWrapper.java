package loon.tea.dom;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface DataTransferWrapper extends JSObject {

    @JSProperty
    DataTransferItemArrayWrapper getItems();

    @JSProperty
    FileListWrapper getFiles();

    @JSMethod
    String getData(String format);

    @JSMethod
    void setData(String format, String data);
}
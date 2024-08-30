package loon.tea.dom;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface DragEventWrapper extends EventWrapper, JSObject {

    @JSProperty
    DataTransferWrapper getDataTransfer();
}
package loon.tea.dom;

import org.teavm.jso.JSProperty;

public interface ClipboardEventWrapper extends EventWrapper {

    @JSProperty
    DataTransferWrapper getClipboardData();
}

package org.teavm.classlib.java.nio;

import loon.tea.dom.typedarray.ArrayBufferViewWrapper;

public interface HasArrayBufferView {
    ArrayBufferViewWrapper getArrayBufferView();
    ArrayBufferViewWrapper getOriginalArrayBufferView();
    int getElementSize();
}
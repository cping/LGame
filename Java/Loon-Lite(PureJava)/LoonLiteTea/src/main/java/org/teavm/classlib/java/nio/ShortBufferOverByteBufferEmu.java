package org.teavm.classlib.java.nio;

import loon.tea.dom.typedarray.ArrayBufferViewWrapper;
import loon.tea.dom.typedarray.Int16ArrayWrapper;
import loon.tea.dom.typedarray.Int8ArrayWrapper;
import loon.tea.dom.typedarray.TypedArrays;
import loon.tea.make.Emulate;

@Emulate(valueStr = "java.nio.ShortBufferOverByteBuffer", updateCode = true)
public abstract class ShortBufferOverByteBufferEmu extends TShortBufferOverByteBuffer implements HasArrayBufferView {

    @Emulate
    Int16ArrayWrapper backupArray;
    @Emulate
    Int16ArrayWrapper shortArray;
    @Emulate
    int positionCache;
    @Emulate
    int remainingCache;

    public ShortBufferOverByteBufferEmu(int start, int capacity, TByteBufferImpl byteBuffer, int position, int limit, boolean readOnly) {
        super(start, capacity, byteBuffer, position, limit, readOnly);
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getArrayBufferView() {
        // Int8Array
        Int8ArrayWrapper int8Array = (Int8ArrayWrapper)getOriginalArrayBufferView();
        if(shortArray == null) {
            shortArray = TypedArrays.createInt16Array(int8Array.getBuffer());
        }
        int position1 = position();
        int remaining1 = remaining();
        if(backupArray == null || positionCache != position1 || remaining1 != remainingCache) {
            positionCache = position1;
            remainingCache = remaining1;
            backupArray = shortArray.subarray(position1, remaining1);
        }
        return backupArray;
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getOriginalArrayBufferView() {
        HasArrayBufferView buff = (HasArrayBufferView)byteByffer;
        return buff.getOriginalArrayBufferView();
    }

    @Override
    @Emulate
    public int getElementSize() {
        return 2;
    }
}
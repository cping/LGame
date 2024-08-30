package org.teavm.classlib.java.nio;

import loon.tea.dom.typedarray.ArrayBufferViewWrapper;
import loon.tea.dom.typedarray.Int32ArrayWrapper;
import loon.tea.dom.typedarray.TypedArrays;
import loon.tea.make.Emulate;

@Emulate(valueStr = "java.nio.TIntBufferOverArray", updateCode = true)
public abstract class IntBufferOverArrayEmu extends TIntBufferOverArray implements HasArrayBufferView {

    @Emulate
    Int32ArrayWrapper backupArray;
    @Emulate
    int positionCache;
    @Emulate
    int remainingCache;

    public IntBufferOverArrayEmu(int start, int capacity, int[] array, int position, int limit, boolean readOnly) {
        super(start, capacity, array, position, limit, readOnly);
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getArrayBufferView() {
        Int32ArrayWrapper originalBuffer = (Int32ArrayWrapper)getOriginalArrayBufferView();
        int position1 = position();
        int remaining1 = remaining();
        if(backupArray == null || positionCache != position1 || remaining1 != remainingCache) {
            positionCache = position1;
            remainingCache = remaining1;
            backupArray = originalBuffer.subarray(position1, remaining1);
        }
        return backupArray;
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getOriginalArrayBufferView() {
        return TypedArrays.getTypedArray(array);
    }

    @Override
    @Emulate
    public int getElementSize() {
        return 4;
    }
}
package org.teavm.classlib.java.nio;

import loon.tea.dom.typedarray.ArrayBufferViewWrapper;
import loon.tea.dom.typedarray.Float32ArrayWrapper;
import loon.tea.dom.typedarray.TypedArrays;
import loon.tea.make.Emulate;

@Emulate(valueStr = "java.nio.TFloatBufferOverArray", updateCode = true)
public abstract class FloatBufferOverArrayEmu extends TFloatBufferOverArray implements HasArrayBufferView {

    @Emulate
    Float32ArrayWrapper backupArray;
    @Emulate
    int positionCache;
    @Emulate
    int remainingCache;

    public FloatBufferOverArrayEmu(int start, int capacity, float[] array, int position, int limit, boolean readOnly) {
        super(start, capacity, array, position, limit, readOnly);
    }

    @Override
    @Emulate
    public ArrayBufferViewWrapper getArrayBufferView() {
        Float32ArrayWrapper originalBuffer = (Float32ArrayWrapper)getOriginalArrayBufferView();
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
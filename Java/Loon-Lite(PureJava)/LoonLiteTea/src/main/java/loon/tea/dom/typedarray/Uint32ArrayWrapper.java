package loon.tea.dom.typedarray;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSByRef;
import org.teavm.jso.JSIndexer;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.ArrayBufferView;

public abstract class Uint32ArrayWrapper extends ArrayBufferView {
    @JSIndexer
    public abstract int get(int index);

    @JSIndexer
    public abstract void set(int index, int value);

    public abstract void set(@JSByRef int[] data, int offset);

    public abstract void set(@JSByRef int[] data);

    public abstract Uint32ArrayWrapper subarray(int start, int end);

    @JSBody(params = "length", script = "return new Uint32Array(length);")
    public static native Uint32ArrayWrapper create(int length);

    @JSBody(params = "buffer", script = "return new Uint32Array(buffer);")
    public static native Uint32ArrayWrapper create(ArrayBuffer buffer);

    @JSBody(params = "buffer", script = "return new Uint32Array(buffer);")
    public static native Uint32ArrayWrapper create(ArrayBufferView buffer);

    @JSBody(params = { "buffer", "offset", "length" }, script = "return new Uint32Array(buffer, offset, length);")
    public static native Uint32ArrayWrapper create(ArrayBuffer buffer, int offset, int length);

    @JSBody(params = { "buffer", "offset" }, script = "return new Uint32Array(buffer, offset);")
    public static native Uint32ArrayWrapper create(ArrayBuffer buffer, int offset);
}

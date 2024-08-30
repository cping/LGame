package emu.java.io;

import loon.tea.dom.typedarray.Float32ArrayWrapper;
import loon.tea.dom.typedarray.Float64ArrayWrapper;
import loon.tea.dom.typedarray.Int32ArrayWrapper;
import loon.tea.dom.typedarray.Int8ArrayWrapper;
import loon.tea.dom.typedarray.TypedArrays;
import loon.tea.make.Emulate;

@Emulate(valueStr = "java.io.Numbers")
public class NumbersEmu {

	static Int8ArrayWrapper wba = TypedArrays.createInt8Array(4);
	static Int32ArrayWrapper wia = TypedArrays.createInt32Array(wba.getBuffer(), 0, 1);
	static Float32ArrayWrapper wfa = TypedArrays.createFloat32Array(wba.getBuffer(), 0, 1);
	static Float64ArrayWrapper wda = TypedArrays.createFloat64Array(wba.getBuffer(), 0, 1);

	static final double LN2 = Math.log(2);

	public static final int floatToIntBits(float f) {
		wfa.set(0, f);
		return wia.get(0);
	}

	public static final float intBitsToFloat(int i) {
		wia.set(0, i);
		return wfa.get(0);
	}

	public static final long doubleToLongBits(Double d) {
		throw new RuntimeException("NYI");
	}

	public static final double longBitsToDouble(long l) {
		throw new RuntimeException("NYI");
	}

	public static long doubleToRawLongBits(double value) {
		throw new RuntimeException("NYI: Numbers.doubleToRawLongBits");
	}

	public static final void setDouble(double d) {
		wda.set(0, d);
	}

	public static final double getDouble() {
		return wda.get(0);
	}

	public static final int getLoInt() {
		return wia.get(0);
	}

	public static final int getHiInt() {
		return wia.get(1);
	}

	public static final void setLoInt(int i) {
		wia.set(0, i);
	}

	public static final void setHiInt(int i) {
		wia.set(1, i);
	}

}
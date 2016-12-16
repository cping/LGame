package java.lang;

import static javaemul.internal.InternalPreconditions.checkArrayType;
import static javaemul.internal.InternalPreconditions.checkNotNull;
import static javaemul.internal.InternalPreconditions.isTypeChecked;

import java.io.PrintStream;

import javaemul.internal.ArrayHelper;
import javaemul.internal.DateUtil;
import javaemul.internal.HashCodes;

import jsinterop.annotations.JsMethod;


/**
 * General-purpose low-level utility methods. GWT only supports a limited subset
 * of these methods due to browser limitations. Only the documented methods are
 * available.
 */
public final class System {

  /**
   * Does nothing in web mode. To get output in web mode, subclass PrintStream
   * and call {@link #setErr(PrintStream)}.
   */
  public static PrintStream err = new PrintStream(null);

  /**
   * Does nothing in web mode. To get output in web mode, subclass
   * {@link PrintStream} and call {@link #setOut(PrintStream)}.
   */
  public static PrintStream out = new PrintStream(null);

	public static void arraycopy(Object src, int srcOfs, Object dest,
			int destOfs, int len) {
		if (src == null || dest == null) {
			throw new NullPointerException();
		}

		Class<?> srcType = src.getClass();
		Class<?> destType = dest.getClass();
		if (!srcType.isArray() || !destType.isArray()) {
			throw new ArrayStoreException("Must be array types");
		}

		Class<?> srcComp = srcType.getComponentType();
		Class<?> destComp = destType.getComponentType();
		if (srcComp.modifiers != destComp.modifiers
				|| (srcComp.isPrimitive() && !srcComp.equals(destComp))) {
			throw new ArrayStoreException("Array types must match");
		}
		int srclen = getArrayLength(src);
		int destlen = getArrayLength(dest);
		if (srcOfs < 0 || destOfs < 0 || len < 0 || srcOfs + len > srclen
				|| destOfs + len > destlen) {
			throw new IndexOutOfBoundsException();
		}
		/*
		 * If the arrays are not references or if they are exactly the same
		 * type, we can copy them in native code for speed. Otherwise, we have
		 * to copy them in Java so we get appropriate errors.
		 */
		if ((!srcComp.isPrimitive() || srcComp.isArray())
				&& !srcType.equals(destType)) {
			// copy in Java to make sure we get ArrayStoreExceptions if the
			// values
			// aren't compatible
			Object[] srcArray = (Object[]) src;
			Object[] destArray = (Object[]) dest;
			if (src == dest && srcOfs < destOfs) {
				// TODO(jat): how does backward copies handle failures in the
				// middle?
				// copy backwards to avoid destructive copies
				srcOfs += len;
				for (int destEnd = destOfs + len; destEnd-- > destOfs;) {
					destArray[destEnd] = srcArray[--srcOfs];
				}
			} else {
				for (int destEnd = destOfs + len; destOfs < destEnd;) {
					destArray[destOfs++] = srcArray[srcOfs++];
				}
			}
		} else {
			nativeArraycopy(src, srcOfs, dest, destOfs, len);
		}
	}

  public static long currentTimeMillis() {
    return (long) currentTimeMillis0();
  }

  private static native double currentTimeMillis0() /*-{
	    return Date.now();
  }-*/;
	
  /**
   * Has no effect; just here for source compatibility.
   *
   * @skip
   */
  public static void gc() {
  }

  /**
   * The compiler replaces getProperty by the actual value of the property.
   */
  @JsMethod(name = "$getDefine", namespace = "nativebootstrap.Util")
  public static native String getProperty(String key);

  /**
   * The compiler replaces getProperty by the actual value of the property.
   */
  @JsMethod(name = "$getDefine", namespace = "nativebootstrap.Util")
  public static native String getProperty(String key, String def);

  public static int identityHashCode(Object o) {
    return HashCodes.getIdentityHashCode(o);
  }

  public static void setErr(PrintStream err) {
    System.err = err;
  }

  public static void setOut(PrintStream out) {
    System.out = out;
  }

  /**
	 * Copy an array using native Javascript. The destination array must be a
	 * real Java array (ie, already has the GWT type info on it). No error
	 * checking is performed -- the caller is expected to have verified
	 * everything first.
	 * 
	 * @param src
	 *            source array for copy
	 * @param srcOfs
	 *            offset into source array
	 * @param dest
	 *            destination array for copy
	 * @param destOfs
	 *            offset into destination array
	 * @param len
	 *            number of elements to copy
	 */
	private static native void nativeArraycopy(Object src, int srcOfs,
			Object dest, int destOfs, int len) /*-{
		// TODO(jgw): using Function.apply() blows up for large arrays (around 8k items at least).
		if (src == dest && srcOfs < destOfs) {
			srcOfs += len;
			for (var destEnd = destOfs + len; destEnd-- > destOfs;) {
				dest[destEnd] = src[--srcOfs];
			}
		} else {
			for (var destEnd = destOfs + len; destOfs < destEnd;) {
				dest[destOfs++] = src[srcOfs++];
			}
		}

		//    Array.prototype.splice.apply(dest, [destOfs, len].concat(src.slice(srcOfs, srcOfs + len)));
	}-*/;

  
    /**
	 * Returns the length of an array via Javascript.
	 */
	private static native int getArrayLength(Object array) /*-{
		return array.length;
	}-*/;
	
	public static native int nanoTime()
	/*-{
		var n = $wnd.now();
		return n * 1000;
	}-*/;

	public static native void exit(int code) /*-{ }-*/;
}
package loon.core;

import java.lang.reflect.Method;

import loon.LSystem;
import loon.LSystem.ApplicationType;

public class LRuntimeHack {

	private static LRuntimeHack _instance;

	public synchronized static LRuntimeHack get() {
		if (_instance == null) {
			_instance = new LRuntimeHack();
		}
		return _instance;
	}

	private Object runtime = null;
	private Method trackAllocation = null;
	private Method trackFree = null;

	public static float getTargetHeapUtilization() {
		if (LSystem.type != ApplicationType.Android) {
			return 0;
		}
		try {
			Class<?> VMRuntimeClass = Class.forName("dalvik.system.VMRuntime");
			Method getRuntimeMethod = VMRuntimeClass.getMethod("getRuntime",
					new Class[0]);
			Class<?>[] arrayOfClass = new Class[1];
			arrayOfClass[0] = Long.TYPE;
			Method getTargetHeapUtilization = VMRuntimeClass.getMethod(
					"getTargetHeapUtilization", new Class[0]);
			Object runtimeObject = getRuntimeMethod.invoke(null, new Object[0]);
			Float ret = (Float) getTargetHeapUtilization.invoke(runtimeObject,
					new Object[0]);
			return ret.floatValue();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void setTargetHeapUtilization(float value) {
		if (LSystem.type != ApplicationType.Android) {
			return;
		}
		try {
			Class<?> VMRuntimeClass = Class.forName("dalvik.system.VMRuntime");
			Method getRuntimeMethod = VMRuntimeClass.getMethod("getRuntime",
					new Class[0]);
			Class<?>[] arrayOfClass = new Class[1];
			arrayOfClass[0] = Float.TYPE;
			Method setTargetHeapUtilizationMethod = VMRuntimeClass.getMethod(
					"setTargetHeapUtilization", arrayOfClass);
			Object runtimeObject = getRuntimeMethod.invoke(null, new Object[0]);
			Object[] arrayOfObject = new Object[1];
			arrayOfObject[0] = Float.valueOf(value);
			setTargetHeapUtilizationMethod.invoke(runtimeObject, arrayOfObject);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public boolean trackAlloc(long size) {
		if (LSystem.type != ApplicationType.Android) {
			return false;
		}
		if (runtime == null) {
			return false;
		}
		try {
			Object res = trackAllocation.invoke(runtime, Long.valueOf(size));
			return (res instanceof Boolean) ? ((Boolean) res).booleanValue()
					: true;
		} catch (Throwable e) {
			return false;
		}
	}

	public boolean trackFree(long size) {
		if (LSystem.type != ApplicationType.Android) {
			return false;
		}
		if (runtime == null) {
			return false;
		}
		try {
			Object res = trackFree.invoke(runtime, Long.valueOf(size));
			return (res instanceof Boolean) ? ((Boolean) res).booleanValue()
					: true;
		} catch (Throwable e) {
			return false;
		}
	}

	public LRuntimeHack() {
		if (LSystem.type != ApplicationType.Android) {
			return;
		}
		boolean success = false;
		try {
			Class<?> cl = Class.forName("dalvik.system.VMRuntime");
			Method getRt = cl.getMethod("getRuntime", new Class[0]);
			runtime = getRt.invoke(null, new Object[0]);
			trackAllocation = cl.getMethod("trackExternalAllocation",
					new Class[] { long.class });
			trackFree = cl.getMethod("trackExternalFree",
					new Class[] { long.class });
			success = true;
		} catch (Throwable e) {
		}
		if (!success) {
			runtime = null;
			trackAllocation = null;
			trackFree = null;
		}
	}
}

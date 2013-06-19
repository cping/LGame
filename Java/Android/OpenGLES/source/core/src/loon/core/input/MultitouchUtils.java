package loon.core.input;

import java.lang.reflect.Method;

import android.view.MotionEvent;

/**
 * 
 * Copyright 2008 - 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email javachenpeng@yahoo.com
 * @version 0.1.0
 */
public class MultitouchUtils {

	public static final int ACTION_POINTER_1_DOWN = 5;

	public static final int ACTION_POINTER_2_DOWN = 261;

	public static final int ACTION_POINTER_3_DOWN = 517;

	public static final int ACTION_POINTER_1_UP = 6;

	public static final int ACTION_POINTER_2_UP = 262;

	public static final int ACTION_POINTER_3_UP = 518;

	public static final int ACTION_POINTER_ID_MASK = 65280;

	public static final int ACTION_POINTER_ID_SHIFT = 8;

	public static final int INVALID_POINTER_ID = -1;

	public static final int ACTION_MASK = 255;

	public static final int ACTION_DOWN = 0;

	public static final int ACTION_UP = 1;

	public static final int ACTION_MOVE = 2;

	public static final int ACTION_CANCEL = 3;

	public static final int ACTION_OUTSIDE = 4;

	public static final int ACTION_POINTER_DOWN = 5;

	public static final int ACTION_POINTER_UP = 6;

	public static final int ACTION_HOVER_MOVE = 7;

	public static final int ACTION_SCROLL = 8;

	public static final int ACTION_HOVER_ENTER = 9;

	public static final int ACTION_HOVER_EXIT = 10;

	public static final int ACTION_POINTER_INDEX_MASK = 65280;

	public static final int ACTION_POINTER_INDEX_SHIFT = 8;

	public static final int FLAG_WINDOW_IS_OBSCURED = 1;

	public static final int EDGE_TOP = 1;

	public static final int EDGE_BOTTOM = 2;

	public static final int EDGE_LEFT = 4;

	public static final int EDGE_RIGHT = 8;

	public static final int AXIS_X = 0;

	public static final int AXIS_Y = 1;

	public static final int AXIS_PRESSURE = 2;

	public static final int AXIS_SIZE = 3;

	public static final int AXIS_TOUCH_MAJOR = 4;

	public static final int AXIS_TOUCH_MINOR = 5;

	public static final int AXIS_TOOL_MAJOR = 6;

	public static final int AXIS_TOOL_MINOR = 7;

	public static final int AXIS_ORIENTATION = 8;

	public static final int AXIS_VSCROLL = 9;

	public static final int AXIS_HSCROLL = 10;

	public static final int AXIS_Z = 11;

	public static final int AXIS_RX = 12;

	public static final int AXIS_RY = 13;

	public static final int AXIS_RZ = 14;

	public static final int AXIS_HAT_X = 15;

	public static final int AXIS_HAT_Y = 16;

	public static final int AXIS_LTRIGGER = 17;

	public static final int AXIS_RTRIGGER = 18;

	public static final int AXIS_THROTTLE = 19;

	public static final int AXIS_RUDDER = 20;

	public static final int AXIS_WHEEL = 21;

	public static final int AXIS_GAS = 22;

	public static final int AXIS_BRAKE = 23;

	public static final int AXIS_DISTANCE = 24;

	public static final int AXIS_TILT = 25;

	public static final int AXIS_GENERIC_1 = 32;

	public static final int AXIS_GENERIC_2 = 33;

	public static final int AXIS_GENERIC_3 = 34;

	public static final int AXIS_GENERIC_4 = 35;

	public static final int AXIS_GENERIC_5 = 36;

	public static final int AXIS_GENERIC_6 = 37;

	public static final int AXIS_GENERIC_7 = 38;

	public static final int AXIS_GENERIC_8 = 39;

	public static final int AXIS_GENERIC_9 = 40;

	public static final int AXIS_GENERIC_10 = 41;

	public static final int AXIS_GENERIC_11 = 42;

	public static final int AXIS_GENERIC_12 = 43;

	public static final int AXIS_GENERIC_13 = 44;

	public static final int AXIS_GENERIC_14 = 45;

	public static final int AXIS_GENERIC_15 = 46;

	public static final int AXIS_GENERIC_16 = 47;

	public static final int BUTTON_PRIMARY = 1;

	public static final int BUTTON_SECONDARY = 2;

	public static final int BUTTON_TERTIARY = 4;

	public static final int BUTTON_BACK = 8;

	public static final int BUTTON_FORWARD = 16;

	public static final int TOOL_TYPE_UNKNOWN = 0;

	public static final int TOOL_TYPE_FINGER = 1;

	public static final int TOOL_TYPE_STYLUS = 2;

	public static final int TOOL_TYPE_MOUSE = 3;

	public static final int TOOL_TYPE_ERASER = 4;

	private static Method motionEvent_GetPointerCount;
	private static Method motionEvent_GetPointerId;
	private static Method motionEvent_FindPointerIndex;
	private static Method motionEvent_GetX;
	private static Method motionEvent_GetY;

	private static boolean isMultitouch;

	private static Object[] emptyObjectArray = new Object[] {};

	static {
		try {
			motionEvent_GetPointerCount = MotionEvent.class.getMethod(
					"getPointerCount", new Class[] {});
			motionEvent_GetPointerId = MotionEvent.class.getMethod(
					"getPointerId", new Class[] { int.class });
			motionEvent_FindPointerIndex = MotionEvent.class.getMethod(
					"findPointerIndex", new Class[] { int.class });
			motionEvent_GetX = MotionEvent.class.getMethod("getX",
					new Class[] { int.class });
			motionEvent_GetY = MotionEvent.class.getMethod("getY",
					new Class[] { int.class });
			isMultitouch = true;
		} catch (NoSuchMethodException ex) {
			isMultitouch = false;
		}

	};
	
	/**
	 * 判断当前系统是否支持多点触摸
	 * 
	 * @return
	 */
	public static boolean isMultitouch() {
		return isMultitouch;
	}

	/**
	 * 获得同一时间内屏幕触摸次数
	 * 
	 * @param e
	 * @return
	 */
	public static int getPointerCount(MotionEvent e) {
		if (!isMultitouch) {
			return 1;
		}
		try {
			int pointerCount = (Integer) motionEvent_GetPointerCount.invoke(e,
					emptyObjectArray);
			return pointerCount;
		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * 查找对应触摸点索引
	 * 
	 * @param e
	 * @param id
	 * @return
	 */
	public static int findPointerIndex(MotionEvent e, int id) {
		if (!isMultitouch) {
			return 0;
		}
		try {
			int pointerIndex = (Integer) motionEvent_FindPointerIndex.invoke(e,
					new Object[] { id });
			return pointerIndex;
		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * 获得对应触摸点的ID
	 * 
	 * @param e
	 * @param pointerIndex
	 * @return
	 */
	public static int getPointId(MotionEvent e, int pointerIndex) {
		if (!isMultitouch) {
			return 0;
		}
		try {
			int pointerCount = (Integer) motionEvent_GetPointerId.invoke(e,
					new Object[] { pointerIndex });
			return pointerCount;
		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * 获得多点触摸下的X轴
	 * 
	 * @param e
	 * @param pointerIndex
	 * @return
	 */
	public static float getX(MotionEvent e, int pointerIndex) {
		if (!isMultitouch) {
			return e.getX();
		}
		try {
			float pointerCount = (Float) motionEvent_GetX.invoke(e,
					new Object[] { pointerIndex });
			return pointerCount;
		} catch (Exception ex) {
			return e.getX();
		}
	}

	/**
	 * 获得多点触摸下的Y轴
	 * 
	 * @param e
	 * @param pointerIndex
	 * @return
	 */
	public static float getY(MotionEvent e, int pointerIndex) {
		if (!isMultitouch) {
			return e.getY();
		}
		try {
			float pointerCount = (Float) motionEvent_GetY.invoke(e,
					new Object[] { pointerIndex });
			return pointerCount;
		} catch (Exception ex) {
			return e.getY();
		}
	}

}

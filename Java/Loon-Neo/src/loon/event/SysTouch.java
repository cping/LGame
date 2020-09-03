/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.event;

import loon.geom.Vector2f;

public class SysTouch {

	public static void startTouchCollection() {
		SysInputFactory.startTouchCollection();
	}

	public static void stopTouchCollection() {
		SysInputFactory.stopTouchCollection();
	}

	public static LTouchCollection getTouchState() {
		return SysInputFactory.getTouchState();
	}

	public static void resetTouch() {
		SysInputFactory.resetTouch();
	}

	public static ActionKey getOnlyKey() {
		return SysInputFactory.getOnlyKey();
	}

	public static final int TOUCH_UNKNOWN = -1;
	
	public static final int TOUCH_DOWN = 0;

	public static final int TOUCH_UP = 1;

	public static final int TOUCH_MOVE = 2;

	public static final int TOUCH_DRAG = 3;

	public static final int LEFT = 0;

	public static final int RIGHT = 1;

	public static final int MIDDLE = 2;

	public final static int UPPER_LEFT = 0;

	public final static int UPPER_RIGHT = 1;

	public final static int LOWER_LEFT = 2;

	public final static int LOWER_RIGHT = 3;

	private static final Vector2f location = new Vector2f();

	public static Vector2f getLocation() {
		location.set(SysInputFactory.finalTouch.x, SysInputFactory.finalTouch.y);
		return location;
	}

	public static int getType() {
		return SysInputFactory.finalTouch.type;
	}

	public static int getButton() {
		return SysInputFactory.finalTouch.button;
	}

	public static int getPointer() {
		return SysInputFactory.finalTouch.pointer;
	}

	public static int x() {
		return (int) SysInputFactory.finalTouch.x;
	}

	public static int y() {
		return (int) SysInputFactory.finalTouch.y;
	}

	public static float getX() {
		return SysInputFactory.finalTouch.x;
	}

	public static float getY() {
		return SysInputFactory.finalTouch.y;
	}

	public static float getDX() {
		return SysInputFactory.finalTouch.dx;
	}

	public static float getDY() {
		return SysInputFactory.finalTouch.dy;
	}

	public static boolean isLeft() {
		return SysInputFactory.finalTouch.isLeft();
	}

	public static boolean isMiddle() {
		return SysInputFactory.finalTouch.isMiddle();
	}

	public static boolean isRight() {
		return SysInputFactory.finalTouch.isRight();
	}

	public static boolean isDown() {
		return SysInputFactory.finalTouch.isDown();
	}

	public static boolean isUp() {
		return SysInputFactory.finalTouch.isUp();
	}

	public static boolean isMove() {
		return SysInputFactory.finalTouch.isMove();
	}

	public static boolean lowerLeft() {
		return SysInputFactory.finalTouch.lowerLeft();
	}

	public static boolean lowerRight() {
		return SysInputFactory.finalTouch.lowerRight();
	}

	public static boolean upperLeft() {
		return SysInputFactory.finalTouch.upperLeft();
	}

	public static boolean upperRight() {
		return SysInputFactory.finalTouch.upperRight();
	}

	public static long getDuration() {
		return SysInputFactory.finalTouch.duration;
	}

	public static long getTimeDown() {
		return SysInputFactory.finalTouch.timeDown;
	}

	public static long getTimeUp() {
		return SysInputFactory.finalTouch.timeUp;
	}

	public static boolean isDrag() {
		return SysInputFactory._isDraging;
	}

	public static GameTouch cpy() {
		return SysInputFactory.finalTouch.cpy();
	}

}

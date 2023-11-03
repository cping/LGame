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
package loon.events;

import loon.LSystem;
import loon.geom.Vector2f;
import loon.utils.StringUtils;

public class SysTouch {

	public final static int toIntKey(String keyName) {
		if (StringUtils.isNullOrEmpty(keyName)) {
			return TOUCH_UNKNOWN;
		}
		final String keyValue = StringUtils.replaces(keyName.trim().toLowerCase(), "", "_", "-", " ");
		if (keyValue.equals(LSystem.UNKNOWN) || keyValue.equals("touchunknown")) {
			return TOUCH_UNKNOWN;
		} else if (keyValue.equals("touchdown") || keyValue.equals("down")) {
			return TOUCH_DOWN;
		} else if (keyValue.equals("touchup") || keyValue.equals("up")) {
			return TOUCH_UP;
		} else if (keyValue.equals("touchmove") || keyValue.equals("move")) {
			return TOUCH_MOVE;
		} else if (keyValue.equals("touchdrag") || keyValue.equals("drag")) {
			return TOUCH_DRAG;
		} else if (keyValue.equals("left") || keyValue.equals("l")) {
			return LEFT;
		} else if (keyValue.equals("middle") || keyValue.equals("m")) {
			return MIDDLE;
		} else if (keyValue.equals("right") || keyValue.equals("r")) {
			return RIGHT;
		}
		return TOUCH_UNKNOWN;
	}

	public final static int toIntType(String keyName) {
		if (StringUtils.isNullOrEmpty(keyName)) {
			return -1;
		}
		final String keyValue = StringUtils.replaces(keyName.trim().toLowerCase(), "", "_", "-", " ");
		if (keyValue.equals("upperleft")) {
			return UPPER_LEFT;
		} else if (keyValue.equals("upperright")) {
			return UPPER_RIGHT;
		} else if (keyValue.equals("lowerleft")) {
			return LOWER_LEFT;
		} else if (keyValue.equals("lowerright")) {
			return LOWER_RIGHT;
		}
		return TOUCH_UNKNOWN;
	}

	public static void startTouchCollection() {
		LSystem.getProcess().getSysInputFactory().startTouchCollection();
	}

	public static void stopTouchCollection() {
		LSystem.getProcess().getSysInputFactory().stopTouchCollection();
	}

	public static LTouchCollection getTouchState() {
		return LSystem.getProcess().getSysInputFactory().getTouchState();
	}

	public static void resetTouch() {
		LSystem.getProcess().getSysInputFactory().resetTouch();
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

	public static int getTypeCode() {
		return SysInputFactory.finalTouch.type;
	}

	public static int getButton() {
		return SysInputFactory.finalTouch.button;
	}

	public static int getPointer() {
		return SysInputFactory.finalTouch.pointer;
	}

	public static int x() {
		return SysInputFactory.finalTouch.x();
	}

	public static int y() {
		return SysInputFactory.finalTouch.y();
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

	public static boolean isDrag() {
		return SysInputFactory.isDraging && SysInputFactory.finalTouch.isDrag();
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

	public static GameTouch cpy() {
		return SysInputFactory.finalTouch.cpy();
	}

}

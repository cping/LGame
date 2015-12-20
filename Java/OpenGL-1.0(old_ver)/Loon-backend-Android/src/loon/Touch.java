package loon;

import loon.core.event.ActionKey;
import loon.core.geom.Vector2f;

public class Touch {

	public static void setOnscreenKeyboardVisible(boolean visible) {
		AndroidInputFactory.setOnscreenKeyboardVisible(visible);
	}

	public static void startTouchCollection() {
		AndroidInputFactory.startTouchCollection();
	}

	public static void stopTouchCollection() {
		AndroidInputFactory.stopTouchCollection();
	}

	public static LTouchCollection getTouchState() {
		return AndroidInputFactory.getTouchState();
	}

	public static void resetTouch() {
		AndroidInputFactory.resetTouch();
	}

	public static ActionKey getOnlyKey() {
		return AndroidInputFactory.getOnlyKey();
	}

	public final static int UPPER_LEFT = 0;

	public final static int UPPER_RIGHT = 1;

	public final static int LOWER_LEFT = 2;

	public final static int LOWER_RIGHT = 3;

	public static final int TOUCH_DOWN = 0;

	public static final int TOUCH_UP = 1;

	public static final int TOUCH_MOVE = 2;

	private static final Vector2f location = new Vector2f();

	public static Vector2f getLocation() {
		location.set(AndroidInputFactory.finalTouch.x, AndroidInputFactory.finalTouch.y);
		return location;
	}

	public static int getType() {
		return AndroidInputFactory.finalTouch.type;
	}

	public static int getButton() {
		return AndroidInputFactory.finalTouch.button;
	}

	public static int getPointer() {
		return AndroidInputFactory.finalTouch.pointer;
	}

	public static int x() {
		return (int) AndroidInputFactory.finalTouch.x;
	}

	public static int y() {
		return (int) AndroidInputFactory.finalTouch.y;
	}

	public static float getX() {
		return AndroidInputFactory.finalTouch.x;
	}

	public static float getY() {
		return AndroidInputFactory.finalTouch.y;
	}

	public static boolean isDown() {
		return AndroidInputFactory.finalTouch.isDown();
	}

	public static boolean isUp() {
		return AndroidInputFactory.finalTouch.isUp();
	}

	public static boolean isMove() {
		return AndroidInputFactory.finalTouch.isMove();
	}

	public static boolean isDrag() {
		return AndroidInputFactory.isDraging;
	}
}

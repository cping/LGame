package loon;

import loon.core.event.ActionKey;
import loon.core.geom.Vector2f;

public class Touch {

	public static void setOnscreenKeyboardVisible(boolean visible) {
		JavaSEInputFactory.setOnscreenKeyboardVisible(visible);
	}

	public static void startTouchCollection() {
		JavaSEInputFactory.startTouchCollection();
	}

	public static void stopTouchCollection() {
		JavaSEInputFactory.stopTouchCollection();
	}

	public static LTouchCollection getTouchState() {
		return JavaSEInputFactory.getTouchState();
	}

	public static void resetTouch() {
		JavaSEInputFactory.resetTouch();
	}

	public static ActionKey getOnlyKey() {
		return JavaSEInputFactory.getOnlyKey();
	}

	public static final int TOUCH_DOWN = 0;

	public static final int TOUCH_UP = 1;

	public static final int TOUCH_MOVE = 2;

	public static final int TOUCH_DRAG = 3;

	public static final int LEFT = 0;

	public static final int RIGHT = 1;

	public static final int MIDDLE = 2;

	private static final Vector2f location = new Vector2f();

	public static Vector2f getLocation() {
		location.set(JavaSEInputFactory.finalTouch.x,
				JavaSEInputFactory.finalTouch.y);
		return location;
	}

	public static int getButton() {
		return JavaSEInputFactory.finalTouch.button;
	}

	public static int getPointer() {
		return JavaSEInputFactory.finalTouch.pointer;
	}

	public static int getType() {
		return JavaSEInputFactory.finalTouch.type;
	}

	public static int x() {
		return (int) JavaSEInputFactory.finalTouch.x;
	}

	public static int y() {
		return (int) JavaSEInputFactory.finalTouch.y;
	}

	public static float getX() {
		return JavaSEInputFactory.finalTouch.x;
	}

	public static float getY() {
		return JavaSEInputFactory.finalTouch.y;
	}

	public static boolean isDown() {
		return JavaSEInputFactory.finalTouch.isDown();
	}

	public static boolean isUp() {
		return JavaSEInputFactory.finalTouch.isUp();
	}

	public static boolean isMove() {
		return JavaSEInputFactory.finalTouch.isMove();
	}

	public static boolean isDrag() {
		return JavaSEInputFactory.isDraging;
	}

	public static boolean isLeft() {
		return JavaSEInputFactory.finalTouch.isLeft();
	}

	public static boolean isMiddle() {
		return JavaSEInputFactory.finalTouch.isMiddle();
	}

	public static boolean isRight() {
		return JavaSEInputFactory.finalTouch.isRight();
	}

}

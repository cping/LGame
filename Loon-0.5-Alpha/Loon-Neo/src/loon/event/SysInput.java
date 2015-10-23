package loon.event;

import loon.geom.Point.Point2i;


public interface SysInput {

	public interface TextEvent {

		public void input(String text);

		public void cancel();

	}

	public interface SelectEvent {

		public void item(int index);

		public void cancel();

	}

	public interface ClickEvent {

		public void clicked();

		public void cancel();

	}

	public final static int NO_BUTTON = -1;

	public final static int NO_KEY = -1;

	public final static int UPPER_LEFT = 0;

	public final static int UPPER_RIGHT = 1;

	public final static int LOWER_LEFT = 2;

	public final static int LOWER_RIGHT = 3;

	public abstract void setKeyDown(int code);

	public abstract void setKeyUp(int code);

	public abstract boolean isMoving();

	public abstract int getRepaintMode();

	public abstract void setRepaintMode(int mode);

	public abstract Point2i getTouch();

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract void refresh();

	public abstract int getTouchX();

	public abstract int getTouchY();

	public abstract int getTouchDX();

	public abstract int getTouchDY();

	public abstract int getTouchReleased();

	public abstract boolean isTouchReleased(int i);

	public abstract int getTouchPressed();

	public abstract boolean isTouchPressed(int i);

	public abstract boolean isTouchType(int i);

	public abstract int getKeyReleased();

	public abstract boolean isKeyReleased(int i);

	public abstract int getKeyPressed();

	public abstract boolean isKeyPressed(int i);

	public abstract boolean isKeyType(int i);

}

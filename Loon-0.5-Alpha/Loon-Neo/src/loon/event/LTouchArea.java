package loon.event;

public interface LTouchArea {

	public static enum Event {
		DOWN, UP, MOVE, DRAG;
	}

	public boolean contains(final float x, final float y);

	public void onAreaTouched(final Event e, final float touchX, final float touchY);
}

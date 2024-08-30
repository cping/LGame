package loon.tea.dom;

public interface WheelEventWrapper extends EventWrapper {
	
    public float getDeltaX();

    public float getDeltaY();

    public float getDeltaZ();

    public float getWheelDelta();
}

package loon;

import loon.action.map.Config;
import loon.geom.Vector3f;

public abstract class LAccelerometer {
	
	// 四方向手机朝向
	protected SensorDirection _direction = SensorDirection.EMPTY;

	// 八方向手机朝向
	protected int _all_direction = Config.EMPTY;

	protected Event event;

	protected float currentX, currentY, currentZ;

	protected float lastX, lastY, lastZ;

	protected float orientation;
	
	public static enum SensorDirection {
		EMPTY, LEFT, RIGHT, UP, DOWN;
	}

	public interface Event {

		public void onDirection(SensorDirection direction, float x, float y,
				float z);

		public void onShakeChanged(float force);
	}


	public static boolean checkAngle(float angle, float actual) {
		return actual > angle - 22.5f && actual < angle + 22.5f;
	}


	public static class AccelerometerState {

		boolean _isConnected;

		final Vector3f _acceleration = new Vector3f();

		public Vector3f getAcceleration() {
			return _acceleration;
		}

		public boolean isConnected() {
			return _isConnected;
		}

	}

	private final AccelerometerState _state = new AccelerometerState();

	private int _sleep = 30;

	public abstract void start();

	public void stop() {
		_state._isConnected = false;
	}

	public float getLastX() {
		return lastX;
	}

	public float getLastY() {
		return lastY;
	}

	public float getLastZ() {
		return lastZ;
	}

	public float getX() {
		return currentX;
	}

	public float getY() {
		return currentY;
	}

	public float getZ() {
		return currentZ;
	}

	public final AccelerometerState getState() {
		return _state;
	}

	public int getSleep() {
		return _sleep;
	}

	public abstract void sleep(int sleep);

	public int getAllDirection() {
		return _all_direction;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public float getOrientation() {
		return orientation;
	}

	public SensorDirection getDirection() {
		return _direction;
	}
}

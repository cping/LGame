package loon;

public interface Accelerometer {
	
	public static enum SensorDirection {
		EMPTY, LEFT, RIGHT, UP, DOWN;
	}

	public interface Event {

		public void onDirection(SensorDirection direction, float x, float y,
				float z);

		public void onShakeChanged(float force);
	}

	public void start() ;

	public void stop() ;

	public float getLastX() ;

	public float getLastY() ;

	public float getLastZ();

	public float getX();

	public float getY() ;

	public float getZ() ;

	public AccelerometerState getState() ;

	public int getSleep();

	public void sleep(int sleep);

	public int getAllDirection();

	public Event getEvent() ;

	public void setEvent(Event event) ;

	public float getOrientation();

	public SensorDirection getDirection();
}

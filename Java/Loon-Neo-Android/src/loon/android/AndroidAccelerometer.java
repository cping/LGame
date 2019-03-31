package loon.android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import loon.Accelerometer;
import loon.AccelerometerState;
import loon.LSystem;
import loon.action.collision.CollisionHelper;
import loon.action.map.Config;
import loon.event.SysKey;
import loon.utils.MathUtils;
import loon.utils.TimeUtils;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimerContext;

public class AndroidAccelerometer implements Accelerometer {

	// 四方向手机朝向
	private SensorDirection _direction = SensorDirection.EMPTY;

	// 八方向手机朝向
	private int _all_direction = Config.EMPTY;

	private Event event;

	private long lastUpdate;

	private float currentX, currentY, currentZ, currenForce;

	private float lastX, lastY, lastZ;

	private float orientation, magnitude;

	private final AccelerometerState _state = new AccelerometerState();

	private int _sleep = 30;

	private SensorManager manager;

	private final float[] accelerometerValues = new float[3];

	private final float[] magneticFieldValues = new float[3];

	private SensorEventListener accelerometerListener;

	private SensorProcess sensorProcess;

	private Loon _game;

	public AndroidAccelerometer(Loon game) {
		super();
		this._game = game;
	}

	private final void onOrientation(float x, float y, float z) {
		// 换算手机翻转角度
		orientation = 0;
		magnitude = x * x + y * y;
		if (magnitude * 4 >= z * z) {
			float angle = MathUtils.atan2(-y, x) * MathUtils.RAD_TO_DEG;
			orientation = 90 - MathUtils.round(angle);
			while (orientation >= 360) {
				orientation -= 360;
			}
			while (orientation < 0) {
				orientation += 360;
			}
		}
		// 将手机翻转角度转为手机朝向
		if (CollisionHelper.checkAngle(0, orientation)
				|| CollisionHelper.checkAngle(360, orientation)) {
			_all_direction = Config.TUP;
			_direction = SensorDirection.UP;
		} else if (CollisionHelper.checkAngle(45, orientation)) {
			_all_direction = Config.LEFT;
			_direction = SensorDirection.LEFT;
		} else if (CollisionHelper.checkAngle(90, orientation)) {
			_all_direction = Config.TLEFT;
			_direction = SensorDirection.LEFT;
		} else if (CollisionHelper.checkAngle(135, orientation)) {
			_all_direction = Config.DOWN;
			_direction = SensorDirection.LEFT;
		} else if (CollisionHelper.checkAngle(180, orientation)) {
			_all_direction = Config.TDOWN;
			_direction = SensorDirection.DOWN;
		} else if (CollisionHelper.checkAngle(225, orientation)) {
			_all_direction = Config.RIGHT;
			_direction = SensorDirection.RIGHT;
		} else if (CollisionHelper.checkAngle(270, orientation)) {
			_all_direction = Config.TRIGHT;
			_direction = SensorDirection.RIGHT;
		} else if (CollisionHelper.checkAngle(315, orientation)) {
			_all_direction = Config.UP;
			_direction = SensorDirection.RIGHT;
		} else {
			_all_direction = Config.EMPTY;
			_direction = SensorDirection.EMPTY;
		}
	}

	private final void onSensor(float[] values) {
		synchronized (this) {

			long curTime = TimeUtils.millis();

			if (LSystem.base() != null && LSystem.base().setting.landscape()) {
				currentX = -values[0];
				currentY = -values[1];
				currentZ = -values[2];
			} else {
				currentX = values[0];
				currentY = values[1];
				currentZ = values[2];
			}

			onOrientation(currentX, currentY, currentZ);

			if ((curTime - lastUpdate) > 30) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;
				currenForce = MathUtils.abs(currentX + currentY + currentZ
						- lastX - lastY - lastZ)
						/ diffTime * 10000;

				if (currenForce > 500 && event != null) {
					event.onShakeChanged(currenForce);
				}
			}

			lastX = currentX;
			lastY = currentY;
			lastZ = currentZ;

			if (event != null) {
				event.onDirection(_direction, currentX, currentY, currentZ);
			}
		}
	}

	private class SensorListener implements SensorEventListener {

		final float[] accelerometerValues;

		final float[] magneticFieldValues;

		SensorListener(float[] accelerometerValues, float[] magneticFieldValues) {
			this.accelerometerValues = accelerometerValues;
			this.magneticFieldValues = magneticFieldValues;
		}

		@Override
		public void onAccuracyChanged(Sensor a, int b) {

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				if (!(LSystem.base() != null && LSystem.base().setting
						.landscape())) {
					System.arraycopy(event.values, 0, accelerometerValues, 0,
							accelerometerValues.length);
				} else {
					accelerometerValues[0] = event.values[1];
					accelerometerValues[1] = -event.values[0];
					accelerometerValues[2] = event.values[2];
				}
			}
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				System.arraycopy(event.values, 0, magneticFieldValues, 0,
						magneticFieldValues.length);
			}
			onSensor(accelerometerValues);
			_state.getAcceleration().set(currentX, currentY, currentZ);
			try {
				Thread.sleep(_sleep);
			} catch (InterruptedException e) {
			}
		}
	}

	class SensorProcess extends RealtimeProcess {

		final float[] accelerometerValues;

		public SensorProcess(float[] values) {
			this.accelerometerValues = values;
			this.setDelay(_sleep);
		}

		public void run(LTimerContext context) {
			if (_state.isConnected()) {
				accelerometerValues[2] = -1f;
				if (SysKey.isDown() && SysKey.getKeyCode() == SysKey.LEFT) {
					accelerometerValues[0]--;
				}
				if (SysKey.isDown() && SysKey.getKeyCode() == SysKey.RIGHT) {
					accelerometerValues[0]++;
				}
				if (SysKey.isDown() && SysKey.getKeyCode() == SysKey.UP) {
					accelerometerValues[1]++;
				}
				if (SysKey.isDown() && SysKey.getKeyCode() == SysKey.DOWN) {
					accelerometerValues[1]--;
				}
				onSensor(accelerometerValues);
				_state.getAcceleration().set(currentX, currentY, currentZ);
			}
		}
	}

	@Override
	public void start() {
		// 模拟器下启动时键盘模拟重力
		if (AndroidGame.isEmulator()) {
			_state.setConnected(true);
			sensorProcess = new SensorProcess(accelerometerValues);
			RealtimeProcessManager.get().addProcess(sensorProcess);
			return;
		}
		if (!_state.isConnected() && manager == null) {
			manager = (SensorManager) _game
					.getSystemService(Context.SENSOR_SERVICE);
			if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() == 0) {
				_state.setConnected(false);
			} else {
				Sensor accelerometer = manager.getSensorList(
						Sensor.TYPE_ACCELEROMETER).get(0);
				accelerometerListener = new SensorListener(
						this.accelerometerValues, this.magneticFieldValues);
				_state.setConnected(manager.registerListener(
						accelerometerListener, accelerometer,
						SensorManager.SENSOR_DELAY_GAME));
			}
			// 如果无法正常启动，则开启伪重力感应
			if (!_state.isConnected()) {
				_state.setConnected(true);
				sensorProcess = new SensorProcess(accelerometerValues);
				RealtimeProcessManager.get().addProcess(sensorProcess);
			}
		}
	}

	public void stop() {
		if (manager != null) {
			if (accelerometerListener != null) {
				manager.unregisterListener(accelerometerListener);
				accelerometerListener = null;
			}
			manager = null;
			_state.setConnected(false);
		} else {
			_state.setConnected(false);
		}
	}

	@Override
	public float getLastX() {
		return lastX;
	}

	@Override
	public float getLastY() {
		return lastY;
	}

	@Override
	public float getLastZ() {
		return lastZ;
	}

	@Override
	public float getX() {
		return currentX;
	}

	@Override
	public float getY() {
		return currentY;
	}

	@Override
	public float getZ() {
		return currentZ;
	}

	@Override
	public final AccelerometerState getState() {
		return _state;
	}

	@Override
	public int getSleep() {
		return _sleep;
	}

	@Override
	public void sleep(int sleep) {
		this._sleep = sleep;
		if (sensorProcess != null) {
			sensorProcess.setDelay(sleep);
		}
	}

	/**
	 * 屏幕方向(返回Config类中配置值)
	 * 
	 * @return
	 */
	@Override
	public int getAllDirection() {
		return _all_direction;
	}

	@Override
	public Event getEvent() {
		return event;
	}

	/**
	 * 事件监听
	 * 
	 * @param event
	 */
	@Override
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * 屏幕旋转度数
	 * 
	 * @return
	 */
	@Override
	public float getOrientation() {
		return orientation;
	}

	/**
	 * 返回四方向手机朝向
	 * 
	 * @return
	 */
	@Override
	public SensorDirection getDirection() {
		return _direction;
	}
}

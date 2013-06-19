/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core.input;

import loon.action.map.Config;
import loon.core.LSystem;
import loon.core.geom.Vector3f;
import loon.core.input.LInputFactory.Key;
import loon.utils.MathUtils;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

//0.3.3版修改后重力感应方式，与原有的Screen重力感应方式不同，该类非绑定于Screen
public class LAccelerometer {

	public static enum SensorDirection {
		EMPTY, LEFT, RIGHT, UP, DOWN;
	}

	public interface Event {

		public void onDirection(SensorDirection direction, float x, float y,
				float z);

		public void onShakeChanged(float force);
	}

	// 四方向手机朝向
	private SensorDirection _direction = SensorDirection.EMPTY;

	// 八方向手机朝向
	private int _all_direction = Config.EMPTY;

	private Event event;

	private long lastUpdate;

	private float currentX, currentY, currentZ, currenForce;

	private float lastX, lastY, lastZ;

	private float orientation, magnitude;

	public LAccelerometer() {
	
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
		if (CheckAngle(0, orientation) || CheckAngle(360, orientation)) {
			_all_direction = Config.TUP;
			_direction = SensorDirection.UP;
		} else if (CheckAngle(45, orientation)) {
			_all_direction = Config.LEFT;
			_direction = SensorDirection.LEFT;
		} else if (CheckAngle(90, orientation)) {
			_all_direction = Config.TLEFT;
			_direction = SensorDirection.LEFT;
		} else if (CheckAngle(135, orientation)) {
			_all_direction = Config.DOWN;
			_direction = SensorDirection.LEFT;
		} else if (CheckAngle(180, orientation)) {
			_all_direction = Config.TDOWN;
			_direction = SensorDirection.DOWN;
		} else if (CheckAngle(225, orientation)) {
			_all_direction = Config.RIGHT;
			_direction = SensorDirection.RIGHT;
		} else if (CheckAngle(270, orientation)) {
			_all_direction = Config.TRIGHT;
			_direction = SensorDirection.RIGHT;
		} else if (CheckAngle(315, orientation)) {
			_all_direction = Config.UP;
			_direction = SensorDirection.RIGHT;
		} else {
			_all_direction = Config.EMPTY;
			_direction = SensorDirection.EMPTY;
		}
	}

	public static boolean CheckAngle(float angle, float actual) {
		return actual > angle - 22.5f && actual < angle + 22.5f;
	}

	private final void onSensor(float[] values) {
		synchronized (this) {

			long curTime = System.currentTimeMillis();

			if (LSystem.SCREEN_LANDSCAPE) {
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

	public class AccelerometerState {

		boolean _isConnected;

		final Vector3f _acceleration = new Vector3f();

		public Vector3f getAcceleration() {
			return _acceleration;
		}

		public boolean isConnected() {
			return _isConnected;
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
				if (!LSystem.SCREEN_LANDSCAPE) {
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
			_state._acceleration.set(currentX, currentY, currentZ);
			try {
				Thread.sleep(_sleep);
			} catch (InterruptedException e) {
			}
		}
	}

	class SensorThread extends Thread {

		final float[] accelerometerValues;

		public SensorThread(float[] values) {
			this.accelerometerValues = values;
		}

		@Override
		public void run() {
			while (_state._isConnected) {
				accelerometerValues[2] = -1f;
				if (Key.isDown() && Key.getKeyCode() == Key.LEFT) {
					accelerometerValues[0]--;
				}
				if (Key.isDown() && Key.getKeyCode() == Key.RIGHT) {
					accelerometerValues[0]++;
				}
				if (Key.isDown() && Key.getKeyCode() == Key.UP) {
					accelerometerValues[1]++;
				}
				if (Key.isDown() && Key.getKeyCode() == Key.DOWN) {
					accelerometerValues[1]--;
				}
				onSensor(accelerometerValues);
				_state._acceleration.set(currentX, currentY, currentZ);
				try {
					Thread.sleep(_sleep);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private final AccelerometerState _state = new AccelerometerState();

	private int _sleep = 30;

	private SensorManager manager;

	private final float[] accelerometerValues = new float[3];

	private final float[] magneticFieldValues = new float[3];

	private SensorEventListener accelerometerListener;

	public void start() {
		// 模拟器下启动时键盘模拟重力
		if (LSystem.isEmulator()) {
			_state._isConnected = true;
			LSystem.callScreenRunnable(new SensorThread(accelerometerValues));
			return;
		}
		if (!_state._isConnected && manager == null) {
			manager = (SensorManager) LSystem.getActivity().getSystemService(
					Context.SENSOR_SERVICE);
			if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() == 0) {
				_state._isConnected = false;
			} else {
				Sensor accelerometer = manager.getSensorList(
						Sensor.TYPE_ACCELEROMETER).get(0);
				accelerometerListener = new SensorListener(
						this.accelerometerValues, this.magneticFieldValues);
				_state._isConnected = manager.registerListener(
						accelerometerListener, accelerometer,
						SensorManager.SENSOR_DELAY_GAME);
			}
			// 如果无法正常启动，则开启伪重力感应
			if (!_state._isConnected) {
				_state._isConnected = true;
				LSystem.callScreenRunnable(new SensorThread(accelerometerValues));
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
			_state._isConnected = false;
		} else {
			_state._isConnected = false;
		}
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

	public void sleep(int sleep) {
		this._sleep = sleep;
	}

	/**
	 * 屏幕方向(返回Config类中配置值)
	 * 
	 * @return
	 */
	public int getAllDirection() {
		return _all_direction;
	}

	public Event getEvent() {
		return event;
	}

	/**
	 * 事件监听
	 * 
	 * @param event
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * 屏幕旋转度数
	 * 
	 * @return
	 */
	public float getOrientation() {
		return orientation;
	}

	/**
	 * 返回四方向手机朝向
	 * 
	 * @return
	 */
	public SensorDirection getDirection() {
		return _direction;
	}

}

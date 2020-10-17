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
package loon;

import loon.action.collision.CollisionHelper;
import loon.action.map.Config;
import loon.events.SysKey;
import loon.utils.MathUtils;
import loon.utils.TimeUtils;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimerContext;

public class AccelerometerDefault implements Accelerometer {

	// 四方向手机朝向
	private SensorDirection _direction = SensorDirection.EMPTY;

	// 八方向手机朝向
	private int _all_direction = Config.EMPTY;

	private Event event;

	private long lastUpdate;

	private float currentX, currentY, currentZ, currenForce;

	private float lastX, lastY, lastZ;

	private float orientation, magnitude;

	public AccelerometerDefault() {

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

			currentX = values[0];
			currentY = values[1];
			currentZ = values[2];

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

	class SensorProcess extends RealtimeProcess {

		final float[] accelerometerValues;

		public SensorProcess(float[] values) {
			this.accelerometerValues = values;
			this.setDelay(_sleep);
			this.setProcessType(GameProcessType.Orientation);
		}

		@Override
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

	private final AccelerometerState _state = new AccelerometerState();

	private int _sleep = 30;

	private final float[] accelerometerValues = new float[3];

	private SensorProcess sensorProcess;

	@Override
	public void start() {
		if (!_state.isConnected()) {
			_state.setConnected(true);
			sensorProcess = new SensorProcess(accelerometerValues);
			RealtimeProcessManager.get().addProcess(sensorProcess);
		}
	}

	@Override
	public void stop() {
		_state.setConnected(false);
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

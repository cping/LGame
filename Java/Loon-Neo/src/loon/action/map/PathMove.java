/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.action.map;

import loon.LRelease;
import loon.LSystem;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimerContext;

public class PathMove extends RealtimeProcess implements LRelease {

	public Vector2f position;

	public Vector2f origin;

	public Vector2f target;

	private float speed = 0f;
	private boolean horizontal;
	private boolean running;
	private boolean closed;

	/**
	 * 把当前位置向指定目标位置以指定速度移动
	 * 
	 * @param origin 初始位置
	 * @param target 指定位置
	 * @param speed  移动速度
	 */
	public PathMove(Vector2f origin, Vector2f target, float speed) {
		this.setPosition(origin);
		this.setOrigin(origin);
		this.setTarget(target);
		this.speed = speed;
		horizontal = origin.y == target.y;
	}

	public PathMove begin() {
		this.running = true;
		return this;
	}

	public PathMove end() {
		this.running = false;
		return this;
	}

	public boolean isRunning() {
		return this.running;
	}

	public void update(long elapsedTime) {
		update(MathUtils.max(elapsedTime / 1000f, LSystem.MIN_SECONE_SPEED_FIXED));
	}

	public void update(LTimerContext context) {
		update(context.getMilliseconds());
	}

	public void update(float dt) {
		if (this.running) {
			if (this.horizontal) {
				if (origin.x < target.x) {
					if (position.x < target.x && position.x + speed * dt < target.x) {
						float next = position.x + speed * dt;
						position.set(next, position.y);
					} else {
						position.set(target.x, target.y);
						running = false;
					}
				} else {
					if (position.x > target.x && position.x - speed * dt > target.x) {
						float next = position.x - speed * dt;
						position.set(next, position.y);
					} else {
						position.set(target.x, target.y);
						running = false;
					}
				}
			} else {
				if (origin.y < target.y && position.y + speed * dt < target.y) {
					if (position.y < target.y) {
						float next = position.y + speed * dt;
						position.set(position.x, next);
					} else {
						position.set(target.x, target.y);
						running = false;
					}
				} else {
					if (position.y > target.y && position.y - speed * dt > target.y) {
						float next = position.y - speed * dt;
						position.set(position.x, next);
					} else {
						position.set(target.x, target.y);
						running = false;
					}
				}
			}
		}
	}

	@Override
	public void run(LTimerContext context) {
		update(context);
		if (isClosed()) {
			kill();
		}
	}

	public PathMove submit() {
		synchronized (RealtimeProcessManager.class) {
			RealtimeProcessManager.get().delete(this);
			setDelay(0);
			RealtimeProcessManager.get().addProcess(this);
		}
		return this;
	}

	public PathMove post() {
		return submit();
	}

	public PathMove pause() {
		return end();
	}

	public PathMove exit() {
		this.closed = false;
		return this;
	}

	public boolean isClosed() {
		return closed;
	}

	public Vector2f getPosition() {
		return position;
	}

	public PathMove setPosition(Vector2f pos) {
		if (pos != null) {
			this.position = pos;
		} else {
			this.position = new Vector2f();
		}
		return this;
	}

	public Vector2f getOrigin() {
		return origin;
	}

	public PathMove setOrigin(Vector2f o) {
		if (o != null) {
			this.origin = o;
		} else {
			this.origin = new Vector2f();
		}
		return this;
	}

	public Vector2f getTarget() {
		return target;
	}

	public PathMove setTarget(Vector2f t) {
		if (t != null) {
			this.target = t;
		} else {
			this.target = new Vector2f();
		}
		return this;
	}

	public float getSpeed() {
		return speed;
	}

	public PathMove setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public PathMove setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
		return this;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("PathMove");
		builder.kv("position", position).comma().kv("origin", origin).comma().kv("target", target).comma()
				.kv("speed", speed).comma().kv("horizontal", horizontal).comma().kv("running", running).comma()
				.kv("closed", closed);
		return builder.toString();
	}

	@Override
	public void close() {
		stop();
		close();
		exit();
	}

}

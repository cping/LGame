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
import loon.utils.timer.Duration;
import loon.utils.timer.LTimerContext;

/**
 * 工具用类,操作指定Vector2对象从当前位置向指定位置移动
 */
public class PathMove extends RealtimeProcess implements LRelease {

	public Vector2f origin = new Vector2f();

	public Vector2f target = new Vector2f();

	private float offsetX = 0f;
	private float offsetY = 0f;

	private float speed = 0f;

	private float startPosX = 0f;
	private float startPosY = 0f;

	private float endPosX = 0f;
	private float endPosY = 0f;

	private boolean limitDirection;

	private boolean horizontal;

	private boolean running;

	private boolean closed;

	private boolean onUp, onDown, onLeft, onRight;

	/**
	 * 把当前位置向指定目标位置以指定速度移动
	 * 
	 * @param origin 初始位置
	 * @param target 指定位置
	 * @param speed  移动速度
	 * @param limit  是否限制移动方向
	 */
	public PathMove(Vector2f origin, Vector2f target, float speed, boolean limit) {
		this.setOrigin(origin);
		this.setTarget(target);
		this.speed = MathUtils.max(0f, speed);
		this.limitDirection = limit;
		horizontal = origin.y == target.y;
	}

	public PathMove(Vector2f origin, Vector2f target, float speed) {
		this(origin, target, speed, false);
	}

	public PathMove(float srcX, float srcY, float destX, float destY, float speed, boolean limit) {
		this(Vector2f.at(srcX, srcY), Vector2f.at(destX, destY), speed, limit);
	}

	public PathMove(float srcX, float srcY, float destX, float destY, float speed) {
		this(srcX, srcY, destX, destY, speed, false);
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
		update(MathUtils.max(Duration.toS(elapsedTime), LSystem.MIN_SECONE_SPEED_FIXED));
	}

	public void update(LTimerContext context) {
		update(context.getMilliseconds());
	}

	public void update(float dt) {
		if (this.running) {
			float angle = (this.target.y - origin.y) / (this.target.x - origin.x);
			if (angle < 1f && angle > -1f) {
				if (this.target.x < origin.x) {
					onUp = false;
					onDown = false;
					onLeft = true;
					onRight = false;
				} else {
					onUp = false;
					onDown = false;
					onLeft = false;
					onRight = true;
				}
			} else if (angle > 1f || angle < -1f) {
				if (this.target.y < origin.y) {
					onUp = true;
					onDown = false;
					onLeft = false;
					onRight = false;
				} else {
					onUp = false;
					onDown = true;
					onLeft = false;
					onRight = false;
				}
			} else {
				if (this.target.x < origin.x) {
					onLeft = true;
					onRight = false;
				} else {
					onLeft = false;
					onRight = true;
				}
			}
			float newSpeed = dt + speed;
			// only direction
			if (limitDirection) {
				if (this.horizontal) {
					if (origin.x < target.x) {
						if (origin.x < target.x && origin.x + newSpeed < target.x) {
							float next = origin.x + newSpeed;
							origin.set(next + offsetX, origin.y + offsetY);
						} else {
							origin.set(target.x + offsetX, target.y + offsetY);
							running = false;
						}
					} else {
						if (origin.x > target.x && origin.x - newSpeed > target.x) {
							float next = origin.x - newSpeed;
							origin.set(next + offsetX, origin.y + offsetY);
						} else {
							origin.set(target.x + offsetX, target.y + offsetY);
							running = false;
						}
					}
				} else {
					if (origin.y < target.y && origin.y + newSpeed < target.y) {
						if (origin.y < target.y) {
							float next = origin.y + newSpeed;
							origin.set(origin.x + offsetX, next + offsetY);
						} else {
							origin.set(target.x + offsetX, target.y + offsetY);
							running = false;
						}
					} else {
						if (origin.y > target.y && origin.y - newSpeed > target.y) {
							float next = origin.y - newSpeed;
							origin.set(origin.x + offsetX, next + offsetY);
						} else {
							origin.set(target.x + offsetX, target.y + offsetY);
							running = false;
						}
					}
				}
			} else {
				float x = origin.getX();
				float y = origin.getY();
				int dirX = (int) (endPosX - startPosX);
				int dirY = (int) (endPosY - startPosY);
				int count = 0;
				if (dirX > 0) {
					if (x >= endPosX) {
						count++;
					} else {
						x += newSpeed;
					}
				} else if (dirX < 0) {
					if (x <= endPosX) {
						count++;
					} else {
						x -= newSpeed;
					}
				} else {
					count++;
				}
				if (dirY > 0) {
					if (y >= endPosY) {
						count++;
					} else {
						y += newSpeed;
					}
				} else if (dirY < 0) {
					if (y <= endPosY) {
						count++;
					} else {
						y -= newSpeed;
					}
				} else {
					count++;
				}

				float newX = x + offsetX;
				float newY = y + offsetY;
				this.running = count < 2;
				origin.set(newX, newY);
			}
		}
	}

	public String toDirectionString() {
		return Side.getDirectionName(getDirection());
	}

	public int getDirection() {
		if (this.target.equals(origin)) {
			return Config.EMPTY;
		}
		return Field2D.getDirection(this.origin, this.target);
	}

	@Override
	public void run(LTimerContext context) {
		update(context);
		if (isClosed()) {
			kill();
		}
	}

	public PathMove submit() {
		this.running = true;
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
		this.running = false;
		this.closed = true;
		return this;
	}

	public boolean isClosed() {
		return closed;
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
		this.startPosX = this.origin.x;
		this.startPosY = this.origin.y;
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
		this.endPosX = target.x;
		this.endPosY = target.y;
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

	public boolean isUp() {
		return onUp;
	}

	public boolean isDown() {
		return onDown;
	}

	public boolean isLeft() {
		return onLeft;
	}

	public boolean isRight() {
		return onRight;
	}

	public boolean isLimitDirection() {
		return limitDirection;
	}

	public PathMove setLimitDirection(boolean limitDirection) {
		this.limitDirection = limitDirection;
		return this;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public PathMove setOffsetX(float offsetX) {
		this.offsetX = offsetX;
		return this;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public PathMove setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		return this;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("PathMove");
		builder.kv("origin", origin).comma().kv("target", target).comma().kv("speed", speed).comma()
				.kv("horizontal", horizontal).comma().kv("running", running).comma().kv("closed", closed);
		return builder.toString();
	}

	@Override
	public void close() {
		super.close();
		exit();
	}

}

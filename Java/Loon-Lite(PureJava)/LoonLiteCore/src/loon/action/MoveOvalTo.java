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
package loon.action;

import loon.LSystem;
import loon.geom.Vector2f;
import loon.utils.Easing.EasingMode;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.timer.EaseTimer;

/**
 * 让指定对象环绕运动(与MoveRoundTo差异在于此类可以控制环绕的width和height范围大小，也就是走不规则椭圆),
 * 需要偏移或矫正显示位置时请设置setOffset参数
 */
public class MoveOvalTo extends ActionEvent {

	private final float angle;
	private final float startAngle;
	private final float width;
	private final float height;
	private final float rotateScale;
	private float currentPosX;
	private final float per;
	private Vector2f startPoint, oldStartPoint;
	private Vector2f centerPoint, oldCenterPoint;
	private int directionX = 1;
	private int directionY = -1;
	private final EaseTimer easeTimer;

	public MoveOvalTo(float startAngle, float angle, float width, float height, Vector2f centerPoint, float duration) {
		this(startAngle, angle, width, height, centerPoint, null, duration, LSystem.DEFAULT_EASE_DELAY,
				EasingMode.Linear);
	}

	public MoveOvalTo(float startAngle, float angle, float width, float height, Vector2f centerPoint, float duration,
			EasingMode easing) {
		this(startAngle, angle, width, height, centerPoint, null, duration, LSystem.DEFAULT_EASE_DELAY, easing);
	}

	public MoveOvalTo(float startAngle, float angle, float width, float height, Vector2f centerPoint, float duration,
			float delay, EasingMode easing) {
		this(startAngle, angle, width, height, centerPoint, null, duration, delay, easing);
	}

	public MoveOvalTo(float startAngle, float angle, float width, float height, Vector2f centerPoint,
			Vector2f startPoint, float duration, float delay, EasingMode easing) {
		if (angle > 360) {
			angle = 360;
		}
		this.angle = angle;
		this.width = width;
		this.startAngle = (startAngle % 360f);
		if (startPoint == null) {
			startPoint = new Vector2f();
		}
		initDirection();
		this.height = height;
		this.rotateScale = (MathUtils.abs(angle) / 90f);
		this.per = (this.rotateScale / duration);
		float radian = MathUtils.toRadians(this.startAngle);
		float oneDivrr = MathUtils.pow(MathUtils.cos(radian) / width, 2f)
				+ MathUtils.pow(MathUtils.sin(radian) / height, 2f);
		float radius = MathUtils.sqrt(1f / oneDivrr);
		float startY = this.startAngle == 0.0F ? height : (float) (radius * MathUtils.sin(radian));
		this.centerPoint = centerPoint;
		this.centerPoint.y += (this.directionY == 1 ? -startY : startY);
		this.startPoint = startPoint;
		this.easeTimer = new EaseTimer(duration, delay, easing);
		this.oldStartPoint = startPoint;
		this.oldCenterPoint = centerPoint;
	}

	private void initDirection() {
		int dx;
		int dy;
		if (this.startAngle > 90f) {
			dx = 1;
			dy = 1;
		} else {
			if (this.startAngle > 180f) {
				dx = -1;
				dy = 1;
			} else {
				if (this.startAngle > 270f) {
					dx = -1;
					dy = -1;
				} else {
					dx = 1;
					dy = -1;
				}
			}
		}
		this.directionX = dx;
		this.directionY = dy;
	}

	@Override
	public void update(long elapsedTime) {
		easeTimer.update(elapsedTime);
		if (easeTimer.isCompleted()) {
			_isCompleted = true;

			float radian = MathUtils.toRadians((this.startAngle + this.angle - 90f) % 360f);

			float oneDivrr = MathUtils.pow(MathUtils.cos(radian) / this.width, 2f)
					+ MathUtils.pow(MathUtils.sin(radian) / this.height, 2f);

			float radius = MathUtils.sqrt(1f / oneDivrr);
			float x = this.centerPoint.x + (radius * MathUtils.cos(radian));
			float y = this.centerPoint.y + (radius * MathUtils.sin(radian));

			movePos(x + offsetX, y + offsetY);
			return;
		}

		this.currentPosX = ((float) (this.per * easeTimer.getProgress()));
		if (this.currentPosX > 1f) {
			initDirection();
			int cnt = (int) (this.startAngle / 90f);
			while (this.currentPosX > 1f) {
				this.currentPosX -= 1f;
				cnt++;
				if (cnt >= 4) {
					cnt = 0;
				}
				int backupX = this.directionX;
				if ((cnt == 0) || (cnt == 2))
					this.directionX = (-this.directionX);
				else {
					this.directionY = (-this.directionY);
				}
				if ((backupX != this.directionX) && (this.currentPosX <= 1f)) {
					if (this.directionX == 1)
						this.currentPosX = (1f - this.currentPosX);
				} else if (this.currentPosX <= 1f)
					this.currentPosX = (1f - this.currentPosX);

			}

		}

		float addX = (this.angle > 0f ? this.currentPosX : -this.currentPosX) * this.width;
		this.startPoint.x = (this.centerPoint.x + addX * this.directionX);
		float addY = MathUtils
				.sqrt(this.height * this.height - this.height * this.height * addX * addX / (this.width * this.width));
		this.startPoint.y = (this.centerPoint.y + addY * this.directionY);

		movePos(this.startPoint.x + offsetX, this.startPoint.y + offsetY);
	}

	public MoveOvalTo reset() {
		easeTimer.reset();
		return this;
	}
	
	public MoveOvalTo loop(int count) {
		easeTimer.setLoop(count);
		return this;
	}

	public MoveOvalTo loop(boolean l) {
		easeTimer.setLoop(l);
		return this;
	}

	public boolean isLoop() {
		return easeTimer.isLoop();
	}
	
	public float getAngle() {
		return angle;
	}

	public float getStartAngle() {
		return startAngle;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getRotateScale() {
		return rotateScale;
	}

	public float getCurrentPosX() {
		return currentPosX;
	}

	public float getPer() {
		return per;
	}

	public int getDirectionX() {
		return directionX;
	}

	public int getDirectionY() {
		return directionY;
	}

	@Override
	public void onLoad() {
		if (startPoint == null || startPoint.getX() == -1 || startPoint.getY() == -1) {
			this.startPoint = new Vector2f(original.getX(), original.getY());
		}
		this.oldStartPoint.set(startPoint);
		this.oldCenterPoint.set(centerPoint);
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		MoveOvalTo moveoval = new MoveOvalTo(startAngle, angle, width, height, oldCenterPoint, oldStartPoint,
				easeTimer.getDuration(), easeTimer.getDelay(), easeTimer.getEasingMode());
		moveoval.set(this);
		return moveoval;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "moveoval";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startAngle", startAngle).comma().kv("angle", angle).comma().kv("rotateScale", rotateScale).comma()
				.kv("size", (width + " x " + height)).comma().kv("per", per).comma().kv("startPoint", startPoint)
				.comma().kv("currentPosX", currentPosX).comma().kv("directionX", directionX).comma()
				.kv("directionY", directionY).comma().kv("EaseTimer", easeTimer);
		return builder.toString();
	}
}

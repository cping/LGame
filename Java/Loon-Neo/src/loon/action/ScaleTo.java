/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.action;

import loon.utils.MathUtils;
import loon.utils.StringKeyValue;

public class ScaleTo extends ActionEvent {

	private float dt;

	private float deltaX, deltaY;

	private float startX = -1f, startY = -1f;

	private float endX, endY;

	private float speed;

	public ScaleTo(float s) {
		this(s, s);
	}

	public ScaleTo(float sx, float sy) {
		this(-1, -1, sx, sy, 0.1f);
	}

	public ScaleTo(float sx, float sy, float sp) {
		this(-1, -1, sx, sy, sp);
	}

	public ScaleTo(float stx, float sty, float sx, float sy, float sp) {
		this.startX = stx;
		this.startY = sty;
		this.endX = sx;
		this.endY = sy;
		this.speed = sp;
		this.deltaX = endX - startX;
		this.deltaY = endY - startY;
	}

	public void setSpeed(float s) {
		this.speed = s;
	}

	public float getSpeed() {
		return speed;
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public void onLoad() {
		if (original != null) {
			if (startX == -1) {
				startX = original.getScaleX();
			}
			if (startY == -1) {
				startY = original.getScaleY();
			}
			deltaX = endX - startX;
			deltaY = endY - startY;
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (original != null) {
			synchronized (original) {
				if (original != null) {
					dt += MathUtils.max((elapsedTime / 1000), speed);
					original.setScale(startX + (deltaX * dt), startY + (deltaY * dt));
					_isCompleted = (deltaX > 0 ? (original.getScaleX() >= endX) : (original.getScaleX() <= endX))
							&& (deltaY > 0 ? (original.getScaleY() >= endY) : (original.getScaleY() <= endY));
				}
			}
		} else {
			_isCompleted = true;
		}
		if (_isCompleted && original != null) {
			original.setScale(endX, endY);
		}
	}

	public float getDeltaX() {
		return deltaX;
	}

	public float getDeltaY() {
		return deltaY;
	}

	public float getStartX() {
		return startX;
	}

	public float getStartY() {
		return startY;
	}

	public float getEndX() {
		return endX;
	}

	public float getEndY() {
		return endY;
	}

	@Override
	public ActionEvent cpy() {
		ScaleTo scale = new ScaleTo(startX, startY, endX, endY, speed);
		scale.set(this);
		return scale;
	}

	@Override
	public ActionEvent reverse() {
		ScaleTo scale = new ScaleTo(endX, endY, startX, startY, speed);
		scale.set(this);
		return scale;
	}

	@Override
	public String getName() {
		return "scale";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startX", startX).comma().kv("startY", startY).comma().kv("deltaX", deltaX).comma()
				.kv("deltaY", deltaY).comma().kv("endX", endX).comma().kv("endY", endY).comma().kv("speed", speed)
				.comma().kv("delta", dt);
		return builder.toString();
	}

}

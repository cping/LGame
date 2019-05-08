/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

import loon.canvas.LColor;
import loon.utils.StringKeyValue;

/**
 * 没有过渡效果,直接让指定ActionBind对象[立刻]完成指定参数转换
 */
public class TransformTo extends ActionEvent {

	private float newX = -1f;

	private float newY = -1f;

	private float newScaleX = -1f;

	private float newScaleY = -1f;

	private float newRotation = -1f;

	private float newAlpha = -1f;

	private LColor newColor = null;

	private float oldX = 0f;

	private float oldY = 0f;

	private float oldScaleX = 0f;

	private float oldScaleY = 0f;

	private float oldRotation = 0f;

	private float oldAlpha = 0f;

	private LColor oldColor = null;

	public static TransformTo pos(float x, float y) {
		return new TransformTo(x, y);
	}

	public static TransformTo scale(float scaleX, float scaleY) {
		return new TransformTo(-1, -1, scaleX, scaleY, -1f, -1f, null);
	}

	public static TransformTo alpha(float alpha) {
		return new TransformTo(-1, -1, -1f, -1f, -1f, alpha, null);
	}

	public static TransformTo rotation(float rotation) {
		return new TransformTo(-1, -1, -1f, -1f, rotation, -1f, null);
	}

	public static TransformTo color(LColor color) {
		return new TransformTo(-1, -1, -1f, -1f, -1f, -1f, color);
	}

	public TransformTo(float x, float y) {
		this(x, y, -1f, -1f, -1f, -1f, null);
	}

	public TransformTo(float x, float y, float scaleX, float scaleY, float rotation, float alpha, LColor color) {
		this.newX = x;
		this.newY = y;
		this.newScaleX = scaleX;
		this.newScaleY = scaleY;
		this.newRotation = rotation;
		this.newAlpha = alpha;
		this.newColor = color;
	}

	@Override
	public void update(long elapsedTime) {
		if (newX != -1) {
			original.setX(newX);
		}
		if (newY != -1) {
			original.setY(newY);
		}
		if (newScaleX != -1 && newScaleY != -1) {
			original.setScale(newScaleX, newScaleY);
		} else if (newScaleX != -1) {
			original.setScale(newScaleX, original.getScaleY());
		} else if (newScaleY != -1) {
			original.setScale(original.getScaleY(), newScaleY);
		}
		if (newRotation != -1) {
			original.setRotation(newRotation);
		}
		if (newAlpha != -1) {
			original.setRotation(newRotation);
		}
		if (newColor != null) {
			original.setColor(newColor);
		}
		this._isCompleted = true;
	}

	@Override
	public void onLoad() {
		if (original != null) {
			oldX = original.getX();
			oldY = original.getY();
			oldScaleX = original.getScaleX();
			oldScaleY = original.getScaleY();
			oldRotation = original.getRotation();
			oldAlpha = original.getAlpha();
			oldColor = original.getColor();
		}
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		TransformTo trans = new TransformTo(newX, newY, newScaleX, newScaleY, newRotation, newAlpha, newColor);
		trans.set(this);
		return trans;
	}

	@Override
	public ActionEvent reverse() {
		TransformTo trans = new TransformTo(oldX, oldY, oldScaleX, oldScaleY, oldRotation, oldAlpha, oldColor);
		trans.set(this);
		return trans;
	}

	@Override
	public String getName() {
		return "transform";
	}
	
	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("newX", newX)
		.comma()
		.kv("newY", newY)
		.comma()
		.kv("newScaleX",newScaleX)
		.comma()
		.kv("newScaleY",newScaleY)
		.comma()
		.kv("newRotation",newRotation)
		.comma()
		.kv("newAlpha", newAlpha)
		.comma()
		.kv("newColor", newColor);
		return builder.toString();
	}
}

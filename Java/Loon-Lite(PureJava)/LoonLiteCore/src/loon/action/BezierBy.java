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

import loon.geom.Bezier;
import loon.geom.Vector2f;
import loon.utils.StringKeyValue;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

public class BezierBy extends ActionEvent {

	protected Bezier bezier;

	protected float startX = -1, startY = -1;

	public BezierBy(float duration, Bezier b) {
		this(-1, -1, duration, EasingMode.Linear, b);
	}

	public BezierBy(float sx, float sy, float duration, Bezier b) {
		this(sx, sy, duration, EasingMode.Linear, b);
	}

	public BezierBy(float sx, float sy, float duration, EasingMode mode, Bezier b) {
		this._easeTimer = new EaseTimer(duration, mode);
		this.bezier = b;
		this.startX = sx;
		this.startY = sy;
	}

	@Override
	public void update(long elapsedTime) {
		_easeTimer.update(elapsedTime);
		if (_easeTimer.isCompleted()) {
			_isCompleted = true;
			return;
		}

		final float u = 1 - _easeTimer.getProgress();
		final float tt = _easeTimer.getProgress() * _easeTimer.getProgress();
		final float uu = u * u;

		final float ut2 = 2 * u * _easeTimer.getProgress();

		final float x = (uu * bezier.controlPoint1.x) + (ut2 * bezier.controlPoint2.x) + (tt * bezier.endPosition.x);
		final float y = (uu * bezier.controlPoint1.y) + (ut2 * bezier.controlPoint2.x) + (tt * bezier.endPosition.y);

		movePos(startX + x, startY + y);

	}

	@Override
	public void onLoad() {
		if (original != null && startX == -1f && startY == -1f) {
			startX = original.getX();
			startY = original.getY();
		}
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		BezierBy by = new BezierBy(startX, startY, _easeTimer.getDuration(), _easeTimer.getEasingMode(), bezier.cpy());
		by.set(this);
		return this;
	}

	@Override
	public ActionEvent reverse() {
		Bezier b = new Bezier();
		b.endPosition = bezier.endPosition.negate();
		b.controlPoint1 = Vector2f.addNew(bezier.controlPoint2, bezier.endPosition.negate());
		b.controlPoint2 = Vector2f.addNew(bezier.controlPoint1, bezier.endPosition.negate());
		BezierBy by = new BezierBy(startX, startY, _easeTimer.getDuration(), _easeTimer.getEasingMode(), b);
		by.set(this);
		return this;
	}

	public float getStartX() {
		return startX;
	}

	public float getStartY() {
		return startY;
	}

	@Override
	public String getName() {
		return "bezierby";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("bezier", bezier).comma().kv("startX", startX).comma().kv("startY", startY).comma().kv("EaseTimer",
				_easeTimer);
		return builder.toString();
	}

}

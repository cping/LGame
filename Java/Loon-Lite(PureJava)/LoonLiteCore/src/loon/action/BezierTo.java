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

public class BezierTo extends BezierBy {

	final Bezier originalconfig;

	public BezierTo(float duration, Bezier b) {
		this(-1, -1, duration, EasingMode.Linear, b);
	}

	public BezierTo(float sx, float sy, float duration, Bezier b) {
		this(sx, sy, duration, EasingMode.Linear, b);
	}

	public BezierTo(float sx, float sy, float duration, EasingMode mode, Bezier b) {
		super(sx, sy, duration, mode, b);
		originalconfig = new Bezier();
		originalconfig.controlPoint1 = Vector2f.at(b.controlPoint1.x, b.controlPoint1.y);
		originalconfig.controlPoint2 = Vector2f.at(b.controlPoint2.x, b.controlPoint2.y);
		originalconfig.endPosition = Vector2f.at(b.endPosition.x, b.endPosition.y);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		Vector2f startPosition = new Vector2f(startX, startY);
		bezier.controlPoint1.set(Vector2f.subNew(originalconfig.controlPoint1, startPosition));
		bezier.controlPoint2.set(Vector2f.subNew(originalconfig.controlPoint2, startPosition));
		bezier.endPosition.set(Vector2f.subNew(originalconfig.endPosition, startPosition));
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		BezierTo bto = new BezierTo(startX, startY, _easeTimer.getDuration(), _easeTimer.getEasingMode(), bezier.cpy());
		bto.set(this);
		return bto;
	}

	@Override
	public ActionEvent reverse() {
		Bezier b = new Bezier();
		b.endPosition = bezier.endPosition.negate();
		b.controlPoint1 = Vector2f.addNew(bezier.controlPoint2, bezier.endPosition.negate());
		b.controlPoint2 = Vector2f.addNew(bezier.controlPoint1, bezier.endPosition.negate());
		BezierTo bto = new BezierTo(startX, startY, _easeTimer.getDuration(), _easeTimer.getEasingMode(), b);
		bto.set(this);
		return bto;
	}

	@Override
	public String getName() {
		return "bezierto";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("bezierOriginal", originalconfig).comma().kv("bezier", bezier).comma().kv("startX", startX).comma()
				.kv("startY", startY).comma().kv("EaseTimer", _easeTimer);
		return builder.toString();
	}
}

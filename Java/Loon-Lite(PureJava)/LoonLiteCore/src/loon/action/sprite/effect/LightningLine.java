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
package loon.action.sprite.effect;

import loon.LTexture;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.GLEx.Direction;
import loon.utils.MathUtils;

/*
 * 单纯绘制一个电元素
 */
public class LightningLine {

	private final static float imageThickness = 8;

	private Vector2f _capOrigin = new Vector2f();
	private Vector2f _middleOrigin = new Vector2f();
	private Vector2f _middleScale = new Vector2f();

	protected Vector2f _lineA;
	protected Vector2f _lineB;
	protected float _thickness;

	public LightningLine(Vector2f a, Vector2f b) {
		this(a, b, 1f);
	}

	public LightningLine(Vector2f a, Vector2f b, float t) {
		this._lineA = a;
		this._lineB = b;
		this._thickness = t;
	}

	public void draw(GLEx g, float offsetX, float offsetY, LColor tint) {

		final Vector2f tangent = _lineB.sub(_lineA);
		final float theta = MathUtils.atan2(tangent.y, tangent.x);
		final float thicknessScale = _thickness / imageThickness;

		final LTexture HalfCircle = LightningEffect.get().getHalfCircle();
		final LTexture LightningSegment = LightningEffect.get().getLightningSegment();

		final float rotate = theta * MathUtils.RAD_TO_DEG;

		_capOrigin.set(HalfCircle.getWidth(), HalfCircle.getHeight() / 2f);
		_middleOrigin.set(0, LightningSegment.getHeight() / 2f);
		_middleScale.set(tangent.length(), thicknessScale);

		Vector2f pos = _lineA.add(offsetX, offsetY);
		g.draw(LightningSegment, pos.x, pos.y, tint, rotate, _middleOrigin, _middleScale, Direction.TRANS_NONE, true);
		g.draw(HalfCircle, pos.x, pos.y, tint, rotate, _capOrigin, thicknessScale, Direction.TRANS_NONE, true);
		pos = _lineB.add(offsetX, offsetY);
		g.draw(HalfCircle, pos.x, pos.y, tint, (theta + MathUtils.PI) * MathUtils.RAD_TO_DEG, _capOrigin,
				thicknessScale, Direction.TRANS_NONE, true);

	}
}

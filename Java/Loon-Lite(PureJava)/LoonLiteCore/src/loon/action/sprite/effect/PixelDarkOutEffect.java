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

import loon.LSystem;
import loon.canvas.LColor;
import loon.opengl.GLEx;

/**
 * 像素化三角旋转效果(离开画面),用来代表技能释放
 */
public class PixelDarkOutEffect extends PixelBaseEffect {

	private float _viewX;

	private float _viewY;

	private TriangleEffect[] _de;

	public PixelDarkOutEffect(LColor color) {
		this(color, 0, 0, LSystem.viewSize.getWidth() / 2, LSystem.viewSize.getHeight() / 2);
	}

	public PixelDarkOutEffect(LColor color, float x, float y, float w, float h) {
		super(color, x, y, w, h);
		this._viewX = x;
		this._viewY = y;
		float[][] res1 = { { 0.0f, 30f }, { 24f, -15f }, { -24f, -15f } };
		float[][] res2 = { { 24f, 15f }, { -24f, 15f }, { 0.0f, -30f } };
		this._de = new TriangleEffect[4];
		this._de[0] = new TriangleEffect(w, h, res1, 0, 0, -9f);
		this._de[1] = new TriangleEffect(w, h, res2, 0, 0, -9f);
		this._de[2] = new TriangleEffect(w, h, res1, 0, 0, -9f);
		this._de[3] = new TriangleEffect(w, h, res2, 0, 0, -9f);
		this._limit = 90;
		this._triangleEffects.add(_de);
		this.setDelay(0);
		setEffectDelay(0);
	}

	@Override
	public void draw(GLEx g, float tx, float ty) {
		if (super._completed) {
			return;
		}
		float x = _viewX - tx;
		float y = _viewY - ty;
		int tmp = g.color();
		g.setColor(_baseColor);
		if (super._frame == 40) {
			_de[0].setMoveX(5f);
			_de[1].setMoveX(-5f);
			_de[2].setMoveY(5f);
			_de[3].setMoveY(-5f);
		}
		for (TriangleEffect element : _de) {
			element.drawPaint(g, x, y);
		}
		if (super._frame >= _limit) {
			this._completed = true;
		}
		g.setColor(tmp);
	}

}

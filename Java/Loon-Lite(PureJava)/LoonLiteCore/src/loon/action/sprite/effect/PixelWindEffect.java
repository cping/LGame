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
import loon.utils.MathUtils;

/**
 * 像素化风
 */
public class PixelWindEffect extends PixelBaseEffect {

	private int randSeed;

	private TriangleEffect[] sd;

	private LColor[] colors;

	public PixelWindEffect(LColor color) {
		this(color, 32, 32, 64);
	}

	public PixelWindEffect(LColor color, int s, int r, int time) {
		this(color, s, r, time, 160);
	}

	public PixelWindEffect(LColor color, int s, int r, int time, int limit) {
		super(color, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
		this.randSeed = r;
		this.limit = limit;
		this.sd = new TriangleEffect[s];
		this.colors = new LColor[s];
		this.triangleEffects.add(sd);
		this.setDelay(0);
		setEffectDelay(0);
	}

	@Override
	public void draw(GLEx g, float tx, float ty) {
		if (super.completed) {
			return;
		}
		if (super.frame > limit) {
			super.completed = true;
		}
		int tmp = g.color();
		g.setColor(_baseColor);
		for (int i = 0; i < sd.length; i++) {
			if (sd[i] != null) {
				continue;
			}
			if (MathUtils.random(100) >= randSeed) {
				break;
			}
			float[][] res = { { 0.0f, 10f }, { 8f, -5f }, { -8f, -5f } };
			int index = MathUtils.random(3) + 1;
			for (int j = 0; j < res.length; j++) {
				for (int c = 0; c < res[j].length; c++) {
					res[j][c] *= index;
				}
			}
			int x = MathUtils.random(32) + 16;
			float y = x / 10;
			sd[i] = new TriangleEffect(res, x, y, MathUtils.random(24) + 24);
			sd[i].setPosY(MathUtils.random(_height));
			sd[i].setPosX(0f);
			colors[i] = new LColor(_baseColor.getRed(), (int) (_baseColor.g * (128 + MathUtils.random(128))),
					(int) (_baseColor.b * MathUtils.random(128)));
		}

		for (int j = 0; j < sd.length; j++) {
			if (colors[j] == null) {
				continue;
			}
			g.setColor(colors[j]);
			if (sd[j] == null) {
				continue;
			}
			sd[j].drawPaint(g, 0, 0);
			if (sd[j].getPosX() > _width || sd[j].getPosY() > _height) {
				sd[j] = null;
			}
		}

		g.setColor(tmp);
	}

}

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

public class PixelThunderEffect extends PixelBaseEffect {

	private float viewX, viewY;

	private float width;

	public PixelThunderEffect(LColor color) {
		this(color, LSystem.viewSize.width / 2, LSystem.viewSize.height - 100);
	}

	public PixelThunderEffect(LColor color, float x, float y) {
		this(color, x, y, LSystem.viewSize.width, LSystem.viewSize.height, 3);
	}

	public PixelThunderEffect(LColor color, float x, float y, float w, float h, float width) {
		super(color, x, y, w, h);
		this.width = width;
		this.viewX = x;
		this.viewY = y;
		this.limit = 50;
		this.setDelay(0);
		setEffectDelay(0);
	}

	@Override
	public void draw(GLEx g, float tx, float ty) {
		if (super.completed) {
			return;
		}
		int tmp = g.color();
		g.setColor(_baseColor);
		float x = viewX - tx;
		float y = viewY - ty;
		int f = super.frame;
		g.setColor(_baseColor);
		if (f <= 20) {
			float size = y - (getWidth() * (20 - super.frame)) / 20;
			g.setAlpha(0.5f);
			g.drawLine(x, size - 100, x, size, width);
			g.drawLine(x + 1, (size - 100) + 1, x + 1, size - 1, width);
			g.drawLine(x - 1, (size - 100) + 1, x - 1, size - 1, width);
			g.setAlpha(1f);
		} else {
			f -= 20;
			for (int j = 0; j < 6; j++) {
				g.drawOval(x - f * 6, y - f - j, f * 12, f * 2 + j * 2);
			}
		}
		g.setColor(tmp);
		if (super.frame >= limit) {
			super.completed = true;
		}
	}

}

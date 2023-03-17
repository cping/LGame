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
package loon.action.sprite.effect.explosion;

import loon.LTexture;
import loon.canvas.LColor;
import loon.geom.RectI;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public abstract class Fragment {

	protected float cx;
	protected float cy;
	protected float ox;
	protected float oy;
	protected int color;

	protected float width;
	protected float height;
	protected float alpha;
	protected RectI parBound;
	protected LTexture ovalTexture;

	private LColor imgColor = LColor.white.cpy();

	public Fragment(int color, float x, float y, RectI bound, LTexture tex) {
		this.color = color;
		this.cx = x;
		this.cy = y;
		this.parBound = bound;
		ovalTexture = tex;
	}

	public void reset(){
		this.cx = this.cy = 0;
		this.alpha = 0f;
	}

	protected void update(float factor) {
		width = width - factor * MathUtils.nextInt(2);
		height = height - factor * MathUtils.nextInt(2);
		alpha = (1f - factor);
	}

	protected abstract void caculate(float factor);

	protected void oval(GLEx g, float x, float y) {
		int tint = g.color();
		if (ovalTexture != null) {
			imgColor.setColor(LColor.alpha(color, alpha));
			g.setColor(imgColor);
			g.draw(ovalTexture, x + cx, y + cy, width, height, imgColor);
		} else {
			g.setColor(LColor.alpha(color, alpha));
			g.fillOval(x + cx, y + cy, width, height);
		}
		g.setTint(tint);
	}

	public void draw(GLEx g, float x, float y, float factor) {
		caculate(factor);
		oval(g, x, y);
	}
}

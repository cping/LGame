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

import loon.canvas.LColor;
import loon.opengl.GLEx;

public class PixelChopEffect extends PixelBaseEffect {

	private float t_x, t_y;

	private float width = 3;

	public PixelChopEffect(LColor color, float x, float y) {
		this(color, x, y, 3);
	}

	public PixelChopEffect(LColor color, float x, float y, float width) {
		super(color, x, y, 0, 0);
		this.width = width;
		this.t_x = x;
		this.t_y = y;
		this.limit = 25;
		setDelay(0);
		setEffectDelay(0);
	}

	@Override
	public void draw(GLEx g, float tx, float ty) {
		if (super.completed) {
			return;
		}
		int tmp = g.color();
		g.setColor(_baseColor);
		float x = t_x - tx;
		float y = t_y - ty;
		int f = super.frame;
		if (f > 25) {
			f = 25 - f;
		}
		float x1 = x - f;
		float y1 = y - f;
		float x2 = x + f;
		float y2 = y + f;
		g.drawLine(x1, y1, x2, y2, width);
		g.setColor(tmp);
		if (super.frame >= limit) {
			super.completed = true;
		}
	}

}

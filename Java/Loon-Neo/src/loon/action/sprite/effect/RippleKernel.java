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

import loon.action.sprite.effect.RippleEffect.Model;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

public class RippleKernel {

	protected LColor color;
	
	protected float x, y;
	
	protected int existTime;
	
	protected int limit;
	
	protected LTimer timer = new LTimer(0);

	public RippleKernel(float x, float y) {
		this(x, y, 25);
	}

	public RippleKernel(float x, float y, int l) {
		this.x = x;
		this.y = y;
		this.existTime = 0;
		this.limit = l;
	}

	public void draw(final GLEx g, Model model, float mx, float my) {
		int span = existTime * 2;
		switch (model) {
		case OVAL:
			g.drawOval(mx + x - span / 2, my + y - span / 2, span, span);
			break;
		case RECT:
			g.drawRect(mx + x - span / 2, my + y - span / 2, span, span);
			break;
		}
		existTime++;
	}

	public boolean isExpired() {
		return existTime >= limit;
	}

}

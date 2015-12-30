/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
package loon.srpg.effect;

import loon.canvas.LColor;
import loon.opengl.GLEx;

// 默认使用的斩击特效
public class SRPGChopEffect extends SRPGEffect {

	private int t_x, t_y;

	private final static LColor c1 = new LColor(255, 0, 0), c2 = new LColor(
			255, 32, 32), c3 = new LColor(255, 64, 64);

	public SRPGChopEffect(int x, int y) {
		this.t_x = x;
		this.t_y = y;
		this.setExist(true);
	}

	@Override
	public void draw(GLEx g, int tx, int ty) {
		next();
		int x = t_x - tx;
		int y = t_y - ty;
		int f = super.frame;
		if (f > 15) {
			f = 15 - f;
		}
		int x1 = x - f;
		int y1 = y - f;
		int x2 = x + f;
		int y2 = y + f;
		g.setColor(c1);
		g.drawLine(x1, y1, x2, y2);
		g.setColor(c2);
		g.drawLine(x1 + 1, y1 + 1 + 1, x2 - 1, (y2 - 1) + 1);
		g.drawLine(x1 + 1, (y1 + 1) - 1, x2 - 1, y2 - 1 - 1);
		g.setColor(c3);
		g.drawLine(x1 + 2, y1 + 2 + 1, x2 - 2, (y2 - 2) + 1);
		g.drawLine(x1 + 2, (y1 + 2) - 1, x2 - 2, y2 - 2 - 1);
		if (super.frame > 15) {
			setExist(false);
		}
	}

}

package loon.srpg.effect;

import loon.LSystem;
import loon.core.graphics.device.LColor;
import loon.core.graphics.opengl.GLEx;


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
// 默认的落雷魔法效果
public class SRPGThunderEffect extends SRPGEffect {

	private final static LColor color = new LColor(0, 64, 255);

	private int t_x, t_y;

	public SRPGThunderEffect(int x, int y) {
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
		if (f <= 20) {
			int size = y - (LSystem.screenRect.width * (20 - super.frame)) / 20;
			g.setColor(color);
			g.setAlpha(0.5F);
			g.drawLine(x, size - 100, x, size);
			g.drawLine(x + 1, (size - 100) + 1, x + 1, size - 1);
			g.drawLine(x - 1, (size - 100) + 1, x - 1, size - 1);
			g.setAlpha(1.0F);
		} else {
			g.setColor(LColor.white);
			f -= 20;
			for (int k1 = 0; k1 < 6; k1++) {
				g.drawOval(x - f * 6, y - f - k1, f * 12, f * 2 + k1 * 2);
			}
		}
		if (super.frame > 50) {
			setExist(false);
		}
	}

}

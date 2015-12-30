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

import loon.LSystem;
import loon.canvas.LColor;
import loon.opengl.GLEx;

// 一个默认的黑暗魔法特效
public class SRPGDarkEffect extends SRPGEffect {

	private int t_x, t_y;

	private TriangleEffect[] sd;

	public SRPGDarkEffect(int x, int y) {
		t_x = x;
		t_y = y;
		float[][][] res = { { { 0.0f, 30f }, { 24f, -15f }, { -24f, -15f } },
				{ { -120f, 30f }, { -96f, -15f }, { -144f, -15f } },
				{ { 120f, 30f }, { 144f, -15f }, { 96f, -15f } },
				{ { 0.0f, -90f }, { 24f, -135f }, { -24f, -135f } },
				{ { 0.0f, 150f }, { 24f, 105f }, { -24f, 105f } } };
		sd = new TriangleEffect[5];
		sd[0] = new TriangleEffect(res[0], 0.0f, 0.0f, 3f);
		sd[1] = new TriangleEffect(res[1], 3f, 0.0f, 9f);
		sd[2] = new TriangleEffect(res[2], -3f, 0.0f, 9f);
		sd[3] = new TriangleEffect(res[3], 0.0f, 3f, 9f);
		sd[4] = new TriangleEffect(res[4], 0.0f, -3f, 9f);
		setExist(true);
	}

	@Override
	public void draw(GLEx g, int tx, int ty) {
		next();
		int x = t_x - tx;
		int y = t_y - ty;
		g.setColor(LColor.black);
		if (super.frame < 40) {
			for (int i = 0; i < sd.length; i++) {
				sd[i].drawPaint(g, x, (LSystem.viewSize.height - y));
			}
		} else {
			int r = ((super.frame - 40) * 220) / 40;
			g.setColor(r, 0, 0);
			sd[0].drawPaint(g, x, (LSystem.viewSize.height - y));
		}
		if (super.frame > 80) {
			setExist(false);
		}
	}

}

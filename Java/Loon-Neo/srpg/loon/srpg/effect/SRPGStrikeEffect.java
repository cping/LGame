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
import loon.utils.MathUtils;


// 默认的基础魔法技能打击效果
public class SRPGStrikeEffect extends SRPGEffect {

	private int s_x;

	private int s_y;

	private int t_x;

	private int t_y;

	private int max;

	private LColor color;

	private TriangleEffect arrow;

	private TriangleEffect[] dif;

	public SRPGStrikeEffect(int x1, int y1, int x2, int y2) {
		this(x1, y1, x2, y2, LColor.black);
	}

	public SRPGStrikeEffect(int x1, int y1, int x2, int y2, LColor color) {
		this.setExist(true);
		this.s_x = x1;
		this.s_y = y1;
		this.t_x = x2;
		this.t_y = y2;
		this.color = color;
		this.max = 0;
		float d = x2 - x1;
		float d1 = y2 - y1;
		float d2 = MathUtils.sqrt(MathUtils.pow(d, 2f) + MathUtils.pow(d1, 2f));
		float d3 = (d / d2) * 8f;
		float d4 = (d1 / d2) * 8f;
		this.max = (int) (d2 / 8f + 0.5f);
		float res[][] = { { 8f, 0.0f }, { -4f, 6f }, { -4f, -6f } };
		float res1[][] = { { 2f, 0.0f }, { -1f, 2f }, { -1f, -2f } };
		float res2[][] = { { 1.0f, 0.0f }, { 0.0f, 1.0f }, { -1f, 0.0f },
				{ 0.0f, -1f }, { 0.70999999999999996f, 0.70999999999999996f },
				{ 0.70999999999999996f, -0.70999999999999996f },
				{ -0.70999999999999996f, 0.70999999999999996f },
				{ -0.70999999999999996f, -0.70999999999999996f } };
		this.arrow = new TriangleEffect(res, d3, d4 * -1f, 36f);
		this.dif = new TriangleEffect[8];
		for (int i1 = 0; i1 < dif.length; i1++) {
			dif[i1] = new TriangleEffect(res1, res2[i1][0], res2[i1][1] * -1f, 36f);
		}
	}

	@Override
	public void draw(GLEx g, int x, int y) {
		next();
		g.setColor(color);
		if (super.frame < max) {
			arrow.drawPaint(g, s_x - x, LSystem.viewSize.height - (s_y - y));
		} else {
			for (int i = 0; i < dif.length; i++) {
				dif[i].drawPaint(g, t_x - x, LSystem.viewSize.height
						- (t_y - y));
			}
		}
		if (super.frame >= max + 60) {
			setExist(false);
		}
	}

}

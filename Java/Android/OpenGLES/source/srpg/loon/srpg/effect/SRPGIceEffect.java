package loon.srpg.effect;

import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.utils.MathUtils;


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
// 默认为冰弹魔法效果，当然也可以通过改变颜色作为其他效果使用.
public class SRPGIceEffect extends SRPGEffect {

	private int s_x;

	private int s_y;

	private int t_x;

	private int t_y;

	private int max;

	private LColor color;

	private TriangleEffect arrow;

	private TriangleEffect[] dif;

	private TriangleEffect[][] child;

	public SRPGIceEffect(int x1, int y1, int x2, int y2) {
		this(x1, y1, x2, y2, LColor.white);
	}

	public SRPGIceEffect(int x1, int y1, int x2, int y2, LColor color) {
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
		float res[][] = { { 32f, 0.0f }, { -16f, 24f }, { -16f, -24f } };
		float res1[][] = { { 16f, 0.0f }, { -8f, 12f }, { -8f, -12f } };
		float res2[][] = { { 8f, 0.0f }, { -4f, 6f }, { -4f, -6f } };
		float res3[][] = { { 4f, 0.0f }, { 0.0f, 4f }, { -4f, 0.0f },
				{ 0.0f, -4f }, { 2.8300000000000001f, 2.8300000000000001f },
				{ 2.8300000000000001f, -2.8300000000000001f },
				{ -2.8300000000000001f, 2.8300000000000001f },
				{ -2.8300000000000001f, -2.8300000000000001f } };
		this.arrow = new TriangleEffect(res, d3, d4 * -1f, 36f);
		this.dif = new TriangleEffect[8];
		this.child = new TriangleEffect[dif.length][8];
		for (int i = 0; i < dif.length; i++) {
			dif[i] = new TriangleEffect(res1, res3[i][0], res3[i][1] * -1f, 36f);
			for (int j1 = 0; j1 < child[i].length; j1++) {
				child[i][j1] = new TriangleEffect(res2, res3[j1][0] / 2f,
						(res3[j1][1] / 2f) * -1f, 36f);
				child[i][j1].setPos(res3[i][0] * 20f, res3[i][1] * 20f);
			}
		}
	}

	public void draw(GLEx g, int tx, int ty) {
		next();
		g.setColor(color);
		if (super.frame < max) {
			arrow.drawPaint(g, s_x - tx, LSystem.screenRect.height - s_y - ty);
		} else if (super.frame < max + 20) {
			for (int j = 0; j < dif.length; j++) {
				dif[j].drawPaint(g, t_x - tx, LSystem.screenRect.height - (t_y
						- ty));
			}
		} else {
			for (int j = 0; j < child.length; j++) {
				for (int i = 0; i < child[j].length; i++) {
					child[j][i].drawPaint(g, t_x - tx,
							LSystem.screenRect.height - (t_y - ty));
				}
			}
		}
		if (super.frame >= max + 50) {
			setExist(false);
		}
	}
}

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
// 一组箭头划过特效，也可用于刀剑等效果.
public class SRPGArrowsEffect extends SRPGEffect {

	private int s_x;

	private int s_y;

	private LColor color;

	private int[] max;

	private boolean[] exist;

	private TriangleEffect[][] arrow;

	public SRPGArrowsEffect(int x1, int y1, int x2, int y2) {
		this(x1, y1, x2, y2, LColor.white);
	}

	public SRPGArrowsEffect(int x1, int y1, int x2, int y2, LColor color) {
		this.setExist(true);
		this.s_x = x1;
		this.s_y = y1;
		this.color = color;
		this.arrow = new TriangleEffect[9][3];
		this.max = new int[9];
		this.exist = new boolean[9];
		int[][] result = { { 0, 0 }, { 32, 0 }, { -32, 0 }, { 0, 32 },
				{ 0, -32 }, { 16, 16 }, { -16, 16 }, { 16, -16 }, { -16, -16 } };
		float[][][] arrays = { { { -16f, 0.0f }, { 8f, 2f }, { 8f, -2f } },
				{ { -16f, 0.0f }, { -10f, 6f }, { -10f, -6f } },
				{ { 4f, 0.0f }, { 8f, 6f }, { 8f, -6f } } };
		float[] res = { 0.0f, 0.0f };
		for (int j = 0; j < arrow.length; j++) {
			max[j] = 0;
			exist[j] = true;
			int x = x2 + result[j][0];
			int y = y2 + result[j][1];
			float d = x - x1;
			float d1 = y - y1;
			float d2 = MathUtils.sqrt(MathUtils.pow(d, 2f)
					+ MathUtils.pow(d1, 2f));
			float d3 = (d / d2) * 8f;
			float d4 = (d1 / d2) * 8f;
			max[j] = (int) (d2 / 8f + 0.5D);
			for (int i = 0; i < arrays.length; i++) {
				arrow[j][i] = new TriangleEffect(arrays[i], res, d3, d4 * -1f,
						0.0f);
				arrow[j][i].setVector((int) (TriangleEffect.getDegrees(x1 - x,
						(y1 - y) * -1) + 0.5f));
			}

		}

	}

	public void draw(GLEx g, int x, int y) {
		next();
		g.setColor(color);
		boolean flag = false;
		for (int j = 0; j < arrow.length; j++) {
			if (0 > max[j]) {
				exist[j] = false;
			}
			if (!exist[j]) {
				continue;
			}
			flag = true;
			if (j * 3 > super.frame) {
				continue;
			}
			for (int i = 0; i < arrow[j].length; i++) {
				arrow[j][i].drawPaint(g, s_x - x,
						(LSystem.screenRect.height - (s_y - y)));
			}
			max[j]--;
		}
		if (!flag) {
			setExist(false);
		}
	}

}

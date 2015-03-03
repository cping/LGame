package loon.srpg.effect;

import loon.LSystem;
import loon.core.graphics.device.LColor;
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
// 攻箭划过特效，也可用于刀剑等效果.
public class SRPGArrowEffect extends SRPGEffect {

	private int s_x;

	private int s_y;

	private int max;

	private TriangleEffect[] arrow;

	private LColor color;

	public SRPGArrowEffect(int x1, int y1, int x2, int y2) {
		this(x1, y1, x2, y2, LColor.white);
	}

	public SRPGArrowEffect(int x1, int y1, int x2, int y2, LColor color) {
		setExist(true);
		this.s_x = x1;
		this.s_y = y1;
		this.color = color;
		this.max = 0;
		float d = x2 - x1;
		float d1 = y2 - y1;
		float d2 = MathUtils.sqrt(MathUtils.pow(d, 2f) + MathUtils.pow(d1, 2f));
		float d3 = (d / d2) * 8f;
		float d4 = (d1 / d2) * 8f;
		max = (int) (d2 / 8f + 0.5f);
		float[][][] sizes = { { { -16f, 0.0f }, { 8f, 2f }, { 8f, -2f } },
				{ { -16f, 0.0f }, { -10f, 6f }, { -10f, -6f } },
				{ { 4f, 0.0f }, { 8f, 6f }, { 8f, -6f } } };
		float[] result = { 0.0f, 0.0f };
		arrow = new TriangleEffect[3];
		for (int i1 = 0; i1 < sizes.length; i1++) {
			arrow[i1] = new TriangleEffect(sizes[i1], result, d3, d4 * -1f, 0.0f);
			arrow[i1].setVector((int) (TriangleEffect.getDegrees(x1 - x2, (y1 - y2)
					* -1) + 0.5f));
		}
	}

	@Override
	public void draw(GLEx g, int x, int y) {
		next();
		g.setColor(color);
		for (int j = 0; j < arrow.length; j++) {
			arrow[j].drawPaint(g, s_x - x, (LSystem.screenRect.height
					- (s_y - y)));
		}
		if (super.frame >= max) {
			setExist(false);
		}
	}

}

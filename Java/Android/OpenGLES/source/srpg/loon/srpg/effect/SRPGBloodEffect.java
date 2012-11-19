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
// 默认的魔法效果之一，用以模拟吸血蝙蝠吸血
public class SRPGBloodEffect extends SRPGEffect {

	private int t_x, t_y;

	private TriangleEffect[] force;

	private LColor color;

	public SRPGBloodEffect(int x, int y) {
		this(x, y, LColor.black);
	}

	public SRPGBloodEffect(int x, int y, LColor color) {
		this.t_x = x;
		this.t_y = y;
		this.color = color;
		float[][] res = { { 8f, 0.0f }, { -4f, 6f }, { -4f, -6f } };
		this.force = new TriangleEffect[960];
		for (int k = 0; k < force.length; k++) {
			float d = LSystem.random.nextInt(100) + 100;
			d /= 30f;
			float d1 = LSystem.random.nextInt(360);
			float d2 = MathUtils.cos((d1 * 3.1415926535897931f) / 180f) * d;
			float d3 = MathUtils.sin((d1 * 3.1415926535897931f) / 180f) * d;
			force[k] = new TriangleEffect(res, d2, d3, 36f);
		}
		setExist(true);
	}

	public void draw(GLEx g, int tx, int ty) {
		next();
		int x = t_x - tx;
		int y = t_y - ty;
		g.setColor(color);
		if (super.frame < 120) {
			for (int j = 0; j < super.frame * 8; j++) {
				if (j + 120 > super.frame * 8) {
					force[j].drawPaint(g, x, (LSystem.screenRect.height - y));
				}
			}
		}
		if (super.frame >= 130) {
			setExist(false);
		}
	}
}

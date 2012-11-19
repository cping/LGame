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
// 默认的魔法特效之一，适合于风雪魔法效果
public class SRPGSnowEffect extends SRPGEffect {

	private int t_x, t_y;

	private TriangleEffect first;

	private TriangleEffect[] force;

	private TriangleEffect[] dif;

	private LColor[] colors;

	private final float[][] fdelta = { { 0.0f, 3f },
			{ 2.3999999999999999f, -1.5f }, { -2.3999999999999999f, -1.5f } };

	public SRPGSnowEffect() {
		this(LSystem.screenRect.width / 2, LSystem.screenRect.height / 2);
	}

	public SRPGSnowEffect(int x, int y) {
		this.t_x = x;
		this.t_y = y;
		this.first = new TriangleEffect(fdelta, 0.0f, 0.0f, 3f);
		float[][] vector = { { 8f, 0.0f }, { -4f, 6f }, { -4f, -6f } };
		this.force = new TriangleEffect[240];
		for (int j = 0; j < force.length; j++) {
			float d1 = LSystem.random.nextInt(200) - 100;
			d1 /= 25f;
			float d2 = LSystem.random.nextInt(200) - 100;
			d2 /= 25f;
			force[j] = new TriangleEffect(vector, d1, d2, 9f);
		}
		float res[][] = { { 32f, 0.0f }, { -16f, 24f }, { -16f, -24f } };
		dif = new TriangleEffect[256];
		colors = new LColor[256];
		for (int j = 0; j < dif.length; j++) {
			float d1 = LSystem.random.nextInt(9000);
			d1 /= 100f;
			float d3 = LSystem.random.nextInt(8000) + 2000;
			float d4 = LSystem.random.nextInt(8000) + 2000;
			d3 /= 100f;
			d4 /= 100f;
			d3 *= MathUtils.cos((d1 * 3.1415926535897931f) / 180f);
			d4 *= MathUtils.sin((d1 * 3.1415926535897931f) / 180f);
			if (LSystem.random.nextInt(2) == 1) {
				d3 *= -1f;
			}
			if (LSystem.random.nextInt(2) == 1) {
				d4 *= -1f;
			}
			d3 /= 15f;
			d4 /= 15f;
			dif[j] = new TriangleEffect(res, d3, d4,
					LSystem.random.nextInt(30) + 3);
			int r = LSystem.random.nextInt(64) + 192;
			colors[j] = new LColor(r, r, r);
		}

		setExist(true);
	}

	public void draw(GLEx g, int tx, int ty) {
		next();
		int x = t_x - tx;
		int y = t_y - ty;
		g.setColor(LColor.white);
		if (super.frame < 120) {
			float[][] delta = first.getDelta();
			for (int j = 0; j < delta.length; j++) {
				for (int i = 0; i < delta[j].length; i++) {
					delta[j][i] += fdelta[j][i] / 25D;
				}
			}
			first.setDelta(delta);
			first.resetAverage();
			first.drawPaint(g, x, LSystem.screenRect.height - y);
			for (int j = 0; j < super.frame * 2; j++) {
				force[j].draw(g, x, LSystem.screenRect.height - y);
			}
		} else if (super.frame < 125)
			g.fillRect(0, 0, LSystem.screenRect.width,
					LSystem.screenRect.height);
		else if (super.frame < 240) {
			for (int i = 0; i < dif.length; i++) {
				g.setColor(colors[i]);
				dif[i].drawPaint(g, x, LSystem.screenRect.height - y);
			}
		} else {
			setExist(false);
		}
	}

}

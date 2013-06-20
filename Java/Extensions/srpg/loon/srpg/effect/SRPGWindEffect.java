package loon.srpg.effect;

import loon.core.LSystem;
import loon.core.graphics.LColor;
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
// 默认的风系魔法特效
public class SRPGWindEffect extends SRPGEffect {

	private int rand;

	private int time;

	private TriangleEffect[] sd;

	private LColor[] colors;

	public SRPGWindEffect() {
		this(64, 40, 60);
	}

	public SRPGWindEffect(int s, int r, int t) {
		this.rand = r;
		this.time = t;
		this.sd = new TriangleEffect[s];
		this.colors = new LColor[s];
		this.setExist(true);
	}

	@Override
	public synchronized void draw(GLEx g, int x, int y) {
		next();
		for (int i = 0; i < sd.length; i++) {
			if (sd[i] != null) {
				continue;
			}
			if (LSystem.random.nextInt(100) >= rand) {
				break;
			}
			float[][] res = { { 0.0f, 10f }, { 8f, -5f }, { -8f, -5f } };
			int index = LSystem.random.nextInt(3) + 1;
			for (int j = 0; j < res.length; j++) {
				for (int c = 0; c < res[j].length; c++) {
					res[j][c] *= index;
				}
			}
			int r = LSystem.random.nextInt(32) + 16;
			float d = r / 10;
			sd[i] = new TriangleEffect(res, r, d,
					LSystem.random.nextInt(24) + 24);
			sd[i].setPosY(LSystem.random.nextInt(LSystem.screenRect.height));
			sd[i].setPosX(-30f);
			colors[i] = new LColor(0, 128 + LSystem.random.nextInt(128),
					LSystem.random.nextInt(128));
		}

		for (int j = 0; j < sd.length; j++) {
			if (colors[j] == null) {
				continue;
			}
			g.setColor(colors[j]);
			if (sd[j] == null) {
				continue;
			}
			sd[j].drawPaint(g, 0, 0);
			if (sd[j].getPosX() > LSystem.screenRect.width) {
				sd[j] = null;
			}
		}

		if (super.frame > time) {
			setExist(false);
		}
	}

}

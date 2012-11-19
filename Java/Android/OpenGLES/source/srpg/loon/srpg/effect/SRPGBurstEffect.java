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
// 用以模拟一个角色的爆炸效果
public class SRPGBurstEffect extends SRPGEffect {

	private int value;

	private int wait;

	private LColor color;

	private float[][] pos;

	private float[][] vector;

	public SRPGBurstEffect(int x, int y) {
		this(x, y, LColor.red);
	}

	public SRPGBurstEffect(int x, int y, LColor color) {
		this(x, y, 150, 32, color);
	}

	public SRPGBurstEffect(int x1, int y1, int v, int wait, LColor color) {
		this.setExist(true);
		this.value = v;
		this.wait = wait;
		this.color = color;
		this.pos = new float[v][2];
		this.vector = new float[v][2];
		for (int i = 0; i < v; i++) {
			pos[i][0] = x1;
			pos[i][1] = y1;
			float f = LSystem.random.nextInt(50);
			float f1 = LSystem.random.nextInt(50);
			int j1 = LSystem.random.nextInt(50);
			f += j1;
			f1 += 49 - j1;
			if (LSystem.random.nextInt(2) == 0) {
				f *= -1F;
			}
			if (LSystem.random.nextInt(2) == 0) {
				f1 *= -1F;
			}
			vector[i][0] = f / 100F;
			vector[i][1] = f1 / 100F;
		}

	}

	public void draw(GLEx g, int x, int y) {
		next();
		g.setColor(color);
		float[] xs = new float[value];
		float[] ys = new float[value];
		for (int j = 0; j < value; j++) {
			pos[j][0] += vector[j][0];
			pos[j][1] += vector[j][1];
			xs[j] = (int) (pos[j][0]) - x;
			ys[j] = (int) (pos[j][1]) - y;
		}
		g.drawPoints(xs, ys, value);
		if (super.frame > wait) {
			setExist(false);
		}
	}

}

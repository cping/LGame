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

// 令角色身体冒出火花，可用于升级，治疗等特效
public class SRPGSparkEffect extends SRPGEffect {

	private int left;

	private int top;

	private int width;

	private int height;

	private int value;

	private LColor color;

	private int[][] effpos;

	private int[] effframe;

	private int max;

	private int wait;

	public SRPGSparkEffect(int x, int y, int w, int h, int v, int wait) {
		this(x, y, w, h, v, wait, new LColor(220, 220, 0));
	}

	public SRPGSparkEffect(int x, int y, int w, int h, int v, int wait,
			LColor color) {
		this.setExist(true);
		this.left = x;
		this.top = y;
		this.width = w;
		this.height = h;
		this.value = v;
		this.wait = wait;
		this.color = color;
		this.effpos = new int[v][2];
		this.effframe = new int[v];
		for (int i = 0; i < v; i++) {
			effframe[i] = -1;
		}
		this.max = 0;
	}

	@Override
	public void draw(GLEx g, int x, int y) {
		next();
		g.setColor(color);
		if (value > max && super.frame % wait == 0) {
			effpos[max][0] = left + LSystem.random.nextInt(width);
			effpos[max][1] = top + LSystem.random.nextInt(height);
			effframe[max] = 0;
			max++;
		}
		for (int i = 0; i < max; i++) {
			if (effframe[i] != -1 && effframe[i] < 10) {
				effframe[i]++;
				int nx = effpos[i][0] - x;
				int ny = effpos[i][1] - y;
				int f = effframe[i];
				g.drawLine(nx, ny - f, nx, ny + f);
				g.drawLine(nx - f, ny, nx + f, ny);
			}
		}
		if (super.frame > 10 + value * wait) {
			setExist(false);
		}
	}

}

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


public class SRPGRCohesionEffect extends SRPGEffect {

	private int t_x, t_y;

	private LColor color;

	private TriangleEffect[] delta;

	public SRPGRCohesionEffect(int x, int y) {
		this(x, y, LColor.orange);
	}

	public SRPGRCohesionEffect(int x, int y, LColor color) {
		this.t_x = x;
		this.t_y = y;
		this.color = color;
		float[][] res = { { 0.0f, 30f }, { 24f, -15f }, { -24f, -15f } };
		this.delta = new TriangleEffect[8];
		delta[0] = new TriangleEffect(res, 2f, 0.0f, 36f);
		delta[1] = new TriangleEffect(res, 0.0f, 2f, 36f);
		delta[2] = new TriangleEffect(res, -2f, 0.0f, 36f);
		delta[3] = new TriangleEffect(res, 0.0f, -2f, 36f);
		delta[4] = new TriangleEffect(res, 1.3999999999999999f,
				1.3999999999999999f, 36f);
		delta[5] = new TriangleEffect(res, -1.3999999999999999f,
				1.3999999999999999f, 36f);
		delta[6] = new TriangleEffect(res, 1.3999999999999999f,
				-1.3999999999999999f, 36f);
		delta[7] = new TriangleEffect(res, -1.3999999999999999f,
				-1.3999999999999999f, 36f);
		setExist(true);
	}

	@Override
	public void draw(GLEx g, int x, int y) {
		next();
		g.setColor(color);
		for (int i = 0; i < delta.length; i++) {
			delta[i].drawPaint(g, t_x - x, LSystem.viewSize.height
					- (t_y - y));
		}
		if (super.frame > 40) {
			setExist(false);
		}
	}

}

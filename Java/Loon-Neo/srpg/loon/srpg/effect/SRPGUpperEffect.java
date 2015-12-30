/**
 * 
 * Copyright 2008 - 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless req(uired by applicable law or agreed to in writing, software
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

public class SRPGUpperEffect extends SRPGEffect {

	private int t_x;

	private int t_y;

	private TriangleEffect[] force;

	private LColor color;

	public SRPGUpperEffect(int x, int y, LColor color) {
		this.t_x = x;
		this.t_y = y;
		this.color = color;
		float[][] res = { { 8f, 0.0f }, { -4f, 6f }, { -4f, -6f } };
		this.force = new TriangleEffect[16];
		for (int k = 0; k < force.length; k++) {
			float d = MathUtils.random.nextInt(256) + 10;
			d /= 50f;
			force[k] = new TriangleEffect(res, 0.0f, d, 36f);
			force[k].setPosX(MathUtils.random.nextInt(32) - 15);
		}
		this.setExist(true);
	}

	@Override
	public void draw(GLEx g, int x, int y) {
		next();
		LColor c = g.getColor();
		g.setColor(color);
		if (super.frame < 20) {
			for (int i = 0; i < force.length; i++) {
				force[i]
						.draw(g, t_x - x, LSystem.viewSize.height - (t_y - y));
			}
		} else {
			setExist(false);
		}
		g.setColor(c);
	}

}

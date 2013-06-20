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
// 某些事物内聚的效果
public class SRPGCohesionEffect extends SRPGEffect {

	private int t_x;

	private int t_y;

	private LColor color;

	private TriangleEffect[] de;

	public SRPGCohesionEffect(int x, int y) {
		this(x, y, LColor.orange);
	}

	public SRPGCohesionEffect(int x, int y, LColor color) {
		this.t_x = x;
		this.t_y = y;
		this.color = color;
		float[][] res = { { 0.0f, 30f }, { 24f, -15f }, { -24f, -15f } };
		this.de = new TriangleEffect[8];
		for (int k = 0; k < de.length; k++) {
			float d = 360f / de.length;
			d *= k;
			d = (d * 3.1415926535897931f) / 180f;
			float d1 = MathUtils.cos(d);
			float d2 = MathUtils.sin(d);
			de[k] = new TriangleEffect(res, d1, d2, 36f);
			de[k].setPos(d1 * -40f, d2 * -40f);
		}
		setExist(true);
	}

	@Override
	public void draw(GLEx g, int tx, int ty) {
		next();
		g.setColor(color);
		for (int j = 0; j < de.length; j++) {
			de[j].drawPaint(g, t_x - tx,
					(LSystem.screenRect.height - (t_y - ty)));
		}
		if (super.frame > 40) {
			setExist(false);
		}
	}

}

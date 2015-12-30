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
import loon.utils.MathUtils;


// 默认的爆炸效果
public class SRPGBlastEffect extends SRPGEffect {

	private int t_x, t_y;

	private LColor color;

	private TriangleEffect[][] de;

	public SRPGBlastEffect(int x, int y) {
		this(x, y, LColor.red);
	}

	public SRPGBlastEffect(int x, int y, LColor color) {
		this.t_x = x;
		this.t_y = y;
		this.color = color;
		float[][] res = { { 0.0f, 30f }, { 24f, -15f }, { -24f, -15f } };
		de = new TriangleEffect[2][0];
		de[0] = new TriangleEffect[8];
		de[1] = new TriangleEffect[16];
		for (int j = 0; j < de.length; j++) {
			for (int i = 0; i < de[j].length; i++) {
				float d = i;
				d *= 360f / de[j].length;
				d = (d * 3.1415926535897931f) / 180f;
				float d1 = MathUtils.cos(d) * (j * 5 + 2);
				float d2 = MathUtils.sin(d) * (j * 5 + 2);
				de[j][i] = new TriangleEffect(res, d1, d2, 36f);
			}
		}
		setExist(true);
	}

	@Override
	public void draw(GLEx g, int x, int y) {
		next();
		g.setColor(color);
		for (int j = 0; j < de.length; j++) {
			for (int l = 0; l < de[j].length; l++) {
				if (j != 0 || super.frame > 20) {
					de[j][l].drawPaint(g, t_x - x,
							(LSystem.viewSize.height - (t_y - y)));
				}
			}
		}
		if (super.frame > 80) {
			setExist(false);
		}
	}
}

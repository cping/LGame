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
import loon.srpg.SRPGType;
import loon.utils.MathUtils;


public class SRPGExtinctEffect extends SRPGEffect {

	private final String mes;

	private int t_x;

	private int t_y;

	private int w, h;

	private LColor color;

	private TriangleEffect[][] de;

	public SRPGExtinctEffect(int x, int y, LColor color, String mes) {
		this.mes = mes;
		this.w = SRPGType.DEFAULT_BIG_FONT.stringWidth(mes);
		this.h = SRPGType.DEFAULT_BIG_FONT.getSize();
		t_x = x;
		this.t_y = y;
		this.color = color;
		float[][] res = { { 0.0f, 30f }, { 24f, -15f }, { -24f, -15f } };
		this.de = new TriangleEffect[2][0];
		this.de[0] = new TriangleEffect[8];
		this.de[1] = new TriangleEffect[16];
		for (int i = 0; i < de.length; i++) {
			for (int j = 0; j < de[i].length; j++) {
				float d = j;
				d *= 360f / de[i].length;
				d = (d * 3.1415926535897931f) / 180f;
				float d1 = MathUtils.cos(d) * (i * 5 + 2);
				float d2 = MathUtils.sin(d) * (i * 5 + 2);
				de[i][j] = new TriangleEffect(res, d1, d2, 36f);
			}
		}
		setExist(true);
	}

	@Override
	public void draw(GLEx g, int x, int y) {
		next();
		g.setColor(color);
		if (super.frame < 120) {
			g.fillRect(0, 0, LSystem.screenRect.width,
					LSystem.screenRect.height);
			LFont old = g.getFont();
			g.setFont(SRPGType.DEFAULT_BIG_FONT);
			g.drawString(mes, (LSystem.screenRect.width - w) / 2,
					(LSystem.screenRect.height - h) / 2,
					LColor.red);
			g.setFont(old);
		} else {
			g.setColor(color);
			for (int j = 0; j < de.length; j++) {
				for (int i = 0; i < de[j].length; i++)
					if (j != 0 || super.frame > 120) {
						de[j][i].drawPaint(g, t_x - x,
								LSystem.screenRect.height - (t_y - y));
					}

			}
		}
		if (super.frame >= 230) {
			setExist(false);
		}
	}

}

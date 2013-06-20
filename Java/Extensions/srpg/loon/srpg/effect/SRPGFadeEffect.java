package loon.srpg.effect;

import loon.core.LSystem;
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
// 角色消失或出现用特效，较适合魔法传送等时机使用
public class SRPGFadeEffect extends SRPGEffect {

	private int t_x;

	private int t_y;

	private TriangleEffect[] de;

	public SRPGFadeEffect(int x, int y) {
		this.t_x = x;
		this.t_y = y;
		float[][] res1 = { { 0.0f, 30f }, { 24f, -15f }, { -24f, -15f } };
		float[][] res2 = { { 24f, 15f }, { -24f, 15f }, { 0.0f, -30f } };
		this.de = new TriangleEffect[4];
		this.de[0] = new TriangleEffect(res1, 0.0f, 0.0f, -9f);
		this.de[1] = new TriangleEffect(res2, 0.0f, 0.0f, -9f);
		this.de[2] = new TriangleEffect(res1, 0.0f, 0.0f, -9f);
		this.de[3] = new TriangleEffect(res2, 0.0f, 0.0f, -9f);
		this.setExist(true);
	}

	@Override
	public void draw(GLEx g, int tx, int ty) {
		next();
		int x = t_x - tx;
		int y = t_y - ty;
		if (super.frame == 40) {
			de[0].setMoveX(5f);
			de[1].setMoveX(-5f);
			de[2].setMoveY(5f);
			de[3].setMoveY(-5f);
		}
		g.setColor((255 * super.frame) / 80, (255 * super.frame) / 80,
				(255 * super.frame) / 80);
		for (int j = 0; j < de.length; j++) {
			de[j].drawPaint(g, x, LSystem.screenRect.height - y);
		}
		if (super.frame >= 80) {
			setExist(false);
		}
	}

}

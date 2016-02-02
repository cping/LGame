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
import loon.canvas.LGradation;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.srpg.SRPGType;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

public class SRPGPhaseEffect extends SRPGEffect {

	private LTimer timer;

	private int count, index;

	private boolean isEnd;

	private int twidth;

	private int fontLenght;

	private StringBuffer sbr;

	private char[] mes;

	public SRPGPhaseEffect(String mes) {
		super.isExist = true;
		super.frame = 0;
		this.timer = new LTimer(10);
		this.sbr = new StringBuffer();
		this.mes = mes.toCharArray();
		this.fontLenght = mes.length() - 1;
		this.twidth = SRPGType.DEFAULT_BIG_FONT.stringWidth(mes);
		this.index = MathUtils.random.nextInt(4);
	}

	@Override
	public void draw(GLEx g, int tx, int ty) {
		next();
		if (!isEnd) {
			if (timer.action(1)) {
				sbr.append(mes[count]);
				count++;
			}
			if (count > fontLenght) {
				this.count = 0;
				this.isEnd = true;
			}
		} else {
			count++;
			if (count > 30) {
				this.isExist = false;
			}
		}
		LColor color = null;
		switch (index) {
		case 0:
			color = LColor.red;
			break;
		case 1:
			color = LColor.orange;
			break;
		case 2:
			color = LColor.blue;
			break;
		case 3:
			color = LColor.yellow;
			break;
		default:
			color = LColor.orange;
			break;
		}
		LGradation
				.getInstance(color, LColor.black, LSystem.viewSize.getWidth(), 80)
				.drawHeight(g, 0, (LSystem.viewSize.getHeight() - 80) / 2);
		IFont old = g.getFont();
		g.setFont(SRPGType.DEFAULT_BIG_FONT);
		g.drawString(sbr.toString(),
				(LSystem.viewSize.width - twidth) / 2,
				(LSystem.viewSize.height ) / 2 + 10, LColor.white);
		g.setFont(old);

	}

}

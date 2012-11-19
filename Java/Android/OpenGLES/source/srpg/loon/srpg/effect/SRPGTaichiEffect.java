package loon.srpg.effect;

import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.core.timer.LTimer;


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
public class SRPGTaichiEffect extends SRPGEffect {

	private LTimer timer;

	private int flag;

	private LColor[] colors = { LColor.black, LColor.white };

	private int t_x, t_y, twidth;

	public SRPGTaichiEffect(int x, int y, int w) {
		timer = new LTimer(50);
		this.t_x = x;
		this.t_y = y;
		this.twidth = w;
		this.setExist(true);
	}

	public void draw(GLEx g, int tx, int ty) {
		next();
		int x = (t_x - tx) - twidth / 2;
		int y = (t_y - ty) - twidth / 2;
		if (timer.action(1)) {
			flag = 1 - flag;
		}
		g.setColor(colors[flag]);
		g.fillArc(x, y, twidth, twidth, 270, 180);
		g.setColor(colors[1 - flag]);
		g.fillArc(x, y, twidth, twidth, 90, 180);
		g.fillArc(x + twidth / 4, y, twidth / 2, twidth / 2, 270, 180);
		g.setColor(colors[flag]);
		g.fillOval(x + twidth / 4, y + twidth / 2, twidth / 2, twidth / 2);
		g.fillOval(x + twidth / 16 * 7, y + twidth / 6, twidth / 8, twidth / 8);
		g.setColor(colors[1 - flag]);
		g.fillOval(x + twidth / 16 * 7, y + twidth / 6 + twidth / 2,
				twidth / 8, twidth / 8);
		if (super.frame >= 120) {
			setExist(false);
		}
	}

}

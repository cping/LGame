package loon.srpg.view;

import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.LGradation;
import loon.core.graphics.LImage;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;
import loon.srpg.actor.SRPGStatus;


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
public class SRPGMiniStatusView extends SRPGDrawView {

	private final static LFont deffont = LFont.getFont("黑体", 0, 15);

	private SRPGStatus status;

	private int[] spans;

	private int[] values;

	private static LTexture cache;

	public SRPGMiniStatusView(SRPGStatus status) {
		this.set(status);
	}

	public SRPGMiniStatusView(SRPGStatus status, int w, int h) {
		this.set(status, w, h);
	}

	public void set(SRPGStatus status) {
		this.set(status, 180, 90);
	}

	public void set(SRPGStatus status, int w, int h) {
		this.setExist(true);
		this.setLock(false);
		super.width = w;
		super.height = h;
		this.status = status;
		this.values = new int[6];
		this.spans = new int[6];
		this.setSpans(true);
	}

	private void setSpans(boolean flag) {
		int[] res = { status.level, status.exp, status.hp, status.max_hp,
				status.mp, status.max_mp };
		for (int i = 0; i < res.length; i++) {
			if (values[i] != res[i] || flag) {
				spans[i] = deffont.stringWidth(String.valueOf(res[i]));
				values[i] = res[i];
			}
		}
	}

	private void drawLazy(LGraphics g) {
		if (!exist) {
			return;
		}
		LColor color = LColor.blue;
		if (status.team != 0) {
			color = LColor.red;
		}
		LGradation.getInstance(color, LColor.black, super.width, super.height)
				.drawHeight(g, 0, 0);
		int hp = 0;
		int width = super.width - 10;
		if (status.max_hp > 0) {
			hp = (width * status.hp) / status.max_hp;
		}
		g.setColor(LColor.black);
		g.fillRect(5 + 0, 2 + 0, width, 3);
		g.setColor(96, 128, 255);
		g.fillRect(5 + 0, 2 + 0, hp, 3);
		int mp = 0;
		if (status.max_mp > 0) {
			mp = (width * status.mp) / status.max_mp;
		}
		g.setColor(LColor.black);
		g.fillRect(5 + 0, 6 + 0, width, 3);
		g.setColor(255, 128, 96);
		g.fillRect(5 + 0, 6 + 0, mp, 3);
		g.setColor(LColor.white);
		g.setFont(deffont);

		g.drawString(status.name, 5 + 0, 23 + 0);
		g.drawString(status.jobname, 5 + 0, 40 + 0);

		g.drawString("LV", 5 + 0, 55 + 0);
		g.drawString(String.valueOf(status.level), (55 - spans[0]) + 0, 55 + 0);

		g.drawString("EXP", 80 + 0, 55 + 0);
		g.drawString(String.valueOf(status.exp), (120 - spans[1]) + 0, 55 + 0);

		int size = 5 + 0;
		g.drawString("HP", size, 70 + 0);
		size += 55 - spans[2];
		g.drawString(String.valueOf(status.hp), size, 70 + 0);
		size += 8 + spans[3];
		g.drawString("/", size, 70 + 0);
		g.drawString(String.valueOf(status.max_hp), size + 15 + 0, 70 + 0);

		size = 5 + 0;
		g.drawString("MP", size, 85 + 0);
		size += 55 - spans[4];
		g.drawString(String.valueOf(status.mp), size, 85 + 0);
		size += 8 + spans[5];
		g.drawString("/", size, 85 + 0);
		g.drawString(String.valueOf(status.max_mp), size + 15 + 0, 85 + 0);
	}

	public void draw(GLEx gl) {
		if (!exist) {
			return;
		}
		if (cache == null) {
			LImage image = new LImage(super.width, super.height, true);
			LGraphics g = image.getLGraphics();
			drawLazy(g);
			g.dispose();
			image.setFormat(Format.SPEED);
			cache = image.getTexture();
			if (image != null) {
				image.dispose();
				image = null;
			}
			return;
		}
		gl.resetColor();
		gl.drawTexture(cache, super.left, super.top);
	}

	public boolean isExist() {
		boolean exist = super.isExist();
		if (!exist) {
			if (cache != null) {
				cache.destroy();
				cache = null;
			}
		}
		return exist;
	}

}

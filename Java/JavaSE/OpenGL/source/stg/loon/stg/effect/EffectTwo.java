package loon.stg.effect;

import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;

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
public class EffectTwo extends EffectOne {

	private String message;

	private int mesWidth;

	private int mesHeight;

	private LTexture[] shoutImg;

	public static final int TYPE_STRING = 3;

	public static final int TYPE_IMGS = 4;

	public EffectTwo(String fileName) {
		this(new LTexture(fileName));
	}

	public EffectTwo(final LTexture texture) {
		this(new LTexture[] { texture });
	}

	public EffectTwo(LTexture[] image) {
		this(image, 20);
	}

	public EffectTwo(LTexture[] image, int num) {
		super(image, num);
		this.message = "";
	}

	public void setString(String str, int width, int height) {
		this.message = str;
		this.mesWidth = width;
		this.mesHeight = height;
	}

	public void setShoutImg(LTexture[] img) {
		this.shoutImg = img;
	}

	protected void renderExpand(GLEx g, int type) {
		if (type == TYPE_STRING) {
			drawString(g);
		} else if (type == TYPE_IMGS && shoutImg != null) {
			drawImage(g);
		}
	}

	private void drawString(GLEx g) {
		int activeNum = getActiveNum();
		arrayR[activeNum] = 20;
		g.setColor(224, 255, 255);
		for (int j = 0; j < number; j++) {
			arrayR[j] += 8;
			g.drawString(message, getShoutX(j) - mesWidth, getShoutY(j)
					- mesHeight);
		}
		g.resetColor();
	}

	protected int getShoutX(int direct) {
		return drawX + (int) ((double) arrayR[direct] * cosX[direct]);
	}

	protected int getShoutY(int direct) {
		return drawY + (int) ((double) arrayR[direct] * sinX[direct]);
	}

	private void drawImage(GLEx g) {
		int length = shoutImg.length;
		if (length == 0) {
			return;
		}
		int activeNum = getActiveNum();
		arrayR[activeNum] = 20;
		for (int j = 0; j < number; j++) {
			arrayR[j] += 8;
			for (int i = 0; i < 20; i++) {
				g.drawTexture(shoutImg[arrayR[j] / 8 & length], getX(j, i),
						getY(j, i));
			}
		}
	}
}

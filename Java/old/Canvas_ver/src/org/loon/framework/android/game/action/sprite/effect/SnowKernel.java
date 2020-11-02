package org.loon.framework.android.game.action.sprite.effect;

import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.utils.GraphicsUtils;

/**
 * Copyright 2008 - 2009
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
public class SnowKernel implements IKernel {

	private boolean exist;

	private LImage snow;

	private double offsetX, offsetY, speed, x, y, width, height, snowWidth,
			snowHeight;

	public SnowKernel(int n, int w, int h) {
		snow = GraphicsUtils.loadImage((LSystem.FRAMEWORK_IMG_NAME+"snow_" + n + ".png")
				.intern());
		snowWidth = snow.getWidth();
		snowHeight = snow.getHeight();
		width = w;
		height = h;
		offsetX = 0;
		offsetY = n * 0.6 + 1.9 + Math.random() * 0.2;
		speed = Math.random();
	}

	public void make() {
		exist = true;
		x = Math.random() * width;
		y = -snowHeight;
	}

	public void move() {
		if (!exist) {
			if (Math.random() < 0.002) {
				make();
			}
		} else {
			x += offsetX;
			y += offsetY;
			offsetX += speed;
			speed += (Math.random() - 0.5) * 0.3;
			if (offsetX >= 1.5) {
				offsetX = 1.5;
			}
			if (offsetX <= -1.5) {
				offsetX = -1.5;
			}
			if (speed >= 0.2) {
				speed = 0.2;
			}
			if (speed <= -0.2) {
				speed = -0.2;
			}
			if (y >= height) {
				y = -snowHeight;
				x = Math.random() * width;
			}
		}
	}

	public void draw(LGraphics g) {
		if (exist) {
			g.drawImage(snow, (int) x, (int) y);
		}
	}

	public LImage getSnow() {
		return snow;
	}

	public double getSnowHeight() {
		return snowHeight;
	}

	public double getSnowWidth() {
		return snowWidth;
	}

}

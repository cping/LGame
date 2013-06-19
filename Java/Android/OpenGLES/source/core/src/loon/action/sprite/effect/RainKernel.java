package loon.action.sprite.effect;

import loon.core.LSystem;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.utils.MathUtils;


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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class RainKernel implements IKernel {

	private boolean exist;

	private LTexture rain;
	
	private int id;

	private float offsetX, offsetY, x, y, width, height, rainWidth, rainHeight;

	public RainKernel(int n, int w, int h) {
		rain = LTextures.loadTexture(
				(LSystem.FRAMEWORK_IMG_NAME + "rain_" + n + ".png").intern());
		rainWidth = rain.getWidth();
		rainHeight = rain.getHeight();
		width = w;
		height = h;
		offsetX = 0;
		offsetY = (5 - n) * 30 + 75 + MathUtils.random() * 15;
	}

	@Override
	public int id() {
		return id;
	}

	public void make() {
		exist = true;
		x = MathUtils.random() * width;
		y = -rainHeight;
	}

	@Override
	public void update() {
		if (!exist) {
			if (MathUtils.random() < 0.002) {
				make();
			}
		} else {
			x += offsetX;
			y += offsetY;
			if (y >= height) {
				x = MathUtils.random() * width;
				y = -rainHeight * MathUtils.random();
			}
		}
	}

	@Override
	public void draw(GLEx g) {
		if (exist) {
			rain.draw(x, y);
		}
	}

	@Override
	public LTexture get() {
		return rain;
	}

	@Override
	public float getHeight() {
		return rainHeight;
	}

	@Override
	public float getWidth() {
		return rainWidth;
	}

	@Override
	public void dispose() {
		if (rain != null) {
			rain.destroy();
			rain = null;
		}
	}

}

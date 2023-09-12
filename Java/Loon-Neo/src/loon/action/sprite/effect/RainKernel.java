/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.action.sprite.effect;

import loon.LSystem;
import loon.LTexture;
import loon.opengl.GLEx;
import loon.opengl.LTexturePack;
import loon.utils.MathUtils;

/**
 * 图片雨的具体实现效果
 */
public class RainKernel implements IKernel {

	private boolean exist;

	private LTexture rain;

	private int id;

	private float offsetX, offsetY;

	private float x, y, width, height;

	private float rainWidth, rainHeight;

	public RainKernel(LTexturePack pack, int n, int w, int h) {
		this(pack.getTexture(LSystem.getSystemImagePath() + "rain_" + n), n, w, h, -1f);
	}

	public RainKernel(LTexture texture, int n, int w, int h, float r) {
		this.rain = texture;
		this.rainWidth = rain.width();
		this.rainHeight = rain.height();
		this.width = w;
		this.height = h;
		this.offsetX = 0;
		if (r == -1f) {
			offsetY = (5 - n) * 30 + 75 + MathUtils.random() * 15;
		} else {
			offsetY = r;
		}
	}

	@Override
	public int id() {
		return id;
	}

	public RainKernel make() {
		exist = true;
		x = MathUtils.random() * width;
		y = -rainHeight;
		return this;
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
	public void draw(GLEx g, float mx, float my) {
		if (exist) {
			rain.draw(mx + x, my + y);
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

	public boolean isClosed() {
		return rain == null || rain.isClosed();
	}

	@Override
	public void close() {
		if (rain != null) {
			rain.close();
			rain = null;
		}
	}

}

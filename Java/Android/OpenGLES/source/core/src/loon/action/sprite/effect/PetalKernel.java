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
public class PetalKernel implements IKernel {

	private boolean exist;

	private LTexture sakura;

	private float offsetX, offsetY, speed, x, y, width, height, sakuraWidth,
			sakuraHeight;

	private int id;

	public PetalKernel(int n, int w, int h) {
		id = n;
		sakura = LTextures.loadTexture(
				(LSystem.FRAMEWORK_IMG_NAME + "sakura_" + n + ".png").intern());
		sakuraWidth = sakura.getWidth();
		sakuraHeight = sakura.getHeight();
		width = w;
		height = h;
		offsetX = 0;
		offsetY = n * 0.6f + 1.9f + MathUtils.random() * 0.2f;
		speed = MathUtils.random();
	}

	@Override
	public int id() {
		return id;
	}

	public void make() {
		exist = true;
		x = MathUtils.random() * width;
		y = -sakuraHeight;
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
			offsetX += speed;
			speed += (MathUtils.random() - 0.5) * 0.3;
			if (offsetX >= 1.5) {
				offsetX = 1.5f;
			}
			if (offsetX <= -1.5) {
				offsetX = -1.5f;
			}
			if (speed >= 0.2) {
				speed = 0.2f;
			}
			if (speed <= -0.2) {
				speed = -0.2f;
			}
			if (y >= height) {
				y = -(LSystem.random.nextFloat() * 1) - sakuraHeight;
				x = (LSystem.random.nextFloat() * (width - 1));
			}
		}
	}

	@Override
	public void draw(GLEx g) {
		if (exist) {
			sakura.draw(x, y);
		}
	}

	@Override
	public LTexture get() {
		return sakura;
	}

	@Override
	public float getHeight() {
		return sakuraHeight;
	}

	@Override
	public float getWidth() {
		return sakuraWidth;
	}

	@Override
	public void dispose() {
		if (sakura != null) {
			sakura.destroy();
			sakura = null;
		}
	}
}

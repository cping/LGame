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

public class PetalKernel implements IKernel {

	private boolean exist;

	private LTexture sakura;

	private float offsetX, offsetY, speed, x, y, width, height, sakuraWidth, sakuraHeight;

	private int id;

	public PetalKernel(LTexturePack pack, int n, int w, int h) {
		this(pack.getTexture(LSystem.getSystemImagePath() + "sakura_" + n), n, w, h);
	}

	public PetalKernel(LTexture texture, int n, int w, int h) {
		this.id = n;
		this.sakura = texture;
		this.sakuraWidth = sakura.width();
		this.sakuraHeight = sakura.height();
		this.width = w;
		this.height = h;
		this.offsetX = 0;
		this.offsetY = n * 0.6f + 1.9f + MathUtils.random() * 0.2f;
		speed = MathUtils.random();
	}

	@Override
	public int id() {
		return id;
	}

	public void make() {
		this.exist = true;
		this.x = MathUtils.random() * width;
		this.y = -sakuraHeight;
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
				y = -(MathUtils.random() * 1) - sakuraHeight;
				x = (MathUtils.random() * (width - 1));
			}
		}
	}

	@Override
	public void draw(GLEx g, float mx, float my) {
		if (exist) {
			sakura.draw(mx + x, my + y);
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

	public boolean isClosed() {
		return sakura == null || sakura.isClosed();
	}

	@Override
	public void close() {
		if (sakura != null) {
			sakura.close();
			sakura = null;
		}
	}

}

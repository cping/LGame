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
package loon.action.sprite;

import loon.Graphics;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.geom.Dimension;

public class CanvasPlayer extends Entity {

	private final Graphics gfx;
	private Canvas canvas;

	public CanvasPlayer() {
		this(LSystem.base().graphics(), LSystem.viewSize);
	}

	public CanvasPlayer(Graphics gfx, Dimension size) {
		this(gfx, size.width(), size.height());
	}

	public CanvasPlayer(float width, float height) {
		this(LSystem.base().graphics(), width, height);
	}

	public CanvasPlayer(Graphics gfx, float width, float height) {
		this.gfx = gfx;
		this.setRepaint(true);
		resize(width, height);
	}

	public CanvasPlayer(Graphics gfx, Canvas canvas) {
		this.gfx = gfx;
		this.canvas = canvas;
		setTexture(canvas.image.createTexture(LTexture.Format.DEFAULT));
	}

	public CanvasPlayer resize(float width, float height) {
		if (canvas != null) {
			canvas.close();
		}
		canvas = gfx.createCanvas(width, height);
		setSize(width, height);
		return this;
	}

	@Override
	public Entity setTexture(LTexture tex) {
		if (tex != null) {
			setSize(tex.width(), tex.height());
		}
		if (this.getBitmap() != tex) {
			if (this.getBitmap() != null) {
				this.getBitmap().texture().release();
			}
			super.setTexture(tex);
			if (tex != null) {
				tex.texture().reference();
			}
		}
		return this;
	}

	public Canvas begin() {
		return canvas;
	}

	public CanvasPlayer end() {
		LTexture tex = super.getBitmap();
		Image image = canvas.image;
		if (tex != null && tex.pixelWidth() == image.pixelWidth() && tex.pixelHeight() == image.pixelHeight()) {
			tex.update(image, false);
		} else {
			setTexture(canvas.image.texture());
		}
		return this;
	}

	@Override
	protected void _onDestroy() {
		super._onDestroy();
		if (canvas != null) {
			canvas.close();
			if (canvas.image != null) {
				canvas.image.close();
			}
			canvas = null;
		}
	}
}

/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package com.mygame;

import loon.LRelease;
import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;

public class Painter implements LRelease {

	private int clipH;
	private int clipW;
	private int clipX;
	private int clipY;
	private boolean disposed;
	private boolean doClip;
	private int int_angle;
	private float opacity;
	private int savedAngle;
	private float savedScaleX;
	private float savedScaleY;
	private float savedTranslateX;
	private float savedTranslateY;
	private float scaleX;
	private float scaleY;
	public SpriteBatch batch;
	private LColor tmp_color;
	private float translateX;
	private float translateY;
	private boolean translations;

	public Painter(SpriteBatch batch) {
		this.batch = batch;
		this.setOpacity(1f);
	}

	private boolean applyClip(Vector2f pos, RectBox src, Vector2f origin,
			Vector2f scale) {
		int width = (int) (src.width * scale.x);
		int height = (int) (src.height * scale.y);
		int sx = (int) (pos.x * scale.x);
		int sy = (int) (pos.y * scale.y);
		if (sx < this.clipX) {
			int offsetX = (int) (((this.clipX - sx)) / scale.x);
			if (offsetX >= src.width) {
				return false;
			}
			src.x += offsetX;
			src.width -= offsetX;
			origin.x = width / 2;
		}
		if (width > (this.clipX + this.clipW)) {
			int offsetWidth = (int) ((((width - this.clipX) - this.clipW)) / scale.x);
			if (offsetWidth >= src.width) {
				return false;
			}
			src.width -= offsetWidth;
		}
		if (sy < this.clipY) {
			int offsetY = (int) (((this.clipY - sy)) / scale.y);
			if (offsetY >= src.height) {
				return false;
			}
			src.y += offsetY;
			src.height -= offsetY;
			origin.y = height / 2;
		}
		if (height > (this.clipY + this.clipH)) {
			int offsetHeight = (int) ((((height - this.clipY) - this.clipH)) / scale.y);
			if (offsetHeight >= src.height) {
				return false;
			}
			src.height -= offsetHeight;
		}
		return true;
	}

	private boolean applyClipFlipped(Vector2f pos, RectBox src,
			Vector2f origin, Vector2f scale) {
		int width = (int) (src.width * scale.x);
		int height = (int) (src.height * scale.y);
		int sx = (int) (pos.x * scale.x);
		int sy = (int) (pos.y * scale.y);
		if (sx < this.clipX) {
			int offsetX = (int) (((this.clipX - sx)) / scale.x);
			if (offsetX >= src.width) {
				return false;
			}
			src.width -= offsetX;
			origin.x = width / 2;
		}
		if (width > (this.clipX + this.clipW)) {
			int offsetWidth = (int) ((((width - this.clipX) - this.clipW)) / scale.x);
			if (offsetWidth >= src.width) {
				return false;
			}
			src.x += offsetWidth;
			src.width -= offsetWidth;
		}
		if (sy < this.clipY) {
			int offsetY = (int) (((this.clipY - sy)) / scale.y);
			if (offsetY >= src.height) {
				return false;
			}
			src.y += offsetY;
			src.height -= offsetY;
			origin.y = height / 2;
		}
		if (height > (this.clipY + this.clipH)) {
			int offsetHeight = (int) ((((height - this.clipY) - this.clipH)) / scale.y);
			if (offsetHeight >= src.height) {
				return false;
			}
			src.height -= offsetHeight;
		}
		return true;
	}

	public void begin() {
		this.batch.begin();
		this.translateX = 0f;
		this.translateY = 0f;
		this.scaleX = 1f;
		this.scaleY = 1f;
		this.int_angle = 0;
		this.translations = false;
		this.removeClip();
	}

	private void checkIfTranslate() {
		if (((this.translateX != 0f) || (this.translateY != 0f))
				|| (((this.scaleX != 1f) || (this.scaleY != 1f)) || (this.int_angle != 0))) {
			this.translations = true;
		} else {
			this.translations = false;
		}
	}

	public void close() {
		this.disposed = true;
	}

	public void draw(LTexture texture, Vector2f pos, Vector2f origin,
			RectBox src) {
		if (this.doClip) {
			Vector2f scale = new Vector2f(1f, 1f);
			if (!this.applyClip(pos, src, origin, scale)) {
				return;
			}
		}
		this.translatePos(pos);
		tmp_scale.set(this.scaleX, this.scaleY);
		this.batch.draw(texture, tmp_location, src, this.tmp_color,
				this.int_angle, origin, tmp_scale, SpriteEffects.None);
	}

	public void drawScaled(LTexture texture, Vector2f pos, Vector2f origin,
			Vector2f scale, RectBox src) {
		this.translatePos(pos);
		tmp_scale.set(scale.x * this.scaleX, scale.y * this.scaleY);
		this.batch.draw(texture, tmp_location, src, this.tmp_color,
				this.int_angle, origin, scale, SpriteEffects.None);
	}

	public void drawScaled(LTexture texture, Vector2f pos, Vector2f origin,
			Vector2f scale, RectBox src, boolean flip) {
		if (this.doClip) {
			boolean flag = false;
			if (flip) {
				flag = this.applyClipFlipped(pos, src, origin, scale);
			} else {
				flag = this.applyClip(pos, src, origin, scale);
			}
			if (!flag) {
				return;
			}
		}
		this.translatePos(pos);
		tmp_scale.set(scale.x * this.scaleX, scale.y * this.scaleY);
		this.batch.draw(texture, tmp_location, src, this.tmp_color,
				this.int_angle, origin, scale,
				flip ? SpriteEffects.FlipHorizontally : SpriteEffects.None);
	}

	public void drawScaledRotated(LTexture texture, Vector2f pos,
			Vector2f origin, Vector2f scale, float angle, RectBox src) {
		this.translatePos(pos);
		angle += this.int_angle;
		tmp_scale.set(scale.x * this.scaleX, scale.y * this.scaleY);
		this.batch.draw(texture, tmp_location, src, this.tmp_color, angle,
				origin, tmp_scale, SpriteEffects.None);
	}

	private Vector2f tmp_scale = new Vector2f();

	public void drawScaledRotated(LTexture texture, Vector2f pos,
			Vector2f origin, Vector2f scale, final float angle, RectBox src,
			boolean flip) {
		this.translatePos(pos);
		float rotation = angle;
		rotation += this.int_angle;
		tmp_scale.set(scale.x * this.scaleX, scale.y * this.scaleY);
		this.batch.draw(texture, tmp_location, src, this.tmp_color, rotation,
				origin, tmp_scale, flip ? SpriteEffects.FlipHorizontally
						: SpriteEffects.None);
	}

	public void end() {
		this.batch.end();
	}

	public void removeClip() {
		this.doClip = false;
	}

	public void restore() {
		this.translateX = this.savedTranslateX;
		this.translateY = this.savedTranslateY;
		this.scaleX = this.savedScaleX;
		this.scaleY = this.savedScaleY;
		this.int_angle = this.savedAngle;
		this.checkIfTranslate();
	}

	public void rotate(int aAngle) {
		this.int_angle = aAngle;
		this.checkIfTranslate();
	}

	public void save() {
		this.savedTranslateX = this.translateX;
		this.savedTranslateY = this.translateY;
		this.savedScaleX = this.scaleX;
		this.savedScaleY = this.scaleY;
		this.savedAngle = this.int_angle;
	}

	public void scale(float x, float y) {
		this.scaleX = x * this.scaleX;
		this.scaleY = y * this.scaleY;
		this.checkIfTranslate();
	}

	public void setClip(int x, int y, int w, int h) {
		this.doClip = true;
		this.clipX = x;
		this.clipY = y;
		this.clipW = w;
		this.clipH = h;
	}

	public void setOpacity(float aOpacity) {
		this.opacity = aOpacity;
		this.tmp_color = LColor.white.multiply(opacity);
	}

	public void translate(float x, float y) {
		this.translateX += x * this.scaleX;
		this.translateY += y * this.scaleY;
		this.checkIfTranslate();
	}

	private Vector2f tmp_location = new Vector2f();

	private void translatePos(Vector2f pos) {
		tmp_location.set(pos.x, pos.y);
		if (this.translations) {
			tmp_location.x *= this.scaleX;
			tmp_location.y *= this.scaleY;
			tmp_location.x += this.translateX;
			tmp_location.y += this.translateY;
			if (this.int_angle != 0) {
				float x = tmp_location.x;
				float y = tmp_location.y;
				tmp_location.x = ((x * GameUtils.cos(this.int_angle)) - (y * GameUtils
						.sin(this.int_angle))) / 8192f;
				tmp_location.y = ((x * GameUtils.sin(this.int_angle)) + (y * GameUtils
						.cos(this.int_angle))) / 8192f;
			}
		}
	}

	public boolean isDisposed() {
		return disposed;
	}

}

package loon.action.sprite;

import loon.core.LObject;
import loon.core.geom.RectBox;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;

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
public class Picture extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1982153514439690901L;

	private boolean visible;

	private int width, height;

	private LTexture image;

	public Picture(String fileName) {
		this(fileName, 0, 0);
	}

	public Picture(int x, int y) {
		this((LTexture) null, x, y);
	}

	public Picture(String fileName, int x, int y) {
		this(new LTexture(fileName), x, y);
	}

	public Picture(LTexture image) {
		this(image, 0, 0);
	}

	public Picture(LTexture image, int x, int y) {
		if (image != null) {
			this.setImage(image);
			this.width = image.getWidth();
			this.height = image.getHeight();
		}
		this.setLocation(x, y);
		this.visible = true;
	}

	@Override
	public void createUI(GLEx g) {
		if (visible) {
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(alpha);
			}
			g.drawTexture(image, x(), y());
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(1.0f);
			}
		}
	}

	public boolean equals(Picture p) {
		if (image.equals(p.image)) {
			return true;
		}
		if (this.width == p.width && this.height == p.height) {
			if (image.hashCode() == p.image.hashCode()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void update(long timer) {
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void dispose() {
		if (image != null) {
			image.dispose();
			image = null;
		}
	}

	public void setImage(String fileName) {
		this.image = new LTexture(fileName);
		this.width = image.getWidth();
		this.height = image.getHeight();
	}

	public void setImage(LTexture image) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	@Override
	public LTexture getBitmap() {
		return image;
	}

}

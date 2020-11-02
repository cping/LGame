/**
 * Copyright 2014
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
 * @version 0.4.1
 */
package loon.action.sprite;

import loon.core.LRelease;
import loon.core.geom.RectBox.Rect2i;
import loon.core.graphics.device.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class SpriteBatchSheet implements LRelease {

	private float rate;
	private float currentFrame;
	private boolean finished;
	private boolean centered;
	private int imgWidth;
	private int imgHeight;
	private int spriteWidth;
	private int spriteHeight;
	private int hFrames;
	private int vFrames;
	private int frames;
	private int x;
	private int y;
	private LTexture image;
	private Rect2i spriteRect, destRect;

	public SpriteBatchSheet(String fileName, int hFrames, int vFrames, float r) {
		this(LTextures.loadTexture(fileName), hFrames, vFrames, r);
	}

	public SpriteBatchSheet(LTexture image, int hFrames, int vFrames, float r) {
		this.hFrames = hFrames;
		this.vFrames = vFrames;
		this.image = image;
		this.rate = r;
		this.spriteWidth = imgWidth = image.getWidth() / this.hFrames;
		this.spriteHeight = imgHeight = image.getHeight() / this.vFrames;
		this.frames = (hFrames * vFrames);
		this.spriteRect = new Rect2i(0, 0, imgWidth, imgHeight);
		this.destRect = new Rect2i();
	}

	public boolean animate() {
		finished = false;
		if (currentFrame + rate < frames) {
			currentFrame += rate;
		} else {
			finished = true;
			currentFrame = 0;
		}
		spriteRect.top = (((int) currentFrame) / hFrames) * spriteHeight;
		spriteRect.bottom = spriteHeight;
		spriteRect.left = (((int) currentFrame) % hFrames) * spriteWidth;
		spriteRect.right = spriteWidth;
		return finished;
	}

	public void animate(int frame) {
		currentFrame = frame;
		spriteRect.top = ((frame) / hFrames) * spriteHeight;
		spriteRect.bottom = spriteHeight;
		spriteRect.left = ((frame) % hFrames) * spriteWidth;
		spriteRect.right = spriteWidth;
	}

	public void animate(int start, int end) {
		end++;
		if (currentFrame < start) {
			currentFrame = start;
		}
		if (currentFrame + rate < end - 1) {
			currentFrame += rate;
		} else {
			finished = true;
			currentFrame = start;
		}
		spriteRect.top = (((int) currentFrame) / hFrames) * spriteHeight;
		spriteRect.bottom = spriteHeight;
		spriteRect.left = (((int) currentFrame) % hFrames) * spriteWidth;
		spriteRect.right = spriteWidth;
	}

	public void animate(int start, int end, float mod) {
		end++;
		if (currentFrame < start) {
			currentFrame = start;
		}
		if (currentFrame + (rate * mod) < end - 1)
			currentFrame += (rate * mod);
		else {
			finished = true;
			currentFrame = start;
		}
		spriteRect.top = (((int) currentFrame) / hFrames) * spriteHeight;
		spriteRect.bottom = spriteHeight;
		spriteRect.left = (((int) currentFrame) % hFrames) * spriteWidth;
		spriteRect.right = spriteWidth;
	}

	public void build(float x, float y, int xSize, int ySize) {
		update(x, y, xSize, ySize);
		this.x = (int) x;
		this.y = (int) y;
	}

	private LColor color = new LColor(LColor.white);

	public void draw(SpriteBatch batch) {
		if (currentFrame >= 0) {
			batch.draw(this.image, getDestRectLeft(), getDestRectTop(),
					getDestRectRight(), getDestRectBottom(), getSpriteLeft(),
					getSpriteTop(), getSpriteRight(), getSpriteBottom(), color);
		}
	}

	public void draw(SpriteBatch batch, int maxX, int maxY) {
		if (getDestRectRight() >= 0 && getDestRectBottom() >= 0
				&& getDestRectLeft() < maxX && getDestRectTop() < maxY) {
			draw(batch);
		}
	}

	public void update(float x, float y) {
		if (centered) {
			destRect.top = (int) (y - (imgHeight / 2));
			destRect.bottom = imgHeight;
			destRect.left = (int) (x - (imgWidth / 2));
			destRect.right = imgWidth;
		} else {
			destRect.top = (int) y;
			destRect.bottom = imgHeight;
			destRect.left = (int) x;
			destRect.right = imgWidth;
		}
	}

	public void update(float x, float y, int xSize, int ySize) {
		imgWidth = xSize;
		imgHeight = ySize;
		update(x, y);
	}

	public void updateSprite(int x1, int y1, int x2, int y2) {
		spriteRect.top = y1;
		spriteRect.right = x2;
		spriteRect.bottom = y2;
		spriteRect.left = x1;
	}

	public void reflect() {
		int oldLeft = spriteRect.left;
		spriteRect.left = spriteRect.right;
		spriteRect.right = oldLeft;
	}

	public void flip() {
		int oldTop = spriteRect.top;
		spriteRect.top = spriteRect.bottom;
		spriteRect.bottom = oldTop;
	}

	public void resize(int imgWidth, int imgHeight) {
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
	}

	public void resetDest() {
		destRect.top = 0;
		destRect.bottom = 0;
		destRect.left = 0;
		destRect.right = 0;
	}

	public void updateLayout() {
		updateLayout(vFrames, hFrames);
	}

	public void updateLayout(int vFrames, int hFrames) {
		if (vFrames < 1) {
			vFrames = 1;
		} else if (vFrames > 99) {
			vFrames = 99;
		}
		if (hFrames < 1) {
			hFrames = 1;
		} else if (hFrames > 99) {
			hFrames = 99;
		}
		this.hFrames = hFrames;
		this.vFrames = vFrames;
		spriteWidth = imgWidth = image.getWidth() / this.hFrames;
		spriteHeight = imgHeight = image.getHeight() / this.vFrames;
		frames = (hFrames * vFrames);
		spriteRect.right = spriteWidth;
		spriteRect.bottom = spriteHeight;
	}

	public String framesToString() {
		String v = vFrames < 10 ? "0" + vFrames : "" + vFrames;
		String h = hFrames < 10 ? "0" + hFrames : "" + hFrames;
		return v + h;
	}

	public void setImage(LTexture img) {
		this.image = img;
	}

	public LTexture getImage() {
		return image;
	}

	public Rect2i getDestRect() {
		return destRect;
	}

	public Rect2i getSpriteRect() {
		return spriteRect;
	}

	public int getImageWidth() {
		return imgWidth;
	}

	public int getImageHeight() {
		return imgWidth;
	}

	public int getOriginalImageWidth() {
		return image.getWidth();
	}

	public int getOriginalImageHeight() {
		return image.getHeight();
	}

	public int getHFrames() {
		return hFrames;
	}

	public int getVFrames() {
		return vFrames;
	}

	public int getCurrentFrame() {
		return (int) currentFrame;
	}

	public int getFrames() {
		return frames;
	}

	public int getSpriteWidth() {
		return spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}

	public int getSpriteTop() {
		return spriteRect.top;
	}

	public int getSpriteRight() {
		return spriteRect.right;
	}

	public int getSpriteBottom() {
		return spriteRect.bottom;
	}

	public int getSpriteLeft() {
		return spriteRect.left;
	}

	public int getDestRectTop() {
		return destRect.top;
	}

	public int getDestRectRight() {
		return destRect.right;
	}

	public int getDestRectBottom() {
		return destRect.bottom;
	}

	public int getDestRectLeft() {
		return destRect.left;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public float getRate() {
		return rate;
	}

	public void center() {
		centered = true;
	}

	public boolean isCentered() {
		return centered;
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean isAnimating() {
		return currentFrame > 0;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	@Override
	public void dispose() {
		if (image != null) {
			image.dispose();
		}

	}
}

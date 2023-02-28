/**
 * 
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
package loon.component;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.events.CallFunction;
import loon.opengl.GLEx;

/**
 * 一个让选中位置闪啊闪的UI
 */
public class LSelectorIcon extends LComponent {

	private CallFunction _function;

	private int x;
	private int y;

	private int pSize;
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;

	private float alpha;
	private float alphaRate;
	private boolean increaseAlpha;

	private LColor borderColor = LColor.black;

	public LSelectorIcon(float x, float y, int size) {
		this((int) x, (int) y, size);
	}

	public LSelectorIcon(int x, int y, int size) {
		this(x, y, size, LColor.black, LColor.white);
	}

	public LSelectorIcon(int x, int y, int size, LColor b, LColor c) {
		super(x, y, size, size);
		this.minX = 0;
		this.minY = 0;
		this.maxX = 1;
		this.maxY = 1;
		this.pSize = size;
		this.alphaRate = 3f;
		this.borderColor = b;
		this._component_baseColor = c;
	}

	public void draw(Canvas g, int mainX, int mainY) {
		int fill = g.getFillColor();
		int stroke = g.getStrokeColor();
		g.setColor(_component_baseColor.getRed(), _component_baseColor.getGreen(), _component_baseColor.getBlue(), 125);
		g.fillRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize, pSize);
		g.strokeRect(mainX + (x * pSize), mainY + (y * pSize), pSize - 2, pSize - 2);
		g.setColor(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), (int) alpha);
		g.strokeRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize + 1, pSize + 1);
		g.setFillColor(fill);
		g.setStrokeColor(stroke);
	}

	public void draw(GLEx g, int mainX, int mainY) {
		int color = g.color();
		g.setColor(_component_baseColor.getRed(), _component_baseColor.getGreen(), _component_baseColor.getBlue(), 125);
		g.fillRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize, pSize);
		g.drawRect(mainX + (x * pSize), mainY + (y * pSize), pSize - 2, pSize - 2);
		g.setColor(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), (int) alpha);
		g.drawRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize + 1, pSize + 1);
		g.setColor(color);
	}

	public void draw(SpriteBatch batch, int mainX, int mainY) {
		float color = batch.getFloatColor();
		batch.setColor(_component_baseColor.getRed(), _component_baseColor.getGreen(), _component_baseColor.getBlue(),
				125);
		batch.fillRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize, pSize);
		batch.drawRect(mainX + (x * pSize), mainY + (y * pSize), pSize - 2, pSize - 2);
		batch.setColor(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), (int) alpha);
		batch.drawRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize + 1, pSize + 1);
		batch.setColor(color);
	}

	public void move(int direction) {
		switch (direction) {
		case (0):
			if (y > minY)
				y--;
			else {
				if (x > minX) {
					y = maxY - 1;
					x--;
				} else {
					x = maxX - 1;
					y = maxY - 1;
				}
			}
			break;
		case (1):
			if (x < maxX - 1)
				x++;
			else {
				if (y < maxY - 1) {
					y++;
					x = minX;
				} else {
					y = minY;
					x = minX;
				}
			}
			break;
		case (2):
			if (y < maxY - 1)
				y++;
			else {
				if (x < maxX - 1) {
					y = minY;
					x++;
				} else {
					x = minX;
					y = minY;
				}
			}
			break;
		case (3):
			if (x > minX)
				x--;
			else {
				if (y > minY) {
					y--;
					x = maxX - 1;
				} else {
					y = maxY - 1;
					x = maxX - 1;
				}
			}
			break;
		default:
			move(0);
			break;
		}
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setMinMax(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		if (x < minX) {
			x = minX;
		}
		if (x > maxX - 1) {
			x = maxX - 1;
		}
		if (y < minY) {
			y = minY;
		}
		if (y > maxY - 1) {
			y = maxY - 1;
		}
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		if (increaseAlpha) {
			if (alpha + alphaRate < 255.0)
				alpha += alphaRate;
			else {
				alpha = 255;
				increaseAlpha = false;
			}
		} else {
			if (alpha - alphaRate > 70)
				alpha -= alphaRate;
			else {
				alpha = 70;
				increaseAlpha = true;
			}
		}
	}

	public int getMinX() {
		return minX;
	}

	public void setMinX(int minX) {
		this.minX = minX;
	}

	public int getMinY() {
		return minY;
	}

	public void setMinY(int minY) {
		this.minY = minY;
	}

	public int getMaxX() {
		return maxX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	public float getAlphaRate() {
		return alphaRate;
	}

	public void setAlphaRate(float alphaRate) {
		this.alphaRate = alphaRate;
	}

	public boolean isIncreaseAlpha() {
		return increaseAlpha;
	}

	public void setIncreaseAlpha(boolean increaseAlpha) {
		this.increaseAlpha = increaseAlpha;
	}

	public LColor getBackgroundColor() {
		return _component_baseColor.cpy();
	}

	public void setBackgroundColor(LColor c) {
		this._component_baseColor = c;
	}

	public LColor getBorderColor() {
		return borderColor.cpy();
	}

	public void setBorderColor(LColor borderColor) {
		this.borderColor = borderColor;
	}

	@Override
	public void processTouchReleased() {
		super.processTouchReleased();
		if (_function != null) {
			_function.call(this);
		}
	}

	public CallFunction getFunction() {
		return _function;
	}

	public void setFunction(CallFunction function) {
		this._function = function;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		draw(g, x, y);
	}

	@Override
	public String getUIName() {
		return "SelectorIcon";
	}

}

package loon.action.sprite.effect;

import loon.action.map.Config;
import loon.action.sprite.ISprite;
import loon.core.LObject;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.LTimer;


/**
 * Copyright 2008 - 2011
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
/**
 * 0.3.2版新增类，用以实现特定图像的滚动播放(循环展示)
 */
public class ScrollEffect extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int backgroundLoop;

	private int count;

	private int width, height;

	private LTexture texture;

	private boolean visible, stop;

	private LTimer timer;

	private int code;

	public ScrollEffect(String fileName) {
		this(LTextures.loadTexture(fileName));
	}

	public ScrollEffect(String fileName, RectBox rect) {
		this(Config.DOWN, LTextures.loadTexture(fileName), rect);
	}
	
	public ScrollEffect(LTexture tex2d) {
		this(Config.DOWN, tex2d, LSystem.screenRect);
	}

	public ScrollEffect(int d, String fileName) {
		this(d,  LTextures.loadTexture(fileName));
	}

	public ScrollEffect(int d, LTexture tex2d) {
		this(d, tex2d, LSystem.screenRect);
	}

	public ScrollEffect(int d, String fileName, RectBox limit) {
		this(d, LTextures.loadTexture(fileName), limit);
	}

	public ScrollEffect(int d, LTexture tex2d, RectBox limit) {
		this(d, tex2d, limit.x, limit.y, limit.width, limit.height);
	}

	public ScrollEffect(int d, LTexture tex2d, float x, float y, int w, int h) {
		this.setLocation(x, y);
		this.texture = tex2d;
		this.width = w;
		this.height = h;
		this.count = 1;
		this.timer = new LTimer(10);
		this.visible = true;
		this.code = d;
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
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
	public void update(long elapsedTime) {
		if (stop) {
			return;
		}
		if (timer.action(elapsedTime)) {
			switch (code) {
			case Config.DOWN:
			case Config.TDOWN:
			case Config.UP:
			case Config.TUP:
				this.backgroundLoop = ((backgroundLoop + count) % height);
				break;
			case Config.LEFT:
			case Config.RIGHT:
			case Config.TLEFT:
			case Config.TRIGHT:
				this.backgroundLoop = ((backgroundLoop + count) % width);
				break;
			}
		}
	}

	@Override
	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(alpha);
		}
		texture.glBegin();
		switch (code) {
		case Config.DOWN:
		case Config.TDOWN:
			for (int i = -1; i < 1; i++) {
				for (int j = 0; j < 1; j++) {
					texture.draw(x() + (j * width), y()
							+ (i * height + backgroundLoop), width, height, 0,
							0, width, height);
				}
			}
			break;
		case Config.RIGHT:
		case Config.TRIGHT:
			for (int j = -1; j < 1; j++) {
				for (int i = 0; i < 1; i++) {
					texture.draw(x() + (j * width + backgroundLoop), y()
							+ (i * height), width, height, 0, 0, width, height);
				}
			}
			break;
		case Config.UP:
		case Config.TUP:
			for (int i = -1; i < 1; i++) {
				for (int j = 0; j < 1; j++) {
					texture.draw(x() + (j * width), y()
							- (i * height + backgroundLoop), width, height, 0,
							0, width, height);
				}
			}
			break;
		case Config.LEFT:
		case Config.TLEFT:
			for (int j = -1; j < 1; j++) {
				for (int i = 0; i < 1; i++) {
					texture.draw(x() - (j * width + backgroundLoop), y()
							+ (i * height), width, height, 0, 0, width, height);
				}
			}
			break;
		}
		texture.glEnd();
		if (alpha > 0 && alpha < 1) {
			g.setAlpha(1f);
		}
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public LTexture getBitmap() {
		return texture;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
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
		if (texture != null) {
			texture.destroy();
			texture = null;
		}
	}

}

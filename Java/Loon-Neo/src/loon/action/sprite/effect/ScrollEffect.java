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
import loon.action.map.Config;
import loon.action.sprite.Entity;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

/**
 * 滚屏效果类,用以实现特定图像的滚动播放(循环展示)
 */
public class ScrollEffect extends Entity implements BaseEffect {

	private int backgroundLoop;

	private int count;

	private boolean completed;

	private LTimer timer;

	private int model;

	public ScrollEffect(String fileName) {
		this(LSystem.loadTexture(fileName));
	}

	public ScrollEffect(String fileName, RectBox rect) {
		this(Config.DOWN, LSystem.loadTexture(fileName), rect);
	}

	public ScrollEffect(LTexture tex2d) {
		this(Config.DOWN, tex2d, null);
	}

	public ScrollEffect(int d, String fileName) {
		this(d, LSystem.loadTexture(fileName));
	}

	public ScrollEffect(int d, LTexture tex2d) {
		this(d, tex2d, null);
	}

	public ScrollEffect(int d, String fileName, RectBox limit) {
		this(d, LSystem.loadTexture(fileName), limit);
	}

	public ScrollEffect(int d, LTexture tex2d, RectBox limit) {
		if (limit == null) {
			if (tex2d == null) {
				limit = LSystem.viewSize.getRect();
			} else {
				limit = new RectBox(0, 0, tex2d.getWidth(), tex2d.getHeight());
			}
		}
		this.setLocation(limit.x, limit.y);
		this.setSize(limit.width, limit.height);
		this.setTexture(tex2d);
		this.setRepaint(true);
		this.count = 1;
		this.timer = new LTimer(10);
		this.model = d;
	}

	public ScrollEffect setDelay(long delay) {
		timer.setDelay(delay);
		return this;
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public ScrollEffect setDelayS(float s) {
		timer.setDelayS(s);
		return this;
	}

	public float getDelayS() {
		return timer.getDelayS();
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (completed) {
			return;
		}
		if (timer.action(elapsedTime)) {
			switch (model) {
			case Config.DOWN:
			case Config.TDOWN:
			case Config.UP:
			case Config.TUP:
				this.backgroundLoop = (int) ((backgroundLoop + count) % _height);
				break;
			case Config.LEFT:
			case Config.RIGHT:
			case Config.TLEFT:
			case Config.TRIGHT:
				this.backgroundLoop = (int) ((backgroundLoop + count) % _width);
				break;
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (_image == null) {
			return;
		}
		switch (model) {
		case Config.DOWN:
		case Config.TDOWN:
			for (int i = -1; i < 1; i++) {
				for (int j = 0; j < 1; j++) {
					final float offX = (j * _width) + offsetX;
					final float offY = (i * _height + backgroundLoop) + offsetX;
					g.draw(_image, drawX(offX), drawX(offY), _width, _height, 0, 0, _width, _height);
				}
			}
			break;
		case Config.RIGHT:
		case Config.TRIGHT:
			for (int j = -1; j < 1; j++) {
				for (int i = 0; i < 1; i++) {
					final float offX = (j * _width - backgroundLoop + _width) + offsetX;
					final float offY = (i * _height) + offsetX;
					g.draw(_image, drawX(offX), drawX(offY), _width, _height, 0, 0, _width, _height);
				}
			}
			break;
		case Config.UP:
		case Config.TUP:
			for (int i = -1; i < 1; i++) {
				for (int j = 0; j < 1; j++) {
					final float offX = (j * _width) + offsetX;
					final float offY = (i * _height - backgroundLoop + _height) + offsetX;
					g.draw(_image, drawX(offX), drawX(offY), _width, _height, 0, 0, _width, _height);
				}
			}
			break;
		case Config.LEFT:
		case Config.TLEFT:
			for (int j = -1; j < 1; j++) {
				for (int i = 0; i < 1; i++) {
					final float offX = (j * _width + backgroundLoop) + offsetX;
					final float offY = (i * _height) + offsetX;
					g.draw(_image, drawX(offX), drawX(offY), _width, _height, 0, 0, _width, _height);
				}
			}
			break;
		}
		g.flush();
	}

	public int getCount() {
		return count;
	}

	public ScrollEffect setCount(int count) {
		this.count = count;
		return this;
	}

	@Override
	public ScrollEffect setStop(boolean completed) {
		this.completed = completed;
		return this;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void close() {
		super.close();
		completed = true;
	}

}

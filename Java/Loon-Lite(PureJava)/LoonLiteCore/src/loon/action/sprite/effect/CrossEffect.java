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

import loon.LTexture;
import loon.LSystem;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;

/**
 * 0.3.2起新增类，百叶窗特效 0--竖屏,1--横屏
 */
public class CrossEffect extends BaseAbstractEffect {

	private boolean _createTexture;

	private LColor _crossColor;

	private LTexture otexture, ntexture;

	private int count, code;

	private int maxcount = 16;

	private int part;

	private int left;

	private int right;

	private LTexture tmp;

	public CrossEffect(int c, String fileName) {
		this(c, LSystem.loadTexture(fileName));
	}

	public CrossEffect(int c, String oldImgPath, String newImgPath) {
		this(c, LSystem.loadTexture(oldImgPath), LSystem.loadTexture(newImgPath));
	}

	public CrossEffect(int c, LTexture o) {
		this(c, o, null);
	}

	public CrossEffect(int c, LTexture o, LTexture n) {
		this.code = c;
		this.otexture = o;
		this.ntexture = n;
		init(o.getWidth(), o.getHeight());
	}

	public CrossEffect(int c, LColor color, float w, float h) {
		this.code = c;
		this.otexture = this.ntexture = null;
		this._crossColor = color;
		this._createTexture = true;
		this.init(w, h);
	}

	protected void init(float w, float h) {
		this._width = w;
		this._height = h;
		if (_width > _height) {
			maxcount = 16;
		} else {
			maxcount = 8;
		}
		this.setDelay(160);
		this.setRepaint(true);
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_createTexture) {
			return;
		}
		if (this.count > this.maxcount) {
			this._completed = true;
		}
		if (_timer.action(elapsedTime)) {
			count++;
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (_createTexture) {
			if (_crossColor != null) {
				otexture = TextureUtils.createTexture(width(), height(), _crossColor);
			}
			_createTexture = false;
			return;
		}
		if (_completed) {
			if (ntexture != null) {
				g.draw(ntexture, drawX(offsetX), drawY(offsetY));
			}
			return;
		}
		part = 0;
		left = 0;
		right = 0;
		tmp = null;
		switch (code) {
		default:
			part = (int) (_width / this.maxcount / 2);
			for (int i = 0; i <= this.maxcount; i++) {
				if (i <= this.count) {
					tmp = this.ntexture;
					if (tmp == null) {
						continue;
					}
				} else {
					tmp = this.otexture;
				}
				left = i * 2 * part;
				right = (int) (_width - ((i + 1) * 2 - 1) * part);
				g.draw(tmp, drawX(offsetX + left), drawY(offsetY), part, _height, left, 0, left + part, _height);
				g.draw(tmp, drawX(offsetX + right), drawY(offsetY), part, _height, right, 0, right + part, _height);
			}
			break;
		case 1:
			part = (int) (_height / this.maxcount / 2);
			for (int i = 0; i <= this.maxcount; i++) {
				if (i <= this.count) {
					tmp = this.ntexture;
					if (tmp == null) {
						continue;
					}
				} else {
					tmp = this.otexture;
				}
				int up = i * 2 * part;
				int down = (int) (_height - ((i + 1) * 2 - 1) * part);
				g.draw(tmp, drawX(offsetX), drawY(up), _width, part, 0, up, _width, up + part);
				g.draw(tmp, drawX(offsetY), drawY(down), _width, part, 0, down, _width, down + part);
			}
			break;
		}

	}

	@Override
	public CrossEffect reset() {
		super.reset();
		this.count = 0;
		return this;
	}

	@Override
	public LTexture getBitmap() {
		return otexture;
	}

	public int getMaxCount() {
		return maxcount;
	}

	public CrossEffect setMaxCount(int maxcount) {
		this.maxcount = maxcount;
		return this;
	}

	@Override
	public CrossEffect setAutoRemoved(boolean a) {
		super.setAutoRemoved(a);
		return this;
	}

	@Override
	public void close() {
		super.close();
		if (otexture != null) {
			otexture.close();
			otexture = null;
		}
		if (ntexture != null) {
			ntexture.close();
			ntexture = null;
		}
	}

}

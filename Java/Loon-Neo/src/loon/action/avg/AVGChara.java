/**
 * Copyright 2008 - 2010
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
package loon.action.avg;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.Visible;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.events.DrawListener;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public final class AVGChara implements Visible, XY, LRelease {

	private LTexture _cgTexture;

	private DrawListener<AVGChara> _listener;

	private float _cgWidth;

	private float _cgHeight;

	private float _cgMovePos;

	private float _moveSpeed;

	private int _cgDirection;

	private int _cgMoveSleep;

	private boolean _cgMoving, _closed;

	private LColor _tempColor = new LColor();

	private LColor _charaColor;

	protected AVGAnm anm;

	protected float x;

	protected float y;

	protected int flag;

	protected float time;

	protected float currentFrame;

	protected float opacity;

	protected boolean flipX, flipY;

	protected boolean moved, showAnimation, visible;

	protected int maxWidth, maxHeight;

	/**
	 * 构造函数，初始化角色图
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param cw
	 * @param ch
	 */
	public AVGChara(LTexture image, final int x, final int y, int cw, int ch) {
		this.load(image, x, y, cw, ch, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public AVGChara(LTexture image, final float x, final float y) {
		this.load(image, x, y);
	}

	public AVGChara(final String resName, final float x, final float y, final int sw, final int sh) {
		this(resName, x, y, -1, -1, sw, sh);
	}

	public AVGChara(final String resName, final float x, final float y, final int w, final int h, final int sw,
			final int sh) {
		String path = resName;
		if (StringUtils.startsWith(path, LSystem.DOUBLE_QUOTES)) {
			path = resName.replaceAll("\"", LSystem.EMPTY);
		}
		if (path.endsWith(".an")) {
			this.x = x;
			this.y = y;
			this._cgWidth = w;
			this._cgHeight = h;
			this.showAnimation = true;
			this.anm = new AVGAnm(path);
			if (_cgWidth <= 0) {
				_cgWidth = anm.getWidth();
			}
			if (_cgHeight <= 0) {
				_cgHeight = anm.getHeight();
			}
			this.maxWidth = sw;
			this.maxHeight = sh;
		} else {
			this.load(LSystem.loadTexture(path), x, y, w, h, sw, sh);
		}
		this._moveSpeed = 1f;
		this._cgMoveSleep = 10;
		this.flag = -1;
		this.visible = true;
	}

	private void load(LTexture image, final float x, final float y) {
		this.load(image, x, y, image.getWidth(), image.getHeight(), LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	private void load(LTexture image, final float x, final float y, int cw, int ch, final int w, final int h) {
		this._cgTexture = image;
		if (_cgTexture != null) {
			this._cgWidth = (cw == -1) ? _cgTexture.getWidth() : cw;
			this._cgHeight = (ch == -1) ? _cgTexture.getHeight() : ch;
		} else {
			this._cgWidth = cw;
			this._cgHeight = ch;
		}
		this.maxWidth = w;
		this.maxHeight = h;
		this.showAnimation = false;
		this.moved = true;
		this.visible = true;
		this.x = x;
		this.y = y;
		this.flag = -1;
		this._cgMovePos = 0;
		this._cgDirection = getDirection();
		this._moveSpeed = 1f;
		this._cgMoveSleep = 10;
		if (_cgDirection == 0) {
			this._cgMovePos = -(_cgWidth / 2);
		} else {
			this._cgMovePos = maxWidth;
		}
	}

	public AVGChara setMoveSpeed(float s) {
		this._moveSpeed = s;
		return this;
	}

	public float getMoveSpeed() {
		return this._moveSpeed;
	}

	public AVGChara setFlag(int f, float delay) {
		this.flag = f;
		this.time = delay;
		if (flag == ISprite.TYPE_FADE_IN) {
			this.currentFrame = this.time;
		} else {
			this.currentFrame = 0;
		}
		return this;
	}

	public int getScreenWidth() {
		return maxWidth;
	}

	public int getScreenHeight() {
		return maxHeight;
	}

	private int getDirection() {
		int offsetX = maxWidth / 2;
		if (x < offsetX) {
			return 0;
		} else {
			return 1;
		}
	}

	public AVGChara setColor(String c) {
		if (_charaColor == null) {
			_charaColor = new LColor(c);
		} else {
			_charaColor.setColor(c);
		}
		return this;
	}

	public AVGChara setColor(LColor c) {
		if (_charaColor == null) {
			_charaColor = new LColor(c);
		} else {
			_charaColor.setColor(c);
		}
		return this;
	}

	public LColor getColor() {
		return _charaColor;
	}

	public AVGChara setMove(boolean move) {
		moved = move;
		return this;
	}

	public boolean isMoved() {
		return moved;
	}

	public void flush() {
		_cgTexture = null;
		x = 0;
		y = 0;
	}

	public float getNext() {
		return _cgMovePos;
	}

	public void clearMovePos() {
		this._cgMovePos = 0f;
	}

	public float getMaxNext() {
		return x;
	}

	public boolean isXMoved() {
		return MathUtils.equal(_cgMovePos, x);
	}

	public boolean isYMoved() {
		return MathUtils.equal(_cgMovePos, y);
	}

	public boolean next() {
		_cgMoving = false;
		if (!isXMoved()) {
			for (int sleep = 0; sleep < _cgMoveSleep; sleep++) {
				if (_cgDirection == 0) {
					_cgMoving = (x > _cgMovePos);
				} else {
					_cgMoving = (x < _cgMovePos);
				}
				if (_cgMoving) {
					switch (_cgDirection) {
					case 0:
						_cgMovePos += LSystem.toIScaleFPS(_moveSpeed);
						break;
					case 1:
						_cgMovePos -= LSystem.toIScaleFPS(_moveSpeed);
						break;
					default:
						_cgMovePos = x;
						break;
					}
				} else {
					_cgMovePos = x;
				}
			}
		}
		return _cgMoving;
	}

	void update(long t) {
		if (_listener != null) {
			_listener.update(t);
		}
	}

	void draw(GLEx g, LColor color) {
		draw(g, 0f, 0f, color);
	}

	void draw(GLEx g, float nx, float ny, LColor c) {
		if (_cgWidth <= 0f && _cgHeight <= 0f) {
			g.draw(_cgTexture, nx + _cgMovePos, ny + y, _tempColor.setColor(LColor.combine(c, _charaColor)), flipX,
					flipY);
		} else {
			g.draw(_cgTexture, nx + _cgMovePos, ny + y, _cgWidth, _cgHeight,
					_tempColor.setColor(LColor.combine(c, _charaColor)), flipX, flipY);
		}
		if (_listener != null) {
			_listener.draw(g, nx, ny);
		}
	}

	public AVGChara setListener(DrawListener<AVGChara> l) {
		this._listener = l;
		return this;
	}

	public DrawListener<AVGChara> getListener() {
		return this._listener;
	}

	public boolean isFlipX() {
		return this.flipX;
	}

	public boolean isFlipY() {
		return this.flipY;
	}

	public AVGChara setFlipX(boolean x) {
		this.flipX = x;
		return this;
	}

	public AVGChara setFlipY(boolean y) {
		this.flipY = y;
		return this;
	}

	public AVGChara setFlip(boolean x, boolean y) {
		this.setFlipX(x);
		this.setFlipY(y);
		return this;
	}

	@Override
	public float getX() {
		return x;
	}

	public AVGChara setX(float x) {
		if (moved) {
			float move = x - this._cgMovePos;
			if (move < 0) {
				this._cgMovePos = this.x;
				this._cgDirection = 1;
				this.x = x;
			} else {
				this._cgMovePos = move;
				this.x = x;
			}
		} else {
			this._cgMovePos = x;
			this.x = x;
		}
		return this;
	}

	@Override
	public float getY() {
		return y;
	}

	public AVGChara setY(float y) {
		this.y = y;
		return this;
	}

	public float getHeight() {
		return _cgHeight;
	}

	public void setHeight(float h) {
		this._cgHeight = h;
	}

	public float getWidth() {
		return _cgWidth;
	}

	public void setWidth(float w) {
		this._cgWidth = w;
	}

	public int getMoveSleep() {
		return _cgMoveSleep;
	}

	public void setMoveSleep(int s) {
		this._cgMoveSleep = s;
	}

	public float getMoveX() {
		return _cgMovePos;
	}

	public boolean isAnimation() {
		return showAnimation;
	}

	public AVGChara setAnimation(boolean a) {
		this.showAnimation = a;
		return this;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	public LTexture getTexture() {
		return _cgTexture;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		this.visible = false;
		if (_cgTexture != null) {
			_cgTexture.close();
			_cgTexture = null;
		}
		if (anm != null) {
			anm.close();
			anm = null;
			showAnimation = false;
		}
		_closed = true;
	}

}

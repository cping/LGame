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

	private float _flashScale;

	private int _cgDirection;

	private int _cgMoveSleep;

	private boolean _cgMoving, _closed;

	private boolean _cgFlashing;

	private LColor _charaColor;

	private AVGCG _avgParent;

	private float _flashOffset;

	private float _maxFlashZoom;

	private boolean _zooming;

	protected AVGAnm anm;

	protected float x;

	protected float y;

	protected int flag;

	protected float time;

	protected float currentFrame;

	protected float opacity;

	protected boolean flipX, flipY;

	protected float scaleX, scaleY;

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
			this.init();
		} else {
			this.load(LSystem.loadTexture(path), x, y, w, h, sw, sh);
		}
	}

	private void load(LTexture image, final float x, final float y) {
		this.load(image, x, y, image.getWidth(), image.getHeight(), LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	private void load(LTexture image, final float x, final float y, int cw, int ch, final int w, final int h) {
		this.init();
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
		this._cgDirection = getDirection();
		if (_cgDirection == 0) {
			this._cgMovePos = -(_cgWidth / 2);
		} else {
			this._cgMovePos = maxWidth;
		}
	}

	private void init() {
		this._cgMovePos = 0;
		this._moveSpeed = 1f;
		this._cgMoveSleep = 10;
		this._flashScale = 1f;
		this._flashOffset = 0.05f;
		this._maxFlashZoom = 1.25f;
		this._zooming = false;
		this.flag = -1;
		this.scaleX = scaleY = 1f;
		this.visible = true;
	}

	public AVGChara setFlashOffset(float s) {
		this._flashOffset = s;
		return this;
	}

	public float getFlashOffset() {
		return this._flashOffset;
	}

	public AVGChara setMaxFlashZoom(float z) {
		this._maxFlashZoom = z;
		return this;
	}

	public float getMaxFlashZoom() {
		return this._maxFlashZoom;
	}

	public AVGChara setParent(AVGCG cg) {
		this._avgParent = cg;
		return this;
	}

	public AVGCG getParent() {
		return this._avgParent;
	}

	public AVGChara setScale(float x, float y) {
		setScaleX(x);
		setScaleY(y);
		return this;
	}

	public AVGChara setScaleX(float x) {
		this.scaleX = x;
		return this;
	}

	public AVGChara setScaleY(float y) {
		this.scaleY = y;
		return this;
	}

	public float getScaleX() {
		return this.scaleX;
	}

	public float getScaleY() {
		return this.scaleY;
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

	public float getMaxScale() {
		return MathUtils.max(scaleX, scaleY);
	}

	public void setFlashing(boolean f) {
		this._cgFlashing = f;
		this._zooming = false;
		this._flashScale = 1f;
	}

	public boolean isFlashing() {
		return this._cgFlashing;
	}

	void update(long t) {
		if (!_cgMoving && _cgFlashing) {
			final float defScale = getMaxScale();
			final float maxScale = defScale * _maxFlashZoom;
			if (!_zooming && _flashScale <= maxScale) {
				_flashScale += LSystem.toScaleFPS(_flashOffset, LSystem.MIN_SECONE_SPEED_FIXED);
				if (_flashScale >= maxScale) {
					_zooming = true;
				}
			} else if (_zooming && _flashScale >= defScale) {
				_flashScale -= LSystem.toScaleFPS(_flashOffset, LSystem.MIN_SECONE_SPEED_FIXED);
				if (_flashScale <= defScale) {
					_flashScale = 1f;
					_cgFlashing = false;
				}
			}
		}
		if (_listener != null) {
			_listener.update(t);
		}
	}

	protected float curScaleX() {
		return scaleX * _flashScale;
	}

	protected float curScaleY() {
		return scaleY * _flashScale;
	}

	void draw(GLEx g) {
		draw(g, 0f, 0f);
	}

	void draw(GLEx g, float nx, float ny) {
		if (_cgWidth <= 0f && _cgHeight <= 0f) {
			g.draw(_cgTexture, nx + _cgMovePos, ny + y, _charaColor, curScaleX(), curScaleY(), flipX, flipY);
		} else {
			g.draw(_cgTexture, nx + _cgMovePos, ny + y, _cgWidth, _cgHeight, _charaColor, curScaleX(), curScaleY(),
					flipX, flipY);
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


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
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.StringUtils;

public class AVGChara implements Visible, XY, LRelease {

	private LTexture _cgTexture;

	private float _cgWidth;

	private float _cgHeight;

	private float _cgMovePos;

	private int _cgDirection;

	private int _cgMoveSleep = 10;

	private boolean _cgMoving, _closed;

	protected AVGAnm anm;

	protected float x;

	protected float y;

	protected int flag = -1;

	protected float time;

	protected float currentFrame;

	protected float opacity;

	protected boolean moved, showAnimation, visible;

	protected int maxWidth, maxHeight;

	/**
	 * 构造函数，初始化角色图
	 *
	 * @param image
	 * @param x
	 * @param y
	 * @param _cgWidth
	 * @param _cgHeight
	 */
	public AVGChara(LTexture image, final int x, final int y, int _cgWidth, int _cgHeight) {
		this.load(image, x, y, _cgWidth, _cgHeight, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public AVGChara(LTexture image, final int x, final int y) {
		this.load(image, x, y);
	}

	public AVGChara(final String resName, final int x, final int y) {
		this(resName, x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public AVGChara(final String resName, final int x, final int y, final int w, final int h) {
		String path = resName;
		if (StringUtils.startsWith(path, LSystem.DOUBLE_QUOTES)) {
			path = resName.replaceAll("\"", LSystem.EMPTY);
		}
		if (path.endsWith(".an")) {
			this.x = x;
			this.y = y;
			this.showAnimation = true;
			this.anm = new AVGAnm(path);
			this.maxWidth = w;
			this.maxHeight = h;
		} else {
			this.load(LSystem.loadTexture(path), x, y);
		}
		this.visible = true;
	}

	private void load(LTexture image, final int x, final int y) {
		this.load(image, x, y, image.getWidth(), image.getHeight(), LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	private void load(LTexture image, final int x, final int y, int _cgWidth, int _cgHeight, final int w, final int h) {
		this.maxWidth = w;
		this.maxHeight = h;
		this.showAnimation = false;
		this._cgTexture = image;
		this.moved = true;
		this.visible = true;
		this._cgWidth = _cgWidth;
		this._cgHeight = _cgHeight;
		this.x = x;
		this.y = y;
		this._cgMovePos = 0;
		this._cgDirection = getDirection();
		if (_cgDirection == 0) {
			this._cgMovePos = -(_cgWidth / 2);
		} else {
			this._cgMovePos = maxWidth;
		}
	}

	public void setFlag(int f, float delay) {
		this.flag = f;
		this.time = delay;
		if (flag == ISprite.TYPE_FADE_IN) {
			this.currentFrame = this.time;
		} else {
			this.currentFrame = 0;
		}
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

	public void setMove(boolean move) {
		moved = move;
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

	public float getMaxNext() {
		return x;
	}

	public boolean next() {
		_cgMoving = false;
		if (_cgMovePos != x) {
			for (int sleep = 0; sleep < _cgMoveSleep; sleep++) {
				if (_cgDirection == 0) {
					_cgMoving = (x > _cgMovePos);
				} else {
					_cgMoving = (x < _cgMovePos);
				}
				if (_cgMoving) {
					switch (_cgDirection) {
					case 0:
						_cgMovePos += 1;
						break;
					case 1:
						_cgMovePos -= 1;
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

	}

	void draw(GLEx g) {
		g.draw(_cgTexture, _cgMovePos, y);
	}

	@Override
	public float getX() {
		return x;
	}

	public void setX(float x) {
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

	}

	@Override
	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
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

	void setAnimation(boolean a) {
		this.showAnimation = a;
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

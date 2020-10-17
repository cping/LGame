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
package loon.action.sprite;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.PlayerUtils;
import loon.Screen;
import loon.Director.Origin;
import loon.LGame;
import loon.action.ActionTween;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.map.TileMap;
import loon.action.map.items.Attribute;
import loon.canvas.LColor;
import loon.events.ResizeListener;
import loon.geom.Affine2f;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.Flip;
import loon.utils.MathUtils;

/**
 * 和瓦片地图绑定的动作对象,用来抽象一些简单的地图中精灵动作,允许渲染到SpriteBatch或者GLEx中
 */
public abstract class ActionObject extends LObject<ISprite> implements Flip<ActionObject>, Config, ISprite {

	private ResizeListener<ActionObject> _resizeListener;

	private Origin _origin = Origin.CENTER;

	private Vector2f _pivot = new Vector2f(-1, -1);

	private boolean _debugDraw = false;

	protected boolean visible = true;

	protected boolean flipX = false, flipY = false;

	protected float scaleX = 1, scaleY = 1;

	protected Sprites sprites = null;

	protected Attribute attribute;

	protected Animation animation;

	protected TileMap tiles;

	protected RectBox rectBox;

	protected float dstWidth, dstHeight;

	protected float fixedWidthOffset = 0;

	protected float fixedHeightOffset = 0;

	private LColor _filterColor = new LColor(1f, 1f, 1f, 1f);

	private LColor _debugDrawColor = LColor.red;

	public ActionObject(float x, float y, String path) {
		this(x, y, 0, 0, Animation.getDefaultAnimation(path), null);
	}

	public ActionObject(float x, float y, Animation animation) {
		this(x, y, 0, 0, animation, null);
	}

	public ActionObject(float x, float y, float dw, float dh, Animation animation, TileMap map) {
		this.setLocation(x, y);
		this.tiles = map;
		this.animation = animation;
		this.dstWidth = dw;
		this.dstHeight = dh;
		if (dw < 1 && dh < 1) {
			this.rectBox = new RectBox(x, y, animation.getSpriteImage().width(), animation.getSpriteImage().height());
		} else {
			this.rectBox = new RectBox(x, y, dw, dh);
		}
	}

	public ActionObject(float x, float y, Animation animation, TileMap map) {
		this.setLocation(x, y);
		this.tiles = map;
		this.animation = animation;
		this.dstWidth = animation.getSpriteImage().width();
		this.dstHeight = animation.getSpriteImage().height();
		if (dstWidth < 1 && dstHeight < 1) {
			this.rectBox = new RectBox(x, y, animation.getSpriteImage().width(), animation.getSpriteImage().height());
		} else {
			this.rectBox = new RectBox(x, y, dstWidth, dstHeight);
		}
	}

	public void draw(SpriteBatch batch, float offsetX, float offsetY) {
		if (!visible) {
			return;
		}
		float tmp = batch.color();
		float alpha = batch.alpha();
		try {
			batch.setAlpha(_alpha);
			batch.setColor(_filterColor);
			float nx = this._location.x + offsetX;
			float ny = this._location.y + offsetY;
			LTexture texture = animation.getSpriteImage();
			float width = dstWidth <= 1 ? texture.getWidth() : dstWidth;
			float height = dstHeight <= 1 ? texture.getHeight() : dstHeight;
			batch.drawFlip(texture, nx, ny, width, height, scaleX, scaleY, getRotation(), flipX, flipY);
			if (_debugDraw) {
				LGame game = LSystem.base();
				if (game != null) {
					GLEx gl = game.display().GL();
					if (gl != null) {
						boolean update = (_rotation != 0) || !(scaleX == 1f && scaleY == 1f);
						if (update) {
							gl.saveTx();
							Affine2f tx = gl.tx();
							final float centerX = this._pivot.x == -1 ? (nx + _origin.ox(width)) : nx + this._pivot.x;
							final float centerY = this._pivot.y == -1 ? (ny + _origin.oy(height)) : ny + this._pivot.y;
							if (_rotation != 0) {
								tx.translate(centerX, centerY);
								tx.preRotate(_rotation);
								tx.translate(-centerX, -centerY);
							}
							if (((scaleX != 1) || (scaleY != 1))) {
								tx.translate(centerX, centerY);
								tx.preScale(scaleX, scaleY);
								tx.translate(-centerX, -centerY);
							}
							boolean useAll = gl.isAlltextures();
							gl.setAlltextures(true);
							gl.drawRect(nx, ny, width, height, _debugDrawColor);
							gl.setAlltextures(useAll);
							gl.restoreTx();
						} else {
							batch.setColor(_debugDrawColor);
							batch.drawRect(nx, ny, width, height);
							batch.setColor(tmp);
						}
					}
				}
			}
		} finally {
			batch.setColor(tmp);
			batch.setAlpha(alpha);
		}
	}

	public void draw(GLEx gl, float offsetX, float offsetY) {
		if (!visible) {
			return;
		}
		float alpha = gl.alpha();
		int blend = gl.getBlendMode();
		boolean update = (_rotation != 0) || !(scaleX == 1f && scaleY == 1f) || flipX || flipY;
		try {
			gl.setBlendMode(_blend);
			gl.setAlpha(_alpha);
			float nx = this._location.x + offsetX;
			float ny = this._location.y + offsetY;
			LTexture texture = animation.getSpriteImage();
			float width = dstWidth <= 1 ? texture.getWidth() : dstWidth;
			float height = dstHeight <= 1 ? texture.getHeight() : dstHeight;
			if (update) {
				gl.saveTx();
				Affine2f tx = gl.tx();
				final float centerX = this._pivot.x == -1 ? (nx + _origin.ox(width)) : nx + this._pivot.x;
				final float centerY = this._pivot.y == -1 ? (ny + _origin.oy(height)) : ny + this._pivot.y;
				if (_rotation != 0) {
					tx.translate(centerX, centerY);
					tx.preRotate(_rotation);
					tx.translate(-centerX, -centerY);
				}
				if (flipX || flipY) {
					if (flipX && flipY) {
						Affine2f.transform(tx, centerX, centerY, Affine2f.TRANS_ROT180);
					} else if (flipX) {
						Affine2f.transform(tx, centerX, centerY, Affine2f.TRANS_MIRROR);
					} else if (flipY) {
						Affine2f.transform(tx, centerX, centerY, Affine2f.TRANS_MIRROR_ROT180);
					}
				}
				if (((scaleX != 1) || (scaleY != 1))) {
					tx.translate(centerX, centerY);
					tx.preScale(scaleX, scaleY);
					tx.translate(-centerX, -centerY);
				}
			}
			gl.draw(texture, nx, ny, width, height, _filterColor);
			if (_debugDraw) {
				boolean useAll = gl.isAlltextures();
				gl.setAlltextures(true);
				gl.drawRect(nx, ny, width, height, _debugDrawColor);
				gl.setAlltextures(useAll);
			}
		} finally {
			gl.setAlpha(alpha);
			gl.setBlendMode(blend);
			if (update) {
				gl.restoreTx();
			}
		}
	}

	public TileMap getTileMap() {
		return tiles;
	}

	@Override
	public Field2D getField2D() {
		return tiles.getField2D();
	}

	public void setFilterColor(LColor f) {
		this._filterColor.setColor(f);
	}

	public LColor getFilterColor() {
		return new LColor(this._filterColor);
	}

	public ActionObject setWidth(float w) {
		if (w != this.dstWidth) {
			this.onResize();
		}
		this.dstWidth = MathUtils.max(1f, w);
		return this;
	}

	public ActionObject setHeight(float h) {
		if (h != this.dstHeight) {
			this.onResize();
		}
		this.dstHeight = MathUtils.max(1f, h);
		return this;
	}

	public ActionObject setSize(float w, float h) {
		if (this.dstWidth != w || this.dstHeight != h) {
			this.dstWidth = MathUtils.max(1f, w);
			this.dstHeight = MathUtils.max(1f, h);
			this.onResize();
		}
		return this;
	}

	public boolean isCollision(ActionObject o) {
		RectBox src = getCollisionArea();
		RectBox dst = o.getCollisionArea();
		if (src.intersects(dst) || src.contains(dst)) {
			return true;
		}
		return false;
	}

	@Override
	public float getWidth() {
		return (int) ((dstWidth > 1 ? (int) dstWidth : animation.getSpriteImage().width()) * scaleX) - fixedWidthOffset;
	}

	@Override
	public float getHeight() {
		return (int) ((dstHeight > 1 ? (int) dstHeight : animation.getSpriteImage().height()) * scaleY)
				- fixedHeightOffset;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation a) {
		this.animation = a;
	}

	public void setIndex(int index) {
		if (animation instanceof AnimationStorage) {
			((AnimationStorage) animation).playIndex(index);
		}
	}

	public boolean isMirror() {
		return flipX;
	}

	public void setMirror(boolean mirror) {
		this.flipX = mirror;
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return getCollisionArea().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionArea();
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void createUI(GLEx g) {
		draw(g, 0, 0);
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		draw(g, offsetX, offsetY);
	}

	@Override
	public RectBox getCollisionBox() {
		return getCollisionArea();
	}

	@Override
	public LTexture getBitmap() {
		return animation.getSpriteImage();
	}

	@Override
	public void setColor(LColor c) {
		setFilterColor(c);
	}

	@Override
	public LColor getColor() {
		return getFilterColor();
	}

	@Override
	public ActionObject setFlipX(boolean x) {
		this.flipX = x;
		return this;
	}

	@Override
	public ActionObject setFlipY(boolean y) {
		this.flipY = y;
		return this;
	}

	@Override
	public ActionObject setFlipXY(boolean x, boolean y) {
		setFlipX(x);
		setFlipY(y);
		return this;
	}

	@Override
	public boolean isFlipX() {
		return flipX;
	}

	@Override
	public boolean isFlipY() {
		return flipY;
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

	@Override
	public void setSprites(Sprites ss) {
		if (this.sprites == ss) {
			return;
		}
		this.sprites = ss;
	}

	@Override
	public Sprites getSprites() {
		return this.sprites;
	}

	@Override
	public Screen getScreen() {
		if (this.sprites == null) {
			return LSystem.getProcess().getScreen();
		}
		return this.sprites.getScreen() == null ? LSystem.getProcess().getScreen() : this.sprites.getScreen();
	}

	public ActionObject setPivotX(float pX) {
		_pivot.setX(pX);
		return this;
	}

	public ActionObject setPivotY(float pY) {
		_pivot.setY(pY);
		return this;
	}

	public float getPivotX() {
		return _pivot.getX();
	}

	public float getPivotY() {
		return _pivot.getY();
	}

	public ActionObject setPivot(float pX, float pY) {
		_pivot.set(pX, pY);
		return this;
	}

	public ActionObject setScale(final float s) {
		this.setScale(s, s);
		return this;
	}

	@Override
	public void setScale(final float sx, final float sy) {
		this.scaleX = sx;
		this.scaleY = sy;
	}

	@Override
	public float getScaleX() {
		return this.scaleX;
	}

	@Override
	public float getScaleY() {
		return this.scaleY;
	}

	public boolean isDebugDraw() {
		return _debugDraw;
	}

	public ISprite setDebugDraw(boolean debugDraw) {
		this._debugDraw = debugDraw;
		return this;
	}

	public LColor getDebugDrawColor() {
		return _debugDrawColor.cpy();
	}

	public ISprite setDebugDrawColor(LColor debugColor) {
		if (debugColor == null) {
			return this;
		}
		this._debugDrawColor = debugColor;
		return this;
	}

	public float getFixedWidthOffset() {
		return fixedWidthOffset;
	}

	public void setFixedWidthOffset(float fixedWidthOffset) {
		this.fixedWidthOffset = fixedWidthOffset;
	}

	public float getFixedHeightOffset() {
		return fixedHeightOffset;
	}

	public void setFixedHeightOffset(float fixedHeightOffset) {
		this.fixedHeightOffset = fixedHeightOffset;
	}

	@Override
	public boolean collides(ISprite e) {
		if (e == null || !e.isVisible()) {
			return false;
		}
		return rectBox.intersects(e.getCollisionBox());
	}

	@Override
	public boolean collidesX(ISprite other) {
		if (other == null || !other.isVisible()) {
			return false;
		}
		RectBox rectSelf = getRectBox();
		RectBox a = new RectBox(rectSelf.getX(), 0, rectSelf.getWidth(), rectSelf.getHeight());
		RectBox rectDst = getRectBox();
		RectBox b = new RectBox(rectDst.getX(), 0, rectDst.getWidth(), rectDst.getHeight());
		return a.intersects(b);
	}

	@Override
	public boolean collidesY(ISprite other) {
		if (other == null || !other.isVisible()) {
			return false;
		}
		RectBox rectSelf = getRectBox();
		RectBox a = new RectBox(0, rectSelf.getY(), rectSelf.getWidth(), rectSelf.getHeight());
		RectBox rectDst = getRectBox();
		RectBox b = new RectBox(0, rectDst.getY(), rectDst.getWidth(), rectDst.getHeight());
		return a.intersects(b);
	}

	public ResizeListener<ActionObject> getResizeListener() {
		return _resizeListener;
	}

	public ActionObject setResizeListener(ResizeListener<ActionObject> listener) {
		this._resizeListener = listener;
		return this;
	}

	@Override
	public void onResize() {
		if (_resizeListener != null) {
			_resizeListener.onResize(this);
		}
	}

	public boolean isClosed() {
		return isDisposed();
	}

	@Override
	public void close() {
		if (animation != null) {
			animation.close();
		}
		setState(State.DISPOSED);
		removeActionEvents(this);
		_resizeListener = null;
	}

}

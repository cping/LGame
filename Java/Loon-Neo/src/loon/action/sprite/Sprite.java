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
package loon.action.sprite;

import loon.Director.Origin;
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTrans;
import loon.action.ActionControl;
import loon.action.Flip;
import loon.canvas.LColor;
import loon.events.EventAction;
import loon.events.ResizeListener;
import loon.geom.Affine2f;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;
import loon.utils.LayerSorter;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.TimeUtils;
import loon.utils.res.MovieSpriteSheet;

/**
 * 一个精灵类的具体实现,仿照J2ME的Sprite同名类实现的精灵类
 *
 */
public class Sprite extends SpriteBase<ISprite> implements Flip<Sprite>, ISprite, LTrans {

	// 默认每帧刷新时间
	private static final long defaultDelay = 150;

	private final static LayerSorter<ISprite> spriteSorter = new LayerSorter<ISprite>(false);

	public final static Sprite load(String path) {
		return new Sprite(path);
	}

	public final static Sprite load(LTexture tex) {
		return new Sprite(tex);
	}

	public final static Sprite load(String path, int row, int col) {
		return new Sprite(path, row, col);
	}

	public final static Sprite load(String path, int row, int col, long delay) {
		return new Sprite(path, row, col, delay);
	}

	private boolean _elastic = false;

	// 动画
	private Animation _animation = new Animation();

	private Vector2f _pivot = new Vector2f();

	private int _transform;

	private LColor _filterColor;

	/**
	 * 默认构造函数
	 * 
	 */
	public Sprite() {
		this(0, 0);
	}

	/**
	 * 以下参数分别为 坐标x,坐标y
	 * 
	 * @param x
	 * @param y
	 */
	public Sprite(float x, float y) {
		this("Sprite" + TimeUtils.millis(), x, y);
	}

	/**
	 * 以下参数分别为 精灵名,坐标x,坐标y
	 * 
	 * @param spriteName
	 * @param x
	 * @param y
	 */
	private Sprite(String spriteName, float x, float y) {
		this.setLocation(x, y);
		this._objectName = spriteName;
		this._visible = true;
		this._transform = LTrans.TRANS_NONE;
	}

	/**
	 * 以下参数分别为 取材文件,每行取材宽度,每列取材长度
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 */
	public Sprite(String fileName, int row, int col) {
		this(fileName, -1, 0, 0, row, col, defaultDelay);
	}

	/**
	 * 以下参数分别为 取材文件,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @param timer
	 */
	public Sprite(String fileName, int row, int col, long timer) {
		this(fileName, -1, 0, 0, row, col, timer);
	}

	/**
	 * 以下参数分别为 取材文件,坐标x,坐标y,每行取材宽度,每列取材长度
	 * 
	 * @param fileName
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 */
	public Sprite(String fileName, float x, float y, int row, int col) {
		this(fileName, x, y, row, col, defaultDelay);
	}

	/**
	 * 以下参数分别为 取材文件,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param fileName
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 * @param timer
	 */
	private Sprite(String fileName, float x, float y, int row, int col, long timer) {
		this(fileName, -1, x, y, row, col, timer);
	}

	/**
	 * 以下参数分别为 取材文件,最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度
	 * 
	 * @param fileName
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 */
	public Sprite(String fileName, int maxFrame, float x, float y, int row, int col) {
		this(fileName, maxFrame, x, y, row, col, defaultDelay);
	}

	/**
	 * 以下参数分别为 取材文件,最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param fileName
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 * @param timer
	 */
	public Sprite(String fileName, int maxFrame, float x, float y, int row, int col, long timer) {
		this("Sprite" + TimeUtils.millis(), fileName, maxFrame, x, y, row, col, timer);
	}

	/**
	 * 以下参数分别为 精灵名，取材文件，最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param spriteName
	 * @param fileName
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 * @param timer
	 */
	public Sprite(String spriteName, String fileName, int maxFrame, float x, float y, int row, int col, long timer) {
		this(spriteName, TextureUtils.getSplitTextures(fileName, row, col), maxFrame, x, y, timer);
	}

	/**
	 * 注入指定图片
	 * 
	 * @param fileName
	 */
	public Sprite(String fileName) {
		this(LSystem.loadTexture(fileName));
	}

	/**
	 * 注入指定图片
	 * 
	 * @param images
	 */
	public Sprite(final LTexture img) {
		this(new LTexture[] { img }, 0, 0);
	}

	/**
	 * 以下参数分别为 图像数组
	 * 
	 * @param images
	 */
	public Sprite(LTexture[] images) {
		this(images, 0, 0);
	}

	/**
	 * 以下参数分别为 图像数组,坐标x,坐标y
	 * 
	 * @param images
	 * @param x
	 * @param y
	 */
	public Sprite(LTexture[] images, float x, float y) {
		this(images, x, y, defaultDelay);
	}

	/**
	 * 以下参数分别为 图像数组,平均每桢显示时间
	 * 
	 * @param images
	 * @param timer
	 */
	public Sprite(LTexture[] images, long timer) {
		this(images, -1, 0, 0, defaultDelay);
	}

	/**
	 * 以下参数分别为 图像数组,坐标x,坐标y,平均每桢显示时间
	 * 
	 * @param images
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(LTexture[] images, float x, float y, long timer) {
		this(images, -1, x, y, timer);
	}

	/**
	 * 以下参数分别为 图像数组,最大分解桢数,坐标x,坐标y,平均每桢显示时间
	 * 
	 * @param spriteName
	 * @param images
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(LTexture[] images, int maxFrame, float x, float y, long timer) {
		this("Sprite" + TimeUtils.millis(), images, maxFrame, x, y, timer);
	}

	/**
	 * 以下参数分别为 精灵图，坐标x,坐标y
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(LTexture image, float x, float y) {
		this(new LTexture[] { image }, -1, x, y, 0);
	}

	/**
	 * 以下参数分别为 精灵名，图像数组，最大分解桢数,坐标x,坐标y,平均每桢显示时间
	 * 
	 * @param spriteName
	 * @param images
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(String spriteName, LTexture[] images, int maxFrame, float x, float y, long timer) {
		this.setLocation(x, y);
		this.setAnimation(_animation, images, maxFrame, timer);
		this._objectName = spriteName;
		this._visible = true;
		this._transform = LTrans.TRANS_NONE;
	}

	/**
	 * 以资源SpriteSheet构建精灵
	 * 
	 * @param sheet
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(MovieSpriteSheet sheet, float x, float y, long timer) {
		this("Sprite" + TimeUtils.millis(), sheet, x, y, timer);
	}

	/**
	 * 以资源SpriteSheet构建精灵
	 * 
	 * @param spriteName
	 * @param sheet
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(String spriteName, MovieSpriteSheet sheet, float x, float y, long timer) {
		this.setLocation(x, y);
		this._objectName = spriteName;
		LTexture[] texs = sheet.getTextures();
		this.setAnimation(_animation, texs, texs.length, timer);
		this._visible = true;
		this._transform = LTrans.TRANS_NONE;
	}

	/**
	 * 是否在播放动画
	 * 
	 * @param running
	 */
	public Sprite setRunning(boolean running) {
		_animation.setRunning(running);
		return this;
	}

	/**
	 * 返回当前总桢数
	 * 
	 * @return
	 */
	public int getTotalFrames() {
		return _animation.getTotalFrames();
	}

	/**
	 * 设定当前帧
	 * 
	 * @param index
	 */
	public Sprite setCurrentFrameIndex(int index) {
		_animation.setCurrentFrameIndex(index);
		return this;
	}

	/**
	 * 返回当前桢索引
	 * 
	 * @return
	 */
	public int getCurrentFrameIndex() {
		return _animation.getCurrentFrameIndex();
	}

	/**
	 * 插入指定动画
	 * 
	 * @param myAnimation
	 * @param images
	 * @param maxFrame
	 * @param timer
	 */
	private void setAnimation(Animation myAnimation, LTexture[] images, int max, long timer) {
		myAnimation.addFrame(images, max, timer);
	}

	/**
	 * 插入指定动画
	 * 
	 * @param fileName
	 * @param maxFrame
	 * @param row
	 * @param col
	 * @param timer
	 */
	public Sprite setAnimation(String fileName, int maxFrame, int row, int col, long timer) {
		setAnimation(new Animation(), TextureUtils.getSplitTextures(fileName, row, col), maxFrame, timer);
		return this;
	}

	/**
	 * 插入指定动画
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @param timer
	 */
	public Sprite setAnimation(String fileName, int row, int col, long timer) {
		setAnimation(fileName, -1, row, col, timer);
		return this;
	}

	/**
	 * 插入指定动画
	 * 
	 * @param images
	 * @param maxFrame
	 * @param timer
	 */
	public Sprite setAnimation(LTexture[] images, int maxFrame, long timer) {
		setAnimation(new Animation(), images, maxFrame, timer);
		return this;
	}

	/**
	 * 插入指定动画
	 * 
	 * @param images
	 * @param timer
	 */
	public Sprite setAnimation(LTexture[] images, long timer) {
		setAnimation(new Animation(), images, -1, timer);
		return this;
	}

	/**
	 * 插入指定动画
	 * 
	 * @param _animation
	 */
	public Sprite setAnimation(Animation a) {
		this._animation = a;
		return this;
	}

	public Animation getAnimation() {
		return _animation;
	}

	public Sprite pause() {
		if (!this._ignoreUpdate) {
			ActionControl.get().paused(this._ignoreUpdate = true, this);
			if (_animation != null) {
				_animation.pause();
			}
		}
		return this;
	}

	public Sprite resume() {
		if (this._ignoreUpdate) {
			ActionControl.get().paused(this._ignoreUpdate = false, this);
			if (_animation != null) {
				_animation.resume();
			}
		}
		return this;
	}

	/**
	 * 变更动画
	 */
	@Override
	public void update(long elapsedTime) {
		if (!this._ignoreUpdate) {
			_animation.update(elapsedTime);
			onBaseUpdate(elapsedTime);
		}
	}

	/**
	 * 变更定位器坐标
	 * 
	 * @param vector
	 */
	public Sprite updateLocation(Vector2f vector) {
		this.setX(MathUtils.round(vector.getX()));
		this.setY(MathUtils.round(vector.getY()));
		return this;
	}

	public LTexture getImage() {
		return _animation.getSpriteImage();
	}

	@Override
	public float getAniWidth() {
		LTexture si = _animation.getSpriteImage();
		if (si == null) {
			return -1;
		}
		return si.width();
	}

	@Override
	public float getAniHeight() {
		LTexture si = _animation.getSpriteImage();
		if (si == null) {
			return -1;
		}
		return si.height();
	}

	@Override
	public float getWidth() {
		return (getAniWidth() * _scaleX) - _fixedWidthOffset;
	}

	@Override
	public float getHeight() {
		return (getAniHeight() * _scaleY) - _fixedHeightOffset;
	}

	@Override
	public void createUI(GLEx g) {
		createUI(g, 0f, 0f);
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (!_visible || _objectAlpha < 0.01f) {
			return;
		}
		if (_animation != null) {
			_image = _animation.getSpriteImage();
		}
		final boolean notImg = _image == null;
		if (_animation != null && _animation.length > 0 && notImg) {
			return;
		}

		final float width = notImg ? getContainerWidth() : _image.getWidth();
		final float height = notImg ? getContainerHeight() : _image.getHeight();

		final boolean update = (_objectRotation != 0) || !(_scaleX == 1f && _scaleY == 1f) || _flipX || _flipY;
		final int tmp = g.color();
		final int blend = g.getBlendMode();
		try {
			g.setBlendMode(_GL_BLEND);
			final float nx = drawX(offsetX);
			final float ny = drawY(offsetY);
			if (_elastic) {
				g.setClip(nx, ny, width, height);
			}
			if (update) {
				g.saveTx();
				Affine2f tx = g.tx();
				final float centerX = _scaleCenterX == -1 ? (nx + _origin.ox(width)) : nx + _scaleCenterX;
				final float centerY = _scaleCenterY == -1 ? (ny + _origin.oy(height)) : ny + _scaleCenterY;
				if (_objectRotation != 0 && notImg) {
					tx.translate(centerX, centerY);
					tx.preRotate(_objectRotation);
					tx.translate(-centerX, -centerY);
				}
				if (_flipX || _flipY) {
					if (_flipX && _flipY) {
						Affine2f.transform(tx, centerX, centerY, Affine2f.TRANS_ROT180);
					} else if (_flipX) {
						Affine2f.transform(tx, centerX, centerY, Affine2f.TRANS_MIRROR);
					} else if (_flipY) {
						Affine2f.transform(tx, centerX, centerY, Affine2f.TRANS_MIRROR_ROT180);
					}
				}
				if (((_scaleX != 1) || (_scaleY != 1)) && notImg) {
					tx.translate(centerX, centerY);
					tx.preScale(_scaleX, _scaleY);
					tx.translate(-centerX, -centerY);
				}
			}
			g.setAlpha(_objectAlpha);
			if (!notImg) {
				if (LTrans.TRANS_NONE == _transform) {
					g.draw(_image, nx, ny, width, height, _filterColor, _objectRotation, _scaleCenterX, _scaleCenterY,
							_scaleX, _scaleY);
				} else {
					_pivot.set(_scaleCenterX, _scaleCenterY);
					g.drawRegion(_image, 0, 0, MathUtils.ifloor(width), MathUtils.ifloor(height), _transform,
							MathUtils.ifloor(nx), MathUtils.ifloor(ny), LTrans.TOP | LTrans.LEFT, _filterColor, _pivot,
							_scaleX, _scaleY, _objectRotation);
				}
			}
			if (_childrenVisible && _childrens != null && _childrens.size > 0) {
				final TArray<ISprite> childs = this._childrens;
				final int count = childs.size;
				for (int i = 0; i < count; i++) {
					ISprite spr = childs.get(i);
					if (spr != null) {
						float px = 0, py = 0;
						ISprite parent = spr.getParent();
						if (parent == null) {
							parent = this;
						}
						px += parent.getX() + parent.getOffsetX();
						py += parent.getY() + parent.getOffsetY();
						for (; (parent = parent.getParent()) != null;) {
							px += parent.getX() + parent.getOffsetX();
							py += parent.getY() + parent.getOffsetY();
						}
						spr.createUI(g, px + offsetX + spr.getOffsetX(), py + offsetY + spr.getOffsetY());
					}
				}
			}
			if (_debugDraw) {
				g.drawRect(nx, ny, width, height, _debugDrawColor);
			}
		} finally {
			g.setColor(tmp);
			if (update) {
				g.restoreTx();
			}
			if (_elastic) {
				g.clearClip();
			}
			g.setBlendMode(blend);
		}
	}

	public boolean isClip() {
		return this._elastic;
	}

	public boolean isElastic() {
		return this._elastic;
	}

	public Sprite clip(boolean e) {
		return setElastic(true);
	}

	public Sprite setElastic(boolean e) {
		this._elastic = e;
		return this;
	}

	public ISprite setComponentIgnoreUpdate(boolean c) {
		this._componentsIgnoreUpdate = c;
		return this;
	}

	public ISprite with(TComponent<ISprite> c) {
		return addComponent(c);
	}

	public ISprite removeComponents() {
		clearComponentAll();
		return this;
	}

	public Sprite setChildrenVisible(final boolean v) {
		this._childrenVisible = v;
		return this;
	}

	public String getSpriteName() {
		return _objectName;
	}

	public Sprite setSpriteName(String spriteName) {
		this._objectName = spriteName;
		return this;
	}

	public int getTransform() {
		return _transform;
	}

	public Sprite setTransform(int t) {
		this._transform = t;
		return this;
	}

	public LColor getFilterColor() {
		return new LColor(_filterColor);
	}

	public Sprite setFilterColor(LColor f) {
		this._filterColor = f;
		return this;
	}

	public Sprite setScaleX(float scaleX) {
		this._scaleX = scaleX;
		return this;
	}

	public Sprite setScaleY(float scaleY) {
		this._scaleY = scaleY;
		return this;
	}

	public Sprite setPivotX(float pX) {
		setScaleCenterX(pX);
		return this;
	}

	public Sprite setPivotY(float pY) {
		setScaleCenterY(pY);
		return this;
	}

	public float getPivotX() {
		return getScaleCenterX();
	}

	public float getPivotY() {
		return getScaleCenterY();
	}

	public Sprite setPivot(float pX, float pY) {
		setScaleCenter(pX, pY);
		return this;
	}

	public Sprite coord(float x, float y) {
		setLocation(x, y);
		return this;
	}

	public Sprite setScale(float s) {
		this.setScale(s, s);
		return this;
	}

	public Sprite setSize(float size) {
		return setSize(size, size);
	}

	@Override
	public Sprite setSize(float width, float height) {
		this._scaleX = getWidth() / width;
		this._scaleY = getHeight() / height;
		return this;
	}

	public int getMaxFrame() {
		return _animation.getMaxFrame();
	}

	public Sprite setMaxFrame(int maxFrame) {
		this._animation.setMaxFrame(maxFrame);
		return this;
	}

	public boolean isDescendantOf(ISprite actor) {
		if (actor == null) {
			throw new LSysException("Actor cannot be null");
		}
		ISprite parent = this;
		for (;;) {
			if (parent == null) {
				return false;
			}
			if (parent == actor) {
				return true;
			}
			parent = parent.getParent();
		}
	}

	public boolean isAscendantOf(ISprite actor) {
		if (actor == null) {
			throw new LSysException("Actor cannot be null");
		}
		for (;;) {
			if (actor == null) {
				return false;
			}
			if (actor == this) {
				return true;
			}
			actor = actor.getParent();
		}
	}

	@Override
	public void setWidth(float w) {
		this._scaleX = (w / getWidth());
	}

	@Override
	public void setHeight(float h) {
		this._scaleY = (h / getHeight());
	}

	public Sprite addChildAt(ISprite spr, float x, float y) {
		if (spr != null) {
			spr.setLocation(x, y);
			addChild(spr);
		}
		return this;
	}

	public Sprite addChild(ISprite spr) {
		if (spr == null) {
			return this;
		}
		if (spr == this) {
			return this;
		}
		if (_childrens == null) {
			allocateChildren();
		}
		spr.setParent(this);
		spr.setSprites(this._sprites);
		spr.setState(State.ADDED);
		_childrens.add(spr);
		spriteSorter.sort(_childrens);
		return this;
	}

	public Sprite removeChildren() {
		super.removeChilds();
		return this;
	}

	@Override
	public void sort() {
		if (_childrens == null) {
			return;
		}
		spriteSorter.sort(_childrens);
	}

	public Sprite loop(EventAction la) {
		this._loopAction = la;
		return this;
	}

	@Override
	public void setRotation(float rotate) {
		super.setRotation(rotate);
		if (_childrens != null) {
			for (int i = _childrens.size - 1; i > -1; i--) {
				ISprite sprite = _childrens.get(i);
				if (sprite != null) {
					sprite.setRotation(rotate);
				}
			}
		}
	}

	@Override
	public void setColor(LColor color) {
		setFilterColor(color);
	}

	@Override
	public LColor getColor() {
		return getFilterColor();
	}

	@Override
	public void setLayer(int z) {
		if (_sprites != null && getLayer() != z) {
			_sprites.setDirtyChildren(true);
		}
		super.setLayer(z);
	}

	public Sprite setOrigin(Origin o) {
		this._origin = o;
		return this;
	}

	@Override
	public Sprite setFlipX(boolean x) {
		this._flipX = x;
		return this;
	}

	@Override
	public Sprite setFlipY(boolean y) {
		this._flipY = y;
		return this;
	}

	@Override
	public Sprite setFlipXY(boolean x, boolean y) {
		setFlipX(x);
		setFlipY(y);
		return this;
	}

	@Override
	public ISprite setSprites(Sprites ss) {
		setSpritesObject(ss);
		return this;
	}

	public ISprite setDebugDraw(boolean debugDraw) {
		this._debugDraw = debugDraw;
		return this;
	}

	public ISprite setDebugDrawColor(LColor debugColor) {
		if (debugColor == null) {
			return this;
		}
		this._debugDrawColor = debugColor;
		return this;
	}

	@Override
	public Sprite triggerCollision(SpriteCollisionListener sc) {
		this._collSpriteListener = sc;
		return this;
	}

	@Override
	public void onCollision(ISprite coll, int dir) {
		if (_collSpriteListener != null) {
			_collSpriteListener.onCollideUpdate(coll, dir);
		}
	}

	@Override
	public float getFixedWidthOffset() {
		return _fixedWidthOffset;
	}

	@Override
	public Sprite setFixedWidthOffset(float fixedWidthOffset) {
		this._fixedWidthOffset = fixedWidthOffset;
		return this;
	}

	@Override
	public float getFixedHeightOffset() {
		return _fixedHeightOffset;
	}

	@Override
	public Sprite setFixedHeightOffset(float fixedHeightOffset) {
		this._fixedHeightOffset = fixedHeightOffset;
		return this;
	}

	public Sprite softCenterOn(float x, float y) {
		final RectBox rect = getSprites() == null ? LSystem.viewSize.getRect() : getSprites().getBoundingBox();
		final ISprite sprite = this.getSuper();
		if (x != 0) {
			float dx = (x - rect.getWidth() / 2 / this.getScaleX() - this.getX()) / 3;
			if (sprite != null) {
				RectBox boundingBox = sprite.getRectBox();
				if (this.getX() + dx < boundingBox.getMinX()) {
					setX(boundingBox.getMinX() / this.getScaleX());
				} else if (this.getX() + dx > (boundingBox.getMaxX() - rect.getWidth()) / this.getScaleX()) {
					setX(MathUtils.max(boundingBox.getMaxX() - rect.getWidth(), boundingBox.getMinX())
							/ this.getScaleX());
				} else {
					this.setX(this.getX() + dx);
				}
			} else {
				this.setX(this.getX() + dx);
			}
		}
		if (y != 0) {
			float dy = (y - rect.getHeight() / 2 / this.getScaleY() - this.getY()) / 3;
			if (sprite != null) {
				RectBox boundingBox = sprite.getRectBox();
				if (this.getY() + dy < boundingBox.getMinY()) {
					this.setY(boundingBox.getMinY() / this.getScaleY());
				} else if (this.getY() + dy > (boundingBox.getMaxY() - rect.getHeight()) / this.getScaleY()) {
					this.setY(MathUtils.max(boundingBox.getMaxY() - rect.getHeight(), boundingBox.getMinY())
							/ this.getScaleY());
				} else {
					this.setY(this.getY() + dy);
				}
			} else {
				this.setY(this.getY() + dy);
			}
		}
		return this;
	}

	@Override
	public ISprite setOffset(Vector2f v) {
		if (v != null) {
			this._offset = v;
		}
		return this;
	}

	public Sprite setOffsetX(float sx) {
		this._offset.setX(sx);
		return this;
	}

	public Sprite setOffsetY(float sy) {
		this._offset.setY(sy);
		return this;
	}

	public Sprite createShadow(boolean s) {
		this._createShadow = s;
		return this;
	}

	public ISprite setChildrenIgnoreUpdate(final boolean c) {
		this._childrenIgnoreUpdate = c;
		return this;
	}

	public Sprite show() {
		setVisible(true);
		return this;
	}

	public Sprite hide() {
		setVisible(false);
		return this;
	}

	public boolean toggleVisible() {
		if (_visible) {
			hide();
		} else {
			show();
		}
		return _visible;
	}

	public ResizeListener<ISprite> getResizeListener() {
		return _resizeListener;
	}

	public Sprite setResizeListener(ResizeListener<ISprite> listener) {
		this._resizeListener = listener;
		return this;
	}

	public Sprite setAutoXYSort(boolean a) {
		this._xySort = a;
		return this;
	}

	@Override
	public ISprite buildToScreen() {
		if (_sprites != null) {
			_sprites.add(this);
			return this;
		}
		getScreen().add(this);
		return this;
	}

	@Override
	public ISprite removeFromScreen() {
		if (_sprites != null) {
			_sprites.remove(this);
			return this;
		}
		getScreen().remove(this);
		return this;
	}

	public SpriteEntity toEntity() {
		return new SpriteEntity(this);
	}

	public Sprite dispose(LRelease r) {
		_disposed = r;
		return this;
	}

	@Override
	protected void _onDestroy() {
		closeBase();
		if (_image != null) {
			_image.close();
		}
		if (_animation != null) {
			_animation.close();
		}
	}

}

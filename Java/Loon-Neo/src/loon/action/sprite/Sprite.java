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

import loon.LObject;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTrans;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.collision.CollisionHelper;
import loon.action.collision.CollisionObject;
import loon.action.collision.Gravity;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.events.ResizeListener;
import loon.geom.Affine2f;
import loon.geom.BoxSize;
import loon.geom.Point;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;
import loon.utils.Flip;
import loon.utils.IArray;
import loon.utils.LayerSorter;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.TimeUtils;
import loon.utils.res.MovieSpriteSheet;

/**
 * 一个精灵类的具体实现,仿照J2ME的Sprite同名类实现的精灵类
 *
 */
public class Sprite extends LObject<ISprite>
		implements Flip<Sprite>, CollisionObject, ISprite, IArray, LTrans, BoxSize {

	private final static LayerSorter<ISprite> childSorter = new LayerSorter<ISprite>(false);

	private Origin _origin = Origin.CENTER;

	private TArray<ISprite> _childrens = null;

	// 默认每帧刷新时间
	private static final long defaultDelay = 150;

	// 是否可见
	private boolean _visible = true;

	// 精灵图片
	private LTexture _image;

	// 动画
	private Animation _animation = new Animation();

	private LColor _debugDrawColor = LColor.red;

	private ResizeListener<Sprite> _resizeListener;

	private int _transform;

	private LColor _filterColor;

	private float _scaleX = 1f, _scaleY = 1f;

	private float _fixedWidthOffset = 0f, _fixedHeightOffset = 0f;

	private Vector2f _offset = new Vector2f();

	private boolean _spritesVisible = true;

	private boolean _flipX = false, _flipY = false;

	private boolean _debugDraw = false;

	private Vector2f _pivot = new Vector2f(-1, -1);

	private Sprites _sprites = null;

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
	 * @param _image
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(LTexture _image, float x, float y) {
		this(new LTexture[] { _image }, -1, x, y, 0);
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
		this._objectName = spriteName;
		this.setAnimation(_animation, images, maxFrame, timer);
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
	 * 获得当前精灵的窗体居中横坐标
	 * 
	 * @param x
	 * @return
	 */
	public int centerX(int x) {
		return centerX(this, x);
	}

	/**
	 * 获得指定精灵的窗体居中横坐标
	 * 
	 * @param sprite
	 * @param x
	 * @return
	 */
	public static int centerX(Sprite sprite, int x) {
		int newX = (int) (x - (sprite.getWidth() / 2));
		if (newX + sprite.getWidth() >= LSystem.viewSize.getWidth()) {
			return (int) (LSystem.viewSize.getWidth() - sprite.getWidth() - 1);
		}
		if (newX < 0) {
			return x;
		} else {
			return newX;
		}
	}

	/**
	 * 获得当前精灵的窗体居中纵坐标
	 * 
	 * @param y
	 * @return
	 */
	public int centerY(int y) {
		return centerY(this, y);
	}

	/**
	 * 获得指定精灵的窗体居中纵坐标
	 * 
	 * @param sprite
	 * @param y
	 * @return
	 */
	public static int centerY(Sprite sprite, int y) {
		int newY = (int) (y - (sprite.getHeight() / 2));
		if (newY + sprite.getHeight() >= LSystem.viewSize.getHeight()) {
			return (int) (LSystem.viewSize.getHeight() - sprite.getHeight() - 1);
		}
		if (newY < 0) {
			return y;
		} else {
			return newY;
		}
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
	public Sprite setAnimation(Animation _animation) {
		this._animation = _animation;
		return this;
	}

	public Animation getAnimation() {
		return _animation;
	}

	protected void onUpdate(long elapsedTime) {
	}

	/**
	 * 变更动画
	 */
	public void update(long elapsedTime) {
		if (_visible) {
			_animation.update(elapsedTime);
			onUpdate(elapsedTime);
			if (_childrens != null && _childrens.size > 0) {
				for (ISprite spr : _childrens) {
					if (spr != null) {
						spr.update(elapsedTime);
					}
				}
			}
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

	public float getAniWidth() {
		LTexture si = _animation.getSpriteImage();
		if (si == null) {
			return -1;
		}
		return si.width();
	}

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

	/**
	 * 获得精灵的中间位置
	 * 
	 * @return
	 */
	public Point getMiddlePoint() {
		return new Point(x() + getWidth() / 2, y() + getHeight() / 2);
	}

	/**
	 * 获得两个精灵的中间距离
	 * 
	 * @param second
	 * @return
	 */
	public float getDistance(Sprite second) {
		return (float) this.getMiddlePoint().distanceTo(second.getMiddlePoint());
	}

	/**
	 * 返回碰撞盒
	 * 
	 * @return
	 */
	public RectBox getCollisionBox() {
		return getRect(x(), y(), getWidth(), getHeight());
	}

	/**
	 * 检查是否与指定精灵位置发生了矩形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isRectToRect(Sprite sprite) {
		return CollisionHelper.isRectToRect(this.getCollisionBox(), sprite.getCollisionBox());
	}

	/**
	 * 检查是否与指定精灵位置发生了圆形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isCircToCirc(Sprite sprite) {
		return CollisionHelper.isCircToCirc(this.getCollisionBox(), sprite.getCollisionBox());
	}

	/**
	 * 检查是否与指定精灵位置发生了方形与圆形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isRectToCirc(Sprite sprite) {
		return CollisionHelper.isRectToCirc(this.getCollisionBox(), sprite.getCollisionBox());
	}

	@Override
	public void createUI(GLEx g) {
		createUI(g, 0f, 0f);
	}
	
	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (!_visible) {
			return;
		}
		if (_objectAlpha < 0.01) {
			return;
		}

		_image = _animation.getSpriteImage();

		final boolean notImg = _image == null;

		if (_animation != null && _animation.length > 0 && notImg) {
			return;
		}

		float width = notImg ? getContainerWidth() : _image.getWidth();
		float height = notImg ? getContainerHeight() : _image.getHeight();

		boolean update = (_objectRotation != 0) || !(_scaleX == 1f && _scaleY == 1f) || _flipX || _flipY;
		int tmp = g.color();
		int blend = g.getBlendMode();
		try {
			g.setBlendMode(_GL_BLEND);
			float nx = this._objectLocation.x + offsetX + _offset.x;
			float ny = this._objectLocation.y + offsetY + _offset.y;
			if (update) {
				g.saveTx();
				Affine2f tx = g.tx();
				final float centerX = this._pivot.x == -1 ? (nx + _origin.ox(width)) : nx + this._pivot.x;
				final float centerY = this._pivot.y == -1 ? (ny + _origin.oy(height)) : ny + this._pivot.y;
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
					g.draw(_image, nx, ny, width, height, _filterColor, _objectRotation, _pivot, _scaleX, _scaleY);
				} else {
					g.drawRegion(_image, 0, 0, (int) width, (int) height, _transform, (int) nx, (int) ny,
							LTrans.TOP | LTrans.LEFT, _filterColor, _pivot, _scaleX, _scaleY, _objectRotation);
				}
			}
			if (_spritesVisible && _childrens != null && _childrens.size > 0) {
				for (ISprite spr : _childrens) {
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
				boolean useAll = g.isAlltextures();
				g.setAlltextures(true);
				g.drawRect(nx, ny, width, height, _debugDrawColor);
				g.setAlltextures(useAll);
			}
		} finally {
			g.setColor(tmp);
			if (update) {
				g.restoreTx();
			}
			g.setBlendMode(blend);
		}
	}

	public float getScreenX() {
		float x = 0;
		ISprite parent = _objectSuper;
		if (parent != null) {
			x += parent.getX();
			for (; (parent = parent.getParent()) != null;) {
				x += parent.getX();
			}
		}
		return x + getX();
	}

	public float getScreenY() {
		float y = 0;
		ISprite parent = _objectSuper;
		if (parent != null) {
			y += parent.getY();
			for (; (parent = parent.getParent()) != null;) {
				y += parent.getY();
			}
		}
		return y + getY();
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	@Override
	public void setVisible(boolean v) {
		this._visible = v;
	}

	public boolean isChildrenVisible() {
		return this._spritesVisible;
	}

	public Sprite setChildrenVisible(final boolean v) {
		this._spritesVisible = v;
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

	@Override
	public LTexture getBitmap() {
		return this._image;
	}

	@Override
	public float getScaleX() {
		return _scaleX;
	}

	public Sprite setScaleX(float scaleX) {
		this._scaleX = scaleX;
		return this;
	}

	public float getScaleY() {
		return _scaleY;
	}

	public Sprite setScaleY(float scaleY) {
		this._scaleY = scaleY;
		return this;
	}

	public Sprite setPivotX(float pX) {
		_pivot.setX(pX);
		return this;
	}

	public Sprite setPivotY(float pY) {
		_pivot.setY(pY);
		return this;
	}

	public float getPivotX() {
		return _pivot.getX();
	}

	public float getPivotY() {
		return _pivot.getY();
	}

	public Sprite setPivot(float pX, float pY) {
		_pivot.set(pX, pY);
		return this;
	}

	@Override
	public Field2D getField2D() {
		return null;
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return _childrens != null && _childrens.size > 0;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return getCollisionBox().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionBox();
	}

	public Sprite setScale(float s) {
		this.setScale(s, s);
		return this;
	}

	@Override
	public void setScale(float sx, float sy) {
		this._scaleX = sx;
		this._scaleY = sy;
	}

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
			_childrens = new TArray<ISprite>();
		}
		spr.setParent(this);
		spr.setSprites(this._sprites);
		spr.setState(State.ADDED);
		_childrens.add(spr);
		childSorter.sort(_childrens);
		return this;
	}

	public boolean removeChild(ISprite spr) {
		if (spr == null) {
			return true;
		}
		if (_childrens == null) {
			_childrens = new TArray<ISprite>();
		}
		boolean removed = _childrens.remove(spr);
		if (removed) {
			spr.setState(State.REMOVED);
		}
		// 删除精灵同时，删除缓动动画
		if (removed && spr instanceof ActionBind) {
			removeActionEvents((ActionBind) spr);
		}
		return removed;
	}

	public boolean removeChild(int idx) {
		if (idx < 0) {
			return true;
		}
		if (_childrens == null) {
			_childrens = new TArray<ISprite>();
		}
		for (int i = this._childrens.size - 1; i >= 0; i--) {
			if (i == idx) {
				final ISprite removed = this._childrens.removeIndex(i);
				final boolean exist = (removed == null);
				if (exist) {
					removed.setState(State.REMOVED);
				}
				// 删除精灵同时，删除缓动动画
				if (exist && (removed instanceof ActionBind)) {
					removeActionEvents((ActionBind) removed);
				}
				return exist;
			}
		}
		return false;
	}

	public Sprite removeChilds() {
		if (this._childrens == null) {
			return this;
		}
		for (int i = this._childrens.size - 1; i >= 0; i--) {
			final ISprite removed = this._childrens.get(i);
			boolean exist = (removed == null);
			if (exist) {
				removed.setState(State.REMOVED);
			}
			// 删除精灵同时，删除缓动动画
			if (exist && removed instanceof ActionBind) {
				removeActionEvents((ActionBind) removed);
			}
		}
		this._childrens.clear();
		return this;
	}

	public int getChildCount() {
		return size();
	}

	public ISprite getChildByIndex(int idx) {
		if (_childrens != null && idx >= 0 && idx < size()) {
			return _childrens.get(idx);
		}
		return null;
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

	public Origin getOrigin() {
		return _origin;
	}

	public Sprite setOrigin(Origin o) {
		this._origin = o;
		return this;
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
	public boolean isFlipX() {
		return _flipX;
	}

	@Override
	public boolean isFlipY() {
		return _flipY;
	}

	@Override
	public int size() {
		return (_childrens == null ? 0 : _childrens.size);
	}

	@Override
	public void clear() {
		if (_childrens != null) {
			removeChilds();
		}
	}

	@Override
	public float getContainerX() {
		if (_objectSuper != null) {
			return getScreenX() - getX();
		}
		return this._sprites == null ? super.getContainerX() : this._sprites.getX();
	}

	@Override
	public float getContainerY() {
		if (_objectSuper != null) {
			return getScreenY() - getY();
		}
		return this._sprites == null ? super.getContainerY() : this._sprites.getY();
	}

	@Override
	public float getContainerWidth() {
		return this._sprites == null ? super.getContainerWidth() : this._sprites.getWidth();
	}

	@Override
	public float getContainerHeight() {
		return this._sprites == null ? super.getContainerHeight() : this._sprites.getHeight();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ISprite setSprites(Sprites ss) {
		if (this._sprites == ss) {
			return this;
		}
		this._sprites = ss;
		return this;
	}

	@Override
	public Sprites getSprites() {
		return this._sprites;
	}

	@Override
	public Screen getScreen() {
		if (this._sprites == null) {
			return LSystem.getProcess().getScreen();
		}
		return this._sprites.getScreen() == null ? LSystem.getProcess().getScreen() : this._sprites.getScreen();
	}

	@Override
	public RectBox getBoundingRect() {
		return getCollisionBox();
	}

	@Override
	public boolean containsPoint(float x, float y) {
		return inContains(x, y, 1, 1);
	}

	@Override
	public boolean intersects(CollisionObject o) {
		return getCollisionBox().intersects(o.getRectBox());
	}

	@Override
	public boolean intersects(RectBox rect) {
		return getCollisionBox().intersects(rect);
	}

	public Gravity getGravity() {
		return new Gravity("Sprite", this);
	}

	private float toPixelScaleX(float x) {
		return MathUtils.iceil(x / _scaleX);
	}

	private float toPixelScaleY(float y) {
		return MathUtils.iceil(y / _scaleY);
	}

	public Vector2f getUITouch(float x, float y) {
		return getUITouch(x, y, null);
	}

	public Vector2f getUITouch(float x, float y, Vector2f pointResult) {
		if (!(x == -1 && y == -1 && pointResult != null)) {
			if (pointResult == null) {
				pointResult = new Vector2f(x, y);
			} else {
				pointResult.set(x, y);
			}
		}
		float newX = 0f;
		float newY = 0f;
		ISprite parent = getParent();
		if (parent != null) {
			newX = pointResult.x - parent.getX() - getX();
			newY = pointResult.y - parent.getX() - getY();
		} else {
			newX = pointResult.x - getX();
			newY = pointResult.y - getY();
		}
		final float angle = getRotation();
		if (angle == 0 || angle == 360) {
			pointResult.x = toPixelScaleX(newX);
			pointResult.y = toPixelScaleY(newY);
			return pointResult;
		}
		float oldWidth = getAniWidth();
		float oldHeight = getAniHeight();
		float newWidth = getWidth();
		float newHeight = getHeight();
		float offX = oldWidth / 2f - newWidth / 2f;
		float offY = oldHeight / 2f - newHeight / 2f;
		float posX = (newX - offX);
		float posY = (newY - offY);
		if (angle == 90) {
			offX = oldHeight / 2f - newWidth / 2f;
			offY = oldWidth / 2f - newHeight / 2f;
			posX = (newX - offY);
			posY = (newY - offX);
			pointResult.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(90);
			pointResult.set(-pointResult.x, MathUtils.abs(pointResult.y - this.getAniHeight()));
		} else if (angle == -90) {
			offX = oldHeight / 2f - newWidth / 2f;
			offY = oldWidth / 2f - newHeight / 2f;
			posX = (newX - offY);
			posY = (newY - offX);
			pointResult.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(-90);
			pointResult.set(-(pointResult.x - this.getAniWidth()), MathUtils.abs(pointResult.y));
		} else if (angle == -180 || angle == 180) {
			pointResult.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(getRotation())
					.addSelf(this.getAniWidth(), this.getAniHeight());
		} else {
			float rad = MathUtils.toRadians(angle);
			float sin = MathUtils.sin(rad);
			float cos = MathUtils.cos(rad);
			float dx = offX / getScaleX();
			float dy = offY / getScaleY();
			float dx2 = cos * dx - sin * dy;
			float dy2 = sin * dx + cos * dy;
			pointResult.x = getAniWidth() - (newX - dx2);
			pointResult.y = getAniHeight() - (newY - dy2);
		}
		return pointResult;
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

	@Override
	public boolean collides(ISprite e) {
		if (e == null || !e.isVisible()) {
			return false;
		}
		return intersects(e.getCollisionBox());
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

	@Override
	public float getCenterX() {
		return getX() + getWidth() / 2f;
	}

	@Override
	public float getCenterY() {
		return getY() + getHeight() / 2f;
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

	@Override
	public float getOffsetX() {
		return _offset.x;
	}

	@Override
	public float getOffsetY() {
		return _offset.y;
	}

	public ResizeListener<Sprite> getResizeListener() {
		return _resizeListener;
	}

	public Sprite setResizeListener(ResizeListener<Sprite> listener) {
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
		this._visible = false;
		if (_image != null) {
			_image.close();
		}
		if (_animation != null) {
			_animation.close();
		}
		setState(State.DISPOSED);
		removeChilds();
		removeActionEvents(this);
		_resizeListener = null;
	}

}

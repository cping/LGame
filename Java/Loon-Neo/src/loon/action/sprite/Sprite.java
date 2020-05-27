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
import loon.event.ResizeListener;
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

	private TArray<ISprite> _childList = null;

	// 默认每帧刷新时间
	final static private long defaultTimer = 150;

	// 是否可见
	private boolean visible = true;

	// 精灵名称
	private String spriteName;

	// 精灵图片
	private LTexture image;

	// 动画
	private Animation animation = new Animation();

	private LColor _debugDrawColor = LColor.red;

	private ResizeListener<Sprite> _resizeListener;
	
	private int transform;

	private float _scaleX = 1f, _scaleY = 1f;

	private float _fixedWidthOffset = 0f;
	
	private float _fixedHeightOffset = 0f;

	private boolean _flipX = false, _flipY = false;

	private boolean _debugDraw = false;

	private int _maxFrame;

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
		this.spriteName = spriteName;
		this.visible = true;
		this.transform = LTrans.TRANS_NONE;
	}

	/**
	 * 以下参数分别为 取材文件,每行取材宽度,每列取材长度
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 */
	public Sprite(String fileName, int row, int col) {
		this(fileName, -1, 0, 0, row, col, defaultTimer);
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
		this(fileName, x, y, row, col, defaultTimer);
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
		this(fileName, maxFrame, x, y, row, col, defaultTimer);
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
		this(images, x, y, defaultTimer);
	}

	/**
	 * 以下参数分别为 图像数组,平均每桢显示时间
	 * 
	 * @param images
	 * @param timer
	 */
	public Sprite(LTexture[] images, long timer) {
		this(images, -1, 0, 0, defaultTimer);
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
		this.spriteName = spriteName;
		this.setAnimation(animation, images, maxFrame, timer);
		this.visible = true;
		this.transform = LTrans.TRANS_NONE;
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
		this.spriteName = spriteName;
		LTexture[] texs = sheet.getTextures();
		this.setAnimation(animation, texs, texs.length, timer);
		this.visible = true;
		this.transform = LTrans.TRANS_NONE;
	}

	/**
	 * 是否在播放动画
	 * 
	 * @param running
	 */
	public Sprite setRunning(boolean running) {
		animation.setRunning(running);
		return this;
	}

	/**
	 * 返回当前总桢数
	 * 
	 * @return
	 */
	public int getTotalFrames() {
		return animation.getTotalFrames();
	}

	/**
	 * 设定当前帧
	 * 
	 * @param index
	 */
	public Sprite setCurrentFrameIndex(int index) {
		animation.setCurrentFrameIndex(index);
		return this;
	}

	/**
	 * 返回当前桢索引
	 * 
	 * @return
	 */
	public int getCurrentFrameIndex() {
		return animation.getCurrentFrameIndex();
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
		this._maxFrame = max;
		if (_maxFrame != -1) {
			for (int i = 0; i < _maxFrame && i < images.length; i++) {
				myAnimation.addFrame(images[i], timer);
			}
		} else {
			for (int i = 0; i < images.length; i++) {
				myAnimation.addFrame(images[i], timer);
			}
		}
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
	 * @param animation
	 */
	public Sprite setAnimation(Animation animation) {
		this.animation = animation;
		this._maxFrame = animation.getTotalFrames();
		return this;
	}

	public Animation getAnimation() {
		return animation;
	}

	protected void onUpdate(long elapsedTime) {
	}

	/**
	 * 变更动画
	 */
	public void update(long elapsedTime) {
		if (visible) {
			animation.update(elapsedTime);
			onUpdate(elapsedTime);
			if (_childList != null && _childList.size > 0) {
				for (ISprite spr : _childList) {
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
		return animation.getSpriteImage();
	}

	@Override
	public float getWidth() {
		LTexture si = animation.getSpriteImage();
		if (si == null) {
			return -1;
		}
		return (si.width() * _scaleX) - _fixedWidthOffset;
	}

	@Override
	public float getHeight() {
		LTexture si = animation.getSpriteImage();
		if (si == null) {
			return -1;
		}
		return (si.height() * _scaleY) - _fixedHeightOffset;
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

	private LColor filterColor;

	@Override
	public void createUI(GLEx g) {
		createUI(g, 0, 0);
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (!visible) {
			return;
		}
		if (_alpha < 0.01) {
			return;
		}

		if (animation.getCurrentFrameIndex() > _maxFrame) {
			animation.reset();
		}
		image = animation.getSpriteImage();

		final boolean notImg = image == null;

		if (animation != null && animation.length > 0 && notImg) {
			return;
		}

		float width = notImg ? getContainerWidth() : image.getWidth();
		float height = notImg ? getContainerHeight() : image.getHeight();

		boolean update = (_rotation != 0) || !(_scaleX == 1f && _scaleY == 1f) || _flipX || _flipY;
		int tmp = g.color();
		int blend = g.getBlendMode();
		try {
			g.setBlendMode(_blend);
			float nx = this._location.x + offsetX;
			float ny = this._location.y + offsetY;
			if (update) {
				g.saveTx();
				Affine2f tx = g.tx();
				final float centerX = this._pivot.x == -1 ? (nx + _origin.ox(width)) : nx + this._pivot.x;
				final float centerY = this._pivot.y == -1 ? (ny + _origin.oy(height)) : ny + this._pivot.y;
				if (_rotation != 0 && notImg) {
					tx.translate(centerX, centerY);
					tx.preRotate(_rotation);
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
			g.setAlpha(_alpha);
			if (!notImg) {
				if (LTrans.TRANS_NONE == transform) {
					g.draw(image, nx, ny, width, height, filterColor, _rotation, _pivot, _scaleX, _scaleY);
				} else {
					g.drawRegion(image, 0, 0, (int) width, (int) height, transform, (int) nx, (int) ny,
							LTrans.TOP | LTrans.LEFT, filterColor, _pivot, _scaleX, _scaleY, _rotation);
				}
			}
			if (_childList != null && _childList.size > 0) {
				for (ISprite spr : _childList) {
					if (spr != null) {
						float px = 0, py = 0;
						ISprite parent = spr.getParent();
						if (parent != null) {
							px += parent.getX();
							py += parent.getY();
							for (; (parent = parent.getParent()) != null;) {
								px += parent.getX();
								py += parent.getY();
							}
						}
						spr.createUI(g, px, py);
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
		ISprite parent = _super;
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
		ISprite parent = _super;
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
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getSpriteName() {
		return spriteName;
	}

	public Sprite setSpriteName(String spriteName) {
		this.spriteName = spriteName;
		return this;
	}

	public int getTransform() {
		return transform;
	}

	public Sprite setTransform(int transform) {
		this.transform = transform;
		return this;
	}

	public LColor getFilterColor() {
		return new LColor(filterColor);
	}

	public Sprite setFilterColor(LColor filterColor) {
		this.filterColor = filterColor;
		return this;
	}

	@Override
	public LTexture getBitmap() {
		return this.image;
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
		return _childList != null && _childList.size > 0;
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
		return _maxFrame;
	}

	public Sprite setMaxFrame(int maxFrame) {
		this._maxFrame = maxFrame;
		return this;
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
		if (_childList == null) {
			_childList = new TArray<ISprite>();
		}
		spr.setParent(this);
		spr.setSprites(this._sprites);
		spr.setState(State.ADDED);
		_childList.add(spr);
		childSorter.sort(_childList);
		return this;
	}

	public boolean removeChild(ISprite spr) {
		if (spr == null) {
			return true;
		}
		if (_childList == null) {
			_childList = new TArray<ISprite>();
		}
		boolean removed = _childList.remove(spr);
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
		if (_childList == null) {
			_childList = new TArray<ISprite>();
		}
		for (int i = this._childList.size - 1; i >= 0; i--) {
			if (i == idx) {
				final ISprite removed = this._childList.removeIndex(i);
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
		if (this._childList == null) {
			return this;
		}
		for (int i = this._childList.size - 1; i >= 0; i--) {
			final ISprite removed = this._childList.get(i);
			boolean exist = (removed == null);
			if (exist) {
				removed.setState(State.REMOVED);
			}
			// 删除精灵同时，删除缓动动画
			if (exist && removed instanceof ActionBind) {
				removeActionEvents((ActionBind) removed);
			}
		}
		this._childList.clear();
		return this;
	}

	public int getChildCount() {
		return size();
	}

	public ISprite getChildByIndex(int idx) {
		if (_childList != null && idx >= 0 && idx < size()) {
			return _childList.get(idx);
		}
		return null;
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
		return (_childList == null ? 0 : _childList.size);
	}

	@Override
	public void clear() {
		if (_childList != null) {
			removeChilds();
		}
	}

	@Override
	public float getContainerX() {
		if (_super != null) {
			return getScreenX() - getX();
		}
		return this._sprites == null ? super.getContainerX() : this._sprites.getX();
	}

	@Override
	public float getContainerY() {
		if (_super != null) {
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
	public void setSprites(Sprites ss) {
		if (this._sprites == ss) {
			return;
		}
		this._sprites = ss;
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
	public boolean intersects(CollisionObject object) {
		return getCollisionBox().intersects(object.getRectBox());
	}

	@Override
	public boolean intersects(RectBox rect) {
		return getCollisionBox().intersects(rect);
	}

	public Gravity getGravity() {
		return new Gravity("Sprite", this);
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
	public void setFixedWidthOffset(float fixedWidthOffset) {
		this._fixedWidthOffset = fixedWidthOffset;
	}

	@Override
	public float getFixedHeightOffset() {
		return _fixedHeightOffset;
	}

	@Override
	public void setFixedHeightOffset(float fixedHeightOffset) {
		this._fixedHeightOffset = fixedHeightOffset;
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
		this.visible = false;
		if (image != null) {
			image.close();
		}
		if (animation != null) {
			animation.close();
		}
		setState(State.DISPOSED);
		removeChilds();
		removeActionEvents(this);
		_resizeListener = null;
	}

}

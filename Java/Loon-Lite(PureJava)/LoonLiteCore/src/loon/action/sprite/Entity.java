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
package loon.action.sprite;

import java.util.Comparator;

import loon.Director.Origin;
import loon.LObject;
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.PlayerUtils;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionBindData;
import loon.action.ActionControl;
import loon.action.ActionListener;
import loon.action.ActionTween;
import loon.action.PlaceActions;
import loon.action.collision.CollisionHelper;
import loon.action.collision.CollisionMask;
import loon.action.collision.CollisionObject;
import loon.action.collision.Gravity;
import loon.action.map.Field2D;
import loon.action.sprite.Sprites.Created;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.layout.LayoutAlign;
import loon.events.EventAction;
import loon.events.ResizeListener;
import loon.events.SysKey;
import loon.events.SysTouch;
import loon.geom.Affine2f;
import loon.geom.BoxSize;
import loon.geom.Circle;
import loon.geom.Dimension;
import loon.geom.Ellipse;
import loon.geom.Line;
import loon.geom.Point;
import loon.geom.PointF;
import loon.geom.PointI;
import loon.geom.Polygon;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.ShapeNodeType;
import loon.geom.Triangle2f;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.HelperUtils;
import loon.utils.IArray;
import loon.utils.LayerSorter;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 一个精灵类的具体实现,可以用来充当ECS模式中的实体对象用类(当然,Loon中并不强制要求使用ECS模式进行开发)
 */
public class Entity extends LObject<IEntity> implements CollisionObject, IEntity, IArray, BoxSize {

	private static final int CHILDREN_CAPACITY_DEFAULT = 4;

	protected Origin _origin = Origin.CENTER;
	protected boolean _visible = true;
	protected boolean _deform = true;
	protected boolean _ignoreUpdate = false;
	protected boolean _childrenVisible = true;
	protected boolean _childrenIgnoreUpdate = false;
	protected boolean _childrenSortPending = false;
	protected boolean _componentsIgnoreUpdate = false;
	protected boolean _debugDraw = false;

	protected boolean _followRotation = true;
	protected boolean _followScale = true;
	protected boolean _followColor = true;

	protected int _idxTag = Integer.MIN_VALUE;

	protected boolean _repaintDraw = false;

	// 是否传递本身偏移设定数据到自绘部分
	protected boolean _repaintAutoOffset = false;

	protected TArray<IEntity> _childrens;
	protected TArray<TComponent<IEntity>> _components;

	protected RectBox _shear;
	protected LColor _baseColor = new LColor(LColor.white);
	private LColor _debugDrawColor = LColor.red;

	private Vector2f _touchOffset = new Vector2f();

	protected Vector2f _offset = new Vector2f();

	private Vector2f _touchPoint = new Vector2f();
	private boolean _createShadow;
	private boolean _xySort;

	protected float _fixedWidthOffset = 0f;
	protected float _fixedHeightOffset = 0f;

	protected float _rotationCenterX = -1;
	protected float _rotationCenterY = -1;

	protected float _scaleX = 1f;
	protected float _scaleY = 1f;

	protected float _scaleCenterX = -1;
	protected float _scaleCenterY = -1;

	protected float _skewX = 0;
	protected float _skewY = 0;

	protected float _skewCenterX = -1;
	protected float _skewCenterY = -1;

	protected boolean _flipX = false, _flipY = false;

	private final static LayerSorter<IEntity> entitySorter = new LayerSorter<IEntity>(false);

	private ResizeListener<IEntity> _resizeListener;

	private boolean _stopUpdate = false;

	private SpriteCollisionListener _collSpriteListener;

	private Sprites _sprites = null;

	private EventAction _loopAction;

	private Shape _otherShape = null;

	private float _oldShapeRectX, _oldShapeRectY, _oldShapeRectW, _oldShapeRectH;

	private ShapeNodeType _oldNodeType;

	protected float _width, _height;

	protected LTexture _image;

	protected LRelease _disposed;

	public Entity() {
		this((LTexture) null);
	}

	public Entity(final String path) {
		this(LSystem.loadTexture(path));
	}

	public Entity(final String path, final float x, final float y) {
		this(LSystem.loadTexture(path), x, y);
	}

	public Entity(final String path, final Vector2f v) {
		this(path, v == null ? 0 : v.x, v == null ? 0 : v.y);
	}

	public Entity(final LTexture texture) {
		this(texture, 0, 0, texture == null ? 0 : texture.getWidth(), texture == null ? 0 : texture.getHeight());
	}

	public Entity(final LTexture texture, final float x, final float y) {
		this(texture, x, y, texture == null ? 0 : texture.getWidth(), texture == null ? 0 : texture.getHeight());
	}

	public Entity(final LTexture texture, final float x, final float y, final float w, final float h) {
		this.setLocation(x, y);
		this._width = w;
		this._height = h;
		this._image = texture;
	}

	public static Entity make(LTexture texture, final float x, final float y) {
		return new Entity(texture, x, y);
	}

	public static Entity make(String path, final float x, final float y) {
		return new Entity(path, x, y);
	}

	public static Entity make(String path, final Vector2f v) {
		return new Entity(path, v);
	}

	public static Entity make(String path) {
		return new Entity(path, 0, 0);
	}

	protected void onUpdateColor() {

	}

	public IEntity setTexture(String path) {
		return setTexture(LSystem.loadTexture(path));
	}

	public IEntity setTexture(LTexture tex) {
		this._image = tex;
		if (_width <= 0) {
			_width = _image.width();
		}
		if (_height <= 0) {
			_height = _image.height();
		}
		this._repaintDraw = (tex == null);
		return this;
	}

	@Override
	public IEntity view(String path) {
		return setTexture(path);
	}

	@Override
	public IEntity view(LTexture tex) {
		return setTexture(tex);
	}

	@Override
	public boolean isVisible() {
		return this._visible;
	}

	@Override
	public void setVisible(final boolean v) {
		this._visible = v;
	}

	@Override
	public boolean isChildrenVisible() {
		return this._childrenVisible;
	}

	@Override
	public IEntity setChildrenVisible(final boolean v) {
		this._childrenVisible = v;
		return this;
	}

	@Override
	public boolean isIgnoreUpdate() {
		return this._ignoreUpdate;
	}

	@Override
	public void setIgnoreUpdate(final boolean u) {
		this._ignoreUpdate = u;
	}

	@Override
	public boolean isChildrenIgnoreUpdate() {
		return this._childrenIgnoreUpdate;
	}

	@Override
	public IEntity setChildrenIgnoreUpdate(final boolean c) {
		this._childrenIgnoreUpdate = c;
		return this;
	}

	@Override
	public int getIndexTag() {
		return this._idxTag;
	}

	@Override
	public IEntity setIndexTag(final int idx) {
		this._idxTag = idx;
		return this;
	}

	@Override
	public boolean isRotated() {
		return this._objectRotation != 0f;
	}

	@Override
	public float getRotationCenterX() {
		return this._rotationCenterX;
	}

	@Override
	public float getRotationCenterY() {
		return this._rotationCenterY;
	}

	@Override
	public void setRotationCenterX(final float sx) {
		this._rotationCenterX = sx;
	}

	@Override
	public void setRotationCenterY(final float sy) {
		this._rotationCenterY = sy;
	}

	@Override
	public void setRotationCenter(final float sx, final float sy) {
		this._rotationCenterX = sx;
		this._rotationCenterY = sy;
	}

	@Override
	public float getPivotX() {
		return this._rotationCenterX;
	}

	@Override
	public float getPivotY() {
		return this._rotationCenterY;
	}

	@Override
	public void setPivotX(final float rx) {
		this._rotationCenterX = rx;
		this._scaleCenterX = rx;
	}

	@Override
	public void setPivotY(final float ry) {
		this._rotationCenterY = ry;
		this._scaleCenterY = ry;
	}

	@Override
	public void setPivot(final float rx, final float ry) {
		setPivotX(rx);
		setPivotY(ry);
	}

	public IEntity setAnchor(final float scale) {
		return setAnchor(scale, scale);
	}

	public IEntity setAnchor(final float sx, final float sy) {
		setPivot(_width * sx, _height * sy);
		return this;
	}

	public IEntity coord(float x, float y) {
		setLocation(x, y);
		return this;
	}

	@Override
	public boolean isScaled() {
		return (this._scaleX != 1) || (this._scaleY != 1);
	}

	@Override
	public float getScaleX() {
		return this._scaleX;
	}

	@Override
	public float getScaleY() {
		return this._scaleY;
	}

	@Override
	public void setScaleX(final float pScaleX) {
		this.setScale(pScaleX, this._scaleY);
	}

	@Override
	public void setScaleY(final float pScaleY) {
		this.setScale(this._scaleX, pScaleY);
	}

	protected void onScale() {
	}

	@Override
	public void setScale(final float pScale) {
		this.setScale(pScale, pScale);
	}

	@Override
	public void setScale(final float pScaleX, final float pScaleY) {
		if (pScaleX == this._scaleX && pScaleY == this._scaleY) {
			return;
		}
		this._scaleX = pScaleX;
		this._scaleY = pScaleY;
		if (_childrens != null) {
			for (int i = this._childrens.size - 1; i >= 0; i--) {
				final IEntity child = this._childrens.get(i);
				if (child != null && child.isFollowScale() && child != this) {
					child.setScale(pScaleX, pScaleY);
				}
			}
		}
		this.onScale();
	}

	public Entity scaleTo(float width, float height) {
		return scaleTo(width, height, "in-pad");
	}

	public Entity scaleTo(float width, float height, String mode) {
		float scaleX = _scaleX;
		float scaleY = _scaleY;
		float curWidth = _width;
		float curHeight = _height;
		if (width > 0) {
			scaleX = curWidth / width;
			curWidth = width;
		}
		if (height > 0) {
			scaleY = curHeight / height;
			curHeight = height;
		}
		if (curWidth > 0 && curHeight > 0 && !StringUtils.isEmpty(mode)) {
			if ("out".equals(mode) || "out-crop".equals(mode)) {
				scaleX = scaleY = MathUtils.max(scaleX, scaleY);
			} else if ("in".equals(mode) || "in-pad".equals(mode)) {
				scaleX = scaleY = MathUtils.min(scaleX, scaleY);
			}
			setScale(scaleX, scaleY);
			if ("out-crop".equals(mode) || "in-pad".equals(mode)) {
				curWidth = curWidth / scaleX;
				curHeight = curHeight / scaleY;
				setSize(curWidth, curHeight);
			}
		}
		return this;
	}

	@Override
	public float getScaleCenterX() {
		return this._scaleCenterX;
	}

	@Override
	public float getScaleCenterY() {
		return this._scaleCenterY;
	}

	@Override
	public void setScaleCenterX(final float sx) {
		this._scaleCenterX = sx;
	}

	@Override
	public void setScaleCenterY(final float sy) {
		this._scaleCenterY = sy;
	}

	@Override
	public void setScaleCenter(final float sx, final float sy) {
		this._scaleCenterX = sx;
		this._scaleCenterY = sy;
	}

	@Override
	public boolean isSkewed() {
		return (this._skewX != 0) || (this._skewY != 0);
	}

	@Override
	public float getSkewX() {
		return this._skewX;
	}

	@Override
	public float getSkewY() {
		return this._skewY;
	}

	@Override
	public void setSkewX(final float sx) {
		this._skewX = sx;
	}

	@Override
	public void setSkewY(final float sy) {
		this._skewY = sy;
	}

	@Override
	public void setSkew(final float pSkew) {
		this._skewX = pSkew;
		this._skewY = pSkew;
	}

	@Override
	public void setSkew(final float sx, final float sy) {
		this._skewX = sx;
		this._skewY = sy;
	}

	@Override
	public float getSkewCenterX() {
		return this._skewCenterX;
	}

	@Override
	public float getSkewCenterY() {
		return this._skewCenterY;
	}

	@Override
	public void setSkewCenterX(final float sx) {
		this._skewCenterX = sx;
	}

	@Override
	public void setSkewCenterY(final float sy) {
		this._skewCenterY = sy;
	}

	@Override
	public void setSkewCenter(final float sx, final float sy) {
		this._skewCenterX = sx;
		this._skewCenterY = sy;
	}

	@Override
	public boolean isRotatedOrScaledOrSkewed() {
		return (this._objectRotation != 0) || (this._scaleX != 1) || (this._scaleY != 1) || (this._skewX != 0)
				|| (this._skewY != 0);
	}

	@Override
	public float getRed() {
		return this._baseColor.r;
	}

	@Override
	public float getGreen() {
		return this._baseColor.g;
	}

	@Override
	public float getBlue() {
		return this._baseColor.b;
	}

	@Override
	public float getAlpha() {
		return super.getAlpha();
	}

	@Override
	public LColor getColor() {
		return this._baseColor.cpy();
	}

	private void updateAlpha(final float alpha) {
		if (_childrens != null) {
			for (int i = _childrens.size - 1; i > -1; i--) {
				IEntity entity = _childrens.get(i);
				if (entity != null && entity.isFollowColor() && entity != this) {
					entity.setAlpha(alpha);
				}
			}
		}
	}

	private void updateColor(final LColor color) {
		if (_childrens != null) {
			for (int i = _childrens.size - 1; i > -1; i--) {
				IEntity entity = _childrens.get(i);
				if (entity != null && entity.isFollowColor() && entity != this) {
					entity.setColor(color);
				}
			}
		}
	}

	@Override
	public void setColor(final LColor pColor) {
		this._baseColor.setColor(pColor);
		this.updateColor(_baseColor);
		this.onUpdateColor();
	}

	@Override
	public void setColor(final int pColor) {
		this._baseColor.setColor(pColor);
		this.updateColor(_baseColor);
		this.onUpdateColor();
	}

	@Override
	public void setColor(final float r, final float g, final float b) {
		this._baseColor.setColor(r, g, b);
		this.updateColor(_baseColor);
		this.onUpdateColor();

	}

	@Override
	public void setColor(final float r, final float g, final float b, final float a) {
		this._baseColor.setColor(r, g, b, a);
		this.updateColor(_baseColor);
		this.onUpdateColor();
	}

	@Override
	public void setAlpha(final float a) {
		super.setAlpha(a);
		this._baseColor.a = a;
		this.updateAlpha(a);
		this.onUpdateColor();
	}

	@Override
	public int getChildCount() {
		if (this._childrens == null) {
			return 0;
		}
		return this._childrens.size;
	}

	@Override
	public IEntity getChildByTag(final int idx) {
		if (this._childrens == null) {
			return null;
		}
		for (int i = this._childrens.size - 1; i >= 0; i--) {
			final IEntity child = this._childrens.get(i);
			if (child.getIndexTag() == idx) {
				return child;
			}
		}
		return null;
	}

	@Override
	public IEntity getChildByIndex(final int pIndex) {
		if (this._childrens == null) {
			return null;
		}
		return this._childrens.get(pIndex);
	}

	@Override
	public IEntity getFirstChild() {
		if (this._childrens == null) {
			return null;
		}
		return this._childrens.get(0);
	}

	@Override
	public IEntity getLastChild() {
		if (this._childrens == null) {
			return null;
		}
		return this._childrens.get(this._childrens.size - 1);
	}

	@Override
	public IEntity addChild(final IEntity e) {
		if (e == null) {
			return this;
		}
		if (e == this) {
			return this;
		}
		if (this._childrens == null) {
			this.allocateChildren();
		}
		this._childrens.add(e);
		e.setParent(this);
		e.setSprites(this._sprites);
		e.setState(State.ADDED);
		e.onAttached();
		return this;
	}

	@Override
	public IEntity addChildAt(final IEntity e, float x, float y) {
		if (e != null) {
			e.setLocation(x, y);
			addChild(e);
		}
		return this;
	}

	@Override
	public IEntity sortChildren() {
		return this.sortChildren(true);
	}

	@Override
	public IEntity sortChildren(final boolean i) {
		if (this._childrens == null) {
			return this;
		}
		if (i) {
			entitySorter.sort(this._childrens);
		} else {
			this._childrenSortPending = true;
		}
		return this;
	}

	@Override
	public IEntity sortChildren(final Comparator<IEntity> e) {
		if (this._childrens == null) {
			return this;
		}
		entitySorter.sort(this._childrens, e);
		return this;
	}

	@Override
	public boolean removeSelf() {
		final IEntity parent = this._objectSuper;
		if (parent != null) {
			return parent.removeChild(this);
		} else {
			return false;
		}
	}

	@Override
	public IEntity removeChildren() {
		if (this._childrens == null) {
			return this;
		}
		for (int i = this._childrens.size - 1; i >= 0; i--) {
			final IEntity removed = this._childrens.get(i);
			if (removed != null) {
				removed.setState(State.REMOVED);
				removed.onDetached();
			}
			// 删除精灵同时，删除缓动动画
			if (removed != null && removed instanceof ActionBind) {
				removeActionEvents((ActionBind) removed);
			}
		}
		this._childrens.clear();
		return this;
	}

	@Override
	public boolean removeChild(final IEntity e) {
		if (e == null) {
			return true;
		}
		if (this._childrens == null) {
			return false;
		}
		boolean removed = this._childrens.remove(e);
		if (removed) {
			e.setState(State.REMOVED);
			e.onDetached();
		}
		// 删除精灵同时，删除缓动动画
		if (removed && e instanceof ActionBind) {
			removeActionEvents((ActionBind) e);
		}
		return removed;
	}

	@Override
	public IEntity removeChild(final int idx) {
		if (this._childrens == null) {
			return null;
		}
		for (int i = this._childrens.size - 1; i >= 0; i--) {
			if (this._childrens.get(i).getIndexTag() == idx) {
				final IEntity removed = this._childrens.removeIndex(i);
				if (removed != null) {
					removed.setState(State.REMOVED);
					removed.onDetached();
				}
				// 删除精灵同时，删除缓动动画
				if (removed != null && (removed instanceof ActionBind)) {
					removeActionEvents((ActionBind) removed);
				}
				return removed;
			}
		}
		return null;
	}

	@Override
	public void onAttached() {

	}

	@Override
	public void onDetached() {

	}

	@Override
	public Object getUserData() {
		return this.Tag;
	}

	@Override
	public IEntity setUserData(final Object u) {
		this.Tag = u;
		return this;
	}

	@Override
	public final void createUI(final GLEx g) {
		this.createUI(g, 0f, 0f);
	}

	@Override
	public final void createUI(final GLEx g, final float offsetX, final float offsetY) {
		if (this._visible) {
			this.onManagedPaint(g, offsetX, offsetY);
		}
	}

	@Override
	public IEntity reset() {
		this._visible = true;
		this._debugDraw = false;
		this._ignoreUpdate = false;
		this._childrenVisible = true;
		this._childrenIgnoreUpdate = false;

		this._objectRotation = 0f;
		this._previousRotation = 0f;
		this._scaleX = 1f;
		this._scaleY = 1f;
		this._skewX = 0f;
		this._skewY = 0f;
		this._flipX = false;
		this._flipY = false;

		this._baseColor.reset();

		if (this._childrens != null) {
			final TArray<IEntity> entities = this._childrens;
			for (int i = entities.size - 1; i >= 0; i--) {
				IEntity e = entities.get(i);
				if (e != null) {
					e.reset();
				}
			}
		}
		return this;
	}

	public float getScreenX() {
		ISprite parent = getParent();
		if (parent == null) {
			return getX();
		}
		if (parent instanceof SpriteEntity) {
			return getX();
		}
		float x = 0;
		if (parent != null) {
			x += parent.getX();
			for (; (parent = parent.getParent()) != null;) {
				x += parent.getX();
			}
		}
		return x + getX();
	}

	public float getScreenY() {
		ISprite parent = getParent();
		if (parent == null) {
			return getY();
		}
		if (parent instanceof SpriteEntity) {
			return getY();
		}
		float y = 0;
		if (parent != null) {
			y += parent.getY();
			for (; (parent = parent.getParent()) != null;) {
				y += parent.getY();
			}
		}
		return y + getY();
	}

	public float getCurrentX() {
		return this.getX() + _offset.x;
	}

	public float getCurrentY() {
		return this.getY() + _offset.y;
	}

	public float centerX() {
		return getX() + (getWidth() / 2f);
	}

	public float centerY() {
		return getY() + (getHeight() / 2f);
	}

	public float getXdistance(Entity target) {
		return MathUtils.abs(getCenterX() - target.getCenterX());
	}

	public float getYdistance(Entity target) {
		return MathUtils.abs(getCenterY() - target.getCenterY());
	}

	public float getTileDistance(Entity target, int tileSize) {
		return (getXdistance(target) + getYdistance(target)) / tileSize;
	}

	protected void prePaint(final GLEx g) {

	}

	public void paint(final GLEx g) {
		paint(g, 0, 0);
	}

	public void paint(final GLEx g, float offsetX, float offsetY) {
		if (_objectAlpha < 0.01f) {
			return;
		}
		final boolean exist = _image != null || (_width > 0 && _height > 0) || _repaintDraw;
		if (exist) {
			final boolean update = ((_objectRotation != 0 || !(_scaleX == 1f && _scaleY == 1f)
					|| !(_skewX == 0 && _skewY == 0)) || _flipX || _flipY) && _deform;
			final float nx = drawX(offsetX);
			final float ny = drawY(offsetY);
			if (update) {
				g.saveTx();
				g.saveBrush();
				Affine2f tx = g.tx();
				final float rotation = this._objectRotation;
				final float scaleX = this._scaleX;
				final float scaleY = this._scaleY;
				if (rotation != 0) {
					final float rotationCenterX = this._rotationCenterX == -1 ? (nx + _origin.ox(this._width))
							: nx + this._rotationCenterX;
					final float rotationCenterY = this._rotationCenterY == -1 ? (ny + _origin.oy(this._height))
							: ny + this._rotationCenterY;
					tx.translate(rotationCenterX, rotationCenterY);
					tx.preRotate(rotation);
					tx.translate(-rotationCenterX, -rotationCenterY);
				}
				final boolean flipX = this._flipX;
				final boolean flipY = this._flipY;
				if (flipX || flipY) {
					final float rotationCenterX = this._rotationCenterX == -1 ? (nx + _origin.ox(this._width))
							: nx + this._rotationCenterX;
					final float rotationCenterY = this._rotationCenterY == -1 ? (ny + _origin.oy(this._height))
							: ny + this._rotationCenterY;
					if (flipX && flipY) {
						Affine2f.transform(tx, rotationCenterX, rotationCenterY, Affine2f.TRANS_ROT180);
					} else if (flipX) {
						Affine2f.transform(tx, rotationCenterX, rotationCenterY, Affine2f.TRANS_MIRROR);
					} else if (flipY) {
						Affine2f.transform(tx, rotationCenterX, rotationCenterY, Affine2f.TRANS_MIRROR_ROT180);
					}
				}
				if ((scaleX != 1) || (scaleY != 1)) {
					final float scaleCenterX = this._scaleCenterX == -1 ? (nx + _origin.ox(this._width))
							: nx + this._scaleCenterX;
					final float scaleCenterY = this._scaleCenterY == -1 ? (ny + _origin.oy(this._height))
							: ny + this._scaleCenterY;
					tx.translate(scaleCenterX, scaleCenterY);
					tx.preScale(scaleX, scaleY);
					tx.translate(-scaleCenterX, -scaleCenterY);
				}
				final float skewX = this._skewX;
				final float skewY = this._skewY;
				if ((skewX != 0) || (skewY != 0)) {
					final float skewCenterX = this._skewCenterX == -1 ? (nx + _origin.ox(this._width))
							: nx + this._skewCenterX;
					final float skewCenterY = this._skewCenterY == -1 ? (ny + _origin.oy(this._height))
							: ny + this._skewCenterY;
					tx.translate(skewCenterX, skewCenterY);
					tx.preShear(skewX, skewY);
					tx.translate(-skewCenterX, -skewCenterY);
				}
			}
			if (_repaintDraw) {
				final boolean elastic = (_shear != null);
				if (elastic) {
					g.setClip(drawX(offsetX + _shear.x), drawY(offsetY + _shear.y), _shear.width, _shear.height);
				}
				final float tmp = g.alpha();
				g.setAlpha(_objectAlpha);
				if (_repaintAutoOffset) {
					repaint(g, nx, ny);
				} else {
					repaint(g, offsetX, offsetY);
				}
				g.setAlpha(tmp);
				if (elastic) {
					g.clearClip();
				}
			} else {
				if (_image != null) {
					if (_shear == null) {
						g.draw(_image, nx, ny, _width, _height, _baseColor);
					} else {
						g.draw(_image, nx, ny, _width, _height, _shear.x, _shear.y, _shear.width, _shear.height,
								_baseColor);
					}
				} else {
					g.fillRect(nx, ny, _width, _height, _baseColor);
				}
			}
			if (_debugDraw) {
				g.drawRect(nx, ny, _width, _height, _debugDrawColor);
			}
			if (update) {
				g.restoreBrush();
				g.restoreTx();
			}
		}
	}

	public IEntity setRepaint(boolean r) {
		this._repaintDraw = r;
		return this;
	}

	public boolean isRepaint() {
		return this._repaintDraw;
	}

	protected void repaint(GLEx g, float offsetX, float offsetY) {

	}

	protected void postPaint(final GLEx g) {

	}

	public float getScreenScalePixelX() {
		if (_scaleCenterX != -1f) {
			return getScreenX() + _scaleCenterX;
		}
		return ((_scaleX == 1f) ? getScreenX() : (getScreenX() + _origin.ox(getWidth())));
	}

	public float getScreenScalePixelY() {
		if (_scaleCenterY != -1f) {
			return getScreenY() + _scaleCenterY;
		}
		return ((_scaleY == 1f) ? getScreenY() : (getScreenY() + _origin.oy(getHeight())));
	}

	public Entity placeToCenter(ActionBind ab) {
		ab.setLocation(getScreenScalePixelX() + (getWidth() - ab.getWidth()) / 2f,
				getScreenScalePixelY() + (getHeight() - ab.getHeight()) / 2f);
		return this;
	}

	public Entity placeToCenterX(ActionBind ab, float x) {
		ab.setLocation(x, getScreenScalePixelY() + (getHeight() - ab.getHeight()) / 2f);
		return this;
	}

	public Entity placeToCenterY(ActionBind ab, float y) {
		ab.setLocation(getScreenScalePixelX() + (getWidth() - ab.getWidth()) / 2f, y);
		return this;
	}

	private void allocateChildren() {
		this._childrens = new TArray<IEntity>(Entity.CHILDREN_CAPACITY_DEFAULT);
	}

	private void allocateComponents() {
		this._components = new TArray<TComponent<IEntity>>(Entity.CHILDREN_CAPACITY_DEFAULT);
	}

	protected void onManagedPaint(final GLEx g, float offsetX, float offsetY) {
		final TArray<IEntity> children = this._childrens;
		if (!this._childrenVisible || (children == null)) {
			this.prePaint(g);
			this.paint(g, offsetX, offsetY);
			this.postPaint(g);
		} else {
			if (this._childrenSortPending) {
				entitySorter.sort(this._childrens);
				this._childrenSortPending = false;
			}
			final int childCount = children.size;
			this.prePaint(g);
			this.paint(g, offsetX, offsetY);
			for (int i = 0; i < childCount; i++) {
				final IEntity child = children.get(i);
				if (child != null) {
					float px = 0, py = 0;
					ISprite parent = child.getParent();
					if (parent == null) {
						parent = this;
					}
					px += parent.getX() + parent.getOffsetX();
					py += parent.getY() + parent.getOffsetY();
					for (; (parent = parent.getParent()) != null;) {
						px += parent.getX() + parent.getOffsetX();
						py += parent.getY() + parent.getOffsetY();
					}
					child.createUI(g, px + offsetX + child.getOffsetX(), py + offsetY + child.getOffsetY());
				}
			}
			this.postPaint(g);
		}
	}

	protected void onManagedUpdate(final long elapsedTime) {
		if (_stopUpdate) {
			return;
		}
		if ((this._components != null) && !this._componentsIgnoreUpdate) {
			final TArray<TComponent<IEntity>> comps = this._components;
			final int entityCount = comps.size;
			for (int i = 0; i < entityCount; i++) {
				final TComponent<IEntity> c = comps.get(i);
				if (c != null && !c._paused.get()) {
					c.update(this);
				}
			}
		}
		if ((this._childrens != null) && !this._childrenIgnoreUpdate) {
			final TArray<IEntity> entities = this._childrens;
			final int entityCount = entities.size;
			for (int i = 0; i < entityCount; i++) {
				entities.get(i).update(elapsedTime);
			}
		}
		onUpdate(elapsedTime);
		if (_loopAction != null) {
			HelperUtils.callEventAction(_loopAction, this);
		}
	}

	@Override
	public void onCollision(ISprite coll, int dir) {
		if (_collSpriteListener != null) {
			_collSpriteListener.onCollideUpdate(coll, dir);
		}
	}

	protected void onUpdate(final long elapsedTime) {

	}

	public Entity collision(SpriteCollisionListener sc) {
		this._collSpriteListener = sc;
		return this;
	}

	public Entity loop(EventAction la) {
		this._loopAction = la;
		return this;
	}

	public boolean isPaused() {
		return _ignoreUpdate;
	}

	public Entity pause() {
		if (!this._ignoreUpdate) {
			ActionControl.get().paused(this._ignoreUpdate = true, this);
		}
		return this;
	}

	public Entity resume() {
		if (this._ignoreUpdate) {
			ActionControl.get().paused(this._ignoreUpdate = false, this);
		}
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		if (!this._ignoreUpdate) {
			this.onProcess(elapsedTime);
			this.onManagedUpdate(elapsedTime);
		}
	}

	/**
	 * 内部循环用函数,不对外开放
	 * 
	 * @param elapsedTime
	 */
	void onProcess(long elapsedTime) {

	}

	public boolean isCollision(Entity o) {
		if (o == null) {
			return false;
		}
		RectBox src = getCollisionArea();
		RectBox dst = o.getCollisionArea();
		return src.intersects(dst) || src.contains(dst);
	}

	public int width() {
		return (int) getWidth();
	}

	public int height() {
		return (int) getHeight();
	}

	@Override
	public float getWidth() {
		return _width > 1 ? (_width * this._scaleX) - _fixedWidthOffset
				: _image == null ? 0 : (_image.getWidth() * this._scaleX) - _fixedWidthOffset;
	}

	@Override
	public float getHeight() {
		return _height > 1 ? (_height * this._scaleY) - _fixedHeightOffset
				: _image == null ? 0 : (_image.getHeight() * this._scaleY) - _fixedHeightOffset;
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
	public RectBox getCollisionArea() {
		return getCollisionBox();
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionBox();
	}

	@Override
	public RectBox getCollisionBox() {
		return setRect(MathUtils.getBounds(getScreenScalePixelX(), getScreenScalePixelY(), getWidth(), getHeight(),
				_objectRotation, _objectRect));
	}

	public float getScalePixelX() {
		if (_scaleCenterX != -1f) {
			return getX() + _scaleCenterX;
		}
		return ((_scaleX == 1f) ? getX() : (getX() + _origin.ox(getWidth())));
	}

	public float getScalePixelY() {
		if (_scaleCenterY != -1f) {
			return getY() + _scaleCenterY;
		}
		return ((_scaleY == 1f) ? getY() : (getY() + _origin.oy(getHeight())));
	}

	public float getContextWidth() {
		if (_childrens == null) {
			return getWidth();
		}
		float max = 0f;
		for (int i = _childrens.size - 1; i > -1; i--) {
			IEntity comp = _childrens.get(i);
			if (comp != null && comp.isVisible()) {
				max = MathUtils.max(comp.getScalePixelX() + comp.getWidth(), max);
			}
		}
		return max;
	}

	public float getContextHeight() {
		if (_childrens == null) {
			return getHeight();
		}
		float max = 0f;
		for (int i = _childrens.size - 1; i > -1; i--) {
			IEntity comp = _childrens.get(i);
			if (comp != null && comp.isVisible()) {
				max = MathUtils.max(comp.getScalePixelY() + comp.getHeight(), max);
			}
		}
		return max;
	}

	@Override
	public LTexture getBitmap() {
		return _image;
	}

	public void clearImage() {
		if (_image != null) {
			_image.close();
			_image = null;
		}
	}

	@Override
	public void setWidth(float w) {
		if (!MathUtils.equal(w, this._width)) {
			this._width = MathUtils.max(1f, w);
			this.onResize();
		}
	}

	@Override
	public void setHeight(float h) {
		if (!MathUtils.equal(h, this._height)) {
			this._height = MathUtils.max(1f, h);
			this.onResize();
		}
	}

	public IEntity setSize(float size) {
		return setSize(size, size);
	}

	@Override
	public IEntity setSize(float w, float h) {
		if (!MathUtils.equal(w, this._width) || !MathUtils.equal(h, this._height)) {
			this._width = MathUtils.max(1f, w);
			this._height = MathUtils.max(1f, h);
			this.onResize();
		}
		return this;
	}

	public IEntity setBounds(RectBox rect) {
		return this.setBounds(rect.x, rect.y, rect.width, rect.height);
	}

	public IEntity setBounds(float x, float y, float width, float height) {
		this.setLocation(x, y);
		this.setSize(width, height);
		return this;
	}

	public Dimension getDimension() {
		return new Dimension(this._width * this._scaleX, this._height * this._scaleY);
	}

	private void allocateShear() {
		if (_shear == null) {
			_shear = new RectBox(0, 0, this._width, this._height);
		}
	}

	public RectBox getClip() {
		return getShear();
	}

	public RectBox getShear() {
		allocateShear();
		return _shear;
	}

	public IEntity setShear(RectBox s) {
		allocateShear();
		this._shear.setBounds(s);
		return this;
	}

	public IEntity setShear(float x, float y, float w, float h) {
		allocateShear();
		this._shear.setBounds(x, y, w, h);
		return this;
	}

	public IEntity setClip(float x, float y, float w, float h) {
		setShear(x, y, w, h);
		return this;
	}

	public boolean isDeform() {
		return _deform;
	}

	public IEntity setDeform(boolean d) {
		this._deform = d;
		return this;
	}

	@Override
	public void setParent(ISprite s) {
		if (_objectSuper == s) {
			return;
		}
		if (s instanceof IEntity) {
			setSuper((IEntity) s);
		} else if (s instanceof ISprite) {
			setSuper(new SpriteEntity(s));
		} else {
			setSuper(null);
		}
	}

	public Origin getOrigin() {
		return _origin;
	}

	public IEntity setOrigin(Origin o) {
		this._origin = o;
		return this;
	}

	public boolean isStopUpdate() {
		return _stopUpdate;
	}

	public IEntity setStopUpdate(boolean s) {
		this._stopUpdate = s;
		return this;
	}

	@Override
	public IEntity setFlipX(boolean x) {
		this._flipX = x;
		return this;
	}

	@Override
	public IEntity setFlipY(boolean y) {
		this._flipY = y;
		return this;
	}

	@Override
	public IEntity setFlipXY(boolean x, boolean y) {
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
		RectBox rectDst = getRectBox();
		return CollisionHelper.checkAABBvsAABB(rectSelf.getX(), 0, rectSelf.getWidth(), rectSelf.getHeight(),
				rectDst.getX(), 0, rectDst.getWidth(), rectDst.getHeight());
	}

	@Override
	public boolean collidesY(ISprite other) {
		if (other == null || !other.isVisible()) {
			return false;
		}
		RectBox rectSelf = getRectBox();
		RectBox rectDst = getRectBox();
		return CollisionHelper.checkAABBvsAABB(0, rectSelf.getY(), rectSelf.getWidth(), rectSelf.getHeight(), 0,
				rectDst.getY(), rectDst.getWidth(), rectDst.getHeight());
	}

	public float getTouchDX() {
		final Screen screen = getScreen();
		return toPixelScaleX(screen == null ? SysTouch.getDX() : screen.getTouchDX());
	}

	public float getTouchDY() {
		final Screen screen = getScreen();
		return toPixelScaleY(screen == null ? SysTouch.getDY() : screen.getTouchDY());
	}

	public float getTouchX() {
		final Screen screen = getScreen();
		return toPixelScaleX(screen == null ? SysTouch.getX() : screen.getTouchX());
	}

	public float getTouchY() {
		final Screen screen = getScreen();
		return toPixelScaleY(screen == null ? SysTouch.getY() : screen.getTouchY());
	}

	public boolean isPointInUI(Vector2f v) {
		return isPointInUI(v.x, v.y);
	}

	public boolean isPointInUI(PointI p) {
		return isPointInUI(p.x, p.y);
	}

	public boolean isPointInUI(PointF p) {
		return isPointInUI(p.x, p.y);
	}

	public boolean isPointInUI(float x, float y) {
		return getCollisionBox().contains(x, y);
	}

	public boolean isPointInUI() {
		return isPointInUI(getTouchX(), getTouchY());
	}

	public boolean isKeyDown(int key) {
		final Screen screen = getScreen();
		if (screen == null) {
			return SysKey.isKeyPressed(key);
		}
		return screen.isKeyPressed(key) || SysKey.isKeyPressed(key);
	}

	public boolean isKeyUp(int key) {
		final Screen screen = getScreen();
		if (screen == null) {
			return SysKey.isKeyReleased(key);
		}
		return screen.isKeyReleased(key) || SysKey.isKeyReleased(key);
	}

	public boolean isKeyDown(String key) {
		final Screen screen = getScreen();
		if (screen == null) {
			return SysKey.isKeyPressed(key);
		}
		return screen.isKeyPressed(key) || SysKey.isKeyPressed(key);
	}

	public boolean isKeyUp(String key) {
		final Screen screen = getScreen();
		if (screen == null) {
			return SysKey.isKeyReleased(key);
		}
		return screen.isKeyReleased(key) || SysKey.isKeyReleased(key);
	}

	public boolean isClickDown() {
		final Screen screen = getScreen();
		if (screen == null) {
			return SysTouch.isDown();
		}
		return screen.getTouchPressed() == SysTouch.TOUCH_DOWN || SysTouch.isDown();
	}

	public boolean isClickUp() {
		final Screen screen = getScreen();
		if (screen == null) {
			return SysTouch.isUp();
		}
		return screen.getTouchReleased() == SysTouch.TOUCH_UP || SysTouch.isUp();
	}

	public boolean isClickDrag() {
		final Screen screen = getScreen();
		if (screen == null) {
			return SysTouch.isDrag();
		}
		return screen.getTouchPressed() == SysTouch.TOUCH_DRAG || SysTouch.isDrag();
	}

	@Override
	public void toString(final StrBuilder s) {
		s.append(super.toString());
		if ((this._childrens != null) && (this._childrens.size > 0)) {
			s.append(LSystem.LS);
			s.append(" [");
			final TArray<IEntity> entities = this._childrens;
			for (int i = 0; i < entities.size; i++) {
				entities.get(i).toString(s);
				if (i < (entities.size - 1)) {
					s.append(", ");
				}
			}
			s.append("]");
		}
	}

	@Override
	public String toString() {
		final StrBuilder sbr = new StrBuilder();
		this.toString(sbr);
		return sbr.toString();
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
	public int size() {
		return _childrens == null ? 0 : _childrens.size;
	}

	@Override
	public void clear() {
		if (_childrens != null) {
			removeChildren();
		}
	}

	@Override
	public boolean isEmpty() {
		return (_childrens == null ? true : _childrens.size == 0);
	}

	public Vector2f getOffset() {
		return _offset;
	}

	public IEntity setOffset(float x, float y) {
		this._offset.set(x, y);
		return this;
	}

	@Override
	public IEntity setOffset(Vector2f offset) {
		this._offset = offset;
		return this;
	}

	@Override
	public float getOffsetX() {
		return _offset.x;
	}

	public IEntity setOffsetX(float offsetX) {
		this._offset.setX(offsetX);
		return this;
	}

	@Override
	public float getOffsetY() {
		return _offset.y;
	}

	public IEntity setOffsetY(float offsetY) {
		this._offset.setY(offsetY);
		return this;
	}

	protected float drawX(float offsetX) {
		return offsetX + this._objectLocation.x + _offset.x;
	}

	protected float drawY(float offsetY) {
		return offsetY + this._objectLocation.y + _offset.y;
	}

	public float getDrawX() {
		return drawX(0);
	}

	public float getDrawY() {
		return drawY(0);
	}

	@Override
	public float getCenterX() {
		return getX() + getWidth() / 2f;
	}

	@Override
	public float getCenterY() {
		return getY() + getHeight() / 2f;
	}

	public IEntity in() {
		return in(30);
	}

	public IEntity in(float speed) {
		this.setAlpha(0f);
		this.selfAction().fadeIn(speed).start();
		return this;
	}

	public IEntity out() {
		return out(30);
	}

	public IEntity out(float speed) {
		this.selfAction().fadeOut(speed).start().setActionListener(new ActionListener() {

			@Override
			public void stop(ActionBind o) {
				if (getParent() != null) {
					getParent().removeChild((IEntity) o);
				}
				if (getScreen() != null) {
					getScreen().remove((ISprite) o);
				}
				if (_sprites != null) {
					_sprites.remove((ISprite) o);
				}
				close();
			}

			@Override
			public void start(ActionBind o) {

			}

			@Override
			public void process(ActionBind o) {

			}
		});
		return this;
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
		IEntity parent = getParent();
		if (parent != null) {
			newX = pointResult.x - parent.getX() - getX();
			newY = pointResult.y - parent.getX() - getY();
		} else {
			newX = pointResult.x - getX();
			newY = pointResult.y - getY();
		}
		final float angle = getRotation();
		if (angle == 0 || angle == 360) {
			pointResult.x = toPixelScaleX(newX) + _touchOffset.x;
			pointResult.y = toPixelScaleY(newY) + _touchOffset.y;
			return pointResult;
		}
		float oldWidth = _width;
		float oldHeight = _height;
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
			pointResult.set(-pointResult.x, MathUtils.abs(pointResult.y - this._height));
		} else if (angle == -90) {
			offX = oldHeight / 2f - newWidth / 2f;
			offY = oldWidth / 2f - newHeight / 2f;
			posX = (newX - offY);
			posY = (newY - offX);
			pointResult.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(-90);
			pointResult.set(-(pointResult.x - this._width), MathUtils.abs(pointResult.y));
		} else if (angle == -180 || angle == 180) {
			pointResult.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(getRotation()).addSelf(_width, _height);
		} else {
			float rad = MathUtils.toRadians(angle);
			float sin = MathUtils.sin(rad);
			float cos = MathUtils.cos(rad);
			float dx = offX / getScaleX();
			float dy = offY / getScaleY();
			float dx2 = cos * dx - sin * dy;
			float dy2 = sin * dx + cos * dy;
			pointResult.x = _width - (newX - dx2);
			pointResult.y = _height - (newY - dy2);
		}
		pointResult.addSelf(_touchOffset);
		return pointResult;
	}

	public Vector2f getUITouchXY() {
		float newX = 0f;
		float newY = 0f;
		if (getRotation() == 0) {
			if (_objectSuper == null) {
				newX = toPixelScaleX(SysTouch.getX() - getX());
				newY = toPixelScaleY(SysTouch.getY() - getY());
			} else {
				newX = toPixelScaleX(SysTouch.getX() - _objectSuper.getX() - getX());
				newY = toPixelScaleY(SysTouch.getY() - _objectSuper.getY() - getY());
			}
			_touchPoint.set(newX, newY).addSelf(_touchOffset);
		} else {
			newX = SysTouch.getX();
			newY = SysTouch.getY();
			return getUITouch(newX, newY, _touchPoint);
		}
		return _touchPoint;
	}

	public float getUITouchX() {
		return getUITouchXY().x;
	}

	public float getUITouchY() {
		return getUITouchXY().y;
	}

	protected void onRotation() {
	}

	public IEntity addGroup(Created<? extends IEntity> s, int count) {
		if (s == null) {
			return this;
		}
		for (int i = 0; i < count; i++) {
			addChild(s.make());
		}
		return this;
	}

	public IEntity addGroup(LTexture tex, int count) {
		for (int i = 0; i < count; i++) {
			addChild(new Entity(tex));
		}
		return this;
	}

	public IEntity addGroup(String path, int count) {
		for (int i = 0; i < count; i++) {
			addChild(new Entity(path));
		}
		return this;
	}

	public IEntity rect(RectBox rect) {
		return rect(rect, 0);
	}

	public IEntity rect(RectBox rect, int shift) {
		PlaceActions.rect(this, rect, shift);
		return this;
	}

	public IEntity triangle(Triangle2f t) {
		return triangle(t, 1);
	}

	public IEntity triangle(Triangle2f t, int stepRate) {
		PlaceActions.triangle(this, t, stepRate);
		return this;
	}

	public IEntity circle(Circle circle) {
		return circle(circle, -1f, -1f);
	}

	public IEntity circle(Circle circle, float startAngle, float endAngle) {
		PlaceActions.circle(this, circle, startAngle, endAngle);
		return this;
	}

	public IEntity ellipse(Ellipse e) {
		return ellipse(e, -1f, -1f);
	}

	public Entity ellipse(Ellipse e, float startAngle, float endAngle) {
		PlaceActions.ellipse(this, e, startAngle, endAngle);
		return this;
	}

	public IEntity line(Line e) {
		PlaceActions.line(this, e);
		return this;
	}

	public IEntity rotateAround(XY point, float angle) {
		PlaceActions.rotateAround(this, point, angle);
		return this;
	}

	public IEntity rotateAroundDistance(XY point, float angle, float distance) {
		PlaceActions.rotateAroundDistance(this, point, angle, distance);
		return this;
	}

	public ActionBindData getActionData() {
		return new ActionBindData((ActionBind) this);
	}

	@Override
	public void setRotation(float rotate) {
		super.setRotation(rotate);
		if (_childrens != null) {
			for (int i = _childrens.size - 1; i > -1; i--) {
				IEntity entity = _childrens.get(i);
				if (entity != null && entity.isFollowRotation() && entity != this) {
					entity.setRotation(rotate);
				}
			}
		}
		onRotation();
	}

	@Override
	public IEntity buildToScreen() {
		if (_sprites != null) {
			_sprites.add(this);
			return this;
		}
		getScreen().add(this);
		return this;
	}

	@Override
	public IEntity removeFromScreen() {
		if (_sprites != null) {
			_sprites.remove(this);
			return this;
		}
		getScreen().remove(this);
		return this;
	}

	@Override
	public IEntity removeParent() {
		IEntity e = this.getSuper();
		if (e != null) {
			e.removeChild(this);
			setParent(null);
		}
		return this;
	}

	@Override
	public Screen getScreen() {
		if (this._sprites == null) {
			return LSystem.getProcess().getScreen();
		}
		return this._sprites.getScreen() == null ? LSystem.getProcess().getScreen() : this._sprites.getScreen();
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
			return getScreenX() - getY();
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
	public RectBox getBoundingRect() {
		return getCollisionBox();
	}

	@Override
	public boolean containsPoint(float x, float y) {
		return inContains(x, y, 1, 1);
	}

	@Override
	public boolean contains(CollisionObject o) {
		return getCollisionBox().contains(o.getRectBox());
	}

	@Override
	public boolean intersects(CollisionObject o) {
		return getCollisionBox().intersects(o.getRectBox());
	}

	@Override
	public boolean intersects(Shape s) {
		return getCollisionBox().intersects(s);
	}

	@Override
	public boolean contains(Shape shape) {
		return getCollisionBox().contains(shape);
	}

	public boolean collided(Shape shape) {
		return getCollisionBox().collided(shape);
	}

	public Gravity getGravity() {
		return new Gravity("IEntity", this);
	}

	public IEntity softCenterOn(float x, float y) {
		final RectBox rect = getSprites() == null ? LSystem.viewSize.getRect() : getSprites().getBoundingBox();
		final IEntity sprite = this.getSuper();
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
	public boolean showShadow() {
		return _createShadow;
	}

	public Entity createShadow(boolean s) {
		this._createShadow = s;
		return this;
	}

	public boolean isDebugDraw() {
		return _debugDraw;
	}

	public ISprite setDebugDraw(boolean debug) {
		this._debugDraw = debug;
		return this;
	}

	public ISprite debug() {
		return setDebugDraw(true);
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

	public ResizeListener<IEntity> getResizeListener() {
		return _resizeListener;
	}

	public IEntity setResizeListener(ResizeListener<IEntity> listener) {
		this._resizeListener = listener;
		return this;
	}

	@Override
	public void onResize() {
		if (_resizeListener != null) {
			_resizeListener.onResize(this);
		}
		if (_childrens != null) {
			for (int i = this._childrens.size - 1; i >= 0; i--) {
				final IEntity child = this._childrens.get(i);
				if (child != null && child != this) {
					child.onResize();
				}
			}
		}
	}

	@Override
	public IEntity setFollowRotation(boolean r) {
		this._followRotation = r;
		return this;
	}

	@Override
	public IEntity setFollowScale(boolean s) {
		this._followScale = s;
		return this;
	}

	@Override
	public boolean isFollowRotation() {
		return _followRotation;
	}

	@Override
	public boolean isFollowScale() {
		return _followScale;
	}

	@Override
	public boolean isFollowColor() {
		return _followColor;
	}

	@Override
	public float getFixedWidthOffset() {
		return _fixedWidthOffset;
	}

	@Override
	public ISprite setFixedWidthOffset(float fixedWidthOffset) {
		this._fixedWidthOffset = fixedWidthOffset;
		return this;
	}

	@Override
	public float getFixedHeightOffset() {
		return _fixedHeightOffset;
	}

	@Override
	public ISprite setFixedHeightOffset(float fixedHeightOffset) {
		this._fixedHeightOffset = fixedHeightOffset;
		return this;
	}

	public boolean isRepaintAutoOffset() {
		return _repaintAutoOffset;
	}

	public IEntity setRepaintAutoOffset(boolean autoOffset) {
		this._repaintAutoOffset = autoOffset;
		return this;
	}

	public boolean isMirror() {
		return this._flipX;
	}

	public IEntity setMirror(boolean mirror) {
		return setFlipX(mirror);
	}

	/**
	 * 判定当前Entity是否存在(后辈)下级类
	 * 
	 * @param actor
	 * @return
	 */
	@Override
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

	/**
	 * 判定当前Entity是否存在(前辈)上级类
	 * 
	 * @param actor
	 * @return
	 */
	@Override
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
	public TArray<IEntity> getChildren() {
		return _childrens;
	}

	/**
	 * 返回当前Entity所有前辈(上级及其更上级)类
	 * 
	 * @return
	 */
	public TArray<IEntity> getAscendantChildren() {
		final TArray<IEntity> result = new TArray<IEntity>();
		IEntity current = this.getParent();
		if (current == null) {
			return result;
		}
		for (; current != null;) {
			result.add(current);
			current = current.getParent();
		}
		return result.reverse();
	}

	/**
	 * 返回当前Entity所有后背(下级及其更下级)类
	 * 
	 * @return
	 */
	public TArray<IEntity> getDescendantChildren() {
		TArray<IEntity> result = new TArray<IEntity>();
		if (_childrens == null || _childrens.size == 0) {
			return result;
		}
		TArray<IEntity> queue = _childrens.cpy();
		for (; queue.size > 0;) {
			IEntity current = queue.pop();
			if (current != null) {
				queue = queue.concat(current.getChildren());
				result = result.concat(current.getChildren());
			}
		}
		return result;
	}

	public Vector2f getTouchOffset() {
		return _touchOffset;
	}

	public Entity setTouchOffset(Vector2f offset) {
		if (offset != null) {
			this._touchOffset = offset;
		}
		return this;
	}

	public Entity setTouchOffset(float x, float y) {
		return setTouchOffset(Vector2f.at(x, y));
	}

	public boolean hasChild(IEntity e) {
		if (_childrens == null) {
			return false;
		}
		return this._childrens.contains(e);
	}

	public boolean hasActions() {
		return ActionControl.get().containsKey(this);
	}

	public Entity clearActions() {
		ActionControl.get().removeAllActions(this);
		return this;
	}

	@Override
	public IEntity show() {
		setVisible(true);
		return this;
	}

	@Override
	public IEntity hide() {
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

	public boolean isClosed() {
		return isDisposed();
	}

	@Override
	public IEntity setFollowColor(boolean c) {
		this._followColor = c;
		return this;
	}

	@Override
	public boolean autoXYSort() {
		return _xySort;
	}

	public Entity setAutoXYSort(boolean a) {
		this._xySort = a;
		return this;
	}

	public Shape getShape() {
		return getShape(ShapeNodeType.Rectangle);
	}

	public Shape getShape(ShapeNodeType nodeType) {
		final RectBox rect = getCollisionBox();
		if (_oldShapeRectX == rect.x && _oldShapeRectY == rect.y && _oldShapeRectW == rect.width
				&& _oldShapeRectH == rect.height && _oldNodeType == nodeType && _otherShape != null) {
			return _otherShape;
		}
		switch (nodeType) {
		case Rectangle:
		default:
			_otherShape = rect;
			break;
		case Circle:
			_otherShape = Circle.rect(rect.x, rect.y, rect.width, rect.height);
			break;
		case Ellipse:
			_otherShape = Ellipse.rect(rect.x, rect.y, rect.width, rect.height);
			break;
		case Polygon:
			if (_image != null) {
				Image img = _image.getImage();
				if (img != null) {
					_otherShape = CollisionMask.makePolygon(img).setLocation(rect.x, rect.y);
				} else if (!_image.isImageCanvas()) {
					_otherShape = CollisionMask.makePolygon(_image.getSource()).setLocation(rect.x, rect.y);
				} else {
					int[] pixels = _image.getPixels();
					if (pixels != null) {
						_otherShape = CollisionMask.makePolygon(pixels, _image.getWidth(), _image.getHeight())
								.setLocation(rect.x, rect.y);
					} else {
						_otherShape = Polygon.rect(rect.x, rect.y, rect.width, rect.height);
					}
				}
			} else {
				_otherShape = Polygon.rect(rect.x, rect.y, rect.width, rect.height);
			}
			break;
		case Line:
			_otherShape = Line.rect(rect.x, rect.y, rect.width, rect.height);
			break;
		case Triangle:
			_otherShape = Triangle2f.at(rect.x, rect.y, rect.width, rect.height);
			break;
		case Point:
			_otherShape = new Point(rect.x, rect.y);
			break;
		}
		_oldShapeRectX = rect.x;
		_oldShapeRectY = rect.y;
		_oldShapeRectW = rect.width;
		_oldShapeRectH = rect.height;
		_oldNodeType = nodeType;
		return _otherShape;
	}

	/**
	 * 以指定布局移动指定精灵对象
	 * 
	 * @param align
	 * @param spr
	 * @return
	 */
	public Entity move(LayoutAlign align, ISprite spr) {
		if (spr instanceof LObject) {
			moveOn(align, (LObject<?>) spr);
		}
		return this;
	}

	/**
	 * 以指定布局移动指定精灵对象
	 * 
	 * @param align
	 * @param spr
	 * @param x
	 * @param y
	 * @return
	 */
	public Entity move(LayoutAlign align, ISprite spr, float x, float y) {
		if (spr instanceof LObject) {
			moveOn(align, (LObject<?>) spr, x, y);
		}
		return this;
	}

	@Override
	public IEntity with(TComponent<IEntity> c) {
		return addComponent(c);
	}

	@Override
	public TComponent<IEntity> findComponent(String name) {
		if (this._components == null) {
			return null;
		}
		if (StringUtils.isNullOrEmpty(name)) {
			return null;
		}
		for (int i = this._components.size - 1; i >= 0; i--) {
			final TComponent<IEntity> finder = this._components.get(i);
			if (finder != null && finder.getName().equals(name)) {
				return finder;
			}
		}
		return null;
	}

	@Override
	public IEntity addComponent(TComponent<IEntity> c) {
		if (_components == null) {
			allocateComponents();
		}
		if (c != null && !_components.contains(c)) {
			_components.add(c);
			c.onAttached(this);
			c.setCurrent(this);
		}
		return null;
	}

	@Override
	public boolean removeComponentName(String typeName) {
		if (_components == null) {
			allocateComponents();
		}
		int count = 0;
		if (typeName != null) {
			final int size = this._components.size;
			for (int i = size - 1; i >= 0; i--) {
				final TComponent<IEntity> removed = this._components.get(i);
				if (removed != null && typeName.equals(removed._name)) {
					removed.onDetached(this);
					removed.setCurrent(null);
					_components.remove(removed);
					count++;
				}
			}
		}
		return count > 0;
	}

	@Override
	public boolean removeComponentType(Class<? extends TComponent<IEntity>> typeClazz) {
		if (_components == null) {
			allocateComponents();
		}
		int count = 0;
		if (typeClazz != null) {
			final int size = this._components.size;
			for (int i = size - 1; i >= 0; i--) {
				final TComponent<IEntity> removed = this._components.get(i);
				if (removed != null && removed.getClass().equals(typeClazz)) {
					removed.onDetached(this);
					removed.setCurrent(null);
					_components.remove(removed);
					count++;
				}
			}
		}
		return count > 0;
	}

	@Override
	public boolean removeComponent(TComponent<IEntity> c) {
		if (_components == null) {
			allocateComponents();
		}
		if (c != null) {
			_components.remove(c);
			c.onDetached(this);
			c.setCurrent(null);
		}
		return false;
	}

	@Override
	public IEntity removeComponents() {
		if (this._components == null) {
			return this;
		}
		final int size = this._components.size;
		for (int i = size - 1; i >= 0; i--) {
			final TComponent<IEntity> removed = this._components.get(i);
			if (removed != null) {
				removed.onDetached(this);
				removed.setCurrent(null);
			}
		}
		this._components.clear();
		return this;
	}

	@Override
	public boolean hasComponent() {
		return this._components != null && this._components.size > 0;
	}

	@Override
	public int getComponentCount() {
		if (_components == null) {
			allocateComponents();
		}
		return _components.size;
	}

	@Override
	public boolean isComponentIgnoreUpdate() {
		return _childrenIgnoreUpdate;
	}

	@Override
	public IEntity setComponentIgnoreUpdate(boolean c) {
		this._childrenIgnoreUpdate = c;
		return this;
	}

	@Override
	public TArray<TComponent<IEntity>> getComponents() {
		if (_components == null) {
			allocateComponents();
		}
		return new TArray<TComponent<IEntity>>(_components);
	}

	@Override
	public IEntity dispose(LRelease r) {
		_disposed = r;
		return this;
	}

	@Override
	public void close() {
		if (_disposed != null) {
			_disposed.close();
		}
		if (!isDisposed()) {
			if (_image != null) {
				_image.close();
				_image = null;
			}
		}
		_stopUpdate = false;
		_ignoreUpdate = false;
		_childrenIgnoreUpdate = false;
		_componentsIgnoreUpdate = false;
		_loopAction = null;
		_resizeListener = null;
		_otherShape = null;
		_oldNodeType = null;
		_disposed = null;
		setState(State.DISPOSED);
		removeComponents();
		removeChildren();
		removeActionEvents(this);
	}

}

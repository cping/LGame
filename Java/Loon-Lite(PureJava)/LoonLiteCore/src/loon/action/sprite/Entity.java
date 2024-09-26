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
import loon.action.ActionBind;
import loon.action.ActionControl;
import loon.action.ActionListener;
import loon.action.PlaceActions;
import loon.action.sprite.Sprites.Created;
import loon.canvas.LColor;
import loon.component.layout.LayoutAlign;
import loon.events.DrawListener;
import loon.events.DrawLoop;
import loon.events.EventAction;
import loon.events.ResizeListener;
import loon.geom.Affine2f;
import loon.geom.Circle;
import loon.geom.Dimension;
import loon.geom.Ellipse;
import loon.geom.Line;
import loon.geom.RectBox;
import loon.geom.Triangle2f;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.LayerSorter;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 一个精灵类的具体实现,可以用来充当ECS模式中的实体对象用类(当然,Loon中并不强制要求使用ECS模式进行开发)
 */
public class Entity extends SpriteBase<IEntity> implements IEntity {

	private final static LayerSorter<IEntity> entitySorter = new LayerSorter<IEntity>(false);

	public final static Entity load(String path) {
		return new Entity(path);
	}

	public final static Entity load(LTexture tex) {
		return new Entity(tex);
	}

	public final static Entity load(String path, float x, float y) {
		return new Entity(path, x, y);
	}

	public final static Entity load(String path, float x, float y, float w, float h) {
		return new Entity(path, x, y, w, h);
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

	protected boolean _deform = true;

	protected boolean _childrenSortPending = false;
	protected boolean _followRotation = true;
	protected boolean _followScale = true;
	protected boolean _followColor = true;

	protected int _idxTag = Integer.MIN_VALUE;

	protected boolean _repaintDraw = false;

	// 是否传递本身偏移设定数据到自绘部分
	protected boolean _repaintAutoOffset = false;

	protected RectBox _shear;
	protected LColor _baseColor = new LColor(LColor.white);

	protected float _rotationCenterX = -1;
	protected float _rotationCenterY = -1;

	protected float _skewX = 0;
	protected float _skewY = 0;

	protected float _skewCenterX = -1;
	protected float _skewCenterY = -1;

	private boolean _stopUpdate = false;

	private DrawListener<Entity> _drawListener;

	protected float _width, _height;

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

	public Entity(final String path, final float x, final float y, final float w, final float h) {
		this(LSystem.loadTexture(path), x, y, w, h);
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
	public IEntity setChildrenVisible(final boolean v) {
		this._childrenVisible = v;
		return this;
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
	public void sort() {
		if (_childrens == null) {
			return;
		}
		entitySorter.sort(_childrens);
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
		super.removeChilds();
		return this;
	}

	@Override
	public IEntity removeChildIndexTag(final int idx) {
		if (idx < 0) {
			return null;
		}
		if (this._childrens == null) {
			return null;
		}
		for (int i = this._childrens.size - 1; i >= 0; i--) {
			IEntity e = this._childrens.get(i);
			if (e != null && e.getIndexTag() == idx) {
				final IEntity removed = this._childrens.removeIndex(i);
				final boolean exist = (removed != null);
				if (exist) {
					removed.setState(State.REMOVED);
					removed.onDetached();
				}
				// 删除精灵同时，删除缓动动画
				if (exist && (removed instanceof ActionBind)) {
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
		if (!_visible || _objectAlpha < 0.01f) {
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
			DrawListener<Entity> drawing = this._drawListener;
			if (drawing != null) {
				if (_repaintAutoOffset) {
					drawing.draw(g, nx, ny);
				} else {
					drawing.draw(g, offsetX, offsetY);
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
		onBaseUpdate(elapsedTime);
	}

	@Override
	public IEntity setComponentIgnoreUpdate(boolean c) {
		this._componentsIgnoreUpdate = c;
		return this;
	}

	@Override
	public IEntity with(TComponent<IEntity> c) {
		return addComponent(c);
	}

	@Override
	public IEntity removeComponents() {
		clearComponentAll();
		return this;
	}

	@Override
	public void onCollision(ISprite coll, int dir) {
		if (_collSpriteListener != null) {
			_collSpriteListener.onCollideUpdate(coll, dir);
		}
	}

	@Override
	public Entity triggerCollision(SpriteCollisionListener sc) {
		this._collSpriteListener = sc;
		return this;
	}

	public Entity loop(EventAction la) {
		this._loopAction = la;
		return this;
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
			DrawListener<Entity> drawing = this._drawListener;
			if (drawing != null) {
				drawing.update(elapsedTime);
			}
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

	@Override
	public float getAniWidth() {
		return this._width;
	}

	@Override
	public float getAniHeight() {
		return this._height;
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

	public DrawListener<Entity> getDrawListener() {
		return _drawListener;
	}

	public Entity setDrawListener(DrawListener<Entity> drawListener) {
		this._drawListener = drawListener;
		return this;
	}

	public DrawLoop<Entity> getDrawable() {
		if (_drawListener != null && _drawListener instanceof DrawLoop) {
			return ((DrawLoop<Entity>) _drawListener);
		}
		return null;
	}

	public DrawLoop<Entity> drawable(DrawLoop.Drawable draw) {
		DrawLoop<Entity> loop = null;
		if (_drawListener != null && _drawListener instanceof DrawLoop) {
			loop = getDrawable().onDrawable(draw);
		} else {
			setDrawListener(loop = new DrawLoop<Entity>(this, draw));
		}
		return loop;
	}

	@Override
	public void toString(final StrBuilder s) {
		s.append(super.toString());
		if ((this._childrens != null) && (this._childrens.size > 0)) {
			s.append(LSystem.LS);
			s.append(LSystem.BRACKET_START);
			final TArray<IEntity> entities = this._childrens;
			for (int i = 0; i < entities.size; i++) {
				entities.get(i).toString(s);
				if (i < (entities.size - 1)) {
					s.append(LSystem.COMMA);
				}
			}
			s.append(LSystem.BRACKET_END);
		}
	}

	@Override
	public String toString() {
		final StrBuilder sbr = new StrBuilder();
		this.toString(sbr);
		return sbr.toString();
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

	public IEntity setOffsetX(float offsetX) {
		this._offset.setX(offsetX);
		return this;
	}

	public IEntity setOffsetY(float offsetY) {
		this._offset.setY(offsetY);
		return this;
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
	public IEntity setSprites(Sprites ss) {
		setSpritesObject(ss);
		return this;
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

	public Entity createShadow(boolean s) {
		this._createShadow = s;
		return this;
	}

	public IEntity setDebugDraw(boolean debug) {
		this._debugDraw = debug;
		return this;
	}

	public IEntity debug() {
		return setDebugDraw(true);
	}

	public IEntity setDebugDrawColor(LColor debugColor) {
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
	public IEntity setFixedWidthOffset(float fixedWidthOffset) {
		this._fixedWidthOffset = fixedWidthOffset;
		return this;
	}

	@Override
	public float getFixedHeightOffset() {
		return _fixedHeightOffset;
	}

	@Override
	public IEntity setFixedHeightOffset(float fixedHeightOffset) {
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

	@Override
	public IEntity setFollowColor(boolean c) {
		this._followColor = c;
		return this;
	}

	public Entity setAutoXYSort(boolean a) {
		this._xySort = a;
		return this;
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
		_collSpriteListener = null;
		_otherShape = null;
		_oldNodeType = null;
		_drawListener = null;
		_disposed = null;
		setState(State.DISPOSED);
		removeComponents();
		removeChildren();
		removeActionEvents(this);
	}

}

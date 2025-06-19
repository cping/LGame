/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.PlayerUtils;
import loon.Screen;
import loon.Director.Origin;
import loon.action.ActionBind;
import loon.action.ActionBindData;
import loon.action.ActionControl;
import loon.action.ActionTween;
import loon.action.collision.CollisionHelper;
import loon.action.collision.CollisionMask;
import loon.action.collision.CollisionObject;
import loon.action.collision.Gravity;
import loon.action.map.Field2D;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.events.EventAction;
import loon.events.QueryEvent;
import loon.events.ResizeListener;
import loon.events.SysKey;
import loon.events.SysTouch;
import loon.geom.BoxSize;
import loon.geom.Circle;
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
import loon.utils.HelperUtils;
import loon.utils.IArray;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * Sprite包下最基础类,所有Sprite对象由此类衍生,此类继承自LObject
 */
@SuppressWarnings("unchecked")
public abstract class SpriteBase<T extends ISprite> extends LObject<T> implements CollisionObject, IArray, BoxSize {

	static final int CHILDREN_CAPACITY_DEFAULT = 8;

	protected Origin _origin = Origin.CENTER;

	protected float _oldShapeRectX, _oldShapeRectY, _oldShapeRectW, _oldShapeRectH;
	protected float _fixedWidthOffset = 0f;
	protected float _fixedHeightOffset = 0f;
	protected float _scaleX = 1f;
	protected float _scaleY = 1f;
	protected float _scaleCenterX = -1;
	protected float _scaleCenterY = -1;

	protected boolean _childrenVisible = true;
	protected boolean _childrenIgnoreUpdate = false;
	protected boolean _flipX = false, _flipY = false;
	protected boolean _visible = true;
	protected boolean _debugDraw = false;
	protected boolean _ignoreUpdate = false;
	protected boolean _createShadow = false;
	protected boolean _xySort = false;

	protected boolean _componentsIgnoreUpdate = false;
	protected EventAction _loopAction = null;
	protected Vector2f _touchOffset = new Vector2f();
	protected Vector2f _touchPoint = new Vector2f();
	protected Vector2f _offset = new Vector2f();
	protected LTexture _image = null;
	protected LColor _debugDrawColor = LColor.red;

	protected Field2D _arrayMap;

	protected Shape _otherShape = null;

	protected ShapeNodeType _oldNodeType = null;

	protected ResizeListener<T> _resizeListener = null;
	protected SpriteCollisionListener _collSpriteListener = null;

	protected Sprites _sprites = null;

	protected TArray<T> _childrens = null;

	protected TArray<TComponent<T>> _components = null;

	public abstract float getAniWidth();

	public abstract float getAniHeight();

	protected void allocateChildren() {
		synchronized (SpriteBase.class) {
			this._childrens = new TArray<T>(CHILDREN_CAPACITY_DEFAULT);
		}
	}

	protected void allocateComponents() {
		synchronized (SpriteBase.class) {
			this._components = new TArray<TComponent<T>>(CHILDREN_CAPACITY_DEFAULT);
		}
	}

	protected void onBaseUpdate(long elapsedTime) {
		if ((this._components != null) && !this._componentsIgnoreUpdate) {
			final TArray<TComponent<T>> comps = this._components;
			final int entityCount = comps.size;
			for (int i = 0; i < entityCount; i++) {
				final TComponent<T> c = comps.get(i);
				if (c != null && !c._paused.get()) {
					c.onUpdate(elapsedTime);
				}
			}
		}
		if ((this._childrens != null) && !this._childrenIgnoreUpdate) {
			final TArray<T> entities = this._childrens;
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

	protected void onUpdate(long elapsedTime) {
	}

	public TComponent<T> findComponent(String name) {
		if (this._components == null) {
			return null;
		}
		if (StringUtils.isNullOrEmpty(name)) {
			return null;
		}
		for (int i = this._components.size - 1; i >= 0; i--) {
			final TComponent<T> finder = this._components.get(i);
			if (finder != null && finder.getName().equals(name)) {
				return finder;
			}
		}
		return null;
	}

	public TComponent<T> findComponent(Class<? extends TComponent<T>> typeClazz) {
		if (this._components == null) {
			return null;
		}
		if (typeClazz == null) {
			return null;
		}
		for (int i = this._components.size - 1; i >= 0; i--) {
			final TComponent<T> finder = this._components.get(i);
			if (finder != null && finder.getClass().equals(typeClazz)) {
				return finder;
			}
		}
		return null;
	}

	public T addComponent(TComponent<T> c) {
		if (_components == null) {
			allocateComponents();
		}
		if (c != null && !_components.contains(c)) {
			_components.add(c);
			T bind = (T) this;
			c.onAttached(bind);
			c.setCurrent(bind);
		}
		return null;
	}

	public void clearComponentAll() {
		if (this._components == null) {
			return;
		}
		final int size = this._components.size;
		for (int i = size - 1; i >= 0; i--) {
			final TComponent<T> removed = this._components.get(i);
			if (removed != null) {
				removed.onDetached((T) this);
				removed.setCurrent(null);
			}
		}
		this._components.clear();
	}

	public boolean removeComponentName(String typeName) {
		if (_components == null) {
			allocateComponents();
		}
		int count = 0;
		if (typeName != null) {
			final int size = this._components.size;
			for (int i = size - 1; i >= 0; i--) {
				final TComponent<T> removed = this._components.get(i);
				if (removed != null && typeName.equals(removed._name)) {
					removed.onDetached((T) this);
					removed.setCurrent(null);
					_components.remove(removed);
					count++;
				}
			}
		}
		return count > 0;
	}

	public boolean removeComponentType(Class<? extends TComponent<IEntity>> typeClazz) {
		if (_components == null) {
			allocateComponents();
		}
		int count = 0;
		if (typeClazz != null) {
			final int size = this._components.size;
			for (int i = size - 1; i >= 0; i--) {
				final TComponent<T> removed = this._components.get(i);
				if (removed != null && removed.getClass().equals(typeClazz)) {
					removed.onDetached((T) this);
					removed.setCurrent(null);
					_components.remove(removed);
					count++;
				}
			}
		}
		return count > 0;
	}

	public boolean removeComponent(TComponent<T> c) {
		if (_components == null) {
			allocateComponents();
		}
		if (c != null) {
			boolean result = _components.remove(c);
			if (result) {
				c.onDetached((T) this);
				c.setCurrent(null);
			}
			return result;
		}
		return false;
	}

	public boolean hasComponent() {
		return this._components != null && this._components.size > 0;
	}

	public int getComponentCount() {
		if (_components == null) {
			allocateComponents();
		}
		return _components.size;
	}

	public boolean isComponentIgnoreUpdate() {
		return _componentsIgnoreUpdate;
	}

	public TArray<TComponent<T>> getComponents() {
		if (_components == null) {
			allocateComponents();
		}
		return new TArray<TComponent<T>>(_components);
	}

	public boolean removeChild(final T e) {
		if (e == null) {
			return true;
		}
		if (this._childrens == null) {
			return false;
		}
		boolean removed = this._childrens.remove(e);
		if (removed) {
			e.setState(State.REMOVED);
			if (e instanceof IEntity) {
				((IEntity) e).onDetached();
			}
		}
		// 删除精灵同时，删除缓动动画
		if (removed && e instanceof ActionBind) {
			removeActionEvents((ActionBind) e);
		}
		return removed;
	}

	public boolean removeChild(final int idx) {
		if (idx < 0) {
			return false;
		}
		if (this._childrens == null) {
			return false;
		}
		for (int i = this._childrens.size - 1; i >= 0; i--) {
			if (i == idx) {
				final T removed = this._childrens.removeIndex(i);
				final boolean exist = (removed != null);
				if (exist) {
					removed.setState(State.REMOVED);
					if (removed instanceof IEntity) {
						((IEntity) removed).onDetached();
					}
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

	public int removeWhere(QueryEvent<T> query) {
		if (this._childrens == null) {
			return 0;
		}
		int count = 0;
		final int size = this._childrens.size;
		final TArray<T> childs = this._childrens;
		for (int i = size - 1; i >= 0; i--) {
			final T o = childs.get(i);
			boolean exist = (o != null);
			if (exist && query.hit(o)) {
				final T result = childs.removeIndex(i);
				if (result != null) {
					result.setState(State.REMOVED);
					if (result instanceof IEntity) {
						((IEntity) result).onDetached();
					}
					if (exist && result instanceof ActionBind) {
						removeActionEvents((ActionBind) result);
					}
					count++;
				}
			}
		}
		this._childrens = childs;
		return count;
	}

	public TArray<T> findWhere(QueryEvent<T> query) {
		if (this._childrens == null) {
			return null;
		}
		final TArray<T> result = new TArray<T>();
		final int size = this._childrens.size;
		final TArray<T> childs = this._childrens;
		for (int i = size - 1; i >= 0; i--) {
			final T o = childs.get(i);
			if ((o != null) && query.hit(o)) {
				result.add(o);
			}
		}
		return result;
	}

	public void removeChilds() {
		if (this._childrens == null) {
			return;
		}
		final int size = this._childrens.size;
		final TArray<T> childs = this._childrens;
		for (int i = size - 1; i >= 0; i--) {
			final T removed = childs.get(i);
			boolean exist = (removed != null);
			if (exist) {
				removed.setState(State.REMOVED);
				if (removed instanceof IEntity) {
					((IEntity) removed).onDetached();
				}
			}
			// 删除精灵同时，删除缓动动画
			if (exist && removed instanceof ActionBind) {
				removeActionEvents((ActionBind) removed);
			}
		}
		this._childrens.clear();
		return;
	}

	@Override
	public void clear() {
		if (_childrens != null) {
			removeChilds();
		}
	}

	public T getChildByIndex(int idx) {
		if (this._childrens == null || (idx < 0 || idx > this._childrens.size - 1)) {
			return null;
		}
		return this._childrens.get(idx);
	}

	public T getFirstChild() {
		if (this._childrens == null) {
			return null;
		}
		return this._childrens.get(0);
	}

	public T getLastChild() {
		if (this._childrens == null) {
			return null;
		}
		return this._childrens.get(this._childrens.size - 1);
	}

	public abstract void sort();

	public boolean isEqualLayer(ActionBind act) {
		if (act == null) {
			return false;
		}
		return act.getLayer() == this.getLayer();
	}

	public boolean isNotEqualLayer(ActionBind act) {
		return !isEqualLayer(act);
	}

	@Override
	public boolean isEmpty() {
		return getChildCount() == 0;
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	@Override
	public int size() {
		return getChildCount();
	}

	public int getChildCount() {
		if (this._childrens == null) {
			return 0;
		}
		return this._childrens.size;
	}

	public LColor getDebugDrawColor() {
		return _debugDrawColor.cpy();
	}

	public Vector2f getOffset() {
		return _offset;
	}

	public Vector2f getTouchOffset() {
		return _touchOffset;
	}

	public boolean isPaused() {
		return _ignoreUpdate;
	}

	public boolean isMirror() {
		return this._flipX;
	}

	public boolean hasActions() {
		return ActionControl.get().containsKey(this);
	}

	public void clearActions() {
		ActionControl.get().removeAllActions(this);
	}

	public void setTouchOffset(Vector2f offset) {
		if (offset != null) {
			this._touchOffset = offset;
		}
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
		T parent = getParent();
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
			pointResult.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(getRotation()).addSelf(getAniWidth(),
					getAniHeight());
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
		pointResult.addSelf(_touchOffset);
		return pointResult;
	}

	public Vector2f getUITouchXY() {
		float newX = 0f;
		float newY = 0f;
		if (getRotation() == 0) {
			if (_objectSuper == null) {
				newX = toPixelScaleX(getInternalTouchX() - getX());
				newY = toPixelScaleY(getInternalTouchY() - getY());
			} else {
				newX = toPixelScaleX(getInternalTouchX() - _objectSuper.getX() - getX());
				newY = toPixelScaleY(getInternalTouchY() - _objectSuper.getY() - getY());
			}
			_touchPoint.set(newX, newY).addSelf(_touchOffset);
		} else {
			newX = getInternalTouchX();
			newY = getInternalTouchY();
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

	public boolean autoXYSort() {
		return _xySort;
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
	public static <T> int centerX(LObject<T> sprite, int x) {
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
	public static <T> int centerY(LObject<T> sprite, int y) {
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

	public boolean hasChild(T e) {
		if (_childrens == null) {
			return false;
		}
		return this._childrens.contains(e);
	}

	public ActionBindData getActionData() {
		return new ActionBindData((ActionBind) this);
	}

	public Origin getOrigin() {
		return _origin;
	}

	@Override
	public boolean isRotated() {
		return this._objectRotation != 0f;
	}

	public boolean isDebugDraw() {
		return _debugDraw;
	}

	public boolean isChildrenVisible() {
		return this._childrenVisible;
	}

	@Override
	public boolean isContainer() {
		return getChildCount() > 0;
	}

	public TArray<T> getChildren() {
		return _childrens;
	}

	public boolean showShadow() {
		return _createShadow;
	}

	@Override
	public void setScale(float sx, float sy) {
		this._scaleX = sx;
		this._scaleY = sy;
	}

	public void setSizeScale(float w, float h) {
		if (w <= 0 && h <= 0) {
			return;
		}
		if (w > 0) {
			_scaleX = w / this.getWidth();
		} else if (h > 0) {
			_scaleY = h / this.getHeight();
		}
	}

	/**
	 * 获得精灵的中间位置
	 * 
	 * @return
	 */
	public PointF getMiddlePoint(T p) {
		return new PointF(p.getX() + p.getWidth() / 2f, p.getY() + p.getHeight() / 2f);
	}

	public PointF getMiddlePoint() {
		return new PointF(getScreenScalePixelY() + getWidth() / 2f, getScreenScalePixelY() + getHeight() / 2f);
	}

	/**
	 * 获得两个精灵的中间距离
	 * 
	 * @param second
	 * @return
	 */
	public float getDistance(T second) {
		return this.getMiddlePoint().distanceTo(getMiddlePoint(second));
	}

	public float getOffsetX() {
		return _offset.x;
	}

	public float getOffsetY() {
		return _offset.y;
	}

	public boolean isCollision(ActionBind bind) {
		if (bind == null) {
			return false;
		}
		RectBox src = getCollisionArea();
		return src.intersects(bind.getRectBox()) || src.contains(bind.getRectBox());
	}

	public boolean isCollision(Shape shape) {
		if (shape == null) {
			return false;
		}
		RectBox src = getCollisionArea();
		return src.intersects(shape) || src.contains(shape);
	}

	public boolean isCollision(T o) {
		if (o == null) {
			return false;
		}
		RectBox src = getCollisionArea();
		RectBox dst = o.getCollisionBox();
		return src.intersects(dst) || src.contains(dst);
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
	public boolean contains(Shape s) {
		return getCollisionBox().contains(s);
	}

	@Override
	public boolean collided(Shape s) {
		return getCollisionBox().collided(s);
	}

	public boolean collides(ISprite e) {
		if (e == null || !e.isVisible()) {
			return false;
		}
		return intersects(e.getCollisionBox());
	}

	public boolean collidesX(ISprite other) {
		if (other == null || !other.isVisible()) {
			return false;
		}
		RectBox rectSelf = getRectBox();
		RectBox rectDst = getRectBox();
		return CollisionHelper.checkAABBvsAABB(rectSelf.getX(), 0, rectSelf.getWidth(), rectSelf.getHeight(),
				rectDst.getX(), 0, rectDst.getWidth(), rectDst.getHeight());
	}

	public boolean collidesY(ISprite other) {
		if (other == null || !other.isVisible()) {
			return false;
		}
		RectBox rectSelf = getRectBox();
		RectBox rectDst = getRectBox();
		return CollisionHelper.checkAABBvsAABB(0, rectSelf.getY(), rectSelf.getWidth(), rectSelf.getHeight(), 0,
				rectDst.getY(), rectDst.getWidth(), rectDst.getHeight());
	}

	protected float getInternalTouchDX() {
		final Screen screen = getScreen();
		return screen == null ? SysTouch.getDX() : screen.getTouchDX();
	}

	protected float getInternalTouchDY() {
		final Screen screen = getScreen();
		return screen == null ? SysTouch.getDY() : screen.getTouchDY();
	}

	protected float getInternalTouchX() {
		final Screen screen = getScreen();
		return screen == null ? SysTouch.getX() : screen.getTouchX();
	}

	protected float getInternalTouchY() {
		final Screen screen = getScreen();
		return screen == null ? SysTouch.getY() : screen.getTouchY();
	}

	public float getTouchDX() {
		return toPixelScaleX(getInternalTouchDX());
	}

	public float getTouchDY() {
		return toPixelScaleY(getInternalTouchDY());
	}

	public float getTouchX() {
		return toPixelScaleX(getInternalTouchX());
	}

	public float getTouchY() {
		return toPixelScaleY(getInternalTouchY());
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

	public boolean isFlipX() {
		return _flipX;
	}

	public boolean isFlipY() {
		return _flipY;
	}

	public RectBox getInScreenCollisionBox(float offsetX, float offsetY) {
		final Screen screen = getScreen();
		float screenX = 0f;
		float screenY = 0f;
		if (screen != null) {
			screenX = screen.getScalePixelX();
			screenY = screen.getScalePixelY();
		}
		final float newX = getScreenScalePixelX() + screenX;
		final float newY = getScreenScalePixelY() + screenY;
		final float newW = getWidth() - newX;
		final float newH = getHeight() - newY;
		return setRect(MathUtils.getBounds(newX + offsetX, newY + offsetY, newW - offsetX * 2f, newH - offsetY * 2f,
				_objectRotation, _objectRect));
	}

	public boolean containsInScreen(XY xy) {
		return containsInScreen(xy.getX(), xy.getY());
	}

	public boolean containsInScreen(float x, float y) {
		return containsInScreen(x, y, 0f, 0f);
	}

	public boolean containsInScreen(RectBox rect) {
		return containsInScreen(rect.x, rect.y, rect.width, rect.height);
	}

	public boolean containsInScreen(CollisionObject obj) {
		if (obj == null) {
			return false;
		}
		return containsInScreen(obj.getRectBox());
	}

	public boolean containsInScreen(float x, float y, float width, float height) {
		return getInScreenCollisionBox(width, height).contains(x, y, width, height);
	}

	public boolean containsInScreen(ActionBind obj) {
		if (obj == null) {
			return false;
		}
		return containsInScreen(obj.getRectBox());
	}

	public boolean intersectsInScreen(XY xy) {
		return intersectsInScreen(xy.getX(), xy.getY());
	}

	public boolean intersectsInScreen(float x, float y) {
		return intersectsInScreen(x, y, 1f, 1f);
	}

	public boolean intersectsInScreen(RectBox rect) {
		return intersectsInScreen(rect.x, rect.y, rect.width, rect.height);
	}

	public boolean intersectsInScreen(CollisionObject obj) {
		if (obj == null) {
			return false;
		}
		return intersectsInScreen(obj.getRectBox());
	}

	public boolean intersectsInScreen(ActionBind obj) {
		if (obj == null) {
			return false;
		}
		return intersectsInScreen(obj.getRectBox());
	}

	public boolean intersectsInScreen(float x, float y, float width, float height) {
		return getInScreenCollisionBox(width, height).intersects(x, y, width, height);
	}

	public SpriteBase<T> setField2D(Field2D arrayMap) {
		this._arrayMap = arrayMap;
		return this;
	}

	@Override
	public Field2D getField2D() {
		return _arrayMap;
	}

	@Override
	public boolean isBounded() {
		return false;
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

	public RectBox getCollisionBox() {
		return setRect(MathUtils.getBounds(getScreenScalePixelX(), getScreenScalePixelY(), getWidth(), getHeight(),
				_objectRotation, _objectRect));
	}

	/**
	 * 检查是否与指定精灵位置发生了矩形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isRectToRect(ActionBind sprite) {
		return CollisionHelper.isRectToRect(this.getCollisionBox(), sprite.getRectBox());
	}

	/**
	 * 检查是否与指定精灵位置发生了圆形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isCircToCirc(ActionBind sprite) {
		return CollisionHelper.isCircToCirc(this.getCollisionBox(), sprite.getRectBox());
	}

	/**
	 * 检查是否与指定精灵位置发生了方形与圆形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isRectToCirc(ActionBind sprite) {
		return CollisionHelper.isRectToCirc(this.getCollisionBox(), sprite.getRectBox());
	}

	public float getScaleCenterX() {
		return this._scaleCenterX;
	}

	public float getScaleCenterY() {
		return this._scaleCenterY;
	}

	public void setScaleCenterX(final float sx) {
		this._scaleCenterX = sx;
	}

	public void setScaleCenterY(final float sy) {
		this._scaleCenterY = sy;
	}

	public void setScaleCenter(final float sx, final float sy) {
		this._scaleCenterX = sx;
		this._scaleCenterY = sy;
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

	public void placeToCenter(ActionBind ab) {
		ab.setLocation(getScreenScalePixelX() + (getWidth() - ab.getWidth()) / 2f,
				getScreenScalePixelY() + (getHeight() - ab.getHeight()) / 2f);
	}

	public void placeToCenterX(ActionBind ab, float x) {
		ab.setLocation(x, getScreenScalePixelY() + (getHeight() - ab.getHeight()) / 2f);
	}

	public void placeToCenterY(ActionBind ab, float y) {
		ab.setLocation(getScreenScalePixelX() + (getWidth() - ab.getWidth()) / 2f, y);
	}

	public SpriteBase<T> setCustomShape(Shape s) {
		this._otherShape = s;
		return this;
	}

	public Shape getCustomShape() {
		return this._otherShape;
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

	public void clearImage() {
		if (_image != null) {
			_image.close();
			_image = null;
		}
	}

	public LTexture getBitmap() {
		return this._image;
	}

	public void onResize() {
		if (_resizeListener != null) {
			_resizeListener.onResize((T) this);
		}
		if (_childrens != null) {
			for (int i = this._childrens.size - 1; i >= 0; i--) {
				final T child = this._childrens.get(i);
				if (child != null && child != this) {
					child.onResize();
				}
			}
		}
	}

	public T getParent(final QueryEvent<T> test) {
		T p = getParent();
		while (p != null && !test.hit(p)) {
			p = (T) p.getParent();
		}
		return p;
	}

	public T getParentBefore(final QueryEvent<T> test) {
		T p = getParent();
		T prev = null;
		while (p != null && !test.hit(p)) {
			prev = p;
			p = (T) prev.getParent();
		}
		return prev;
	}

	public float measureWidth() {
		if (_childrens == null) {
			return 0f;
		}
		float max = 0;
		for (int i = this._childrens.size - 1; i > -1; i--) {
			T spr = _childrens.get(i);
			if (spr.isVisible()) {
				max = MathUtils.max(spr.getScaleX() + spr.getWidth(), max);
			}
		}
		return max;
	}

	public float measureHeight() {
		if (_childrens == null) {
			return 0f;
		}
		float max = 0;
		for (int i = this._childrens.size - 1; i > -1; i--) {
			T spr = _childrens.get(i);
			if (spr.isVisible()) {
				max = MathUtils.max(spr.getScaleY() + spr.getHeight(), max);
			}
		}
		return max;
	}

	@Override
	public boolean isVisible() {
		return this._visible;
	}

	@Override
	public void setVisible(final boolean v) {
		this._visible = v;
	}

	public SpriteBase<T> setActiveX(boolean a) {
		this._ignoreUpdate = !a;
		this.setVisible(a);
		return this;
	}

	public boolean isActiveX() {
		return (this._visible && !this._ignoreUpdate);
	}

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

	public boolean isIgnoreUpdate() {
		return this._ignoreUpdate;
	}

	public void setIgnoreUpdate(final boolean u) {
		this._ignoreUpdate = u;
	}

	public boolean isChildrenIgnoreUpdate() {
		return this._childrenIgnoreUpdate;
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

	@Override
	public float getContainerWidth() {
		return this._sprites == null ? super.getContainerWidth() : this._sprites.getWidth();
	}

	@Override
	public float getContainerHeight() {
		return this._sprites == null ? super.getContainerHeight() : this._sprites.getHeight();
	}

	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

	protected float toPixelScaleX(float x) {
		return MathUtils.iceil(x / _scaleX);
	}

	protected float toPixelScaleY(float y) {
		return MathUtils.iceil(y / _scaleY);
	}

	public Gravity getGravity() {
		return new Gravity(this.getName(), this);
	}

	public Screen getScreen() {
		if (this._sprites == null) {
			return LSystem.getProcess().getScreen();
		}
		return this._sprites.getScreen() == null ? LSystem.getProcess().getScreen() : this._sprites.getScreen();
	}

	public Sprites getSprites() {
		return this._sprites;
	}

	public void setSpritesObject(Sprites ss) {
		if (this._sprites == ss) {
			return;
		}
		this._sprites = ss;
	}

	public boolean isImageEquals(SpriteBase<T> o) {
		if (null == o) {
			return false;
		}
		if (this == o) {
			return true;
		}
		if (MathUtils.equal(this.getWidth(), o.getWidth()) && MathUtils.equal(this.getHeight(), o.getHeight())
				&& this.getColor().equals(o.getColor())) {
			if (_image == o.getBitmap()) {
				return true;
			}
			if (_image == null || o.getBitmap() == null) {
				return false;
			}
			if (_image.hashCode() == o.getBitmap().hashCode()) {
				return true;
			}
		}
		return false;
	}

}

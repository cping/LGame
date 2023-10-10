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

import loon.LSystem;
import loon.LTrans;
import loon.Screen;
import loon.action.collision.CollisionObject;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.events.EventDispatcher;
import loon.events.ResizeListener;
import loon.geom.BoxSize;
import loon.geom.PointF;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;

public abstract class DisplayObject extends EventDispatcher implements CollisionObject, ISprite, XY, BoxSize {

	protected float _morphX = 1f;

	protected float _morphY = 1f;

	protected ResizeListener<DisplayObject> _resizeListener;

	protected float _fixedWidthOffset = 0f;

	protected float _fixedHeightOffset = 0f;

	protected boolean _visible = true;

	protected RectBox _scrollRect = null;

	protected float _width = 0;

	protected float _height = 0;

	protected float _scaleX = 1f, _scaleY = 1f;

	protected Vector2f _offset = new Vector2f();

	protected boolean _inStage = false;

	protected DisplayObject _parent = null;

	protected int _trans = LTrans.TRANS_NONE;

	public static final int ANCHOR_TOP_LEFT = LTrans.TOP | LTrans.LEFT;

	public static final int ANCHOR_CENTER = LTrans.HCENTER | LTrans.VCENTER;

	protected LColor _baseColor = LColor.white;

	protected int _anchor = DisplayObject.ANCHOR_TOP_LEFT;

	protected Vector2f _anchorValue = new Vector2f();

	protected Vector2f _pivotValue = new Vector2f(-1, -1);

	protected Sprites _sprites = null;
	
	private boolean _createShadow;
	
	private boolean _xySort;

	public DisplayObject() {
	}

	@Override
	public float getWidth() {
		return _width * _scaleX - _fixedWidthOffset;
	}

	@Override
	public float getHeight() {
		return _height * _scaleY - _fixedHeightOffset;
	}

	public int getTrans() {
		return _trans;
	}

	public void setTrans(int value) {
		_trans = value;
	}

	public boolean getVisible() {
		return _visible;
	}

	@Override
	public void setVisible(boolean v) {
		_visible = v;
	}

	public RectBox getScrollRect() {
		return _scrollRect;
	}

	public void setScrollRect(RectBox rect) {
		_scrollRect = rect;
	}

	@Override
	public DisplayObject getParent() {
		return _parent;
	}

	public boolean inStage() {
		return _inStage;
	}

	protected void setParent(DisplayObject parent) {
		if (_parent == parent) {
			return;
		}
		super.setParent(parent);
		boolean isParentInStage = false;
		DisplayObject obj = parent != null ? parent : _parent;
		for (; obj != null;) {
			if (obj == obj.getParent()) {
				isParentInStage = true;
				break;
			} else {
				obj = obj.getParent();
			}
		}
		if (isParentInStage) {
			if (null == parent) {
				_inStage = false;
				removedFromStage();
			} else {
				_inStage = true;
				addedToStage();
			}
		}
		_parent = parent;
	}

	public void setAnchor(int anchor) {
		_anchor = anchor;
	}

	public float getAnchorX() {
		return _anchorValue.x;
	}

	public void setAnchorX(float ax) {
		this._anchorValue.x = ax;
	}

	public float getAnchorY() {
		return _anchorValue.y;
	}

	public void setAnchorY(float ay) {
		this._pivotValue.y = ay;
	}

	public float getPivotX() {
		return _pivotValue.x;
	}

	public void setPivotX(float ax) {
		this._pivotValue.x = ax;
	}

	public float getPivotY() {
		return _pivotValue.y;
	}

	public void setPivotY(float ay) {
		this._pivotValue.y = ay;
	}

	@Override
	abstract public void createUI(GLEx g);

	@Override
	abstract public void createUI(GLEx g, float offsetX, float offsetY);

	@Override
	public float getScaleX() {
		return _scaleX;
	}

	@Override
	public float getScaleY() {
		return _scaleY;
	}

	public void setScale(float scale) {
		setScale(scale, scale);
	}

	@Override
	public void setScale(float sx, float sy) {
		this._scaleX = sx;
		this._scaleY = sy;
	}

	public void setPosition(int x, int y) {
		setLocation(x, y);
	}

	@Override
	public RectBox getRectBox() {
		return getBounds();
	}

	public RectBox getBounds() {
		float x = _objectLocation.x;
		float y = _objectLocation.y;
		switch (_anchor) {
		case ANCHOR_TOP_LEFT:
		default:
			x -= _anchorValue.x;
			y -= _anchorValue.y;
			break;
		case ANCHOR_CENTER:
			x -= ((int) (_width * _scaleX) >> 1);
			y -= ((int) (_height * _scaleY) >> 1);
			break;
		}
		return getRect(x, y, _width * _scaleX, _height * _scaleY);
	}

	public PointF local2Global(float x, float y) {
		float gX = x;
		float gY = y;
		DisplayObject parent = this.getParent();
		for (; parent != null;) {
			gX += parent.getX();
			gY += parent.getY();
			if (parent != parent.getParent()) {
				parent = parent.getParent();
			} else {
				parent = null;
			}
		}
		return new PointF(gX, gY);
	}

	@Override
	public void setWidth(float w) {
		this._width = w;
	}

	@Override
	public void setHeight(float h) {
		this._height = h;
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return getRectBox().contains(x, y, w, h);
	}

	@Override
	public void setColor(LColor c) {
		this._baseColor = c;
	}

	@Override
	public LColor getColor() {
		return new LColor(_baseColor);
	}

	@Override
	public DisplayObject setSize(float w, float h) {
		this._width = w;
		this._height = h;
		return this;
	}
	
	abstract protected void enterFrame(long time);

	abstract protected void addedToStage();

	abstract protected void removedFromStage();

	abstract protected void onScaleChange(float scaleX, float scaleY);

	@Override
	public RectBox getCollisionBox() {
		return getBounds();
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
	public float getContainerX() {
		return this._sprites == null ? super.getContainerX() : this._sprites.getX();
	}

	@Override
	public float getContainerY() {
		return this._sprites == null ? super.getContainerY() : this._sprites.getY();
	}

	@Override
	public RectBox getBoundingRect() {
		return getCollisionBox();
	}

	@Override
	public boolean containsPoint(float x, float y) {
		return getCollisionBox().contains(x, y, 1, 1);
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
	
	@Override
	public boolean collided(Shape shape) {
		return getCollisionBox().collided(shape);
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

	@Override
	public float getCenterX() {
		return getX() + getWidth() / 2f;
	}

	@Override
	public float getCenterY() {
		return getY() + getHeight() / 2f;
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

	public ResizeListener<DisplayObject> getResizeListener() {
		return _resizeListener;
	}

	public DisplayObject setResizeListener(ResizeListener<DisplayObject> listener) {
		this._resizeListener = listener;
		return this;
	}

	public DisplayObject setOffsetX(float sx) {
		this._offset.setX(sx);
		return this;
	}

	public DisplayObject setOffsetY(float sy) {
		this._offset.setY(sy);
		return this;
	}

	@Override
	public DisplayObject setOffset(Vector2f v) {
		if (v != null) {
			this._offset = v;
		}
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
	
	public DisplayObject setAutoXYSort(boolean a) {
		this._xySort = a;
		return this;
	}
	
	@Override
	public boolean autoXYSort() {
		return _xySort;
	}

	@Override
	public void onResize() {
		if (_resizeListener != null) {
			_resizeListener.onResize(this);
		}
	}

	public float getMorphX() {
		return _morphX;
	}

	public DisplayObject setMorphX(float x) {
		this._morphX = x;
		return this;
	}

	public float getMorphY() {
		return _morphY;
	}

	public DisplayObject setMorphY(float y) {
		this._morphY = y;
		return this;
	}

	@Override
	public boolean showShadow() {
		return _createShadow;
	}

	public DisplayObject createShadow(boolean s) {
		this._createShadow = s;
		return this;
	}
}

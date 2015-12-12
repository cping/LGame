package loon.action.sprite;

import loon.LTrans;
import loon.component.layout.BoxSize;
import loon.event.EventDispatcher;
import loon.geom.PointF;
import loon.geom.RectBox;
import loon.geom.XY;
import loon.opengl.GLEx;

public abstract class DisplayObject extends EventDispatcher implements XY,
		BoxSize {

	public static float morphX = 1f, morphY = 1f;

	protected boolean _visible = true;

	protected RectBox _scrollRect = null;

	protected float _x = 0;

	protected float _y = 0;

	protected float _width = 0;

	protected float _height = 0;

	protected boolean _inStage = false;

	protected DisplayObject _parent = null;

	protected int _trans = LTrans.TRANS_NONE;

	public static final int ANCHOR_TOP_LEFT = LTrans.TOP | LTrans.LEFT;

	public static final int ANCHOR_CENTER = LTrans.HCENTER | LTrans.VCENTER;

	protected int _anchor = DisplayObject.ANCHOR_TOP_LEFT;

	public DisplayObject() {

	}

	public float getX() {
		return _x;
	}

	public float getY() {
		return _y;
	}

	public float getWidth() {
		return _width;
	}

	public float getHeight() {
		return _height;
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

	public void setVisible(boolean v) {
		_visible = v;
	}

	public RectBox getScrollRect() {
		return _scrollRect;
	}

	public void setScrollRect(RectBox rect) {
		_scrollRect = rect;
	}

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

	abstract public void paint(GLEx g);

	public void setPosition(int x, int y) {
		_x = x;
		_y = y;
	}

	public RectBox getBounds() {
		float x = _x;
		float y = _y;
		switch (_anchor) {
		case ANCHOR_CENTER:
			x -= ((int) _width >> 1);
			y -= ((int) _height >> 1);
			break;
		}
		RectBox rect = new RectBox(x, y, _width, _height);
		return rect;
	}

	public PointF local2Global(float x, float y) {
		float gX = x;
		float gY = y;
		DisplayObject parent = this.getParent();
		while (parent != null) {
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
	public void setX(float x) {
		this._x = x;
	}

	@Override
	public void setY(float y) {
		this._y = y;
	}

	@Override
	public void setWidth(float w) {
		this._width = w;
	}

	@Override
	public void setHeight(float h) {
		this._height = h;
	}

	abstract protected void enterFrame(long time);

	abstract protected void addedToStage();

	abstract protected void removedFromStage();

}

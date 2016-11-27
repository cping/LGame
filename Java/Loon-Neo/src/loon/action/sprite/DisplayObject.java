package loon.action.sprite;

import loon.LTexture;
import loon.LTrans;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.component.layout.BoxSize;
import loon.event.EventDispatcher;
import loon.geom.PointF;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;

public abstract class DisplayObject extends EventDispatcher implements ISprite,
		XY, BoxSize {

	public static float morphX = 1f, morphY = 1f;

	protected boolean _visible = true;

	protected RectBox _scrollRect = null;

	protected float _width = 0;

	protected float _height = 0;

	protected float _scaleX = 1f, _scaleY = 1f;

	protected boolean _inStage = false;

	protected DisplayObject _parent = null;

	protected int _trans = LTrans.TRANS_NONE;

	public static final int ANCHOR_TOP_LEFT = LTrans.TOP | LTrans.LEFT;

	public static final int ANCHOR_CENTER = LTrans.HCENTER | LTrans.VCENTER;

	protected LColor _baseColor = LColor.white;
	
	protected int _anchor = DisplayObject.ANCHOR_TOP_LEFT;

	protected Vector2f _anchorValue = new Vector2f();

	protected Vector2f _pivotValue = new Vector2f(-1, -1);

	public DisplayObject() {

	}

	@Override
	public float getWidth() {
		return _width;
	}

	@Override
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
		float x = _location.x;
		float y = _location.y;
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
		if (_rect == null) {
			_rect = new RectBox(x, y, _width * _scaleX, _height * _scaleY);
		} else {
			_rect.setBounds(x, y, _width * _scaleX, _height * _scaleY);
		}
		return _rect;
	}

	@SuppressWarnings("resource")
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
	public void setColor(LColor c){
		this._baseColor = c;
	}
	
	@Override
	public LColor getColor(){
		return new LColor(_baseColor);
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
	public LTexture getBitmap() {
		return null;
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

}

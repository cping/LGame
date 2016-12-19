package loon.action.sprite;

import java.util.Comparator;

import loon.Director.Origin;
import loon.LObject;
import loon.LTexture;
import loon.LTextures;
import loon.PlayerUtils;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.component.layout.BoxSize;
import loon.geom.Affine2f;
import loon.geom.Dimension;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.LayerSorter;
import loon.utils.TArray;

public class Entity extends LObject<IEntity> implements IEntity, BoxSize {

	private static final int CHILDREN_CAPACITY_DEFAULT = 4;

	protected Origin _origin = Origin.CENTER;
	protected boolean _visible = true;
	protected boolean _deform = true;
	protected boolean _ignoreUpdate = false;
	protected boolean _childrenVisible = true;
	protected boolean _childrenIgnoreUpdate = false;
	protected boolean _childrenSortPending = false;

	protected int _idxTag = IEntity.TAG_INVALID;

	protected boolean _repaintDraw = false;
	protected TArray<IEntity> _childrens;

	protected RectBox _shear;
	protected LColor _baseColor = new LColor(LColor.white);

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

	private final static LayerSorter<IEntity> entitySorter = new LayerSorter<IEntity>(
			false);

	private boolean _stopUpdate = false;

	protected float _width, _height;

	protected LTexture _image;

	public Entity() {
		this((LTexture) null);
	}

	public Entity(final String path) {
		this(LTextures.loadTexture(path));
	}

	public Entity(final LTexture texture) {
		this(texture, 0, 0, texture == null ? 0 : texture.getWidth(),
				texture == null ? 0 : texture.getHeight());
	}

	public Entity(final LTexture texture, final float x, final float y) {
		this(texture, x, y, texture == null ? 0 : texture.getWidth(),
				texture == null ? 0 : texture.getHeight());
	}

	public Entity(final LTexture texture, final float x, final float y,
			final float w, final float h) {
		this.setLocation(x, y);
		this._width = w;
		this._height = h;
		this._image = texture;
	}

	public static Entity make(LTexture texture, final float x, final float y) {
		return new Entity(texture, x, y);
	}

	public static Entity make(String path, final float x, final float y) {
		return new Entity(LTextures.loadTexture(path), x, y);
	}

	protected void onUpdateColor() {

	}

	public void setTexture(String path) {
		setTexture(LTextures.loadTexture(path));
	}

	public void setTexture(LTexture tex) {
		this._image = tex;
		if (_width <= 0) {
			_width = _image.width();
		}
		if (_height <= 0) {
			_height = _image.height();
		}
		this._repaintDraw = (tex == null);
	}

	@Override
	public boolean isVisible() {
		return this._visible;
	}

	@Override
	public void setVisible(final boolean pVisible) {
		this._visible = pVisible;
	}

	@Override
	public boolean isChildrenVisible() {
		return this._childrenVisible;
	}

	@Override
	public void setChildrenVisible(final boolean v) {
		this._childrenVisible = v;
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
	public void setChildrenIgnoreUpdate(final boolean c) {
		this._childrenIgnoreUpdate = c;
	}

	@Override
	public int getIndexTag() {
		return this._idxTag;
	}

	@Override
	public void setIndexTag(final int idx) {
		this._idxTag = idx;
	}

	@Override
	public boolean isRotated() {
		return this._rotation != 0;
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
		this._scaleX = pScaleX;
	}

	@Override
	public void setScaleY(final float pScaleY) {
		this._scaleY = pScaleY;
	}

	@Override
	public void setScale(final float pScale) {
		this._scaleX = pScale;
		this._scaleY = pScale;
	}

	@Override
	public void setScale(final float pScaleX, final float pScaleY) {
		this._scaleX = pScaleX;
		this._scaleY = pScaleY;
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
		return (this._rotation != 0) || (this._scaleX != 1)
				|| (this._scaleY != 1) || (this._skewX != 0)
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
		return this._baseColor;
	}

	@Override
	public void setColor(final LColor pColor) {
		this._baseColor.setColor(pColor);
		this.onUpdateColor();
	}

	@Override
	public void setColor(final int pColor) {
		this._baseColor.setColor(pColor);
		this.onUpdateColor();
	}

	@Override
	public void setAlpha(final float a) {
		super.setAlpha(a);
		this._baseColor.a = a;
		this.onUpdateColor();
	}

	@Override
	public void setColor(final float r, final float g, final float b) {
		this._baseColor.setColor(r, g, b);
		this.onUpdateColor();

	}

	@Override
	public void setColor(final float r, final float g, final float b,
			final float a) {
		this._baseColor.setColor(r, g, b, a);
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
	public void addChild(final IEntity e) {
		if (e == null) {
			return;
		}
		if (this._childrens == null) {
			this.allocateChildren();
		}
		this._childrens.add(e);
		e.setParent(this);
		e.setState(State.ADDED);
		e.onAttached();
	}

	@Override
	public void addChildAt(final IEntity e, float x, float y) {
		if (e != null) {
			e.setLocation(x, y);
			addChild(e);
		}
	}

	@Override
	public void sortChildren() {
		this.sortChildren(true);
	}

	@Override
	public void sortChildren(final boolean i) {
		if (this._childrens == null) {
			return;
		}
		if (i) {
			entitySorter.sort(this._childrens);
		} else {
			this._childrenSortPending = true;
		}
	}

	@Override
	public void sortChildren(final Comparator<IEntity> e) {
		if (this._childrens == null) {
			return;
		}
		entitySorter.sort(this._childrens, e);
	}

	@Override
	public boolean removeSelf() {
		final IEntity parent = this._super;
		if (parent != null) {
			return parent.removeChild(this);
		} else {
			return false;
		}
	}

	@Override
	public void removeChildren() {
		if (this._childrens == null) {
			return;
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
	public void setUserData(final Object u) {
		this.Tag = u;
	}

	@Override
	public final void createUI(final GLEx g) {
		this.createUI(g, 0, 0);
	}

	@Override
	public final void createUI(final GLEx g, final float offsetX,
			final float offsetY) {
		if (this._visible) {
			this.onManagedPaint(g, offsetX, offsetY);
		}
	}

	@Override
	public void reset() {
		this._visible = true;
		this._ignoreUpdate = false;
		this._childrenVisible = true;
		this._childrenIgnoreUpdate = false;

		this._rotation = 0f;
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
	}

	protected void prePaint(final GLEx g) {

	}

	protected void paint(final GLEx g) {
		paint(g, 0, 0);
	}

	protected void paint(final GLEx g, float offsetX, float offsetY) {
		if (_alpha < 0.01) {
			return;
		}
		boolean exist = _image != null || (_width > 0 && _height > 0)
				|| _repaintDraw;
		if (exist) {
			int blend = g.getBlendMode();
			g.setBlendMode(_blend);
			boolean update = ((_rotation != 0
					|| !(_scaleX == 1f && _scaleY == 1f) || !(_skewX == 0 && _skewY == 0))
					|| _flipX || _flipY)
					&& _deform;
			float nx = offsetX + this._location.x;
			float ny = offsetY + this._location.y;
			if (update) {
				g.saveTx();
				g.saveBrush();
				Affine2f tx = g.tx();
				final float rotation = this._rotation;
				final float scaleX = this._scaleX;
				final float scaleY = this._scaleY;
				if (rotation != 0) {
					final float rotationCenterX = this._rotationCenterX == -1 ? (nx + _origin
							.ox(this._width)) : nx + this._rotationCenterX;
					final float rotationCenterY = this._rotationCenterY == -1 ? (ny + _origin
							.oy(this._height)) : ny + this._rotationCenterY;
					tx.translate(rotationCenterX, rotationCenterY);
					tx.preRotate(rotation);
					tx.translate(-rotationCenterX, -rotationCenterY);
				}
				final boolean flipX = this._flipX;
				final boolean flipY = this._flipY;
				if (flipX || flipY) {
					final float rotationCenterX = this._rotationCenterX == -1 ? (nx + _origin
							.ox(this._width)) : nx + this._rotationCenterX;
					final float rotationCenterY = this._rotationCenterY == -1 ? (ny + _origin
							.oy(this._height)) : ny + this._rotationCenterY;
					if (flipX && flipY) {
						Affine2f.transform(tx, rotationCenterX,
								rotationCenterY, Affine2f.TRANS_ROT180);
					} else if (flipX) {
						Affine2f.transform(tx, rotationCenterX,
								rotationCenterY, Affine2f.TRANS_MIRROR);
					} else if (flipY) {
						Affine2f.transform(tx, rotationCenterX,
								rotationCenterY, Affine2f.TRANS_MIRROR_ROT180);
					}
				}
				if ((scaleX != 1) || (scaleY != 1)) {
					final float scaleCenterX = this._scaleCenterX == -1 ? (nx + _origin
							.ox(this._width)) : nx + this._scaleCenterX;
					final float scaleCenterY = this._scaleCenterY == -1 ? (ny + _origin
							.oy(this._height)) : ny + this._scaleCenterY;
					tx.translate(scaleCenterX, scaleCenterY);
					tx.preScale(scaleX, scaleY);
					tx.translate(-scaleCenterX, -scaleCenterY);
				}
				final float skewX = this._skewX;
				final float skewY = this._skewY;
				if ((skewX != 0) || (skewY != 0)) {
					final float skewCenterX = this._skewCenterX == -1 ? (nx + _origin
							.ox(this._width)) : nx + this._skewCenterX;
					final float skewCenterY = this._skewCenterY == -1 ? (ny + _origin
							.oy(this._height)) : ny + this._skewCenterY;
					tx.translate(skewCenterX, skewCenterY);
					tx.preShear(skewX, skewY);
					tx.translate(-skewCenterX, -skewCenterY);
				}
			}
			if (_repaintDraw) {
				boolean elastic = (_shear != null);
				if (elastic) {
					g.setClip(_shear.x, _shear.y, _shear.width, _shear.height);
				}
				float tmp = g.alpha();
				g.setAlpha(_alpha);
				repaint(g, offsetX, offsetY);
				g.setAlpha(tmp);
				if (elastic) {
					g.clearClip();
				}
			} else {
				if (_image != null) {
					if (_shear == null) {
						g.draw(_image, nx, ny, _width, _height, _baseColor);
					} else {
						g.draw(_image, nx, ny, _width, _height, _shear.x,
								_shear.y, _shear.width, _shear.height,
								_baseColor);
					}
				} else {
					g.fillRect(nx, ny, _width, _height, _baseColor);
				}
			}
			if (update) {
				g.restoreBrush();
				g.restoreTx();
			}
			g.setBlendMode(blend);
		}
	}

	public void setRepaint(boolean r) {
		this._repaintDraw = r;
	}

	public boolean isRepaint() {
		return this._repaintDraw;
	}

	protected void repaint(GLEx g, float offsetX, float offsetY) {

	}

	protected void postPaint(final GLEx g) {

	}

	private void allocateChildren() {
		this._childrens = new TArray<IEntity>(Entity.CHILDREN_CAPACITY_DEFAULT);
	}

	protected void onManagedPaint(final GLEx g, float offsetX, float offsetY) {
		final TArray<IEntity> children = this._childrens;
		if ((children == null) || !this._childrenVisible) {
			this.prePaint(g);
			this.paint(g, offsetX, offsetY);
			this.postPaint(g);
		} else {
			if (this._childrenSortPending) {
				entitySorter.sort(this._childrens);
				this._childrenSortPending = false;
			}
			final int childCount = children.size;
			int i = 0;
			for (; i < childCount; i++) {
				final IEntity child = children.get(i);
				if (child.getLayer() < 0) {
					child.createUI(g, offsetX, offsetY);
				} else {
					break;
				}
			}
			this.prePaint(g);
			this.paint(g, offsetX, offsetY);
			this.postPaint(g);
			for (; i < childCount; i++) {
				children.get(i).createUI(g, offsetX, offsetY);
			}
		}
	}

	protected void onManagedUpdate(final long elapsedTime) {
		if (_stopUpdate) {
			return;
		}
		if ((this._childrens != null) && !this._childrenIgnoreUpdate) {
			final TArray<IEntity> entities = this._childrens;
			final int entityCount = entities.size;
			for (int i = 0; i < entityCount; i++) {
				entities.get(i).update(elapsedTime);
			}
		}
		onUpdate(elapsedTime);
	}

	protected void onUpdate(final long elapsedTime) {

	}

	@Override
	public void update(long elapsedTime) {
		if (!this._ignoreUpdate) {
			this.onManagedUpdate(elapsedTime);
		}
	}

	@Override
	public float getWidth() {
		return (_width * this._scaleX);
	}

	@Override
	public float getHeight() {
		return (_height * this._scaleY);
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

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), getWidth(), getHeight());
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
		this._width = w;
	}

	@Override
	public void setHeight(float h) {
		this._height = h;
	}

	public void setSize(float w, float h) {
		setWidth(w);
		setHeight(h);
	}

	public Dimension getDimension() {
		return new Dimension(this._width, this._height);
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

	public void setShear(RectBox s) {
		allocateShear();
		this._shear.setBounds(s);
	}

	public void setShear(float x, float y, float w, float h) {
		allocateShear();
		this._shear.setBounds(x, y, w, h);
	}

	public void setClip(float x, float y, float w, float h) {
		setShear(x, y, w, h);
	}

	public boolean isDeform() {
		return _deform;
	}

	public void setDeform(boolean d) {
		this._deform = d;
	}

	@Override
	public void setParent(ISprite s) {
		if (s instanceof IEntity) {
			setParent((IEntity) s);
		} else if (s instanceof ISprite) {
			setParent(new SpriteToEntity(s));
		}
	}

	public Origin getOrigin() {
		return _origin;
	}

	public void setOrigin(Origin o) {
		this._origin = o;
	}

	public boolean isStopUpdate() {
		return _stopUpdate;
	}

	public Entity setStopUpdate(boolean s) {
		this._stopUpdate = s;
		return this;
	}

	@Override
	public Entity setFlipX(boolean x) {
		this._flipX = x;
		return this;
	}

	@Override
	public Entity setFlipY(boolean y) {
		this._flipY = y;
		return this;
	}

	@Override
	public Entity setFlipXY(boolean x, boolean y) {
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
	public void toString(final StringBuilder s) {
		s.append(this.getClass().getSimpleName());

		if ((this._childrens != null) && (this._childrens.size > 0)) {
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
		final StringBuilder stringBuilder = new StringBuilder();
		this.toString(stringBuilder);
		return stringBuilder.toString();
	}

	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public void close() {
		if (!isDisposed()) {
			if (_image != null) {
				_image.close();
				_image = null;
			}
		}
		_stopUpdate = false;
		setState(State.DISPOSED);
		removeActionEvents(this);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (!isDisposed()) {
			this.close();
		}
	}


}

package loon.action.sprite;

import java.util.ArrayList;
import java.util.Comparator;

import loon.LObject;
import loon.LRelease;
import loon.LTexture;
import loon.LTextures;
import loon.action.ActionBind;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.geom.Affine2f;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.LayerSorter;

public class Entity extends LObject implements ActionBind, IEntity, LRelease {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int CHILDREN_CAPACITY_DEFAULT = 4;

	protected boolean _disposed;
	protected boolean _visible = true;
	protected boolean _ignoreUpdate;
	protected boolean _childrenVisible = true;
	protected boolean _childrenIgnoreUpdate;
	protected boolean _childrenSortPending;

	protected int _idxTag = IEntity.TAG_INVALID;

	private IEntity _parent;

	protected ArrayList<IEntity> _childrens;

	protected LColor _baseColor = new LColor(LColor.white);

	protected float _rotationCenterX = -1;
	protected float _rotationCenterY = -1;

	protected float _scaleX = 1;
	protected float _scaleY = 1;

	protected float _scaleCenterX = -1;
	protected float _scaleCenterY = -1;

	protected float _skewX = 0;
	protected float _skewY = 0;

	protected float _skewCenterX = -1;
	protected float _skewCenterY = -1;

	private final static LayerSorter<IEntity> entitySorter = new LayerSorter<IEntity>(
			false);

	protected float _width, _height;

	protected LTexture _image;

	public Entity() {
		this(null);
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

	public boolean isDisposed() {
		return this._disposed;
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
	public boolean hasParent() {
		return this._parent != null;
	}

	@Override
	public IEntity getParent() {
		return this._parent;
	}

	@Override
	public void setParent(final IEntity e) {
		this._parent = e;
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
		return this._baseColor.a;
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
	public void setAlpha(final float pAlpha) {
		this._baseColor.a = pAlpha;
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
		return this._childrens.size();
	}

	@Override
	public IEntity getChildByTag(final int idx) {
		if (this._childrens == null) {
			return null;
		}
		for (int i = this._childrens.size() - 1; i >= 0; i--) {
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
		return this._childrens.get(this._childrens.size() - 1);
	}

	@Override
	public boolean detachSelf() {
		final IEntity parent = this._parent;
		if (parent != null) {
			return parent.detachChild(this);
		} else {
			return false;
		}
	}

	@Override
	public void detachChildren() {
		if (this._childrens == null) {
			return;
		}
		this._childrens.clear();
	}

	@Override
	public void attachChild(final IEntity e) {
		if (this._childrens == null) {
			this.allocateChildren();
		}
		this._childrens.add(e);
		e.setParent(this);
		e.onAttached();
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
	public boolean detachChild(final IEntity e) {
		if (this._childrens == null) {
			return false;
		}
		return this._childrens.remove(e);
	}

	@Override
	public IEntity detachChild(final int idx) {
		if (this._childrens == null) {
			return null;
		}
		for (int i = this._childrens.size() - 1; i >= 0; i--) {
			if (this._childrens.get(i).getIndexTag() == idx) {
				final IEntity removed = this._childrens.remove(i);
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
		if (this._visible) {
			this.onManagedPaint(g);
		}
	}

	@Override
	public void reset() {
		this._visible = true;
		this._ignoreUpdate = false;
		this._childrenVisible = true;
		this._childrenIgnoreUpdate = false;

		this._rotation = 0;
		this._scaleX = 1;
		this._scaleY = 1;
		this._skewX = 0;
		this._skewY = 0;

		this._baseColor.reset();

		if (this._childrens != null) {
			final ArrayList<IEntity> entities = this._childrens;
			for (int i = entities.size() - 1; i >= 0; i--) {
				entities.get(i).reset();
			}
		}
	}

	@Override
	public void close() {
		if (!this._disposed) {
			this._disposed = true;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (!this._disposed) {
			this.close();
		}
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		this.toString(stringBuilder);
		return stringBuilder.toString();
	}

	protected void prePaint(final GLEx g) {

	}

	protected void paint(final GLEx g) {
		boolean exist = _image != null || (_width > 0 && _height > 0);
		if (exist) {
			boolean update = _rotation != 0
					|| !(_scaleX == 1f && _scaleY == 1f)
					|| !(_skewX == 0 && _skewY == 0);
			if (update) {
				g.saveTx();
				Affine2f tx = g.tx();
				final float _rotation = this._rotation;
				final float scaleX = this._scaleX;
				final float scaleY = this._scaleY;
				if ((scaleX != 1) || (scaleY != 1)) {
					final float scaleCenterX = this._scaleCenterX == -1 ? (this._location.x + this._width / 2f)
							: this._rotationCenterX;
					final float scaleCenterY = this._scaleCenterY == -1 ? (this._location.y + this._height / 2f)
							: this._rotationCenterY;
					tx.translate(scaleCenterX, scaleCenterY);
					tx.preScale(scaleX, scaleY);
					tx.translate(-scaleCenterX, -scaleCenterY);
				}
				final float skewX = this._skewX;
				final float skewY = this._skewY;
				if ((skewX != 0) || (skewY != 0)) {
					final float skewCenterX = this._skewCenterX == -1 ? (this._location.x + this._width / 2f)
							: this._rotationCenterX;
					final float skewCenterY = this._skewCenterY == -1 ? (this._location.y + this._height / 2f)
							: this._rotationCenterY;
					tx.translate(skewCenterX, skewCenterY);
					tx.preShear(skewX, skewY);
					tx.translate(-skewCenterX, -skewCenterY);
				}
				if (_rotation != 0) {
					final float rotationCenterX = this._rotationCenterX == -1 ? (this._location.x + this._width / 2f)
							: this._rotationCenterX;
					final float rotationCenterY = this._rotationCenterY == -1 ? (this._location.y + this._height / 2f)
							: this._rotationCenterY;
					tx.translate(rotationCenterX, rotationCenterY);
					tx.preRotate(_rotation);
					tx.translate(-rotationCenterX, -rotationCenterY);
				}
			}
			if (_image != null) {
				g.draw(_image, _location.x, _location.y, _baseColor);
			} else {
				g.fillRect(_location.x, _location.y, _width, _height,
						_baseColor);
			}
			if (update) {
				g.restoreTx();
			}
		}
	}

	protected void postPaint(final GLEx g) {

	}

	private void allocateChildren() {
		this._childrens = new ArrayList<IEntity>(
				Entity.CHILDREN_CAPACITY_DEFAULT);
	}

	protected void onManagedPaint(final GLEx g) {
		final ArrayList<IEntity> children = this._childrens;
		if ((children == null) || !this._childrenVisible) {
			this.prePaint(g);
			this.paint(g);
			this.postPaint(g);
		} else {
			if (this._childrenSortPending) {
				entitySorter.sort(this._childrens);
				this._childrenSortPending = false;
			}
			final int childCount = children.size();
			int i = 0;
			for (; i < childCount; i++) {
				final IEntity child = children.get(i);
				if (child.getLayer() < 0) {
					child.createUI(g);
				} else {
					break;
				}
			}
			this.prePaint(g);
			this.paint(g);
			this.postPaint(g);
			for (; i < childCount; i++) {
				children.get(i).createUI(g);
			}
		}
	}

	protected void onManagedUpdate(final long elapsedTime) {
		if ((this._childrens != null) && !this._childrenIgnoreUpdate) {
			final ArrayList<IEntity> entities = this._childrens;
			final int entityCount = entities.size();
			for (int i = 0; i < entityCount; i++) {
				entities.get(i).update(elapsedTime);
			}
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (!this._ignoreUpdate) {
			this.onManagedUpdate(elapsedTime);
		}
	}

	@Override
	public int getWidth() {
		return (int) _width;
	}

	@Override
	public int getHeight() {
		return (int) _height;
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
	public boolean inContains(float x, float y, float w, float h) {
		return getCollisionBox().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionBox();
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(getLocation().x(), getLocation().y(), getWidth(),
				getHeight());
	}

	@Override
	public LTexture getBitmap() {
		return _image;
	}

	@Override
	public void toString(final StringBuilder s) {
		s.append(this.getClass().getSimpleName());

		if ((this._childrens != null) && (this._childrens.size() > 0)) {
			s.append(" [");
			final ArrayList<IEntity> entities = this._childrens;
			for (int i = 0; i < entities.size(); i++) {
				entities.get(i).toString(s);
				if (i < (entities.size() - 1)) {
					s.append(", ");
				}
			}
			s.append("]");
		}
	}

}

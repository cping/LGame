package loon.action.sprite;

import java.util.ArrayList;
import java.util.Comparator;

import loon.LObject;
import loon.LRelease;
import loon.LTexture;
import loon.action.ActionBind;
import loon.action.map.Field2D;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.geom.Affine2f;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public class Entity extends LObject implements ActionBind, ISprite, IEntity,
		LRelease {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int VERTEX_INDEX_X = 0;
	public static final int VERTEX_INDEX_Y = 1;

	private static final int CHILDREN_CAPACITY_DEFAULT = 4;

	private static final float[] VERTICES_SCENE_TO_LOCAL_TMP = new float[2];
	private static final float[] VERTICES_LOCAL_TO_SCENE_TMP = new float[2];

	protected boolean _disposed;
	protected boolean _visible = true;
	protected boolean _ignoreUpdate;
	protected boolean _childrenVisible = true;
	protected boolean _childrenIgnoreUpdate;
	protected boolean _childrenSortPending;

	protected int _idxTag = IEntity.TAG_INVALID;

	private IEntity _parent;

	protected ArrayList<IEntity> _childrens;

	protected LColor _baseColor = new LColor(1, 1, 1, 1);

	protected Camera _camera;

	protected float _rotationCenterX = 0;
	protected float _rotationCenterY = 0;

	protected float _scaleX = 1;
	protected float _scaleY = 1;

	protected float _scaleCenterX = 0;
	protected float _scaleCenterY = 0;

	protected float _skewX = 0;
	protected float _skewY = 0;

	protected float _skewCenterX = 0;
	protected float _skewCenterY = 0;

	private boolean _localToParentTransformationDirty = true;
	private boolean _parentToLocalTransformationDirty = true;

	private Affine2f _localToParentTransformation;
	private Affine2f _parentToLocalTransformation;

	private Affine2f _localToSceneTransformation;
	private Affine2f _sceneToLocalTransformation;

	private final static LayerSorter<IEntity> entitySorter = new LayerSorter<IEntity>(false);

	protected float _width, _height;

	public Entity() {
		this(0, 0, 0, 0);
	}

	public Entity(final float x, final float y) {
		this(x, y, 0, 0);
	}

	public Entity(final float x, final float y, final float w, final float h) {
		this.setLocation(x, y);
		this._width = w;
		this._height = h;
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
	public void setX(final float x) {
		this._location.x = x;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setY(final float y) {
		this._location.x = y;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setPosition(final IEntity pOtherEntity) {
		this.setPosition(pOtherEntity.getX(), pOtherEntity.getY());
	}

	@Override
	public void setPosition(final float x, final float y) {
		this._location.set(x, y);
		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public float getRotation() {
		return this._rotation;
	}

	@Override
	public boolean isRotated() {
		return this._rotation != 0;
	}

	@Override
	public void setRotation(final float r) {
		this._rotation = r;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
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

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setRotationCenterY(final float sy) {
		this._rotationCenterY = sy;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setRotationCenter(final float sx, final float sy) {
		this._rotationCenterX = sx;
		this._rotationCenterY = sy;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
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

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setScaleY(final float pScaleY) {
		this._scaleY = pScaleY;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setScale(final float pScale) {
		this._scaleX = pScale;
		this._scaleY = pScale;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setScale(final float pScaleX, final float pScaleY) {
		this._scaleX = pScaleX;
		this._scaleY = pScaleY;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
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

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setScaleCenterY(final float sy) {
		this._scaleCenterY = sy;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setScaleCenter(final float sx, final float sy) {
		this._scaleCenterX = sx;
		this._scaleCenterY = sy;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
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

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setSkewY(final float sy) {
		this._skewY = sy;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setSkew(final float pSkew) {
		this._skewX = pSkew;
		this._skewY = pSkew;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setSkew(final float sx, final float sy) {
		this._skewX = sx;
		this._skewY = sy;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
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

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setSkewCenterY(final float sy) {
		this._skewCenterY = sy;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
	}

	@Override
	public void setSkewCenter(final float sx, final float sy) {
		this._skewCenterX = sx;
		this._skewCenterY = sy;

		this._localToParentTransformationDirty = true;
		this._parentToLocalTransformationDirty = true;
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
	public float[] getSceneCenterCoordinates() {
		return this.convertLocalToSceneCoordinates(0, 0);
	}

	@Override
	public float[] getSceneCenterCoordinates(final float[] res) {
		return this.convertLocalToSceneCoordinates(0, 0, res);
	}

	@Override
	public Affine2f getLocalToParentTransformation() {
		if (this._localToParentTransformation == null) {
			this._localToParentTransformation = new Affine2f();
		}

		final Affine2f localToParentTransformation = this._localToParentTransformation;
		if (this._localToParentTransformationDirty) {
			localToParentTransformation.idt();
			final float scaleX = this._scaleX;
			final float scaleY = this._scaleY;
			if ((scaleX != 1) || (scaleY != 1)) {
				final float scaleCenterX = this._scaleCenterX;
				final float scaleCenterY = this._scaleCenterY;
				localToParentTransformation.postTranslate(-scaleCenterX,
						-scaleCenterY);
				localToParentTransformation.postScale(scaleX, scaleY);
				localToParentTransformation.postTranslate(scaleCenterX,
						scaleCenterY);
			}
			final float skewX = this._skewX;
			final float skewY = this._skewY;
			if ((skewX != 0) || (skewY != 0)) {
				final float skewCenterX = this._skewCenterX;
				final float skewCenterY = this._skewCenterY;

				localToParentTransformation.postTranslate(-skewCenterX,
						-skewCenterY);
				localToParentTransformation.postShear(skewX, skewY);
				localToParentTransformation.postTranslate(skewCenterX,
						skewCenterY);
			}
			final float _rotation = this._rotation;
			if (_rotation != 0) {
				final float rotationCenterX = this._rotationCenterX;
				final float rotationCenterY = this._rotationCenterY;

				localToParentTransformation.postTranslate(-rotationCenterX,
						-rotationCenterY);
				localToParentTransformation.postRotate(_rotation);
				localToParentTransformation.postTranslate(rotationCenterX,
						rotationCenterY);
			}
			localToParentTransformation.postTranslate(_location.x, _location.y);

			this._localToParentTransformationDirty = false;
		}
		return localToParentTransformation;
	}

	@Override
	public Affine2f getParentToLocalTransformation() {
		if (this._parentToLocalTransformation == null) {
			this._parentToLocalTransformation = new Affine2f();
		}
		final Affine2f parentToLocalTransformation = this._parentToLocalTransformation;
		if (this._parentToLocalTransformationDirty) {
			parentToLocalTransformation.idt();
			parentToLocalTransformation.postTranslate(-_location.x,
					-_location.y);
			final float _rotation = this._rotation;
			if (_rotation != 0) {
				final float rotationCenterX = this._rotationCenterX;
				final float rotationCenterY = this._rotationCenterY;
				parentToLocalTransformation.postTranslate(-rotationCenterX,
						-rotationCenterY);
				parentToLocalTransformation.postRotate(-_rotation);
				parentToLocalTransformation.postTranslate(rotationCenterX,
						rotationCenterY);
			}
			final float skewX = this._skewX;
			final float skewY = this._skewY;
			if ((skewX != 0) || (skewY != 0)) {
				final float skewCenterX = this._skewCenterX;
				final float skewCenterY = this._skewCenterY;
				parentToLocalTransformation.postTranslate(-skewCenterX,
						-skewCenterY);
				parentToLocalTransformation.postShear(-skewX, -skewY);
				parentToLocalTransformation.postTranslate(skewCenterX,
						skewCenterY);
			}
			final float scaleX = this._scaleX;
			final float scaleY = this._scaleY;
			if ((scaleX != 1) || (scaleY != 1)) {
				final float scaleCenterX = this._scaleCenterX;
				final float scaleCenterY = this._scaleCenterY;
				parentToLocalTransformation.postTranslate(-scaleCenterX,
						-scaleCenterY);
				parentToLocalTransformation.postScale(1 / scaleX, 1 / scaleY);
				parentToLocalTransformation.postTranslate(scaleCenterX,
						scaleCenterY);
			}

			this._parentToLocalTransformationDirty = false;
		}
		return parentToLocalTransformation;
	}

	@Override
	public Affine2f getLocalToSceneTransformation() {
		if (this._localToSceneTransformation == null) {
			this._localToSceneTransformation = new Affine2f();
		}

		final Affine2f localToSceneTransformation = this._localToSceneTransformation;
		localToSceneTransformation.set(this.getLocalToParentTransformation());

		final IEntity parent = this._parent;
		if (parent != null) {
			localToSceneTransformation.postConcatenate(parent
					.getLocalToSceneTransformation());
		}

		return localToSceneTransformation;
	}

	@Override
	public Affine2f getSceneToLocalTransformation() {
		if (this._sceneToLocalTransformation == null) {
			this._sceneToLocalTransformation = new Affine2f();
		}
		final Affine2f sceneToLocalTransformation = this._sceneToLocalTransformation;
		sceneToLocalTransformation.set(this.getParentToLocalTransformation());
		final IEntity parent = this._parent;
		if (parent != null) {
			sceneToLocalTransformation.preConcatenate(parent
					.getSceneToLocalTransformation());
		}
		return sceneToLocalTransformation;
	}

	@Override
	public float[] convertLocalToSceneCoordinates(final float x, final float y) {
		return this.convertLocalToSceneCoordinates(x, y,
				Entity.VERTICES_LOCAL_TO_SCENE_TMP);
	}

	@Override
	public float[] convertLocalToSceneCoordinates(final float x, final float y,
			final float[] res) {
		final Affine2f localToSceneTransformation = this
				.getLocalToSceneTransformation();

		res[VERTEX_INDEX_X] = x;
		res[VERTEX_INDEX_Y] = y;

		localToSceneTransformation.transform(res);

		return res;
	}

	@Override
	public float[] convertLocalToSceneCoordinates(final float[] coods) {
		return this.convertLocalToSceneCoordinates(coods,
				Entity.VERTICES_LOCAL_TO_SCENE_TMP);
	}

	@Override
	public float[] convertLocalToSceneCoordinates(final float[] coods,
			final float[] res) {
		final Affine2f localToSceneTransformation = this
				.getLocalToSceneTransformation();

		res[VERTEX_INDEX_X] = coods[VERTEX_INDEX_X];
		res[VERTEX_INDEX_Y] = coods[VERTEX_INDEX_Y];

		localToSceneTransformation.transform(res);

		return res;
	}

	@Override
	public float[] convertSceneToLocalCoordinates(final float x, final float y) {
		return this.convertSceneToLocalCoordinates(x, y,
				Entity.VERTICES_SCENE_TO_LOCAL_TMP);
	}

	@Override
	public float[] convertSceneToLocalCoordinates(final float x, final float y,
			final float[] res) {
		res[VERTEX_INDEX_X] = x;
		res[VERTEX_INDEX_Y] = y;

		this.getSceneToLocalTransformation().transform(res);

		return res;
	}

	@Override
	public float[] convertSceneToLocalCoordinates(final float[] coods) {
		return this.convertSceneToLocalCoordinates(coods,
				Entity.VERTICES_SCENE_TO_LOCAL_TMP);
	}

	@Override
	public float[] convertSceneToLocalCoordinates(final float[] coods,
			final float[] res) {
		res[VERTEX_INDEX_X] = coods[VERTEX_INDEX_X];
		res[VERTEX_INDEX_Y] = coods[VERTEX_INDEX_Y];

		this.getSceneToLocalTransformation().transform(res);

		return res;
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
	public final void createUI(final GLEx g, final Camera c) {
		if (this._visible) {
			this.onManagedPaint(g, c);
		}
	}

	@Override
	public void createUI(GLEx g) {
		createUI(g, _camera);
	}

	public void setCamera(Camera c) {
		this._camera = c;
	}

	public Camera getCamera() {
		return this._camera;
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

	protected void prePaint(final GLEx g, final Camera c) {

	}

	protected void paint(final GLEx g, final Camera c) {

	}

	protected void postPaint(final GLEx g, final Camera c) {

	}

	private void allocateChildren() {
		this._childrens = new ArrayList<IEntity>(
				Entity.CHILDREN_CAPACITY_DEFAULT);
	}

	protected void onManagedPaint(final GLEx g, final Camera c) {
		final ArrayList<IEntity> children = this._childrens;
		if ((children == null) || !this._childrenVisible) {
			this.prePaint(g, c);
			this.paint(g, c);
			this.postPaint(g, c);
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
					child.createUI(g, c);
				} else {
					break;
				}
			}
			this.prePaint(g, c);
			this.paint(g, c);
			this.postPaint(g, c);
			for (; i < childCount; i++) {
				children.get(i).createUI(g, c);
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
	public boolean inContains(int x, int y, int w, int h) {
		return getCollisionBox().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionBox();
	}

	@Override
	public int getContainerWidth() {
		return 0;
	}

	@Override
	public int getContainerHeight() {
		return 0;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(getLocation().x(), getLocation().y(), getWidth(),
				getHeight());
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

}

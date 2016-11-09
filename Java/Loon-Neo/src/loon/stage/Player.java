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
package loon.stage;

import loon.LObject;
import loon.LRelease;
import loon.action.ActionBind;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.component.layout.BoxSize;
import loon.geom.Affine2f;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.BaseBatch;
import loon.opengl.GLEx;
import loon.opengl.Painter;
import loon.utils.Array;
import loon.utils.MathUtils;
import loon.utils.reply.Port;
import loon.utils.reply.Var;
import loon.utils.reply.VarView;

public abstract class Player extends LObject<GroupPlayer> implements ActionBind, XY,
		BoxSize, LRelease {

	public interface Pointer {

		public void onStart(float x, float y);

		public void onDrag(float x, float y);

		public void onEnd(float x, float y);

	}

	protected int flags;
	protected float width, height;

	private Array<Pointer> events;
	private HitTester hitTester;
	private BaseBatch batch;

	private float scaleX = 1, scaleY = 1;
	private final Affine2f affine = new Affine2f();

	private RectBox tempRect;
	private Origin origin = Origin.FIXED;
	private float originX, originY;
	protected int baseColor = LColor.DEF_COLOR;
	protected long _elapsed;

	public static enum State {
		REMOVED, ADDED, DISPOSED
	}

	public static enum Origin {

		FIXED {
			public float ox(float width) {
				return 0;
			}

			public float oy(float height) {
				return 0;
			}
		},

		CENTER {
			public float ox(float width) {
				return width / 2;
			}

			public float oy(float height) {
				return height / 2;
			}
		},

		UL {
			public float ox(float width) {
				return 0;
			}

			public float oy(float height) {
				return 0;
			}
		},

		UR {
			public float ox(float width) {
				return width;
			}

			public float oy(float height) {
				return 0;
			}
		},

		LL {
			public float ox(float width) {
				return 0;
			}

			public float oy(float height) {
				return height;
			}
		},

		LR {
			public float ox(float width) {
				return width;
			}

			public float oy(float height) {
				return height;
			}
		},

		TC {
			public float ox(float width) {
				return width / 2;
			}

			public float oy(float height) {
				return 0;
			}
		},

		BC {
			public float ox(float width) {
				return width / 2;
			}

			public float oy(float height) {
				return height;
			}
		},

		LC {
			public float ox(float width) {
				return 0;
			}

			public float oy(float height) {
				return height / 2;
			}
		},

		RC {
			public float ox(float width) {
				return width;
			}

			public float oy(float height) {
				return height / 2;
			}
		};

		public abstract float ox(float width);

		public abstract float oy(float height);
	}

	public interface HitTester {
		Player hitTest(Player l, Vector2f p);
	}

	public final VarView<State> state = Var.create(State.REMOVED);

	public Player() {
		setFlag(Flag.VISIBLE, true);
	}

	public GroupPlayer parent() {
		return _super;
	}

	public Array<Pointer> events() {
		if (events == null) {
			events = new Array<Pointer>();
		}
		return events;
	}

	public boolean hasEventListeners() {
		return events != null && events.size() > 0;
	}

	public boolean interactive() {
		return isSet(Flag.INTERACTIVE);
	}

	public Player setInteractive(boolean interactive) {
		if (interactive() != interactive) {
			if (interactive && _super != null) {
				_super.setInteractive(interactive);
			}
			setFlag(Flag.INTERACTIVE, interactive);
		}
		return this;
	}

	public boolean isVisible() {
		return isSet(Flag.VISIBLE);
	}

	public void setVisible(boolean visible) {
		setFlag(Flag.VISIBLE, visible);
	}

	public boolean isClose() {
		return disposed();
	}

	public boolean disposed() {
		return state.get() == State.DISPOSED;
	}

	public void onAdded(final Port<? super Player> action) {
		onState(State.ADDED, action);
	}

	public void onRemoved(final Port<? super Player> action) {
		onState(State.REMOVED, action);
	}

	public void onDisposed(final Port<? super Player> action) {
		onState(State.DISPOSED, action);
	}

	private void onState(final State tgtState, final Port<? super Player> action) {
		state.connect(new Port<State>() {
			public void onEmit(State state) {
				if (state == tgtState)
					action.onEmit(Player.this);
			}
		});
	}

	@Override
	public void close() {
		if (_super != null) {
			_super.remove(this);
		}
		setState(State.DISPOSED);
		setBatch(null);
	}

	public Affine2f affine() {
		if (isSet(Flag.XFDIRTY)) {
			float sina = MathUtils.sin(_rotation), cosa = MathUtils
					.cos(_rotation);
			float m00 = cosa * scaleX, m01 = sina * scaleY;
			float m10 = -sina * scaleX, m11 = cosa * scaleY;
			float tx = affine.tx(), ty = affine.ty();
			affine.setTransform(m00, m01, m10, m11, tx, ty);
			setFlag(Flag.XFDIRTY, false);
		}
		return affine;
	}

	public float _alpha() {
		return _alpha;
	}

	public Player _alpha(float _alpha) {
		setAlpha(_alpha);
		return this;
	}

	public void setAlpha(float _alpha) {
		this._alpha = _alpha;
		int ialpha = (int) (0xFF * MathUtils.clamp(_alpha, 0, 1));
		this.baseColor = (ialpha << 24) | (baseColor & 0xFFFFFF);
	}

	public int color() {
		return baseColor;
	}

	public Player setColor(int baseColor) {
		this.baseColor = baseColor;
		this._alpha = ((baseColor >> 24) & 0xFF) / 255f;
		return this;
	}

	public float originX() {
		if (isSet(Flag.ODIRTY)) {
			float width = getWidth();
			if (width > 0) {
				this.originX = origin.ox(width);
				this.originY = origin.oy(getHeight());
				setFlag(Flag.ODIRTY, false);
			}
		}
		return originX;
	}

	public float originY() {
		if (isSet(Flag.ODIRTY)) {
			float height = getHeight();
			if (height > 0) {
				this.originX = origin.ox(getWidth());
				this.originY = origin.oy(height);
				setFlag(Flag.ODIRTY, false);
			}
		}
		return originY;
	}

	public Vector2f origin(Vector2f into) {
		return into.set(originX(), originY());
	}

	public Player setOrigin(float x, float y) {
		this.originX = x;
		this.originY = y;
		this.origin = Origin.FIXED;
		setFlag(Flag.ODIRTY, false);
		return this;
	}

	public Player setOrigin(Origin origin) {
		this.origin = origin;
		setFlag(Flag.ODIRTY, true);
		return this;
	}

	public int depth() {
		return _layer;
	}

	@Override
	public void setLayer(int l) {
		this.setDepth(l);
	}

	public Player setDepth(int depth) {
		float oldDepth = this._layer;
		if (depth != oldDepth) {
			this._layer = depth;
			if (_super != null)
				_super.depthChanged(this, oldDepth);
		}
		return this;
	}

	public float tx() {
		return affine.tx();
	}

	public float ty() {
		return affine.ty();
	}

	public Vector2f translation(Vector2f into) {
		return into.set(affine.tx(), affine.ty());
	}

	public Player setTx(float x) {
		affine.setTx(x);
		return this;
	}

	public Player setTy(float y) {
		affine.setTy(y);
		return this;
	}

	public Player setTranslation(float x, float y) {
		affine.setTranslation(x, y);
		return this;
	}

	public Player setTranslation(XY trans) {
		return setTranslation(trans.getX(), trans.getY());
	}

	public float scaleX() {
		return scaleX;
	}

	public float scaleY() {
		return scaleY;
	}

	public Vector2f scale(Vector2f into) {
		return into.set(scaleX, scaleY);
	}

	public void setScale(float scale) {
		setScale(scale, scale);
	}

	public Player setScaleX(float sx) {
		if (scaleX != sx) {
			scaleX = sx;
			setFlag(Flag.XFDIRTY, true);
		}
		return this;
	}

	public Player setScaleY(float sy) {
		if (scaleY != sy) {
			scaleY = sy;
			setFlag(Flag.XFDIRTY, true);
		}
		return this;
	}

	public void setScale(float sx, float sy) {
		if (sx != scaleX || sy != scaleY) {
			scaleX = sx;
			scaleY = sy;
			setFlag(Flag.XFDIRTY, true);
		}
	}

	public float _rotation() {
		return _rotation;
	}

	public Player setToRotation(float angle) {
		setRotation(angle);
		return this;
	}

	public void setRotation(float angle) {
		if (_rotation != angle) {
			_rotation = angle;
			setFlag(Flag.XFDIRTY, true);
		}
		super.setRotation(_rotation);
	}

	protected void setSize(Painter p) {
		if (p != null) {
			setWidth(p.width());
			setHeight(p.height());
		}
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public void setWidth(float w) {
		this.width = w;
	}

	@Override
	public void setHeight(float h) {
		this.height = h;
	}

	public float scaledWidth() {
		return scaleX() * getWidth();
	}

	public float scaledHeight() {
		return scaleX() * getHeight();
	}

	public Player hitTest(Vector2f p) {
		return (hitTester == null) ? hitTestDefault(p) : hitTester.hitTest(
				this, p);
	}

	public Player hitTestDefault(Vector2f p) {
		if (tempRect == null) {
			tempRect = new RectBox(getX() + tx(), getY() + ty(), getWidth()
					* getScaleX(), getHeight() * getScaleY());
		} else {
			tempRect.setBounds(getX() + tx(), getY() + ty(), getWidth()
					* getScaleX(), getHeight() * getScaleY());
		}
		return tempRect.contains(p) ? this : null;
	}

	public Player setHitTester(HitTester tester) {
		hitTester = tester;
		return this;
	}

	public Player getHits() {
		return setHitTester(new Player.HitTester() {
			public Player hitTest(Player l, Vector2f p) {
				Player hit = hitTestDefault(p);
				return (hit == null) ? Player.this : hit;
			}

			@Override
			public String toString() {
				return "<all>";
			}
		});
	}

	public Player setBatch(BaseBatch batch) {
		this.batch = batch;
		return this;
	}

	public final void paint(GLEx gl) {
		if (!isVisible()) {
			return;
		}
		if (_alpha < 0.01) {
			return;
		}
		gl.saveTx();
		int old = gl.combineColor(baseColor);
		BaseBatch obatch = gl.pushBatch(batch);
		gl.concatenate(affine(), originX(), originY());
		try {
			paintImpl(gl);
		} finally {
			gl.popBatch(obatch);
			gl.setColor(old);
			gl.restoreTx();
		}
	}

	protected abstract void paintImpl(GLEx gl);

	protected void setState(State state) {
		((Var<State>) this.state).update(state);
	}

	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder(getName());
		bldr.append(" @ ").append(hashCode());
		bldr.append(" [tx=").append(affine());
		if (hitTester != null) {
			bldr.append(", hitTester=").append(hitTester);
		}
		return bldr.append("]").toString();
	}

	void onAdd() {
		if (disposed()) {
			throw new IllegalStateException("Illegal to use disposed layer: "
					+ this);
		}
		setState(State.ADDED);
	}

	void onRemove() {
		setState(State.REMOVED);
	}

	protected static enum Flag {
		VISIBLE(1 << 0), INTERACTIVE(1 << 1), XFDIRTY(1 << 2), ODIRTY(1 << 3);

		public final int bitmask;

		Flag(int bitmask) {
			this.bitmask = bitmask;
		}
	}

	protected boolean isSet(Flag flag) {
		return (flags & flag.bitmask) != 0;
	}

	protected void setFlag(Flag flag, boolean active) {
		if (active) {
			flags |= flag.bitmask;
		} else {
			flags &= ~flag.bitmask;
		}
	}

	protected void checkOrigin() {
		if (origin != Origin.FIXED) {
			setFlag(Flag.ODIRTY, true);
		}
	}

	protected boolean deactivateOnNoListeners() {
		return true;
	}

	@Override
	public void move_45D_up() {
		move_45D_up(1);
	}

	@Override
	public void move_45D_up(int multiples) {
		_location.set(affine.tx, affine.ty);
		_location.move_multiples(Field2D.UP, multiples);
		affine.setTranslation(_location.x, _location.y);
	}

	@Override
	public void move_45D_left() {
		move_45D_left(1);
	}

	@Override
	public void move_45D_left(int multiples) {
		_location.set(affine.tx, affine.ty);
		_location.move_multiples(Field2D.LEFT, multiples);
		affine.setTranslation(_location.x, _location.y);
	}

	@Override
	public void move_45D_right() {
		move_45D_right(1);
	}

	@Override
	public void move_45D_right(int multiples) {
		_location.set(affine.tx, affine.ty);
		_location.move_multiples(Field2D.RIGHT, multiples);
		affine.setTranslation(_location.x, _location.y);
	}

	@Override
	public void move_45D_down() {
		move_45D_down(1);
	}

	@Override
	public void move_45D_down(int multiples) {
		_location.set(affine.tx, affine.ty);
		_location.move_multiples(Field2D.DOWN, multiples);
		affine.setTranslation(_location.x, _location.y);
	}

	@Override
	public void move_up() {
		move_up(1);
	}

	@Override
	public void move_up(int multiples) {
		_location.set(affine.tx, affine.ty);
		_location.move_multiples(Field2D.TUP, multiples);
		affine.setTranslation(_location.x, _location.y);
	}

	@Override
	public void move_left() {
		move_left(1);
	}

	@Override
	public void move_left(int multiples) {
		_location.set(affine.tx, affine.ty);
		_location.move_multiples(Field2D.TLEFT, multiples);
		affine.setTranslation(_location.x, _location.y);
	}

	@Override
	public void move_right() {
		move_right(1);
	}

	@Override
	public void move_right(int multiples) {
		_location.set(affine.tx, affine.ty);
		_location.move_multiples(Field2D.TRIGHT, multiples);
		affine.setTranslation(_location.x, _location.y);
	}

	@Override
	public void move_down() {
		move_down(1);
	}

	@Override
	public void move_down(int multiples) {
		_location.set(affine.tx, affine.ty);
		_location.move_multiples(Field2D.TDOWN, multiples);
		affine.setTranslation(_location.x, _location.y);
	}

	@Override
	public void move(Vector2f v) {
		_location.set(affine.tx, affine.ty);
		_location.move(v);
		affine.setTranslation(_location.x, _location.y);
	}

	@Override
	public void move(float x, float y) {
		_location.set(affine.tx, affine.ty);
		_location.move(x, y);
		affine.setTranslation(_location.x, _location.y);
	}

	@Override
	public void setLocation(float x, float y) {
		_location.setLocation(x, y);
		affine.setTranslation(_location.x, _location.y);
	}

	@Override
	public int x() {
		return (int) _location.getX();
	}

	@Override
	public int y() {
		return (int) _location.getY();
	}

	@Override
	public float getX() {
		return _location.getX();
	}

	@Override
	public float getY() {
		return _location.getY();
	}

	@Override
	public void setX(Integer x) {
		affine.setTx(x);
		_location.setX(x.intValue());
	}

	@Override
	public void setX(float x) {
		affine.setTx(x);
		_location.setX(x);
	}

	@Override
	public void setY(Integer y) {
		affine.setTy(y);
		_location.setY(y.intValue());
	}

	@Override
	public void setY(float y) {
		affine.setTy(y);
		_location.setY(y);
	}

	@Override
	public void setLocation(Vector2f l) {
		affine.setTranslation(l.x, l.y);
		_location.set(l);
	}

	public RectBox getCollisionBox() {
		return getRect(getLocation().x(), getLocation().y(), getWidth(),
				getHeight());
	}

	@Override
	public Field2D getField2D() {
		return null;
	}

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
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
		return false;
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionBox();
	}

	@Override
	public void update(long elapsedTime) {
		_elapsed = elapsedTime;
	}

	public long getElapsed() {
		return this._elapsed;
	}
}

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
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.geom.Affine2f;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.BaseBatch;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.reply.Act;
import loon.utils.reply.Port;
import loon.utils.reply.Var;
import loon.utils.reply.VarView;

public abstract class Player extends LObject implements LRelease {

	protected int flags;
	protected float depth;

	private String name;
	private GroupPlayer parent;
	private Act<Object> events;
	private HitTester hitTester;
	private BaseBatch batch;

	private float scaleX = 1, scaleY = 1, rotation = 0;
	private final Affine2f affine = new Affine2f();

	private Origin origin = Origin.FIXED;
	private float originX, originY;
	protected int baseColor = LColor.DEF_COLOR;

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
		Player hitTest(Player layer, Vector2f p);
	}

	public final VarView<State> state = Var.create(State.REMOVED);

	public Player() {
		setFlag(Flag.VISIBLE, true);
	}

	public String name() {
		if (name == null) {
			name = getClass().getName();
			name = name.substring(name.lastIndexOf(".") + 1).intern();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GroupPlayer parent() {
		return parent;
	}

	public Act<Object> events() {
		if (events == null)
			events = new Act<Object>() {
				@Override
				protected void connectionAdded() {
					setInteractive(true);
				}

				@Override
				protected void connectionRemoved() {
					if (!hasConnections() && deactivateOnNoListeners()) {
						setInteractive(false);
					}
				}
			};
		return events;
	}

	public boolean hasEventListeners() {
		return events != null && events.hasConnections();
	}

	public boolean interactive() {
		return isSet(Flag.INTERACTIVE);
	}

	public Player setInteractive(boolean interactive) {
		if (interactive() != interactive) {
			if (interactive && parent != null) {
				parent.setInteractive(interactive);
			}
			setFlag(Flag.INTERACTIVE, interactive);
		}
		return this;
	}

	public boolean visible() {
		return isSet(Flag.VISIBLE);
	}

	public Player setVisible(boolean visible) {
		setFlag(Flag.VISIBLE, visible);
		return this;
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
		if (parent != null) {
			parent.remove(this);
		}
		setState(State.DISPOSED);
		setBatch(null);
	}

	public Affine2f affine() {
		if (isSet(Flag.XFDIRTY)) {
			float sina = MathUtils.sin(rotation), cosa = MathUtils
					.cos(rotation);
			float m00 = cosa * scaleX, m01 = sina * scaleY;
			float m10 = -sina * scaleX, m11 = cosa * scaleY;
			float tx = affine.tx(), ty = affine.ty();
			affine.setTransform(m00, m01, m10, m11, tx, ty);
			setFlag(Flag.XFDIRTY, false);
		}
		return affine;
	}

	public float alpha() {
		return alpha;
	}

	public Player alpha(float alpha) {
		setAlpha(alpha);
		return this;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
		int ialpha = (int) (0xFF * MathUtils.clamp(alpha, 0, 1));
		this.baseColor = (ialpha << 24) | (baseColor & 0xFFFFFF);
	}

	public int color() {
		return baseColor;
	}

	public Player setColor(int baseColor) {
		this.baseColor = baseColor;
		this.alpha = ((baseColor >> 24) & 0xFF) / 255f;
		return this;
	}

	public float originX() {
		if (isSet(Flag.ODIRTY)) {
			float width = width();
			if (width > 0) {
				this.originX = origin.ox(width);
				this.originY = origin.oy(height());
				setFlag(Flag.ODIRTY, false);
			}
		}
		return originX;
	}

	public float originY() {
		if (isSet(Flag.ODIRTY)) {
			float height = height();
			if (height > 0) {
				this.originX = origin.ox(width());
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

	public float depth() {
		return depth;
	}

	public Player setDepth(float depth) {
		float oldDepth = this.depth;
		if (depth != oldDepth) {
			this.depth = depth;
			if (parent != null)
				parent.depthChanged(this, oldDepth);
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

	public Player setScale(float scale) {
		return setScale(scale, scale);
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

	public Player setScale(float sx, float sy) {
		if (sx != scaleX || sy != scaleY) {
			scaleX = sx;
			scaleY = sy;
			setFlag(Flag.XFDIRTY, true);
		}
		return this;
	}

	public float rotation() {
		return rotation;
	}

	public Player setToRotation(float angle) {
		setRotation(angle);
		return this;
	}

	public void setRotation(float angle) {
		if(rotation>1f){
			rotation = MathUtils.toRadians(angle);
		}
		if (rotation != angle) {
			rotation = angle;
			setFlag(Flag.XFDIRTY, true);
		}
		if (rect != null) {
			rect = MathUtils.getBounds(location.x, location.y, getWidth(),
					getHeight(), angle, rect);
		}
	}

	@Override
	public int getWidth() {
		return (int) height();
	}

	@Override
	public int getHeight() {
		return (int) width();
	}

	public float width() {
		return 0;
	}

	public float height() {
		return 0;
	}

	public float scaledWidth() {
		return scaleX() * width();
	}

	public float scaledHeight() {
		return scaleX() * height();
	}

	public Player hitTest(Vector2f p) {
		return (hitTester == null) ? hitTestDefault(p) : hitTester.hitTest(
				this, p);
	}

	public Player hitTestDefault(Vector2f p) {
		return (p.x >= 0 && p.y >= 0 && p.x < width() && p.y < height()) ? this
				: null;
	}

	public Player setHitTester(HitTester tester) {
		hitTester = tester;
		return this;
	}

	public Player getHits() {
		return setHitTester(new Player.HitTester() {
			public Player hitTest(Player layer, Vector2f p) {
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
		if (!visible()) {
			return;
		}
		int old = gl.combineColor(baseColor);
		BaseBatch obatch = gl.pushBatch(batch);
		gl.concatenate(affine(), originX(), originY());
		try {
			paintImpl(gl);
		} finally {
			gl.popBatch(obatch);
			gl.setColor(old);
		}
	}

	protected abstract void paintImpl(GLEx gl);

	protected void setState(State state) {
		((Var<State>) this.state).update(state);
	}

	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder(name());
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

	void setParent(GroupPlayer parent) {
		this.parent = parent;
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

	public void move_45D_up() {
		move_45D_up(1);
	}

	public void move_45D_up(int multiples) {
		location.set(affine.tx, affine.ty);
		location.move_multiples(Field2D.UP, multiples);
		affine.setTranslation(location.x, location.y);
	}

	public void move_45D_left() {
		move_45D_left(1);
	}

	public void move_45D_left(int multiples) {
		location.set(affine.tx, affine.ty);
		location.move_multiples(Field2D.LEFT, multiples);
		affine.setTranslation(location.x, location.y);
	}

	public void move_45D_right() {
		move_45D_right(1);
	}

	public void move_45D_right(int multiples) {
		location.set(affine.tx, affine.ty);
		location.move_multiples(Field2D.RIGHT, multiples);
		affine.setTranslation(location.x, location.y);
	}

	public void move_45D_down() {
		move_45D_down(1);
	}

	public void move_45D_down(int multiples) {
		location.set(affine.tx, affine.ty);
		location.move_multiples(Field2D.DOWN, multiples);
		affine.setTranslation(location.x, location.y);
	}

	public void move_up() {
		move_up(1);
	}

	public void move_up(int multiples) {
		location.set(affine.tx, affine.ty);
		location.move_multiples(Field2D.TUP, multiples);
		affine.setTranslation(location.x, location.y);
	}

	public void move_left() {
		move_left(1);
	}

	public void move_left(int multiples) {
		location.set(affine.tx, affine.ty);
		location.move_multiples(Field2D.TLEFT, multiples);
		affine.setTranslation(location.x, location.y);
	}

	public void move_right() {
		move_right(1);
	}

	public void move_right(int multiples) {
		location.set(affine.tx, affine.ty);
		location.move_multiples(Field2D.TRIGHT, multiples);
		affine.setTranslation(location.x, location.y);
	}

	public void move_down() {
		move_down(1);
	}

	public void move_down(int multiples) {
		location.set(affine.tx, affine.ty);
		location.move_multiples(Field2D.TDOWN, multiples);
		affine.setTranslation(location.x, location.y);
	}

	public void move(Vector2f v) {
		location.set(affine.tx, affine.ty);
		location.move(v);
		affine.setTranslation(location.x, location.y);
	}

	public void move(float x, float y) {
		location.set(affine.tx, affine.ty);
		location.move(x, y);
		affine.setTranslation(location.x, location.y);
	}

	public void setLocation(float x, float y) {
		location.setLocation(x, y);
		affine.setTranslation(location.x, location.y);
	}

	public int x() {
		return (int) location.getX();
	}

	public int y() {
		return (int) location.getY();
	}

	public float getX() {
		return location.getX();
	}

	public float getY() {
		return location.getY();
	}

	public void setX(Integer x) {
		affine.setTx(x);
		location.setX(x.intValue());
	}

	public void setX(float x) {
		affine.setTx(x);
		location.setX(x);
	}

	public void setY(Integer y) {
		affine.setTy(y);
		location.setY(y.intValue());
	}

	public void setY(float y) {
		affine.setTy(y);
		location.setY(y);
	}

	public void setLocation(Vector2f l) {
		affine.setTranslation(l.x, l.y);
		location.set(l);
	}
}

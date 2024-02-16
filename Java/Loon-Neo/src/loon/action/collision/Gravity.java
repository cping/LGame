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
package loon.action.collision;

import loon.LRelease;
import loon.LSystem;
import loon.action.ActionBind;
import loon.action.map.Side;
import loon.geom.Circle;
import loon.geom.Ellipse;
import loon.geom.Line;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.Triangle2f;
import loon.geom.Vector2f;
import loon.geom.XY;

/**
 * 自0.3.2版起新增类，用以绑定任意一个LGame对象进行简单的重力牵引操作。
 */
public class Gravity implements LRelease {

	private final RectBox _hitRect = new RectBox();

	protected final Vector2f _oldPos = new Vector2f();

	protected final RectBox _bounds = new RectBox();

	protected float _oldRotate = 0f;

	private final Vector2f _offsetPos = new Vector2f();

	protected boolean _collisioning;

	protected Gravity _collisionObject;

	private Shape _shape;

	private Side _curCollisionDir;

	public Object tag;

	public ActionBind bind;

	public boolean collideSolid;

	public boolean enabled;

	public boolean isSolid;

	public float bounce;

	public boolean limitX;

	public boolean limitY;

	public float gadd;

	public float g;

	public float accelerationX;

	public float accelerationY;

	public float velocityX;

	public float velocityY;

	public float angularVelocity;

	private float damping;

	public String name;

	public Gravity(ActionBind o) {
		this(LSystem.UNKNOWN, o);
	}

	public Gravity(String name, ActionBind o) {
		this(name, o, null);
	}

	public Gravity(String name, ActionBind o, Object tag) {
		this(name, o, tag, o.getX(), o.getY(), o.getWidth(), o.getHeight());
	}

	public Gravity(String name, ActionBind o, Object tag, float x, float y, float w, float h) {
		this.name = name;
		this.tag = tag;
		this.bind = o;
		this.damping = 1f;
		this.enabled = collideSolid = true;
		this._curCollisionDir = new Side();
		this.setBounds(x, y, w, h);
	}

	public Gravity clearSpeed() {
		this.gadd = 0f;
		this.g = 0f;
		this.accelerationX = 0f;
		this.accelerationY = 0f;
		this.velocityX = 0f;
		this.velocityY = 0f;
		return this;
	}

	public Gravity getCollisionObject() {
		return _collisionObject;
	}

	public RectBox getRect() {
		Shape shape = getShape();
		_hitRect.setBounds(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
		return _hitRect;
	}

	protected void initPosRotation() {
		Shape shape = getShape();
		this.setOldPos(shape.getX(), shape.getY());
		this.setOldRotation(shape.getRotation());
	}

	protected void setOldRotation(float angle) {
		this._oldRotate = angle;
	}

	protected void setOldPos(float x, float y) {
		this._oldPos.set(x, y);
	}

	public float getOldRotation() {
		return this._oldRotate;
	}

	public boolean isCollide() {
		return collideSolid;
	}

	public Gravity setCollide(boolean c) {
		this.collideSolid = c;
		return this;
	}

	public float getAccelerationX() {
		return this.accelerationX * this.damping;
	}

	public float getAccelerationY() {
		return this.accelerationY * this.damping;
	}

	public Shape getShape() {
		return _shape == null ? _bounds : _shape;
	}

	public Gravity setShape(Shape s) {
		this._shape = s;
		if (this._bounds != null && this._shape != null) {
			_bounds.setBounds(_shape.getX(), _shape.getY(), _shape.getWidth(), _shape.getHeight());
		}
		return this;
	}

	public float getX() {
		Shape s = getShape();
		if (s != null) {
			return s.getX() + _offsetPos.x;
		}
		return 0f + _offsetPos.x;
	}

	public float getY() {
		Shape s = getShape();
		if (s != null) {
			return s.getY() + _offsetPos.y;
		}
		return 0f + _offsetPos.y;
	}

	public float getScaleX() {
		Shape s = getShape();
		if (s != null) {
			return s.getScaleX();
		}
		return 1f;
	}

	public float getScaleY() {
		Shape s = getShape();
		if (s != null) {
			return s.getScaleY();
		}
		return 1f;
	}

	public float getWidth() {
		Shape s = getShape();
		if (s != null) {
			return s.getWidth();
		}
		return 0f;
	}

	public float getHeight() {
		Shape s = getShape();
		if (s != null) {
			return s.getHeight();
		}
		return 0f;
	}

	public float getRotation() {
		Shape s = getShape();
		if (s != null) {
			return s.getRotation();
		}
		return 0f;
	}

	public Gravity setRotation(float angle) {
		Shape s = getShape();
		if (s != null) {
			s.setRotation(angle);
		}
		return this;
	}

	public Gravity setLocation(float x, float y) {
		getShape().setLocation(_offsetPos.x + x, _offsetPos.y + y);
		return this;
	}

	public Gravity setArea(float x, float y, float w, float h) {
		Shape s = getShape();
		if (s instanceof RectBox) {
			((RectBox) s).setBounds(_offsetPos.x + x, _offsetPos.y + y, w, h);
		} else if (s instanceof Ellipse) {
			((Ellipse) s).setRect(_offsetPos.x + x, _offsetPos.y + y, w, h);
		} else if (s instanceof Circle) {
			((Circle) s).setRect(_offsetPos.x + x, _offsetPos.y + y, w, h);
		} else if (s instanceof Line) {
			((Line) s).set(_offsetPos.x + x, _offsetPos.y + y, w, h);
		} else if (s instanceof Triangle2f) {
			((Triangle2f) s).set(_offsetPos.x + x, _offsetPos.y + y, w, h);
		} else {
			s.setLocation(_offsetPos.x + x, _offsetPos.y + y);
		}
		return this;
	}

	public boolean intersects(float x, float y, float size) {
		return getShape().inPoint(x, y, size);
	}

	public boolean intersects(float x, float y) {
		return getShape().inPoint(x, y, 1f);
	}

	public boolean intersects(float x, float y, float w, float h) {
		return getShape().inRect(x, y, w, h);
	}

	public boolean contains(float x, float y) {
		return getShape().contains(x, y);
	}

	public boolean contains(float x, float y, float w, float h) {
		_hitRect.setBounds(x, y, w, h);
		return getShape().contains(_hitRect);
	}

	public boolean intersects(Gravity g) {
		if (g == null) {
			return false;
		}
		return getShape().intersects(g.getShape());
	}

	public boolean contains(Gravity g) {
		if (g == null) {
			return false;
		}
		return getShape().contains(g.getShape());
	}

	public boolean collided(Gravity g) {
		if (g == null) {
			return false;
		}
		return getShape().collided(g.getShape());
	}

	public boolean collidedBind(Gravity g) {
		if (g == null) {
			return false;
		}
		if (bind == null || g.bind == null) {
			return false;
		}
		return bind.getRectBox().collided(g.bind.getRectBox());
	}

	public boolean containsBind(Gravity g) {
		if (g == null) {
			return false;
		}
		if (bind == null || g.bind == null) {
			return false;
		}
		return bind.getRectBox().contains(g.bind.getRectBox());
	}

	public boolean intersectsBind(Gravity g) {
		if (g == null) {
			return false;
		}
		if (bind == null || g.bind == null) {
			return false;
		}
		return bind.getRectBox().intersects(g.bind.getRectBox());
	}

	public boolean contains(Shape shape) {
		return getShape().contains(shape);
	}

	public boolean intersects(Shape shape) {
		return getShape().intersects(shape);
	}

	public boolean collided(Shape shape) {
		return getShape().collided(shape);
	}

	public boolean isBinded() {
		return this.bind != null;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public Gravity setEnabled(final boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public float getVelocityX() {
		return this.velocityX;
	}

	public float getVelocityY() {
		return this.velocityY;
	}

	public Gravity setVelocityX(final float velocityX) {
		this.velocityX = velocityX;
		return this;
	}

	public Gravity setVelocityY(final float velocityY) {
		this.velocityY = velocityY;
		return this;
	}

	public Gravity setVelocity(final float velocity) {
		return setVelocity(velocity, velocity);
	}

	public Gravity setVelocity(final float velocityX, final float velocityY) {
		this.setVelocityX(velocityX);
		this.setVelocityY(velocityY);
		return this;
	}

	public Gravity drag(final float drag) {
		this.velocityX = (drag * this.velocityX);
		this.velocityY = (drag * this.velocityY);
		return this;
	}

	public boolean isMovingLeft() {
		return this.velocityX < 0f;
	}

	public boolean isMovingRight() {
		return this.velocityX > 0f;
	}

	public boolean isMovingUp() {
		return this.velocityY < 0f;
	}

	public boolean isMovingDown() {
		return this.velocityY > 0f;
	}

	public boolean isLimited() {
		return this.limitX || this.limitY;
	}

	public Gravity setAccelerationX(final float accelerationX) {
		this.accelerationX = accelerationX;
		return this;
	}

	public Gravity setAccelerationY(final float accelerationY) {
		this.accelerationY = accelerationY;
		return this;
	}

	public Gravity setAcceleration(final float accelerationX, final float accelerationY) {
		this.setAccelerationX(accelerationX);
		this.setAccelerationY(accelerationY);
		return this;
	}

	public Gravity setAcceleration(final float acceleration) {
		this.setAcceleration(acceleration, acceleration);
		return this;
	}

	public Gravity accelerate(final float accelerationX, final float accelerationY) {
		this.accelerationX += accelerationX;
		this.accelerationY += accelerationY;
		return this;
	}

	public float getAngularVelocity() {
		return this.angularVelocity * this.damping;
	}

	public void setAngularVelocity(final float angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	public ActionBind getBind() {
		return bind;
	}

	public Object getTag() {
		return tag;
	}

	public String getName() {
		return name;
	}

	public float getG() {
		return g;
	}

	public Gravity setG(float g) {
		this.g = g;
		return this;
	}

	public Gravity setAntiGravityX() {
		return setAntiGravityX(true);
	}

	public Gravity setAntiGravityX(boolean r) {
		float og = this.g;
		float ox = this.velocityX;
		if (r) {
			reset();
		}
		this.g = -og;
		this.velocityX = ox;
		return this;
	}

	public Gravity setAntiGravityY() {
		return setAntiGravityY(true);
	}

	public Gravity setAntiGravityY(boolean r) {
		float og = this.g;
		float oy = this.velocityY;
		if (r) {
			reset();
		}
		this.g = -og;
		this.velocityY = oy;
		return this;
	}

	public float getBounce() {
		return bounce;
	}

	public Gravity setBounce(float bounce) {
		this.bounce = bounce;
		return this;
	}

	public Gravity setBounds(float x, float y, float w, float h) {
		_bounds.setBounds(x, y, w, h);
		return this;
	}

	public int getOppositeSide() {
		return Side.getOppositeSide(getCollisionSide());
	}

	public int getCollisionSide() {
		return getCollisionSide(_collisionObject);
	}

	public int getCollisionSide(Gravity g) {
		if (g == null) {
			return Side.EMPTY;
		}
		if (this.bind == null || g.bind == null) {
			return Side.EMPTY;
		}
		return Side.getCollisionSide(this.bind.getRectBox(), g.bind.getRectBox());
	}

	public int getCollisionSidePos(Gravity g) {
		return getCollisionSidePos(g, 1f);
	}

	public int getCollisionSidePos(Gravity g, float speed) {
		if (g == null) {
			return Side.EMPTY;
		}
		if (this.bind == null || g.bind == null) {
			return Side.EMPTY;
		}
		return Side.getSideFromDirection(Vector2f.at(this.bind.getX(), this.bind.getY()),
				Vector2f.at(g.bind.getX(), g.bind.getY()), speed);
	}

	public int getPointToSelfSide(XY pos) {
		if (pos == null) {
			return Side.EMPTY;
		}
		return Side.getPointCollisionSide(bind.getRectBox(), pos);
	}

	public int getOverlapRect() {
		return getCollisionSide(_collisionObject);
	}

	public RectBox getOverlapRect(Gravity g) {
		if (g == null) {
			return null;
		}
		if (this.bind == null || g.bind == null) {
			return null;
		}
		return Side.getOverlapRect(this.bind.getRectBox(), g.bind.getRectBox());
	}

	public boolean hitInPath(float scale, Gravity other) {
		_hitRect.setBounds(other.getAreaOfTravel(scale));
		return intersects(_hitRect) || _hitRect.overlaps(_bounds);
	}

	public RectBox getAreaOfTravel(float scale) {
		_hitRect.setBounds(getX(), getY(), velocityX * scale + getWidth(), velocityY * scale + getHeight());
		_bounds.normalize(_hitRect);
		return _hitRect;
	}

	public Gravity syncBindToGravity() {
		if (bind != null) {
			_bounds.setLocation(bind.getX(), bind.getY());
			_bounds.setScale(bind.getScaleX(), bind.getScaleY());
			_bounds.setSize(bind.getWidth(), bind.getHeight());
			_bounds.setRotation(bind.getRotation());
		}
		return this;
	}

	public Gravity syncGravityToBind() {
		if (bind != null) {
			bind.setLocation(getX(), getY());
			bind.setScale(getScaleX(), getScaleY());
			bind.setSize(getWidth(), getHeight());
			bind.setRotation(getRotation());
		}
		return this;
	}

	public boolean isCollisioning() {
		return _collisioning;
	}

	public Side getCollisionDirection() {
		return _curCollisionDir;
	}

	public Gravity setDirection(int d) {
		this._curCollisionDir.setDirection(d);
		return this;
	}

	public Gravity updateCollisionDirection() {
		this._curCollisionDir.setDirection(getCollisionSide());
		return this;
	}

	public Gravity reset() {
		this.accelerationX = 0;
		this.accelerationY = 0;
		this.gadd = 0;
		this.g = 0;
		this.bounce = 0;
		this.velocityX = 0;
		this.velocityY = 0;
		this.angularVelocity = 0;
		this._curCollisionDir.reset();
		this._hitRect.setEmpty();
		this._offsetPos.set(0f);
		this._oldPos.set(0f);
		this._bounds.setEmpty();
		this._oldRotate = 0f;
		this._collisioning = false;
		this.limitX = this.limitY = false;
		this.enabled = true;
		return this;
	}

	public Gravity dispose() {
		this.enabled = false;
		this.bind = null;
		return this;
	}

	public boolean isClosed() {
		return bind == null;
	}

	@Override
	public void close() {
		dispose();
	}

}

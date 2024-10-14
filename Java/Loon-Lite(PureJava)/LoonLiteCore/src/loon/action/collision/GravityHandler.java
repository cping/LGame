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
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.Vector2f;
import loon.utils.Easing.EasingMode;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.timer.Duration;
import loon.utils.timer.EaseTimer;

/**
 * 简单的重力控制器,使用时需要绑定Gravity
 */
public class GravityHandler implements LRelease {

	public static interface GravityUpdate {
		public void action(Gravity g, float x, float y);
	}

	private EaseTimer _easeTimer;

	private EasingMode _easingMode;

	private CollisionWorld _collisionWorld;

	protected CollisionFilter worldCollisionFilter;

	private float _objectMaxSpeed;

	private ObjectMap<ActionBind, Gravity> _gravityMap;

	private GravityUpdate _gravitylistener;

	private boolean _closed;

	private float _deviation;

	private boolean _collClearVelocity;

	private float _collScale = 1f;

	private float _bindWidth;

	private float _bindHeight;

	private float _lastX = -1f, _lastY = -1f;

	private float _bindX;

	private float _bindY;

	private float _velocityX, _velocityY;

	private float _gravityScale;

	boolean isBounded;

	boolean isGravityListener;

	boolean isEnabled;

	boolean syncActionBind;

	RectBox rectLimit;

	Gravity[] lazyObjects;

	TArray<Gravity> objects;

	TArray<Gravity> pendingAdd;

	TArray<Gravity> pendingRemove;

	private final TArray<Gravity> collisionObjects = new TArray<Gravity>();

	public GravityHandler() {
		this(EasingMode.Linear, 1f);
	}

	public GravityHandler(int w, int h) {
		this(w, h, EasingMode.Linear, 1f);
	}

	public GravityHandler(EasingMode ease) {
		this(ease, 1f);
	}

	public GravityHandler(EasingMode ease, float duration) {
		this(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), ease, duration);
	}

	public GravityHandler(int w, int h, EasingMode ease) {
		this(w, h, ease, 1f);
	}

	public GravityHandler(float w, float h, EasingMode ease, float duration) {
		this(0f, 0f, w, h, ease, duration);
	}

	public GravityHandler(float x, float y, float w, float h, EasingMode ease, float duration) {
		this.setView(x, y, w, h);
		this._easingMode = ease;
		this._easeTimer = new EaseTimer(duration, _easingMode);
		this._gravityMap = new ObjectMap<ActionBind, Gravity>();
		this._gravityScale = _objectMaxSpeed = 1f;
		this._deviation = LSystem.DEFAULT_EASE_DELAY;
		this.objects = new TArray<Gravity>(10);
		this.pendingAdd = new TArray<Gravity>(10);
		this.pendingRemove = new TArray<Gravity>(10);
		this.lazyObjects = new Gravity[] {};
		this.syncActionBind = true;
		this.isEnabled = true;
	}

	public boolean isGravityRunning() {
		if (objects != null) {
			for (int i = 0; i < objects.size; i++) {
				Gravity g = objects.get(i);
				if (g != null && !g.enabled) {
					return true;
				}
			}
		}
		return false;
	}

	public GravityHandler setView(float x, float y, float w, float h) {
		if (w > 0 && h > 0) {
			setBounded(true);
		} else {
			return this;
		}
		if (rectLimit == null) {
			this.rectLimit = new RectBox(x, y, w, h);
		} else {
			this.rectLimit.setBounds(x, y, w, h);
		}
		return this;
	}

	public GravityHandler setLimit(float w, float h) {
		return setView(rectLimit.getX(), rectLimit.getY(), w, h);
	}

	private float getGravity(Gravity g) {
		return g.g * _gravityScale;
	}

	protected boolean checkCollideSolidObjects(Gravity gravityObject, float delta, float gravity, float newX,
			float newY) {
		if (gravityObject == null) {
			return false;
		}
		if (gravityObject != null && gravityObject.collideSolid) {
			final int size = objects.size;
			for (int i = size - 1; i > -1; i--) {
				Gravity g = objects.get(i);
				if (!gravityObject.isSolid && !g.isSolid || !g.enabled || g == gravityObject) {
					continue;
				}
				if (gravityObject.collided(g)) {
					gravityObject._collisionObject = g;
					gravityObject.updateCollisionDirection();
					_velocityX = gravityObject.velocityX;
					_velocityY = gravityObject.velocityY;
					if (gravityObject.isMovingDown()) {
						gravityObject.bounce = MathUtils.max(gravityObject.bounce, g.bounce);
						if (gravityObject.bounce > 0f) {
							gravityObject.bounce -= (gravityObject.bounce * delta);
							_velocityY = +gravityObject.bounce;
						} else {
							gravityObject.bounce = 0f;
						}
					}
					if (_velocityX > 1f) {
						gravityObject.bind.setX(gravityObject.getX() - _velocityX);
					}
					if (_velocityY > 1f) {
						gravityObject.bind.setY(gravityObject.getY() - _velocityY);
					}
					if (isGravityListener) {
						_gravitylistener.action(g, newX, newY);
					}
					gravityObject._collisioning = true;
					return true;
				}
			}
		}
		gravityObject._collisionObject = null;
		return (gravityObject._collisioning = false);
	}

	public void update(long elapsedTime) {
		if (_closed || !isEnabled) {
			return;
		}
		commits();
		if (objects == null || objects.size == 0) {
			return;
		}

		_easeTimer.action(elapsedTime);

		final float delta = MathUtils.max(Duration.toS(elapsedTime), LSystem.MIN_SECONE_SPEED_FIXED)
				* _easeTimer.getProgress() * _objectMaxSpeed;

		final int size = objects.size;

		for (int i = 0; i < size; i++) {

			Gravity g = objects.get(i);

			if (g.enabled && g.bind != null) {

				final float accelerationX = g.getAccelerationX();
				final float accelerationY = g.getAccelerationY();
				final float angularVelocity = g.getAngularVelocity();
				final float gravity = getGravity(g);

				if (!g._collisioning) {

					g.initPosRotation();

					if (syncActionBind) {
						_bindX = g.bind.getX();
						_bindY = g.bind.getY();
						_bindWidth = g.bind.getWidth();
						_bindHeight = g.bind.getHeight();
						g.setArea(_bindX, _bindY, _bindWidth, _bindHeight);
					} else {
						_bindX = g.getX();
						_bindY = g.getY();
						_bindWidth = g.getWidth();
						_bindHeight = g.getHeight();
					}
					if (angularVelocity != 0) {
						final float rotate = g.bind.getRotation() + angularVelocity * delta;
						Gravity s = g.setRotation(rotate);
						_bindX = s.getX();
						_bindY = s.getY();
						_bindWidth = s.getWidth();
						_bindHeight = s.getHeight();
						g.bind.setRotation(rotate);
					}
				}

				if (accelerationX != 0 || accelerationY != 0) {
					g.velocityX += accelerationX * delta;
					g.velocityY += accelerationY * delta;
				}

				_velocityX = g.velocityX;
				_velocityY = g.velocityY;

				if (_velocityX != 0 || _velocityY != 0) {

					_velocityX = _bindX + (_velocityX * delta);
					_velocityY = _bindY + (_velocityY * delta);

					if (gravity != 0 && g.velocityX != 0) {
						_velocityX += g.gadd;
					}
					if (gravity != 0 && g.velocityY != 0) {
						_velocityY += g.gadd;
					}
					if (gravity != 0) {
						g.gadd += gravity;
					}

					if (isBounded) {
						if (g.bounce != 0f) {
							final float limitWidth = rectLimit.getRight() - _bindWidth;
							final float limitHeight = rectLimit.getBottom() - _bindHeight;
							final boolean chageWidth = _bindX >= limitWidth;
							final boolean chageHeight = _bindY >= limitHeight;
							final float bounce = (g.bounce + gravity);
							if (chageWidth) {
								_bindX -= bounce;
								if (g.bounce > 0) {
									g.bounce -= (bounce * delta) + MathUtils.random(0f, bounce);
								} else if (g.bounce < 0) {
									g.bounce = 0;
									_bindX = limitWidth;
									g.limitX = true;
								}
							}
							if (chageHeight) {
								_bindY -= bounce;
								if (g.bounce > 0) {
									g.bounce -= (bounce * delta) + MathUtils.random(0f, bounce);
								} else if (g.bounce < 0) {
									g.bounce = 0;
									_bindY = limitHeight;
									g.limitY = true;
								}
							}
							if (chageWidth || chageHeight) {
								movePos(g, delta, gravity, _bindX, _bindY);
								return;
							}
						}
						final float limitWidth = rectLimit.getRight() - _bindWidth;
						final float limitHeight = rectLimit.getBottom() - _bindHeight;
						_velocityX = limitValue(g, _velocityX, limitWidth);
						_velocityY = limitValue(g, _velocityY, limitHeight);
					}
					movePos(g, delta, gravity, _velocityX, _velocityY);
				}
			}
		}
	}

	private float limitValue(Gravity g, float value, float limit) {
		if (g.g < 0f) {
			if (value < 0f) {
				value = 0f;
				g.limitX = true;
			}
		}
		if (g.g > 0f) {
			if (limit < value) {
				value = limit;
				g.limitY = true;
			}
		}
		return value;
	}

	protected void commits() {
		if (_closed || !isEnabled) {
			return;
		}
		final int additionCount = pendingAdd.size;
		if (additionCount > 0) {
			for (int i = 0; i < additionCount; i++) {
				Gravity o = pendingAdd.get(i);
				objects.add(o);
			}
			pendingAdd.clear();
		}
		final int removalCount = pendingRemove.size;
		if (removalCount > 0) {
			for (int i = 0; i < removalCount; i++) {
				Gravity o = pendingRemove.get(i);
				objects.remove(o);
			}
			pendingRemove.clear();
		}
	}

	public Gravity[] getObjects() {
		int size = objects.size;
		if (lazyObjects == null || lazyObjects.length != size) {
			lazyObjects = new Gravity[size];
		}
		for (int i = 0; i < size; i++) {
			lazyObjects[i] = objects.get(i);
		}
		return lazyObjects;
	}

	public int getCount() {
		return objects.size;
	}

	public int getConcreteCount() {
		return objects.size + pendingAdd.size - pendingRemove.size;
	}

	public Gravity get(int index) {
		if (index > -1 && index < objects.size) {
			return objects.get(index);
		} else {
			return null;
		}
	}

	public boolean contains(ActionBind o) {
		if (_closed) {
			return false;
		}
		if (o == null) {
			return false;
		}
		return _gravityMap.containsKey(o);
	}

	public boolean contains(Gravity g) {
		if (g == null) {
			return false;
		}
		if (pendingAdd.size > 0) {
			return pendingAdd.contains(g);
		}
		return objects.contains(g);
	}

	public boolean intersect(ActionBind a, ActionBind b) {
		if (a == null || b == null) {
			return false;
		}
		return intersect(get(a), get(b));
	}

	public boolean intersect(Gravity a, Gravity b) {
		if (a == null || b == null) {
			return false;
		}
		return a.getShape().intersects(b.getShape());
	}

	public Gravity intersects(float x, float y) {
		return intersects(x, y, 1f, 1f);
	}

	public Gravity intersects(float x, float y, float w, float h) {
		int size = pendingAdd.size;
		for (int i = 0; i < size; i++) {
			Gravity g = pendingAdd.get(i);
			if (g.intersects(x, y, w, h)) {
				return g;
			}
		}
		size = objects.size;
		for (int i = 0; i < size; i++) {
			Gravity g = objects.get(i);
			if (g.intersects(x, y, w, h)) {
				return g;
			}
		}
		return null;
	}

	public boolean intersect(ActionBind g, float x, float y) {
		if (g == null) {
			return false;
		}
		return intersect(get(g), x, y);
	}

	public boolean intersect(Gravity g, float x, float y) {
		if (g == null) {
			return false;
		}
		return g.intersects(x, y);
	}

	public boolean intersect(ActionBind g, float x, float y, float width, float height) {
		if (g == null) {
			return false;
		}
		return intersect(get(g), x, y, width, height);
	}

	public boolean intersect(Gravity g, float x, float y, float width, float height) {
		if (g == null) {
			return false;
		}
		return g.intersects(x, y, width, height);
	}

	public Gravity contains(float x, float y) {
		return contains(x, y, 1f, 1f);
	}

	public Gravity contains(float x, float y, float w, float h) {
		int size = pendingAdd.size;
		for (int i = 0; i < size; i++) {
			Gravity g = pendingAdd.get(i);
			if (g.contains(x, y, w, h)) {
				return g;
			}
		}
		size = objects.size;
		for (int i = 0; i < size; i++) {
			Gravity g = objects.get(i);
			if (g.contains(x, y, w, h)) {
				return g;
			}
		}
		return null;
	}

	public Gravity collided(Shape s) {
		if (s == null) {
			return null;
		}
		int size = pendingAdd.size;
		for (int i = 0; i < size; i++) {
			Gravity g = pendingAdd.get(i);
			if (g.getShape().collided(s)) {
				return g;
			}
		}
		size = objects.size;
		for (int i = 0; i < size; i++) {
			Gravity g = objects.get(i);
			if (g.getShape().collided(s)) {
				return g;
			}
		}
		return null;
	}

	public Gravity collided(Gravity s) {
		if (s == null) {
			return null;
		}
		int size = pendingAdd.size;
		for (int i = 0; i < size; i++) {
			Gravity g = pendingAdd.get(i);
			if (g.collided(s.getShape())) {
				return g;
			}
		}
		size = objects.size;
		for (int i = 0; i < size; i++) {
			Gravity g = objects.get(i);
			if (g.collided(s.getShape())) {
				return g;
			}
		}
		return null;
	}

	public boolean contains(ActionBind g, float x, float y, float width, float height) {
		if (g == null) {
			return false;
		}
		return contains(get(g), x, y, width, height);
	}

	public boolean contains(Gravity g, float x, float y, float width, float height) {
		if (g == null) {
			return false;
		}
		return g.contains(x, y, width, height);
	}

	public boolean contains(ActionBind a, ActionBind b) {
		if (a == null || b == null) {
			return false;
		}
		return contains(get(a), get(b));
	}

	public boolean contains(Gravity a, Gravity b) {
		if (a == null || b == null) {
			return false;
		}
		return a.contains(b);
	}

	public Gravity add(ActionBind o, float vx, float vy) {
		return add(o, vx, vy, 0);
	}

	public Gravity add(ActionBind o, float vx, float vy, float ave) {
		return add(o, vx, vy, 0, 0, ave);
	}

	public Gravity add(ActionBind o, float vx, float vy, float ax, float ay, float ave) {
		Gravity g = _gravityMap.get(o);
		if (g == null) {
			_gravityMap.put(o, (g = new Gravity(o)));
		}
		g.velocityX = vx;
		g.velocityY = vy;
		g.accelerationX = ax;
		g.accelerationY = ay;
		g.angularVelocity = ave;
		add(g);
		return g;
	}

	public Gravity add(ActionBind o) {
		Gravity g = _gravityMap.get(o);
		if (g == null) {
			_gravityMap.put(o, (g = new Gravity(o)));
		}
		return add(g);
	}

	public Gravity get(ActionBind o) {
		return _gravityMap.get(o);
	}

	public Gravity add(Gravity o) {
		if (o == null) {
			return o;
		}
		if (!pendingAdd.contains(o)) {
			pendingAdd.add(o);
		}
		return o;
	}

	public Gravity remove(Gravity o) {
		if (o == null) {
			return o;
		}
		pendingRemove.add(o);
		return o;
	}

	public void removeAll() {
		final int count = objects.size;
		for (int i = 0; i < count; i++) {
			Gravity g = objects.get(i);
			if (g != null) {
				pendingRemove.add(g);
			}
		}
		pendingAdd.clear();
	}

	public Gravity getObject(String name) {
		commits();
		final int size = objects.size - 1;
		for (int i = size; i > -1; i--) {
			Gravity o = objects.get(i);
			if (o != null && o.name != null) {
				if (o.name.equals(name)) {
					return o;
				}

			}
		}
		return null;
	}

	public GravityResult query(ActionBind bind) {
		return query(bind, rectLimit);
	}

	public GravityResult query(ActionBind bind, RectBox pathBounds) {
		return query(bind, objects, pathBounds);
	}

	public GravityResult query(ActionBind bind, TArray<Gravity> otherObjects, RectBox pathBounds) {
		return query(bind, otherObjects, pathBounds, _collScale, _deviation, _collClearVelocity);
	}

	public GravityResult query(ActionBind bind, TArray<Gravity> otherObjects, RectBox pathBounds,
			boolean clearVelocity) {
		return query(bind, otherObjects, pathBounds, _collScale, _deviation, clearVelocity);
	}

	public GravityResult query(ActionBind bind, TArray<Gravity> otherObjects, RectBox pathBounds, float scale,
			float deviation, boolean clearVelocity) {
		Gravity g = _gravityMap.get(bind);
		if (g != null) {
			return getCollisionBetweenObjects(g, otherObjects, pathBounds, scale, deviation, clearVelocity);
		}
		return new GravityResult();
	}

	public GravityResult query(Gravity target) {
		return getCollisionBetweenObjects(target);
	}

	public GravityResult query(Gravity target, RectBox pathBounds, boolean clearVelocity) {
		return getCollisionBetweenObjects(target, pathBounds, clearVelocity);
	}

	public GravityResult query(Gravity target, boolean clearVelocity) {
		return getCollisionBetweenObjects(target, clearVelocity);
	}

	public GravityResult query(Gravity target, TArray<Gravity> otherObjects) {
		return getCollisionBetweenObjects(target, otherObjects);
	}

	public GravityResult query(Gravity target, TArray<Gravity> otherObjects, RectBox pathBounds) {
		return getCollisionBetweenObjects(target, otherObjects, pathBounds);
	}

	public GravityResult query(Gravity target, TArray<Gravity> otherObjects, RectBox pathBounds,
			boolean clearVelocity) {
		return getCollisionBetweenObjects(target, otherObjects, pathBounds, clearVelocity);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target) {
		return getCollisionBetweenObjects(target, objects, rectLimit, _collClearVelocity);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, RectBox pathBounds, boolean clearVelocity) {
		return getCollisionBetweenObjects(target, objects, pathBounds, clearVelocity);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, boolean clearVelocity) {
		return getCollisionBetweenObjects(target, objects, rectLimit, _collScale, _deviation, clearVelocity);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, TArray<Gravity> otherObjects) {
		return getCollisionBetweenObjects(target, otherObjects, rectLimit);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, TArray<Gravity> otherObjects, RectBox pathBounds) {
		return getCollisionBetweenObjects(target, otherObjects, pathBounds, _collClearVelocity);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, TArray<Gravity> otherObjects, RectBox pathBounds,
			boolean clearVelocity) {
		return getCollisionBetweenObjects(target, otherObjects, pathBounds, _collScale, _deviation, clearVelocity);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, TArray<Gravity> otherObjects, RectBox pathBounds,
			float scale, float deviation, boolean clearVelocity) {

		GravityResult result = new GravityResult();

		result.source = target;

		float remainingVX = target.velocityX * scale * _gravityScale;
		float remainingVY = target.velocityY * scale * _gravityScale;

		final RectBox rect = target.getRect();

		float positionX = rect.x;
		float positionY = rect.y;
		float halfWidth = rect.width * 0.5f;
		float halfHeight = rect.height * 0.5f;
		float moveAmountX = 0;
		float moveAmountY = 0;
		boolean lastIteration = false;

		if (MathUtils.abs(remainingVX) >= MathUtils.abs(remainingVY)) {
			if (MathUtils.abs(remainingVX) > MathUtils.abs(halfWidth)) {
				moveAmountX = halfWidth * remainingVX;
			} else {
				moveAmountX = remainingVX;
			}
			if (MathUtils.abs(remainingVY) > 0) {
				moveAmountY = remainingVY * (remainingVX == 0 ? 1 : MathUtils.abs(remainingVY / remainingVX));
			}
		} else {
			if (MathUtils.abs(remainingVY) > MathUtils.abs(halfHeight)) {
				moveAmountY = halfHeight * remainingVY;
			} else {
				moveAmountY = remainingVY;
			}

			if (MathUtils.abs(remainingVX) > 0) {
				moveAmountX = remainingVX * (remainingVY == 0 ? 1 : MathUtils.abs(remainingVX / remainingVY));
			}
		}
		result.normal.setZero();
		collisionObjects.clear();
		final int len = otherObjects.size;
		for (int i = 0; i < len; i++) {
			Gravity b = otherObjects.get(i);
			if (b != target && pathBounds.contains(b.getShape())) {
				collisionObjects.add(b);
			}
		}
		for (; result.steps <= 1024;) {
			result.steps++;
			final int size = collisionObjects.size;
			for (int i = 0; i < size; i++) {
				Gravity b = collisionObjects.get(i);
				final RectBox rb = b.getRect();
				if (rect.collided(rb)) {
					float overlapX = 0;
					float overlapY = 0;
					Vector2f normal = result.normal;
					normal.setZero();
					if (rect.x <= rb.x) {
						overlapX = (rect.x + rect.width) - rb.x;
						normal.x = -1;
					} else {
						overlapX = (rb.x + rb.width) - rect.x;
						normal.x = 1;
					}
					if (rect.y <= rb.y) {
						overlapY = (rect.y + rect.height) - rb.y;
						normal.y = -1;
					} else {
						overlapY = (rb.y + rb.height) - rect.y;
						normal.y = 1;
					}

					if (MathUtils.abs(overlapX) < MathUtils.abs(overlapY)) {
						normal.y = 0;
					}

					if (MathUtils.abs(overlapY) < MathUtils.abs(overlapX)) {
						normal.x = 0;
					}

					if (MathUtils.abs(overlapX) > rb.width && MathUtils.abs(overlapY) > rb.height) {
						continue;
					}

					if (normal.x == 1) {
						positionX = rb.x + rb.width;
						remainingVX = 0;
						if (clearVelocity) {
							target.velocityX = 0;
						}
						moveAmountX = 0;
					} else if (normal.x == -1) {
						positionX = rb.x - rect.width;
						remainingVX = 0;
						if (clearVelocity) {
							target.velocityX = 0;
						}
						moveAmountX = 0;
					}

					if (normal.y == 1) {
						positionY = rb.y + rb.height;
						remainingVY = 0;
						if (clearVelocity) {
							target.velocityY = 0;
						}
						moveAmountY = 0;
					} else if (normal.y == -1) {
						positionY = rb.y - rect.height;
						remainingVY = 0;
						if (clearVelocity) {
							target.velocityY = 0;
						}
						moveAmountY = 0;
					}
					result.targets.add(b);
					result.collided = true;
				}

				if (positionY + rect.height > rb.y && positionY < rb.y + rb.height) {
					final boolean posResultA = MathUtils.ifloor(positionX + rect.width) == rb.x && remainingVX > 0;
					final boolean posResultB = MathUtils.ifloor(rb.x + rb.width) == positionX && remainingVX < 0;
					if (posResultA || posResultB) {
						if (posResultA && result.normal.x == 0) {
							result.normal.x = -1;
						} else if (posResultB && result.normal.x == 0) {
							result.normal.x = 1;
						}
						remainingVX = 0;
						if (clearVelocity) {
							target.velocityX = 0;
						}
						moveAmountX = 0;
						result.targets.add(b);
						result.collided = true;
					}
				}

				if (positionX + rect.width > rb.x && positionX < rb.x + rb.width) {
					if ((positionY + rect.height == rb.y && remainingVY > 0)
							|| (positionY == rb.y + rb.height && remainingVY < 0)) {

						if ((positionY + rect.height == rb.y && remainingVY > 0) && result.normal.y == 0) {
							result.normal.y = -1;
						} else if ((positionY == rb.y + rb.height && remainingVY < 0) && result.normal.y == 0) {
							result.normal.y = 1;
						}

						remainingVY = 0;
						if (clearVelocity) {
							target.velocityY = 0;
						}
						moveAmountY = 0;
						result.targets.add(b);
						result.collided = true;
					}
				}

			}

			if (!lastIteration) {
				if (MathUtils.abs(remainingVX) < MathUtils.abs(moveAmountX)) {
					moveAmountX = remainingVX;
				}
				if (MathUtils.abs(remainingVY) < MathUtils.abs(moveAmountY)) {
					moveAmountY = remainingVY;
				}
				positionX += moveAmountX;
				positionY += moveAmountY;

				remainingVX -= moveAmountX;
				remainingVY -= moveAmountY;
			}

			if (!lastIteration && MathUtils.isEqual(0, remainingVX, deviation)
					&& MathUtils.isEqual(0, remainingVY, deviation)) {
				lastIteration = true;
				remainingVX = 0;
				remainingVY = 0;
			} else if (lastIteration) {
				break;
			}
			if (!clearVelocity) {
				break;
			}
		}
		result.position.set(positionX, positionY);
		return result;

	}

	protected GravityHandler movePos(Gravity g, float delta, float gravity, float x, float y) {
		if (_collisionWorld == null) {
			return movePos(g, delta, gravity, x, y, _lastX, _lastY);
		}
		return movePos(g, delta, gravity, x, y, -1f, -1f);
	}

	protected GravityHandler movePos(Gravity g, float delta, float gravity, float x, float y, float lastX,
			float lastY) {
		if (g == null) {
			return this;
		}
		ActionBind bind = g.bind;
		if (bind == null) {
			return this;
		}
		if (g.enabled) {
			if (checkCollideSolidObjects(g, delta, gravity, x, y)) {
				return this;
			}
			if (_collisionWorld != null) {
				if (worldCollisionFilter == null) {
					worldCollisionFilter = CollisionFilter.getDefault();
				}
				CollisionResult.Result result = _collisionWorld.move(bind, x, y, worldCollisionFilter);
				if (lastX != -1 && lastY != -1) {
					if (result.goalX != x || result.goalY != y) {
						bind.setLocation(lastX, lastY);
					} else {
						bind.setLocation(result.goalX, result.goalY);
					}
				} else {
					bind.setLocation(result.goalX, result.goalY);
				}
			} else {
				bind.setLocation(x, y);
			}
		}
		if (isGravityListener) {
			_gravitylistener.action(g, x, y);
		}
		this._lastX = x;
		this._lastY = y;
		return this;
	}

	public boolean isOverlapping(Vector2f pA, Vector2f sA, Vector2f pB, Vector2f sB) {
		return MathUtils.abs(pA.x - pB.x) * 2 < sA.x + sB.x && MathUtils.abs(pA.y - pB.y) * 2 < sA.y + sB.y;
	}

	public float getLastX() {
		return _lastX;
	}

	public float getLastY() {
		return _lastY;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isBounded() {
		return isBounded;
	}

	public void setBounded(boolean isBounded) {
		this.isBounded = isBounded;
	}

	public boolean isListener() {
		return isGravityListener;
	}

	public GravityHandler setListener(GravityUpdate listener) {
		return setGravityListener(listener);
	}

	public GravityHandler setGravityListener(GravityUpdate listener) {
		return onUpdate(listener);
	}

	public GravityHandler onUpdate(GravityUpdate listener) {
		this._gravitylistener = listener;
		this.isGravityListener = _gravitylistener != null;
		return this;
	}

	public CollisionFilter getCollisionFilter() {
		return worldCollisionFilter;
	}

	public void setCollisionFilter(CollisionFilter filter) {
		this.worldCollisionFilter = filter;
	}

	public CollisionWorld getCollisionWorld() {
		return _collisionWorld;
	}

	public GravityHandler setCollisionWorld(CollisionWorld world) {
		this._collisionWorld = world;
		return this;
	}

	public EasingMode getEasingMode() {
		return _easingMode;
	}

	public GravityHandler setEasingMode(EasingMode ease) {
		_easeTimer.setEasingMode(ease);
		return this;
	}

	public boolean isSyncBind() {
		return syncActionBind;
	}

	public GravityHandler setSyncBind(boolean syncBind) {
		this.syncActionBind = syncBind;
		return this;
	}

	public float getObjectMaxSpeed() {
		return _objectMaxSpeed;
	}

	public GravityHandler setObjectMaxSpeed(float speed) {
		this._objectMaxSpeed = speed;
		return this;
	}

	public boolean isClosed() {
		return _closed;
	}

	public float getDeviation() {
		return _deviation;
	}

	public GravityHandler setDeviation(float d) {
		this._deviation = d;
		return this;
	}

	public boolean isCollisionClearVelocity() {
		return _collClearVelocity;
	}

	public GravityHandler setCollisionClearVelocity(boolean c) {
		this._collClearVelocity = c;
		return this;
	}

	public float getCollisionScale() {
		return _collScale;
	}

	public GravityHandler setCollisionScale(float s) {
		this._collScale = s;
		return this;
	}

	@Override
	public void close() {
		this.isEnabled = false;
		if (objects != null) {
			objects.clear();
			objects = null;
		}
		if (pendingAdd != null) {
			pendingAdd.clear();
			pendingAdd = null;
		}
		if (pendingAdd != null) {
			pendingAdd.clear();
			pendingAdd = null;
		}
		if (_gravityMap != null) {
			_gravityMap.clear();
			_gravityMap = null;
		}
		lazyObjects = null;
		_closed = true;
	}

}

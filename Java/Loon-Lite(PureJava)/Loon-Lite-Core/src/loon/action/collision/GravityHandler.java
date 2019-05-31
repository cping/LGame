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
import loon.geom.Vector2f;
import loon.utils.Easing.EasingMode;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.cache.Pool;
import loon.utils.timer.EaseTimer;

/**
 * 简单的重力控制器,使用时需要绑定Gravity
 */
public class GravityHandler implements LRelease {

	public static interface GravityUpdate {
		public void action(Gravity g, float x, float y);
	}

	private final Pool<GravityResult> resultGravity = new Pool<GravityResult>() {

		@Override
		protected GravityResult newObject() {
			return new GravityResult();
		}

	};

	private EaseTimer easeTimer;

	private EasingMode easingMode;

	private CollisionWorld collisionWorld;

	protected CollisionFilter worldCollisionFilter;

	private ObjectMap<ActionBind, Gravity> gravityMap;

	private GravityUpdate listener;

	private boolean closed;

	private int width, height;

	private float bindWidth;

	private float bindHeight;

	private float bindX;

	private float bindY;

	private float velocityX, velocityY;

	boolean isBounded;

	boolean isListener;

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

	public GravityHandler(int w, int h, EasingMode ease, float duration) {
		this.setLimit(w, h);
		this.easingMode = ease;
		this.easeTimer = new EaseTimer(duration, easingMode);
		this.gravityMap = new ObjectMap<ActionBind, Gravity>();
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

	public GravityHandler setLimit(int w, int h) {
		if (w > 0 && h > 0) {
			setBounded(true);
		} else {
			return this;
		}
		this.width = w;
		this.height = h;
		if (rectLimit == null) {
			this.rectLimit = new RectBox(0, 0, w, h);
		} else {
			this.rectLimit.setBounds(0, 0, w, h);
		}
		return this;
	}

	public void update(long elapsedTime) {
		if (closed || !isEnabled) {
			return;
		}
		commits();
		if (objects == null || objects.size == 0) {
			return;
		}

		easeTimer.action(elapsedTime);

		final float delta = MathUtils.max(elapsedTime / 1000f, 0.01f) * easeTimer.getProgress();

		for (Gravity g : objects) {

			if (g.enabled && g.bind != null) {

				final float accelerationX = g.accelerationX;
				final float accelerationY = g.accelerationY;
				final float angularVelocity = g.angularVelocity;
				final float gravity = g.g;

				if (syncActionBind) {
					bindX = g.bind.getX();
					bindY = g.bind.getY();
					bindWidth = g.bind.getWidth();
					bindHeight = g.bind.getHeight();
					g.bounds.setBounds(bindX, bindY, bindWidth, bindHeight);
				} else {
					bindX = g.bounds.getX();
					bindY = g.bounds.getY();
					bindWidth = g.bounds.getWidth();
					bindHeight = g.bounds.getHeight();
				}

				if (angularVelocity != 0) {

					final float rotate = g.bind.getRotation() + angularVelocity * delta;
					int[] newObjectRect = MathUtils.getLimit(bindX, bindY, bindWidth, bindHeight, rotate);

					bindWidth = newObjectRect[2];
					bindHeight = newObjectRect[3];

					newObjectRect = null;

					g.bind.setRotation(rotate);
				}

				if (accelerationX != 0 || accelerationY != 0) {
					g.velocityX += accelerationX * delta;
					g.velocityY += accelerationY * delta;
				}

				velocityX = g.velocityX;
				velocityY = g.velocityY;
				if (velocityX != 0 || velocityY != 0) {

					velocityX = bindX + (velocityX * delta);
					velocityY = bindY + (velocityY * delta);

					if (gravity != 0 && g.velocityX != 0) {
						velocityX += g.gadd;
					}
					if (gravity != 0 && g.velocityY != 0) {
						velocityY += g.gadd;
					}
					if (gravity != 0) {
						g.gadd += gravity;
					}

					if (isBounded) {
						if (g.bounce != 0f) {
							final float limitWidth = width - bindWidth;
							final float limitHeight = height - bindHeight;
							final boolean chageWidth = bindX >= limitWidth;
							final boolean chageHeight = bindY >= limitHeight;
							if (chageWidth) {
								bindX -= g.bounce + gravity;
								if (g.bounce > 0) {
									g.bounce -= (g.bounce + delta) + MathUtils.random(0f, 5f);
								} else if (g.bounce < 0) {
									g.bounce = 0;
									bindX = limitWidth;
									g.limitX = true;
								}
							}
							if (chageHeight) {
								bindY -= g.bounce + gravity;
								if (g.bounce > 0) {
									g.bounce -= (g.bounce + delta) + MathUtils.random(0f, 5f);
								} else if (g.bounce < 0) {
									g.bounce = 0;
									bindY = limitHeight;
									g.limitY = true;
								}
							}
							if (chageWidth || chageHeight) {
								movePos(g, bindX, bindY);
								if (isListener) {
									listener.action(g, bindX, bindY);
								}
								return;
							}
						}
						float limitWidth = width - bindWidth;
						float limitHeight = height - bindHeight;
						velocityX = limitValue(g, velocityX, limitWidth);
						velocityY = limitValue(g, velocityY, limitHeight);
					}
					movePos(g, velocityX, velocityY);
					if (isListener) {
						listener.action(g, velocityX, velocityY);
					}
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
		if (closed || !isEnabled) {
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
		if (closed) {
			return false;
		}
		if (o == null) {
			return false;
		}
		return gravityMap.containsKey(o);
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
		if (a.bounds != null && a.bounds != null) {
			return CollisionHelper.intersects(a.bounds.x, a.bounds.y, a.bounds.getWidth(), a.bounds.getHeight(),
					b.bounds.x, b.bounds.y, b.bounds.getWidth(), b.bounds.getHeight());
		}
		return false;
	}

	public Gravity intersects(float x, float y) {
		return intersects(x, y, 1f, 1f);
	}

	public Gravity intersects(float x, float y, float w, float h) {
		int size = pendingAdd.size;
		for (int i = 0; i < size; i++) {
			Gravity g = pendingAdd.get(i);
			if (g.bounds != null) {
				if (g.bounds.intersects(x, y, w, h)) {
					return g;
				}
			}
		}
		size = objects.size;
		for (int i = 0; i < size; i++) {
			Gravity g = objects.get(i);
			if (g.bounds != null) {
				if (g.bounds.intersects(x, y, w, h)) {
					return g;
				}
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
		if (g.bounds != null) {
			return CollisionHelper.intersect(g.bounds, x, y);
		}
		return false;
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
		if (g.bounds != null) {
			return CollisionHelper.intersects(g.bounds.x, g.bounds.y, g.bounds.getWidth(), g.bounds.getHeight(), x, y,
					width, height);
		}
		return false;
	}

	public Gravity contains(float x, float y) {
		return contains(x, y, 1f, 1f);
	}

	public Gravity contains(float x, float y, float w, float h) {
		int size = pendingAdd.size;
		for (int i = 0; i < size; i++) {
			Gravity g = pendingAdd.get(i);
			if (g.bounds != null) {
				if (g.bounds.contains(x, y, w, h)) {
					return g;
				}
			}
		}
		size = objects.size;
		for (int i = 0; i < size; i++) {
			Gravity g = objects.get(i);
			if (g.bounds != null) {
				if (g.bounds.contains(x, y, w, h)) {
					return g;
				}
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
		if (g.bounds != null) {
			return CollisionHelper.contains(g.bounds.x, g.bounds.y, g.bounds.getWidth(), g.bounds.getHeight(), x, y,
					width, height);
		}
		return false;
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
		if (a.bounds != null && b.bounds != null) {
			return CollisionHelper.contains(a.bounds.x, a.bounds.y, a.bounds.getWidth(), a.bounds.getHeight(),
					b.bounds.x, b.bounds.y, b.bounds.getWidth(), b.bounds.getHeight());
		}
		return false;
	}

	public Gravity add(ActionBind o, float vx, float vy) {
		return add(o, vx, vy, 0);
	}

	public Gravity add(ActionBind o, float vx, float vy, float ave) {
		return add(o, vx, vy, 0, 0, ave);
	}

	public Gravity add(ActionBind o, float vx, float vy, float ax, float ay, float ave) {
		Gravity g = gravityMap.get(o);
		if (g == null) {
			gravityMap.put(o, (g = new Gravity(o)));
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
		Gravity g = gravityMap.get(o);
		if (g == null) {
			gravityMap.put(o, (g = new Gravity(o)));
		}
		return add(g);
	}

	public Gravity get(ActionBind o) {
		return gravityMap.get(o);
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
		for (Gravity o : objects) {
			if (o != null && o.name != null) {
				if (o.name.equals(name)) {
					return o;
				}

			}
		}
		return null;
	}

	public GravityResult getCollisionBetweenObjects(Gravity target) {
		return getCollisionBetweenObjects(target, objects, rectLimit, false);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, RectBox pathBounds, boolean clearVelocity) {
		return getCollisionBetweenObjects(target, objects, pathBounds, clearVelocity);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, boolean clearVelocity) {
		return getCollisionBetweenObjects(target, objects, rectLimit, 1f, 1f / 100f, clearVelocity);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, TArray<Gravity> otherObjects) {
		return getCollisionBetweenObjects(target, otherObjects, rectLimit);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, TArray<Gravity> otherObjects, RectBox pathBounds) {
		return getCollisionBetweenObjects(target, otherObjects, pathBounds, false);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, TArray<Gravity> otherObjects, RectBox pathBounds,
			boolean clearVelocity) {
		return getCollisionBetweenObjects(target, otherObjects, pathBounds, 1f, 1f / 100f, clearVelocity);
	}

	public GravityResult getCollisionBetweenObjects(Gravity target, TArray<Gravity> otherObjects, RectBox pathBounds,
			float scale, float deviation, boolean clearVelocity) {

		GravityResult result = resultGravity.obtain();

		float remainingVX = target.velocityX * scale;
		float remainingVY = target.velocityY * scale;
		float positionX = target.bounds.x;
		float positionY = target.bounds.y;
		float halfWidth = target.bounds.width * 0.5f;
		float halfHeight = target.bounds.height * 0.5f;
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

		for (Gravity b : otherObjects) {
			if (b.bounds.overlaps(pathBounds)) {
				collisionObjects.add(b);
			}
		}

		for (;;) {
			result.steps++;
			for (Gravity b : collisionObjects) {
				if (CollisionHelper.intersects(positionX, positionY, target.bounds.width, target.bounds.height,
						b.bounds.x, b.bounds.y, b.bounds.width, b.bounds.height, false)) {

					float overlapX = 0;
					float overlapY = 0;
					Vector2f normal = result.normal;
					normal.setZero();

					if (target.bounds.x <= b.bounds.x) {
						overlapX = (target.bounds.x + target.bounds.width) - b.bounds.x;
						normal.x = -1;
					} else {
						overlapX = (b.bounds.x + b.bounds.width) - target.bounds.x;
						normal.x = 1;
					}

					if (target.bounds.y <= b.bounds.y) {
						overlapY = (target.bounds.y + target.bounds.height) - b.bounds.y;
						normal.y = -1;
					} else {
						overlapY = (b.bounds.y + b.bounds.height) - target.bounds.y;
						normal.y = 1;
					}

					if (MathUtils.abs(overlapX) < MathUtils.abs(overlapY)) {
						normal.y = 0;
					}

					if (MathUtils.abs(overlapY) < MathUtils.abs(overlapX)) {
						normal.x = 0;
					}

					if (MathUtils.abs(overlapX) > b.bounds.width && MathUtils.abs(overlapY) > b.bounds.height) {
						continue;
					}

					if (normal.x == 1) {
						positionX = b.bounds.x + b.bounds.width;
						remainingVX = 0;
						if (clearVelocity) {
							target.velocityX = 0;
						}
						moveAmountX = 0;
					} else if (normal.x == -1) {
						positionX = b.bounds.x - target.bounds.width;
						remainingVX = 0;
						if (clearVelocity) {
							target.velocityX = 0;
						}
						moveAmountX = 0;
					}

					if (normal.y == 1) {
						positionY = b.bounds.y + b.bounds.height;
						remainingVY = 0;
						if (clearVelocity) {
							target.velocityY = 0;
						}
						moveAmountY = 0;
					} else if (normal.y == -1) {
						positionY = b.bounds.y - target.bounds.height;
						remainingVY = 0;
						if (clearVelocity) {
							target.velocityY = 0;
						}
						moveAmountY = 0;
					}

					result.isCollided = true;
				}

				if (positionY + target.bounds.height > b.bounds.y && positionY < b.bounds.y + b.bounds.height) {
					if ((positionX + target.bounds.width == b.bounds.x && remainingVX > 0)
							|| (positionX == b.bounds.x + b.bounds.width && remainingVX < 0)) {
						if ((positionX + target.bounds.width == b.bounds.x && remainingVX > 0)
								&& result.normal.x == 0) {
							result.normal.x = -1;
						} else if ((positionX == b.bounds.x + b.bounds.width && remainingVX < 0)
								&& result.normal.x == 0) {
							result.normal.x = 1;
						}
						remainingVX = 0;
						if (clearVelocity) {
							target.velocityX = 0;
						}
						moveAmountX = 0;
						result.isCollided = true;
					}
				}

				if (positionX + target.bounds.width > b.bounds.x && positionX < b.bounds.x + b.bounds.width) {
					if ((positionY + target.bounds.height == b.bounds.y && remainingVY > 0)
							|| (positionY == b.bounds.y + b.bounds.height && remainingVY < 0)) {

						if ((positionY + target.bounds.height == b.bounds.y && remainingVY > 0)
								&& result.normal.y == 0) {
							result.normal.y = -1;
						} else if ((positionY == b.bounds.y + b.bounds.height && remainingVY < 0)
								&& result.normal.y == 0) {
							result.normal.y = 1;
						}

						remainingVY = 0;
						if (clearVelocity) {
							target.velocityY = 0;
						}
						moveAmountY = 0;
						result.isCollided = true;
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
		}
		result.position.set(positionX, positionY);
		collisionObjects.clear();
		return result;
	}

	public GravityHandler movePos(Gravity g, float x, float y) {
		return movePos(g, x, y, -1f, -1f);
	}

	public GravityHandler movePos(Gravity g, float x, float y, float lastX, float lastY) {
		if (g == null) {
			return this;
		}
		ActionBind bind = g.bind;
		if (bind == null) {
			return this;
		}
		if (collisionWorld != null) {
			if (worldCollisionFilter == null) {
				worldCollisionFilter = CollisionFilter.getDefault();
			}
			CollisionResult.Result result = collisionWorld.move(bind, x, y, worldCollisionFilter);
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
		return this;
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
		return isListener;
	}

	public GravityHandler setListener(GravityUpdate listener) {
		return onUpdate(listener);
	}

	public GravityHandler onUpdate(GravityUpdate listener) {
		this.listener = listener;
		this.isListener = listener != null;
		return this;
	}

	public CollisionFilter getCollisionFilter() {
		return worldCollisionFilter;
	}

	public void setCollisionFilter(CollisionFilter filter) {
		this.worldCollisionFilter = filter;
	}

	public CollisionWorld getCollisionWorld() {
		return collisionWorld;
	}

	public void setCollisionWorld(CollisionWorld world) {
		this.collisionWorld = world;
	}

	public EasingMode getEasingMode() {
		return easingMode;
	}

	public GravityHandler setEasingMode(EasingMode ease) {
		easeTimer.setEasingMode(ease);
		return this;
	}

	public boolean isSyncBind() {
		return syncActionBind;
	}

	public void setSyncBind(boolean syncBind) {
		this.syncActionBind = syncBind;
	}

	public boolean isClosed() {
		return closed;
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
		if (gravityMap != null) {
			gravityMap.clear();
			gravityMap = null;
		}
		lazyObjects = null;
		closed = true;
	}

}

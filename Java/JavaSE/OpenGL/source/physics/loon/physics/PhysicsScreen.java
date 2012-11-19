package loon.physics;

import java.util.ArrayList;

import loon.action.sprite.SpriteBatch;
import loon.core.LSystem;
import loon.core.event.Updateable;
import loon.core.geom.FloatValue;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LTouch;
import loon.core.timer.LTimerContext;
import loon.utils.MathUtils;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * Copyright 2008 - 2010
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public abstract class PhysicsScreen extends Screen implements ContactListener,
		PolygonType {

	public PhysicsFixtureDefBuilder createPhysicsFixtureDefBuilder() {
		return new PhysicsFixtureDefBuilder();
	}

	public PhysicsBodyBuilder createPhysicsBodyBuilder() {
		return new PhysicsBodyBuilder(world);
	}

	private SpriteBatch batch;

	public static final int VELOCITY_ITERATIONS_DEFAULT = 8;

	public static final int POSITION_ITERATIONS_DEFAULT = 8;

	private ArrayList<PhysicsObject> objects = new ArrayList<PhysicsObject>();

	protected int velocityIters = VELOCITY_ITERATIONS_DEFAULT;

	protected int positionIters = POSITION_ITERATIONS_DEFAULT;

	protected static World world;

	private Vector2f gravity;

	private WorldBox worldBox;

	private boolean useContactListener, useBatch;

	public final FloatValue TimeStep = new FloatValue(.5f);

	private Thread physicsThread;

	public PhysicsScreen(RectBox rect) {
		this(rect, 0, 0, true, VELOCITY_ITERATIONS_DEFAULT,
				POSITION_ITERATIONS_DEFAULT);
	}

	public PhysicsScreen(RectBox rect, int velocityIterations,
			int positionIterations) {
		this(rect, 0, 0, true, velocityIterations, positionIterations);
	}

	public PhysicsScreen(RectBox rect, float gx, float gy, boolean doSleep) {
		this(rect, new Vector2f(gx, gy), doSleep, VELOCITY_ITERATIONS_DEFAULT,
				POSITION_ITERATIONS_DEFAULT);
	}

	public PhysicsScreen(RectBox rect, float gx, float gy, boolean doSleep,
			int velocityIterations, int positionIterations) {
		this(rect, new Vector2f(gx, gy), doSleep, velocityIterations,
				positionIterations);
	}

	public PhysicsScreen(final RectBox rect, Vector2f g, boolean doSleep,
			int v, int p) {
		if (PhysicsScreen.world != null) {
			throw new RuntimeException("Physical world is not the only !");
		}
		this.gravity = g;
		this.velocityIters = v;
		this.positionIters = p;
		PhysicsScreen.world = new World(gravity, doSleep);
		worldBox = new WorldBox(world, rect);
		this.physicsThread = new Thread() {
			public void run() {
				for (; !isClose();) {
					yieldUpdate();
					if (LSystem.isPaused) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
					} else {
						synchronized (PhysicsScreen.class) {
							world.step(TimeStep.get(), velocityIters,
									positionIters);
						}
					}
				}
			}
		};
		callEvent(physicsThread);
	}

	public void begineContactListener() {
		synchronized (objects) {
			PhysicsScreen.world.setContactListener(this);
			this.useContactListener = true;
		}
	}

	public void endContactListener() {
		synchronized (objects) {
			PhysicsScreen.world.setContactListener(null);
			this.useContactListener = false;
		}
	}

	public PhysicsObject find(int x, int y) {
		synchronized (objects) {
			if (objects.size() == 0) {
				return null;
			}
			int size = objects.size() - 1;
			for (int i = size; i >= 0; i--) {
				PhysicsObject child = objects.get(i);
				RectBox rect = child.getCollisionBox();
				if (rect != null && rect.contains(x, y)) {
					return child;
				}
			}
			return null;
		}
	}

	public PhysicsObject find(int x, int y, Object tag) {
		synchronized (objects) {
			if (objects.size() == 0) {
				return null;
			}
			int size = objects.size() - 1;
			for (int i = size; i >= 0; i--) {
				PhysicsObject child = objects.get(i);
				if (child.getTag() == tag) {
					RectBox rect = child.getCollisionBox();
					if (rect != null && rect.contains(x, y)) {
						return child;
					}
				}
			}
			return null;
		}
	}

	public PhysicsObject bindTo(String fileName, Body body) {
		synchronized (objects) {
			PhysicsObject o = new PhysicsObject(fileName, body);
			addObject(o);
			return o;
		}
	}

	public PhysicsObject bindTo(LTexture bitmap, Body body) {
		synchronized (objects) {
			PhysicsObject o = new PhysicsObject(bitmap, body);
			addObject(o);
			return o;
		}
	}

	public PhysicsObject bindTo(String fileName, BodyType type, int x, int y) {
		synchronized (objects) {
			PhysicsObject o = new PhysicsObject(fileName, type, x, y);
			addObject(o);
			return o;
		}
	}

	public PhysicsObject bindTo(LTexture bitmap, BodyType type, int x, int y) {
		synchronized (objects) {
			PhysicsObject o = new PhysicsObject(bitmap, type, x, y);
			addObject(o);
			return o;
		}
	}

	public PhysicsObject bindTo(int polyType, BodyType type, int x, int y,
			int w, int h) {
		synchronized (objects) {
			PhysicsObject o = new PhysicsObject(polyType, type, x, y, w, h);
			addObject(o);
			return o;
		}
	}

	public boolean addObject(PhysicsObject o) {
		synchronized (objects) {
			return objects.add(o);
		}
	}

	public void removeObject(PhysicsObject o) {
		synchronized (objects) {
			objects.remove(o);
			o.dispose();
		}
	}

	final public void touchDown(final LTouch e) {
		yieldDraw();
		synchronized (physicsThread) {
			Updateable update = new Updateable() {
				public void action() {
					onDown(e);
				}
			};
			LSystem.load(update);
		}
	}

	public abstract void onDown(LTouch e);

	final public void touchMove(final LTouch e) {
		yieldDraw();
		synchronized (physicsThread) {
			Updateable update = new Updateable() {
				public void action() {
					onMove(e);
				}
			};
			LSystem.load(update);
		}
	}

	public abstract void onMove(LTouch e);

	final public void touchUp(final LTouch e) {
		synchronized (physicsThread) {
			onUp(e);
		}
	}

	public abstract void onUp(LTouch e);

	final public void touchDrag(final LTouch e) {
		yieldDraw();
		synchronized (physicsThread) {
			Updateable update = new Updateable() {
				public void action() {
					onDrag(e);
				}
			};
			LSystem.load(update);
		}
	}

	public abstract void onDrag(LTouch e);

	public static Body createBody(BodyDef bodyDef) {
		return world.createBody(bodyDef);
	}

	public static void destroyBody(Body body) {
		world.destroyBody(body);
	}

	public static Joint createJoint(JointDef def) {
		return world.createJoint(def);
	}

	public static void destroyJoint(Joint joint) {
		world.destroyJoint(joint);
	}

	public static World getWorld() {
		return world;
	}

	public void setGravity(int x, int y) {
		gravity.set(x, y);
		world.setGravity(gravity);
	}

	public Vector2f getGravity() {
		return gravity;
	}

	final public void draw(GLEx g) {
		yieldDraw();
		if (isOnLoadComplete()) {
			if (!worldBox.isBuild()) {
				worldBox.build();
			}
			if (!useBatch) {
				if (batch == null) {
					batch = new SpriteBatch(1000);
				}
				useBatch = true;
			}
			synchronized (physicsThread) {
				batch.begin();
				int size = objects.size();
				for (int i = size - 1; i >= 0; i--) {
					PhysicsObject o = objects.get(i);
					o.update(elapsedTime);
					LTexture texture = o.texture;
					if (texture == null) {
						continue;
					}
					drawObjectGL(batch, texture, o);
				}
				paint(batch);
				batch.end();
			}
		}
	}

	private LColor alphaColor = new LColor(LColor.white);

	private void drawObjectGL(SpriteBatch g, LTexture tex2d, PhysicsObject o) {
		if (o.useMake && o.visible) {
			if (o.body == null) {
				return;
			}
			Vector2f v = o.body.getPosition();
			final float rotation = (o.body.getAngle() * MathUtils.RAD_TO_DEG) % 360;
			o.x = v.x;
			o.y = v.y;
			if (!o.useImage) {
				switch (o.polyType) {
				case Box:
				case Circle:
					o.x = o.x - o.halfWidth;
					o.y = o.y - o.halfHeight;
					break;
				case Triangle2D:
					break;
				default:
					break;
				}
			}
			if (o.polyType == Other) {
				float rotate = MathUtils.toRadians(rotation);
				float sinA = MathUtils.sin(rotate);
				float cosA = MathUtils.cos(rotate);
				o.x = o.x
						- (o.halfWidth - (o.halfWidth * cosA - o.halfHeight
								* sinA));
				o.y = o.y
						- (o.halfHeight - (o.halfHeight * cosA + o.halfWidth
								* sinA));
			}
			o.x = MathUtils.bringToBounds(o.x, getWidth() - o.width, 0);
			o.y = MathUtils.bringToBounds(o.y, getHeight() - o.height, 0);
			if (rotation == 0 || o.lockRotate) {
				if (o.alpha > 0 && o.alpha < 1.0) {
					alphaColor.a = o.alpha;
					g.setColor(alphaColor);
					g.draw(tex2d, o.x, o.y, o.width, o.height);
					g.resetColor();
					alphaColor.a = 1f;
				} else {
					g.setColor(o.color);
					g.draw(tex2d, o.x, o.y, o.width, o.height);
					g.resetColor();
				}
				return;
			}
			if (o.alpha > 0 && o.alpha < 1.0) {
				alphaColor.a = o.alpha;
				g.setColor(alphaColor);
				g.draw(tex2d, o.x, o.y, o.width, o.height, rotation);
				g.resetColor();
				alphaColor.a = 1f;
			} else {
				g.setColor(o.color);
				g.draw(tex2d, o.x, o.y, o.width, o.height, rotation);
				g.resetColor();
			}
		}
	}

	public abstract void paint(SpriteBatch g);

	final public void alter(LTimerContext context) {
		update(context);
	}

	public abstract void update(LTimerContext t);

	public void beginContact(Contact contact) {

	}

	public void endContact(Contact contact) {
		synchronized (objects) {
			try {
				Fixture fixture1 = contact.getFixtureA();
				Fixture fixture2 = contact.getFixtureB();

				Body body1 = fixture1.getBody();
				Body body2 = fixture2.getBody();

				PhysicsObject object1 = (PhysicsObject) body1.getUserData();
				PhysicsObject object2 = (PhysicsObject) body2.getUserData();

				if (object1 != null) {
					object1.onCollision();
				}
				if (object2 != null) {
					object2.onCollision();
				}

				onCollisionEvent(new PhysicsCollisionEvent(body1, body2));
			} catch (Exception ex) {
				throw new RuntimeException("Contact:" + ex.getMessage());
			}
		}
	}

	public void onCollisionEvent(PhysicsCollisionEvent e) {

	}

	public int getPositionIterations() {
		return this.positionIters;
	}

	public void setPositionIterations(final int positionIterations) {
		this.positionIters = positionIterations;
	}

	public int getVelocityIterations() {
		return this.velocityIters;
	}

	public void setVelocityIterations(final int velocityIterations) {
		this.velocityIters = velocityIterations;
	}

	public RectBox getWorldBox() {
		return worldBox.getBox();
	}

	public boolean useContactListener() {
		return useContactListener;
	}

	public void dispose() {
		synchronized (objects) {
			super.dispose();
			try {
				if (world != null) {
					world.dispose();
					world = null;
				}
				if (batch != null) {
					batch.dispose();
					batch = null;
				}
			} catch (Exception e) {
				PhysicsScreen.world = null;
			}
		}
	}

}

/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.physics;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import loon.core.LSystem;
import loon.core.event.Updateable;
import loon.core.geom.FloatValue;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LComponent;
import loon.core.graphics.LContainer;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LInputFactory.Touch;
import loon.core.timer.LTimer;
import loon.utils.MathUtils;
import loon.action.sprite.SpriteBatch;

//该类和PhysicsScreen不能同时存在。
public class PhysicsLayer extends LContainer implements PolygonType {

	public PhysicsFixtureDefBuilder createPhysicsFixtureDefBuilder() {
		return new PhysicsFixtureDefBuilder();
	}

	public PhysicsBodyBuilder createPhysicsBodyBuilder() {
		return new PhysicsBodyBuilder(PhysicsScreen.world);
	}

	private SpriteBatch batch;

	public static final int VELOCITY_ITERATIONS_DEFAULT = 8;

	public static final int POSITION_ITERATIONS_DEFAULT = 8;

	private ArrayList<PhysicsObject> objects = new ArrayList<PhysicsObject>();

	protected int velocityIters = VELOCITY_ITERATIONS_DEFAULT;

	protected int positionIters = POSITION_ITERATIONS_DEFAULT;

	private Vector2f gravity;

	private WorldBox worldBox;

	private boolean useBatch;

	public final FloatValue TimeStep = new FloatValue(.5f);

	private LTimer timer = new LTimer(0);

	private boolean isTouchClick;

	private RectBox layerRect;

	public PhysicsLayer(RectBox rect) {
		this(rect, 0, 0, true, VELOCITY_ITERATIONS_DEFAULT,
				POSITION_ITERATIONS_DEFAULT);
	}

	public PhysicsLayer(RectBox rect, int velocityIterations,
			int positionIterations) {
		this(rect, 0, 0, true, velocityIterations, positionIterations);
	}

	public PhysicsLayer(RectBox rect, float gx, float gy, boolean doSleep) {
		this(rect, new Vector2f(gx, gy), doSleep, VELOCITY_ITERATIONS_DEFAULT,
				POSITION_ITERATIONS_DEFAULT);
	}

	public PhysicsLayer(RectBox rect, float gx, float gy, boolean doSleep,
			int velocityIterations, int positionIterations) {
		this(rect, new Vector2f(gx, gy), doSleep, velocityIterations,
				positionIterations);
	}

	public PhysicsLayer(final RectBox rect, Vector2f g, boolean doSleep, int v,
			int p) {
		this(rect.x(), rect.y(), rect.width, rect.height, g, doSleep, v, p);
	}

	public PhysicsLayer(int x, int y, int w, int h, Vector2f g,
			boolean doSleep, int v, int p) {
		super(x, y, w, h);
		if (PhysicsScreen.world != null) {
			throw new RuntimeException("Physical world is not the only !");
		}
		this.setLocation(x, y);
		this.visible = true;
		this.customRendering = true;
		this.isTouchClick = true;
		this.isLimitMove = true;
		this.setElastic(true);
		this.setLocked(true);
		this.setLayer(100);
		this.layerRect = new RectBox(x, y, w, h);
		this.gravity = g;
		this.velocityIters = v;
		this.positionIters = p;
		PhysicsScreen.world = new World(gravity, doSleep);
		this.worldBox = new WorldBox(PhysicsScreen.world, layerRect);
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isTouchClick() {
		return isTouchClick;
	}

	public boolean isLimitMove() {
		return isLimitMove;
	}

	public void setLimitMove(boolean isLimitMove) {
		this.isLimitMove = isLimitMove;
	}

	public void setTouchClick(boolean isTouchClick) {
		this.isTouchClick = isTouchClick;
	}

	public int getLayerTouchX() {
		return this.input.getTouchX() - this.getScreenX();
	}

	public int getLayerTouchY() {
		return this.input.getTouchY() - this.getScreenY();
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

	public void downClick(int x, int y) {
		if (Click != null) {
			Click.DownClick(this, x, y);
		}
	}

	public void upClick(int x, int y) {
		if (Click != null) {
			Click.UpClick(this, x, y);
		}
	}

	public void drag(int x, int y) {
		if (Click != null) {
			Click.DragClick(this, x, y);
		}
	}

	private boolean clickLock;

	protected void processTouchPressed() {
		if (!isTouchClick) {
			return;
		}
		if (clickLock) {
			return;
		}
		if (Touch.isDown()) {
			final int dx = this.input.getTouchX() - this.getScreenX();
			final int dy = this.input.getTouchY() - this.getScreenY();
			Updateable update = new Updateable() {
				public void action() {
					downClick(dx, dy);
				}
			};
			LSystem.load(update);
		}
		clickLock = true;
	}

	protected void processTouchReleased() {
		if (!isTouchClick) {
			return;
		}
		if (!clickLock) {
			return;
		}
		if (Touch.isUp()) {
			final int dx = this.input.getTouchX() - this.getScreenX();
			final int dy = this.input.getTouchY() - this.getScreenY();
			upClick(dx, dy);
		}
		clickLock = false;
	}

	protected void processTouchDragged() {
		if (Touch.isDrag()) {
			int dx = this.input.getTouchDX();
			int dy = this.input.getTouchDY();
			if (isNotMoveInScreen(dx + this.x(), dy + this.y())) {
				return;
			}
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(dx, dy);
			this.drag(dx, dy);
		}
	}

	protected boolean pressed;

	public void downKey() {
	}

	public void upKey() {
	}

	protected void processTouchEntered() {
		this.pressed = true;
	}

	protected void processTouchExited() {
		this.pressed = false;
	}

	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.downKey();
		}
	}

	protected void processKeyReleased() {
		if (this.isSelected()) {
			this.upKey();
		}
	}

	public boolean isTouchPressed() {
		return this.pressed;
	}

	public static Body createBody(BodyDef bodyDef) {
		return PhysicsScreen.world.createBody(bodyDef);
	}

	public static void destroyBody(Body body) {
		PhysicsScreen.world.destroyBody(body);
	}

	public static Joint createJoint(JointDef def) {
		return PhysicsScreen.world.createJoint(def);
	}

	public static void destroyJoint(Joint joint) {
		PhysicsScreen.world.destroyJoint(joint);
	}

	public static World getWorld() {
		return PhysicsScreen.world;
	}

	public void setGravity(int x, int y) {
		gravity.set(x, y);
		PhysicsScreen.world.setGravity(gravity);
	}

	public Vector2f getGravity() {
		return gravity;
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

	public void action(long elapsedTime) {

	}

	private long elapsedTime;

	public void update(long elapsedTime) {
		if (visible) {
			synchronized (this) {
				super.update(this.elapsedTime = elapsedTime);
				if (timer.action(elapsedTime)) {
					PhysicsScreen.world.step(TimeStep.get(), velocityIters,
							positionIters);
					action(elapsedTime);
				}
			}
		}
	}

	public void createCustomUI(GLEx g, int x, int y, int w, int h) {
		if (!visible) {
			return;
		}
		synchronized (this) {
			if (!worldBox.isBuild()) {
				worldBox.build();
			}
			if (!useBatch) {
				if (batch == null) {
					batch = new SpriteBatch(1000);
				}
				useBatch = true;
			}
			batch.begin();
			int size = objects.size();
			for (int i = size - 1; i >= 0; i--) {
				PhysicsObject o = objects.get(i);
				o.update(elapsedTime);
				LTexture texture = o.texture;
				if (texture == null) {
					continue;
				}
				drawObjectGL(batch, texture, o, x, y);
			}
			if (x == 0 && y == 0) {
				paint(batch);
			} else {
				g.translate(x, y);
				paint(batch);
				g.translate(-x, -y);
			}
			batch.end();
		}
	}

	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {

	}

	public void paint(SpriteBatch batch) {

	}

	private LColor alphaColor = new LColor(LColor.white);

	private void drawObjectGL(SpriteBatch g, LTexture tex2d, PhysicsObject o,
			float xoff, float yoff) {
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

			final float sx = o.x + xoff;
			final float sy = o.y + yoff;
			if (!LSystem.screenRect.contains(sx, sy, o.width, o.height)) {
				return;
			}
			if (rotation == 0 || o.lockRotate) {
				if (o.alpha > 0 && o.alpha < 1.0) {
					alphaColor.a = o.alpha;
					g.setColor(alphaColor);
					g.draw(tex2d, sx, sy, o.width, o.height);
					g.resetColor();
					alphaColor.a = 1f;
				} else {
					g.setColor(o.color);
					g.draw(tex2d, sx, sy, o.width, o.height);
					g.resetColor();
				}
				return;
			}
			if (o.alpha > 0 && o.alpha < 1.0) {
				alphaColor.a = o.alpha;
				g.setColor(alphaColor);
				g.draw(tex2d, sx, sy, o.width, o.height, rotation);
				g.resetColor();
				alphaColor.a = 1f;
			} else {
				g.setColor(o.color);
				g.draw(tex2d, sx, sy, o.width, o.height, rotation);
				g.resetColor();
			}
		}
	}

	public String getUIName() {
		return "PhysicsLayer";
	}

	public void dispose() {
		synchronized (objects) {
			super.dispose();
			try {
				if (PhysicsScreen.world != null) {
					PhysicsScreen.world.dispose();
					PhysicsScreen.world = null;
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

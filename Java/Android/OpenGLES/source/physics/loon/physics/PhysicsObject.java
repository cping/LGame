package loon.physics;

import java.util.HashMap;

import loon.action.map.Field2D;
import loon.core.LObject;
import loon.core.geom.FloatValue;
import loon.core.geom.RectBox;
import loon.core.geom.Triangle;
import loon.core.geom.Triangle2f;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LImage;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.GLLoader;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.utils.MathUtils;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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
public class PhysicsObject extends LObject implements PolygonType {

	private static final Vector2f initLocation = new Vector2f(0, 0);

	protected float halfWidth, halfHeight;

	protected float x, y, width, height;

	protected LColor color;

	protected int layer;

	public FloatValue density = new FloatValue(0),
			friction = new FloatValue(0), restitution = new FloatValue(0);

	public Filter filter;

	protected Body body;

	protected boolean useImage, useMake, useInster, isSensor, visible;

	protected BodyDef bodyDef;

	public float angularVelocity;

	public float linearDamping;

	public float angularDamping;

	public boolean allowSleep = true;

	public boolean awake = true;

	public boolean fixedRotation = false;

	public boolean bullet = false;

	public boolean active = true;

	public boolean lockRotate = false;

	public float inertiaScale = 1;

	public int maxRotateCache = 90;

	private float rotation;

	protected int polyType = Other;

	protected boolean bitmapFilter = false;

	protected Object tag;

	protected BodyType type;

	protected Triangle polyTriangles;

	private PhysicsListener physicsListener;

	private final static HashMap<Integer, Triangle> lazyTriangles = new HashMap<Integer, Triangle>();

	protected LTexture texture;

	PhysicsObject(String fileName, BodyType type, int x, int y) {
		this(LTextures.loadTexture(fileName), type, x, y);
	}

	PhysicsObject(LTexture img, BodyType type, int x, int y) {
		init(0, img, type, x, y, img.getWidth(), img.getHeight());
	}

	PhysicsObject(int polyType, BodyType type, int x, int y, int w, int h) {
		init(polyType, null, type, x, y, w, h);
	}

	PhysicsObject(String fileName, Body body) {
		this(LTextures.loadTexture(fileName), body);
	}

	PhysicsObject(LTexture img, Body b) {
		this.body = b;
		this.texture = img;
		this.x = body.getPosition().x;
		this.y = body.getPosition().y;
		this.type = body.getType();
		this.color = LColor.white;
		this.polyType = Circle;
		this.filter = new Filter();
		this.bodyDef = new BodyDef();
		this.bodyDef.type = type;
		this.bodyDef.position.x = x;
		this.bodyDef.position.y = y;
		this.width = texture.getWidth();
		this.height = texture.getHeight();
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.useInster = true;
		this.visible = true;
	}

	public PhysicsObject(LTexture img, PhysicsBodyBuilder bb) {
		this(img, bb.build());
	}

	public PhysicsObject(String file, PhysicsBodyBuilder bb) {
		this(LTextures.loadTexture(file), bb.build());
	}

	private void init(int polyType, LTexture img, BodyType type, int x, int y,
			int w, int h) {
		this.visible = true;
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		if (width < 0) {
			width = 1;
		}
		if (height < 0) {
			height = 1;
		}
		this.halfWidth = width / 2;
		this.halfHeight = height / 2;
		this.type = type;
		this.color = LColor.white;
		this.filter = new Filter();
		this.bodyDef = new BodyDef();
		this.bodyDef.type = type;
		this.bodyDef.position.x = x;
		this.bodyDef.position.y = y;
		this.texture = img;
		if (texture == null) {
			this.polyType = polyType;
			this.useImage = false;
		} else {
			int index = img.hashCode();
			this.polyTriangles = lazyTriangles.get(index);
			if (polyTriangles == null) {
				PhysicsPolygon ppolygon = new PhysicsPolygon(img);
				polyTriangles = ppolygon.getPolygon().getTriangles();
				lazyTriangles.put(index, polyTriangles);
			}
			this.polyType = Other;
			this.useImage = true;
		}

	}

	/**
	 * 设定Body类型（动态、静态、无质量）
	 * 
	 * @param type
	 */
	public void setType(BodyType type) {
		if (body != null) {
			body.setType(type);
		}
	}

	/**
	 * 传递对象运行间隔时间
	 * 
	 * @param elapsedTime
	 */
	public void update(long elapsedTime) {

	}

	/**
	 * 以设定好的参数创建Body
	 * 
	 */
	public void make() {
		synchronized (this) {
			try {
				bodyDef.angle = rotation;
				bodyDef.angularVelocity = angularVelocity;
				bodyDef.linearDamping = linearDamping;
				bodyDef.angularDamping = angularDamping;
				bodyDef.allowSleep = allowSleep;
				bodyDef.awake = awake;
				bodyDef.fixedRotation = fixedRotation;
				bodyDef.bullet = bullet;
				bodyDef.active = active;
				bodyDef.gravityScale = inertiaScale;
				if (useInster) {
					this.body.setUserData(this);
					this.useMake = true;
					return;
				}
				if (body != null) {
					PhysicsScreen.world.destroyBody(body);
					body = null;
				}
				if (useImage) {
					PhysicsUtils.createShape(
							body = PhysicsScreen.world.createBody(bodyDef),
							this, polyTriangles);
				} else {
					LImage image = null;
					LGraphics g = null;
					FixtureDef fixtureDef = new FixtureDef();
					fixtureDef.density = this.density.get();
					fixtureDef.friction = this.friction.get();
					fixtureDef.isSensor = this.isSensor;
					fixtureDef.restitution = this.restitution.get();
					fixtureDef.filter.categoryBits = this.filter.categoryBits;
					fixtureDef.filter.groupIndex = this.filter.groupIndex;
					fixtureDef.filter.maskBits = this.filter.maskBits;
					fixtureDef.friction = this.friction.get();
					switch (polyType) {
					case Box:
						if (type == BodyType.StaticBody) {
							createStaticBox(fixtureDef);
						} else if (type == BodyType.DynamicBody) {
							createDynamicBox(fixtureDef);
						} else if (type == BodyType.KinematicBody) {
							createKinematicBox(fixtureDef);
						}
						if (texture == null) {
							image = LImage.createImage((int) width,
									(int) height, false);
							g = image.getLGraphics();
							g.setColor(color);
							g.fillRect(0, 0, (int) width - 1, (int) height - 1);
							g.setColor(LColor.white);
							g.drawRect(0, 0, (int) width - 1, (int) height - 1);
						}
						break;
					case Circle:
						if (type == BodyType.StaticBody) {
							createStaticCircle(fixtureDef);
						} else if (type == BodyType.DynamicBody) {
							createDynamicCircle(fixtureDef);
						} else if (type == BodyType.KinematicBody) {
							createKinematicCircle(fixtureDef);
						}
						if (texture == null) {
							image = LImage.createImage((int) width,
									(int) height, true);
							g = image.getLGraphics();
							g.setColor(color);
							g.fillOval(0, 0, (int) width - 1, (int) height - 1);
							g.setColor(LColor.white);
							g.drawOval(0, 0, (int) width - 1, (int) height - 1);
						}
						break;
					case Triangle2D:
						if (type == BodyType.StaticBody) {
							createStaticTriangle(fixtureDef);
						} else if (type == BodyType.DynamicBody) {
							createDynamicTriangle(fixtureDef);
						} else if (type == BodyType.KinematicBody) {
							createKinematicTriangle(fixtureDef);
						}
						if (texture == null) {
							image = LImage.createImage((int) width,
									(int) height, false);
							g = image.getLGraphics();
							Triangle2f t = new Triangle2f();
							t.set((int) width, (int) width);
							g.setColor(LColor.blue);
							g.fillTriangle(t);
							g.setColor(LColor.white);
							g.drawTriangle(t);

							t = null;
						}
						break;
					}
					if (texture == null && image != null) {
						this.setDrawImage(image);
					}
					if (g != null) {
						g.dispose();
					}
				}
				this.body.setUserData(this);
				this.useMake = true;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("PhysicsObject make "
						+ e.getMessage());
			}
		}
	}

	public void setUserData(Object object) {
		if (body != null) {
			this.body.setUserData(object);
		}
	}

	public Object getUserData() {
		if (body != null) {
			return this.body.getUserData();
		}
		return null;
	}

	public float getDirection() {
		if (body == null) {
			return 0;
		}
		return (float) Math.toDegrees(body.getAngle());
	}

	public void setAngle(float angle) {
		bodyDef.angle = angle * MathUtils.DEG_TO_RAD;
		if (body != null) {
			Vector2f v = (Vector2f) body.getPosition().cpy();
			body.setTransform(v, bodyDef.angle);
			body.setLinearVelocity(initLocation);
		}
	}

	public void setDrawImage(LImage img) {
		if (useImage) {
			throw new RuntimeException("Set is not allowed !");
		}
		if (img != null) {
			if (texture != null) {
				texture.destroy();
				texture = null;
			}
			texture = new LTexture(GLLoader.getTextureData(img), Format.LINEAR);
		}
	}

	public void setDrawImage(String fileName) {
		setDrawImage(LImage.createImage(fileName));
	}

	public void setSpeed(float gx, float gy) {
		setSpeed(new Vector2f(gx, gy));
	}

	public void setSpeed(Vector2f speed) {
		if (body == null) {
			return;
		}
		this.body.setLinearVelocity(speed);
	}

	public Vector2f getSpeed() {
		if (body == null) {
			return new Vector2f(0, 0);
		}
		return body.getLinearVelocity();
	}

	public Vector2f getPosition() {
		if (body == null) {
			return new Vector2f(0, 0);
		}
		return body.getPosition();
	}

	public void setDamping(float damping) {
		this.bodyDef.linearDamping = damping;
	}

	public void setRotation(float rotation) {
		if (body == null) {
			return;
		}
		body.setTransform(body.getPosition(), body.getAngle());
	}

	public void setVelocity(float xVelocity, float yVelocity) {
		if (body == null) {
			return;
		}
		Vector2f vel = body.getLinearVelocity();
		vel.x = xVelocity;
		vel.y = yVelocity;
		body.setLinearVelocity(vel);
	}

	public void setAngularVelocity(float vel) {
		if (body == null) {
			return;
		}
		body.setAngularVelocity(vel);
	}

	public void setPosition(float x, float y) {
		setPosition(new Vector2f(x, y));
	}

	public void setPosition(Vector2f position) {
		if (body == null) {
			return;
		}
		body.setTransform(position, 0);
		body.setLinearVelocity(initLocation);
	}

	public Body getBody() {
		if (body == null) {
			return null;
		}
		return body;
	}

	public void reset() {
		setPosition(initLocation);
	}

	public void destroy() {
		if (body == null) {
			return;
		}
		PhysicsScreen.destroyBody(body);
	}

	public void applyforce(Vector2f force) {
		if (body == null) {
			return;
		}
		body.applyForce(force, initLocation);
	}

	public void dispose() {
		if (body != null) {
			PhysicsScreen.world.destroyBody(body);
			body = null;
		}
		useMake = false;
	}

	public LTexture getBitmap() {
		return texture;
	}

	/**
	 * 创建一个指定样式的Body
	 * 
	 * @param bodyDef
	 * @param fixture
	 */
	private void createBody(BodyDef bodyDef, FixtureDef fixture) {
		body = PhysicsScreen.world.createBody(bodyDef);
		body.createFixture(fixture);
		this.bodyDef = bodyDef;
	}

	private void createDynamicBody(FixtureDef fixture) {
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.x = (x + halfWidth);
		bodyDef.position.y = (y + halfHeight);
		bodyDef.angle = rotation * MathUtils.DEG_TO_RAD;
		bodyDef.angularVelocity = angularVelocity;
		bodyDef.linearDamping = linearDamping;
		bodyDef.angularDamping = angularDamping;
		bodyDef.allowSleep = allowSleep;
		bodyDef.allowSleep = awake;
		bodyDef.fixedRotation = fixedRotation;
		bodyDef.bullet = bullet;
		bodyDef.active = active;
		bodyDef.gravityScale = inertiaScale;
		createBody(bodyDef, fixture);
	}

	private void createStaticBody(FixtureDef fixture) {
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.x = (x + halfWidth);
		bodyDef.position.y = (y + halfHeight);
		bodyDef.angle = rotation * MathUtils.DEG_TO_RAD;
		bodyDef.angularVelocity = angularVelocity;
		bodyDef.linearDamping = linearDamping;
		bodyDef.angularDamping = angularDamping;
		bodyDef.allowSleep = allowSleep;
		bodyDef.allowSleep = awake;
		bodyDef.fixedRotation = fixedRotation;
		bodyDef.bullet = bullet;
		bodyDef.active = active;
		bodyDef.gravityScale = inertiaScale;
		createBody(bodyDef, fixture);
	}

	private void createKinematicBody(FixtureDef fixture) {
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.x = (x + halfWidth);
		bodyDef.position.y = (y + halfHeight);
		bodyDef.angle = rotation * MathUtils.DEG_TO_RAD;
		bodyDef.angularVelocity = angularVelocity;
		bodyDef.linearDamping = linearDamping;
		bodyDef.angularDamping = angularDamping;
		bodyDef.allowSleep = allowSleep;
		bodyDef.allowSleep = awake;
		bodyDef.fixedRotation = fixedRotation;
		bodyDef.bullet = bullet;
		bodyDef.active = active;
		bodyDef.gravityScale = inertiaScale;
		createBody(bodyDef, fixture);
	}

	private void createDynamicCircle(FixtureDef fixtureDef) {
		CircleShape circle = new CircleShape();
		circle.setRadius(halfWidth);
		fixtureDef.shape = circle;
		createDynamicBody(fixtureDef);
		circle.dispose();
	}

	private void createStaticCircle(FixtureDef fixtureDef) {
		CircleShape circle = new CircleShape();
		circle.setRadius(halfWidth);
		fixtureDef.shape = circle;
		createStaticBody(fixtureDef);
		circle.dispose();
	}

	private void createKinematicCircle(FixtureDef fixtureDef) {
		CircleShape circle = new CircleShape();
		circle.setRadius(halfWidth);
		fixtureDef.shape = circle;
		createKinematicBody(fixtureDef);
		circle.dispose();
	}

	private void createDynamicBox(FixtureDef fixtureDef) {
		PolygonShape boxPoly = new PolygonShape();
		boxPoly.setAsBox(halfWidth, halfHeight);
		fixtureDef.shape = boxPoly;
		createDynamicBody(fixtureDef);
		boxPoly.dispose();
	}

	private void createStaticBox(FixtureDef fixtureDef) {
		PolygonShape boxPoly = new PolygonShape();
		boxPoly.setAsBox(halfWidth, halfHeight);
		fixtureDef.shape = boxPoly;
		createStaticBody(fixtureDef);
		boxPoly.dispose();
	}

	private void createKinematicBox(FixtureDef fixtureDef) {
		PolygonShape boxPoly = new PolygonShape();
		boxPoly.setAsBox(halfWidth, halfHeight);
		fixtureDef.shape = boxPoly;
		createKinematicBody(fixtureDef);
		boxPoly.dispose();
	}

	private void createDynamicTriangle(FixtureDef fixtureDef) {
		PolygonShape tPoly = new PolygonShape();
		Triangle2f t = new Triangle2f();
		t.set((int) (width), (int) (height));
		tPoly.set(t.getVertexs());
		fixtureDef.shape = tPoly;
		bodyDef.angle = rotation * MathUtils.DEG_TO_RAD;
		createBody(bodyDef, fixtureDef);
		tPoly.dispose();
	}

	private void createStaticTriangle(FixtureDef fixtureDef) {
		PolygonShape tPoly = new PolygonShape();
		Triangle2f t = new Triangle2f();
		t.set((int) (width), (int) (height));
		tPoly.set(t.getVertexs());
		fixtureDef.shape = tPoly;
		bodyDef.angle = rotation * MathUtils.DEG_TO_RAD;
		createBody(bodyDef, fixtureDef);
		tPoly.dispose();
	}

	private void createKinematicTriangle(FixtureDef fixtureDef) {
		PolygonShape tPoly = new PolygonShape();
		Triangle2f t = new Triangle2f();
		t.set((int) (width), (int) (height));
		tPoly.set(t.getVertexs());
		fixtureDef.shape = tPoly;
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.angle = rotation * MathUtils.DEG_TO_RAD;
		createBody(bodyDef, fixtureDef);
		tPoly.dispose();
	}

	public float getDensity() {
		return density.get();
	}

	public void setDensity(float d) {
		this.density.set(d);
	}

	public float getFriction() {
		return friction.get();
	}

	public void setFriction(float f) {
		this.friction.set(f);
	}

	public float getRestitution() {
		return restitution.get();
	}

	public void setRestitution(float r) {
		this.restitution.set(r);
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public void onCollision() {
		if (physicsListener != null) {
			physicsListener.onCollision();
		}
	}

	public boolean getIsSensor() {
		return isSensor;
	}

	public void setIsSensor(boolean isSensor) {
		this.isSensor = isSensor;
	}

	public RectBox getCollisionBox() {
		return getRect(x, y, width, height);
	}

	public int getHeight() {
		return (int) height;
	}

	public int getLayer() {
		return layer;
	}

	public int getWidth() {
		return (int) width;
	}

	public float getX() {
		Vector2f v = getPosition();
		return v.x;
	}

	public float getY() {
		Vector2f v = getPosition();
		return v.y;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public int x() {
		Vector2f v = getPosition();
		return (int) v.x;
	}

	public int y() {
		Vector2f v = getPosition();
		return (int) v.y;
	}

	public void move(float x, float y) {
		move(new Vector2f(x, y));
	}

	public void move(Vector2f vector2) {
		Vector2f v = getPosition();
		float x = v.x + vector2.x();
		float y = v.y + vector2.y();
		setPosition(new Vector2f(x, y));
	}

	public void move_multiples(int direction, int multiples) {
		if (multiples <= 0) {
			multiples = 1;
		}
		Vector2f v = Field2D.getDirection(direction);
		move(v.x() * multiples, v.y() * multiples);
	}

	public void move_45D_up() {
		move_45D_up(1);
	}

	public void move_45D_up(int multiples) {
		move_multiples(Field2D.UP, multiples);
	}

	public void move_45D_left() {
		move_45D_left(1);
	}

	public void move_45D_left(int multiples) {
		move_multiples(Field2D.LEFT, multiples);
	}

	public void move_45D_right() {
		move_45D_right(1);
	}

	public void move_45D_right(int multiples) {
		move_multiples(Field2D.RIGHT, multiples);
	}

	public void move_45D_down() {
		move_45D_down(1);
	}

	public void move_45D_down(int multiples) {
		move_multiples(Field2D.DOWN, multiples);
	}

	public void move_up() {
		move_up(1);
	}

	public void move_up(int multiples) {
		move_multiples(Field2D.TUP, multiples);
	}

	public void move_left() {
		move_left(1);
	}

	public void move_left(int multiples) {
		move_multiples(Field2D.TLEFT, multiples);
	}

	public void move_right() {
		move_right(1);
	}

	public void move_right(int multiples) {
		move_multiples(Field2D.TRIGHT, multiples);
	}

	public void move_down() {
		move_down(1);
	}

	public void move_down(int multiples) {
		move_multiples(Field2D.TDOWN, multiples);
	}

	public PhysicsListener getPhysicsListener() {
		return physicsListener;
	}

	public void setPhysicsListener(PhysicsListener physicsListener) {
		this.physicsListener = physicsListener;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public boolean isBitmapFilter() {
		return bitmapFilter;
	}

	public void setBitmapFilter(boolean bitmapFilter) {
		this.bitmapFilter = bitmapFilter;
	}

	public int getPolyType() {
		return polyType;
	}

	public void setPolyType(int polyType) {
		this.polyType = polyType;
	}

}

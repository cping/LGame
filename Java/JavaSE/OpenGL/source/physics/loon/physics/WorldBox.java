package loon.physics;

import loon.core.geom.FloatValue;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.3.3
 */
//用以生成物理世界边界
public class WorldBox {

	private Body northBody, southBody, eastBody, westBody;

	private RectBox worldBox;

	private World world;

	public final FloatValue density = new FloatValue(0),
			friction = new FloatValue(0), restitution = new FloatValue(0);

	public boolean build;

	public WorldBox(World world, RectBox box) {
		this.build = false;
		this.world = world;
		this.worldBox = box;
		this.friction.set(10f);
		this.density.set(0);
		this.restitution.set(0.5f);
	}

	public synchronized void removeWorld() {
		if (build) {
			world.destroyBody(northBody);
			world.destroyBody(southBody);
			world.destroyBody(eastBody);
			world.destroyBody(westBody);
		}
		build = false;
	}

	public boolean isBuild() {
		return build;
	}

	public void build() {
		build(worldBox);
	}

	public void build(RectBox box) {
		if (build) {
			throw new RuntimeException("Build Error !");
		}
		PolygonShape eastWestShape = new PolygonShape();
		eastWestShape.setAsBox(1.0f, box.getHeight());

		PolygonShape northSouthShape = new PolygonShape();
		northSouthShape.setAsBox(box.getWidth(), 0.0f);

		BodyDef northDef = new BodyDef();
		northDef.type = BodyDef.BodyType.StaticBody;
		northDef.position.set(new Vector2f(0, 0));
		northBody = world.createBody(northDef);
		FixtureDef northFixture = new FixtureDef();
		northFixture.shape = northSouthShape;
		northFixture.density = density.get();
		northFixture.friction = friction.get();
		northFixture.restitution = restitution.get();
		northBody.createFixture(northFixture);

		BodyDef southDef = new BodyDef();
		southDef.type = BodyDef.BodyType.StaticBody;
		southDef.position.set(new Vector2f(0, box.getHeight()));
		southBody = world.createBody(southDef);
		FixtureDef southFixture = new FixtureDef();
		southFixture.shape = northSouthShape;
		southFixture.density = density.get();
		southFixture.friction = friction.get();
		southFixture.restitution = restitution.get();
		southBody.createFixture(southFixture);

		BodyDef eastDef = new BodyDef();
		eastDef.type = BodyDef.BodyType.StaticBody;
		eastDef.position.set(new Vector2f(box.getWidth(), 0));
		eastBody = world.createBody(eastDef);
		FixtureDef eastFixture = new FixtureDef();
		eastFixture.shape = eastWestShape;
		eastFixture.density = density.get();
		eastFixture.friction = friction.get();
		eastFixture.restitution = restitution.get();
		eastBody.createFixture(eastFixture);

		BodyDef westDef = new BodyDef();
		westDef.type = BodyDef.BodyType.StaticBody;
		westDef.position.set(new Vector2f(0, 0));
		westBody = world.createBody(westDef);
		FixtureDef westFixture = new FixtureDef();
		westFixture.density = density.get();
		westFixture.friction = friction.get();
		westFixture.restitution = restitution.get();
		westFixture.shape = eastWestShape;
		westBody.createFixture(westFixture);

		eastWestShape.dispose();
		northSouthShape.dispose();

		this.worldBox = box;
		this.build = true;

	}

	public RectBox getBox() {
		return worldBox;
	}

	public float getDensity() {
		return density.get();
	}

	public void setDensity(float d) {
		this.density.set(d);
	}

	public Body getEastBody() {
		return eastBody;
	}

	public void setEastBody(Body eastBody) {
		this.eastBody = eastBody;
	}

	public float getFriction() {
		return friction.get();
	}

	public void setFriction(float f) {
		this.friction.set(f);
	}

	public Body getNorthBody() {
		return northBody;
	}

	public void setNorthBody(Body northBody) {
		this.northBody = northBody;
	}

	public float getRestitution() {
		return restitution.get();
	}

	public void setRestitution(float r) {
		this.restitution.set(r);
	}

	public Body getSouthBody() {
		return southBody;
	}

	public void setSouthBody(Body southBody) {
		this.southBody = southBody;
	}

	public Body getWestBody() {
		return westBody;
	}

	public void setWestBody(Body westBody) {
		this.westBody = westBody;
	}
}

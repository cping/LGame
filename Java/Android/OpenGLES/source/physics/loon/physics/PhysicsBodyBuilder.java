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

import loon.core.geom.Vector2f;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsBodyBuilder {

	private BodyDef bodyDef;
	private ArrayList<FixtureDef> fixtureDefs;
	private ArrayList<Object> fixtureUserDatas;
	private Object userData = null;
	private Vector2f position = new Vector2f();
	private final World world;
	private float angle;

	PhysicsFixtureDefBuilder fixtureDefBuilder;

	private MassData massData = new MassData();
	private boolean massSet;

	public PhysicsBodyBuilder(World world) {
		this.world = world;
		this.fixtureDefBuilder = new PhysicsFixtureDefBuilder();
		this.fixtureDefs = new ArrayList<FixtureDef>();
		this.fixtureUserDatas = new ArrayList<Object>();
		reset(true);
	}

	public PhysicsFixtureDefBuilder fixtureDefBuilder() {
		return fixtureDefBuilder;
	}

	private void reset(boolean disposeShapes) {

		if (fixtureDefs != null && disposeShapes) {
			for (int i = 0; i < fixtureDefs.size(); i++) {
				FixtureDef fixtureDef = fixtureDefs.get(i);
				fixtureDef.shape.dispose();
			}
		}

		bodyDef = new BodyDef();
		fixtureDefs.clear();
		fixtureUserDatas.clear();
		angle = 0f;
		userData = null;
		position.set(0f, 0f);
		massSet = false;
	}

	public PhysicsBodyBuilder type(BodyType type) {
		bodyDef.type = type;
		return this;
	}

	public PhysicsBodyBuilder bullet() {
		bodyDef.bullet = true;
		return this;
	}

	public PhysicsBodyBuilder fixedRotation() {
		bodyDef.fixedRotation = true;
		return this;
	}

	public PhysicsBodyBuilder linearDamping(float linearDamping) {
		bodyDef.linearDamping = linearDamping;
		return this;
	}

	public PhysicsBodyBuilder angularDamping(float angularDamping) {
		bodyDef.angularDamping = angularDamping;
		return this;
	}

	public PhysicsBodyBuilder fixture(PhysicsFixtureDefBuilder fixtureDef) {
		return fixture(fixtureDef, null);
	}

	public PhysicsBodyBuilder fixture(PhysicsFixtureDefBuilder fixtureDef,
			Object fixtureUserData) {
		fixtureDefs.add(fixtureDef.build());
		fixtureUserDatas.add(fixtureUserData);
		return this;
	}

	public PhysicsBodyBuilder fixture(FixtureDef fixtureDef) {
		return fixture(fixtureDef, null);
	}

	public PhysicsBodyBuilder fixture(FixtureDef fixtureDef,
			Object fixtureUserData) {
		fixtureDefs.add(fixtureDef);
		fixtureUserDatas.add(fixtureUserData);
		return this;
	}

	public PhysicsBodyBuilder fixtures(FixtureDef[] fixtureDefs) {
		return fixtures(fixtureDefs, null);
	}

	public PhysicsBodyBuilder fixtures(FixtureDef[] fixtureDefs,
			Object[] fixtureUserDatas) {
		if (fixtureUserDatas != null
				&& (fixtureDefs.length != fixtureUserDatas.length))
			throw new RuntimeException("length mismatch between fixtureDefs("
					+ fixtureDefs.length + ") and fixtureUserDatas("
					+ fixtureUserDatas.length + ")");

		for (int i = 0; i < fixtureDefs.length; i++) {
			this.fixtureDefs.add(fixtureDefs[i]);
			Object fixtureUserData = fixtureUserDatas != null ? fixtureUserDatas[i]
					: null;
			this.fixtureUserDatas.add(fixtureUserData);
		}

		return this;

	}

	public PhysicsBodyBuilder mass(float mass) {
		this.massData.mass = mass;
		this.massSet = true;
		return this;
	}

	public PhysicsBodyBuilder inertia(float intertia) {
		this.massData.I = intertia;
		this.massSet = true;
		return this;
	}

	public PhysicsBodyBuilder userData(Object userData) {
		this.userData = userData;
		return this;
	}

	public PhysicsBodyBuilder position(float x, float y) {
		this.position.set(x, y);
		return this;
	}

	public PhysicsBodyBuilder angle(float angle) {
		this.angle = angle;
		return this;
	}

	public Body build() {
		return build(true);
	}

	public Body build(boolean disposeShapes) {
		Body body = world.createBody(bodyDef);

		for (int i = 0; i < fixtureDefs.size(); i++) {
			FixtureDef fixtureDef = fixtureDefs.get(i);
			Fixture fixture = body.createFixture(fixtureDef);
			fixture.setUserData(fixtureUserDatas.get(i));
		}

		if (massSet) {
			MassData bodyMassData = body.getMassData();
			massData.center.set(bodyMassData.center);
			body.setMassData(massData);
		}

		body.setUserData(userData);
		body.setTransform(position, angle);

		reset(disposeShapes);
		return body;
	}
}

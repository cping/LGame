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

import loon.core.geom.Vector2f;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class PhysicsFixtureDefBuilder {

	FixtureDef fixtureDef;

	public PhysicsFixtureDefBuilder() {
		reset();
	}

	public PhysicsFixtureDefBuilder sensor() {
		fixtureDef.isSensor = true;
		return this;
	}

	public PhysicsFixtureDefBuilder boxShape(float hx, float hy) {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(hx, hy);
		fixtureDef.shape = shape;
		return this;
	}

	public PhysicsFixtureDefBuilder boxShape(float hx, float hy,
			Vector2f center, float angleInRadians) {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(hx, hy, center, angleInRadians);
		fixtureDef.shape = shape;
		return this;
	}

	public PhysicsFixtureDefBuilder circleShape(float radius) {
		Shape shape = new CircleShape();
		shape.setRadius(radius);
		fixtureDef.shape = shape;
		return this;
	}

	public PhysicsFixtureDefBuilder circleShape(Vector2f center, float radius) {
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		shape.setPosition(center);
		fixtureDef.shape = shape;
		return this;
	}

	public PhysicsFixtureDefBuilder polygonShape(Vector2f[] vertices) {
		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fixtureDef.shape = shape;
		return this;
	}

	public PhysicsFixtureDefBuilder density(float density) {
		fixtureDef.density = density;
		return this;
	}

	public PhysicsFixtureDefBuilder friction(float friction) {
		fixtureDef.friction = friction;
		return this;
	}

	public PhysicsFixtureDefBuilder restitution(float restitution) {
		fixtureDef.restitution = restitution;
		return this;
	}

	public PhysicsFixtureDefBuilder categoryBits(short categoryBits) {
		fixtureDef.filter.categoryBits = categoryBits;
		return this;
	}

	public PhysicsFixtureDefBuilder maskBits(short maskBits) {
		fixtureDef.filter.maskBits = maskBits;
		return this;
	}

	private void reset() {
		fixtureDef = new FixtureDef();
	}

	public FixtureDef build() {
		FixtureDef fixtureDef = this.fixtureDef;
		reset();
		return fixtureDef;
	}
}

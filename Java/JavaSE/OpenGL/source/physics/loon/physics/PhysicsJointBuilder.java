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

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

public class PhysicsJointBuilder {

	private final World world;
	private DistanceJointBuilder distanceJointBuilder;
	private RopeJointBuilder ropeJointBuilder;

	public PhysicsJointBuilder(World world) {
		this.world = world;
		this.distanceJointBuilder = new DistanceJointBuilder();
		this.ropeJointBuilder = new RopeJointBuilder();
	}

	private class InternalJointBuilder {

		JointDef jointDef;

		public void setJointDef(JointDef jointDef) {
			this.jointDef = jointDef;
		}

		public InternalJointBuilder bodyA(Body bodyA) {
			jointDef.bodyA = bodyA;
			return this;
		}

		public InternalJointBuilder bodyB(Body bodyB) {
			jointDef.bodyB = bodyB;
			return this;
		}

		public InternalJointBuilder collideConnected(boolean collideConnected) {
			jointDef.collideConnected = collideConnected;
			return this;
		}
	}

	public class DistanceJointBuilder {

		private InternalJointBuilder internalJointBuilder = new InternalJointBuilder();
		private DistanceJointDef distanceJointDef;

		private void reset() {
			distanceJointDef = new DistanceJointDef();
			internalJointBuilder.setJointDef(distanceJointDef);
		}

		public DistanceJointBuilder bodyA(Body bodyA) {
			internalJointBuilder.bodyA(bodyA);
			return this;
		}

		public DistanceJointBuilder bodyB(Body bodyB) {
			internalJointBuilder.bodyB(bodyB);
			return this;
		}

		public DistanceJointBuilder collideConnected(boolean collideConnected) {
			internalJointBuilder.collideConnected(collideConnected);
			return this;
		}

		public DistanceJointBuilder length(float length) {
			distanceJointDef.length = length;
			return this;
		}

		public DistanceJointBuilder frequencyHz(float frequencyHz) {
			distanceJointDef.frequencyHz = frequencyHz;
			return this;
		}

		public DistanceJointBuilder dampingRatio(float dampingRatio) {
			distanceJointDef.dampingRatio = dampingRatio;
			return this;
		}

		public Joint build() {
			return world.createJoint(distanceJointDef);
		}

	}

	public class RopeJointBuilder {

		private InternalJointBuilder internalJointBuilder = new InternalJointBuilder();
		private RopeJointDef ropeJointDef;

		private void reset() {
			ropeJointDef = new RopeJointDef();
			internalJointBuilder.setJointDef(ropeJointDef);
		}

		public RopeJointBuilder bodyA(Body bodyA) {
			internalJointBuilder.bodyA(bodyA);
			return this;
		}

		public RopeJointBuilder bodyB(Body bodyB) {
			internalJointBuilder.bodyB(bodyB);
			return this;
		}

		public RopeJointBuilder bodyA(Body bodyA, float lx, float ly) {
			internalJointBuilder.bodyA(bodyA);
			ropeJointDef.localAnchorA.set(lx, ly);
			return this;
		}

		public RopeJointBuilder bodyB(Body bodyB, float lx, float ly) {
			internalJointBuilder.bodyB(bodyB);
			ropeJointDef.localAnchorB.set(lx, ly);
			return this;
		}

		public RopeJointBuilder collideConnected(boolean collideConnected) {
			internalJointBuilder.collideConnected(collideConnected);
			return this;
		}

		public RopeJointBuilder maxLength(float maxLength) {
			ropeJointDef.maxLength = maxLength;
			return this;
		}

		public Joint build() {
			return world.createJoint(ropeJointDef);
		}

	}

	public DistanceJointBuilder distanceJoint() {
		distanceJointBuilder.reset();
		return distanceJointBuilder;
	}

	public RopeJointBuilder ropeJointBuilder() {
		ropeJointBuilder.reset();
		return ropeJointBuilder;
	}

}

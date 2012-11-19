/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.physics.box2d.joints;

import loon.core.geom.Vector2f;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;

/** Pulley joint definition. This requires two ground anchors, two dynamic body anchor points, max lengths for each side, and a
 * pulley ratio. */
public class PulleyJointDef extends JointDef {

	public PulleyJointDef () {
		type = JointType.PulleyJoint;
		collideConnected = true;
	}

	/** Initialize the bodies, anchors, lengths, max lengths, and ratio using the world anchors. */
	public void initialize (Body bodyA, Body bodyB, Vector2f groundAnchorA, Vector2f groundAnchorB, Vector2f anchorA,
		Vector2f anchorB, float ratio) {
		this.bodyA = bodyA;
		this.bodyB = bodyB;
		this.groundAnchorA.set(groundAnchorA);
		this.groundAnchorB.set(groundAnchorB);
		this.localAnchorA.set(bodyA.getLocalPoint(anchorA));
		this.localAnchorB.set(bodyB.getLocalPoint(anchorB));
		lengthA = anchorA.dst(groundAnchorA);
		lengthB = anchorB.dst(groundAnchorB);
		this.ratio = ratio;
	}

	/** The first ground anchor in world coordinates. This point never moves. */
	public final Vector2f groundAnchorA = new Vector2f(-1, 1);

	/** The second ground anchor in world coordinates. This point never moves. */
	public final Vector2f groundAnchorB = new Vector2f(1, 1);

	/** The local anchor point relative to bodyA's origin. */
	public final Vector2f localAnchorA = new Vector2f(-1, 0);

	/** The local anchor point relative to bodyB's origin. */
	public final Vector2f localAnchorB = new Vector2f(1, 0);

	/** The a reference length for the segment attached to bodyA. */
	public float lengthA = 0;

	/** The a reference length for the segment attached to bodyB. */
	public float lengthB = 0;

	/** The pulley ratio, used to simulate a block-and-tackle. */
	public float ratio = 1;
}

/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.physics;

import loon.core.geom.Vector2f;

public class PDragJoint extends PJoint {

	private Vector2f anchor;
	private PBody b;
	private Vector2f dragPoint;
	private Vector2f localAnchor;
	private PTransformer mass;
	private Vector2f relAnchor;

	public PDragJoint(PBody b, float px, float py) {
		this.b = b;
		dragPoint = new Vector2f(px, py);
		localAnchor = new Vector2f(px - b.pos.x, py - b.pos.y);
		b.mAng.transpose().mulEqual(localAnchor);
		anchor = b.mAng.mul(localAnchor);
		anchor.addLocal(b.pos);
		type = PJointType.DRAG_JOINT;
		mass = new PTransformer();
	}

	public Vector2f getAnchorPoint() {
		return anchor.clone();
	}

	public PBody getBody() {
		return b;
	}

	public Vector2f getDragPoint() {
		return dragPoint.clone();
	}

	public Vector2f getRelativeAnchorPoint() {
		return relAnchor.clone();
	}

	@Override
	void preSolve(float dt) {
		relAnchor = b.mAng.mul(localAnchor);
		anchor.set(relAnchor.x + b.pos.x, relAnchor.y + b.pos.y);
		mass = PTransformer.calcEffectiveMass(b, relAnchor);
		Vector2f f = anchor.sub(dragPoint);
		float k = b.m;
		f.mulLocal(-k * 20F);
		Vector2f relVel = b.vel.clone();
		relVel.x += -b.angVel * relAnchor.y;
		relVel.y += b.angVel * relAnchor.x;
		relVel.mulLocal((float) Math.sqrt(k * 20F * k));
		f.subLocal(relVel);
		f.mulLocal(dt);
		mass.mulEqual(f);
		b.applyImpulse(f.x, f.y, anchor.x, anchor.y);
	}

	public void setDragPosition(float px, float py) {
		dragPoint.set(px, py);
	}

	public void setRelativeAnchorPoint(float relx, float rely) {
		localAnchor.set(relx, rely);
		b.mAng.transpose().mulEqual(localAnchor);
	}

	@Override
	void solvePosition() {
	}

	@Override
	void solveVelocity(float f) {
	}

	@Override
	void update() {
		relAnchor = b.mAng.mul(localAnchor);
		anchor.set(relAnchor.x + b.pos.x, relAnchor.y + b.pos.y);
		if (b.rem || b.fix) {
			rem = true;
		}
	}

}

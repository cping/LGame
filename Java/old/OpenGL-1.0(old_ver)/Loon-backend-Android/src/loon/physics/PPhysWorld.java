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
import loon.utils.CollectionUtils;

public class PPhysWorld {

	private PBody[] bodies;
	private long collisionDetectionTime;
	private long collisionSolveTime;
	private Vector2f gravity;
	private int iterations;
	private PJoint[] joints;
	private int numBodies;
	private int numJoints;
	private int numShapes;
	private int numSolvers;
	private long positionUpdateTime;
	private PSweepAndPrune sap;
	private PShape[] shapes;
	private PSolver[] solvers;
	private long totalStepTime;

	public PPhysWorld() {
		this.iterations = 10;
		this.gravity = new Vector2f(0.0F, 9.80665F);
		this.bodies = new PBody[1024];
		this.joints = new PJoint[1024];
		this.shapes = new PShape[1024];
		this.solvers = new PSolver[1024];
		this.sap = new PSweepAndPrune();
	}

	public boolean removeBody(PBody b) {
		return removeBody(b, false);
	}

	public boolean removeBody(PBody b, boolean identity) {
		Object[] items = this.bodies;
		if (identity || b == null) {
			for (int i = 0; i < numBodies; i++) {
				if (items[i] == b) {
					removeBody(i);
					return true;
				}
			}
		} else {
			for (int i = 0; i < numBodies; i++) {
				if (b.equals(items[i])) {
					removeBody(i);
					return true;
				}
			}
		}
		return false;
	}

	public void addBody(PBody b) {
		if (numBodies + 1 >= bodies.length) {
			bodies = (PBody[]) CollectionUtils
					.copyOf(bodies, bodies.length * 2);
		}
		b.w = this;
		for (int i = 0; i < b.numShapes; i++) {
			addShape(b.shapes[i]);
		}
		bodies[numBodies] = b;
		numBodies++;
	}

	public void addJoint(PJoint j) {
		if (numJoints + 1 >= joints.length) {
			joints = (PJoint[]) CollectionUtils.copyOf(joints,
					joints.length * 2);
		}
		joints[numJoints] = j;
		numJoints++;
	}

	void addShape(PShape s) {
		if (s._type == PShapeType.CONCAVE_SHAPE) {
			PConcavePolygonShape c = (PConcavePolygonShape) s;
			for (int i = 0; i < c.numConvexes; i++) {
				addShape(((c.convexes[i])));
			}
			return;
		}
		if (numShapes + 1 >= shapes.length) {
			shapes = (PShape[]) CollectionUtils.copyOf(shapes,
					shapes.length * 2);
		}
		shapes[numShapes] = s;
		s._sapAABB.set(sap, s, s._aabb);
		numShapes++;
	}

	private void addSolver(PSolver s) {
		if (numSolvers + 1 >= solvers.length) {
			solvers = (PSolver[]) CollectionUtils.copyOf(solvers,
					solvers.length * 2);
		}
		solvers[numSolvers] = s;
		numSolvers++;
	}

	private PCollisionChooser cc = new PCollisionChooser();

	private void collide(long st) {
		PSortableObject obj[] = sap.sort();
		if (sap.checkX) {
			for (int i = 0; i < sap.numObject; i++)
				if (obj[i].begin) {
					PSortableObject end = obj[i].aabb.endX;
					PShape s1 = obj[i].parent;
					int j = i;
					do {
						j++;
						if (obj[j].begin) {
							PShape s2 = obj[j].parent;
							if ((!s1._parent.fix || !s2._parent.fix)
									&& s1._parent != s2._parent
									&& s1._aabb.isHit(s2._aabb))
								collisionShape(s1, s2, cc);
						}
					} while (obj[j] != end && j < sap.numObject);
				}

		} else {
			for (int i = 0; i < sap.numObject; i++)
				if (obj[i].begin) {
					PSortableObject end = obj[i].aabb.endY;
					PShape s1 = obj[i].parent;
					int j = i;
					do {
						j++;
						if (obj[j].begin) {
							PShape s2 = obj[j].parent;
							if ((!s1._parent.fix || !s2._parent.fix)
									&& s1._parent != s2._parent
									&& s1._aabb.isHit(s2._aabb))
								collisionShape(s1, s2, cc);
						}
					} while (obj[j] != end && j < sap.numObject);
				}
		}
		long en = System.nanoTime();
		collisionDetectionTime = en - st;
		for (int i = 0; i < numSolvers; i++) {
			if (solvers[i].rem) {
				removeSolver(i);
				i--;
			}
		}
	}

	private void collisionShape(PShape s1, PShape s2, PCollisionChooser cc) {
		PContact cs[] = new PContact[2];
		int num = cc.collide(s1, s2, cs);
		if (num == 0) {
			return;
		}
		boolean found = false;
		for (int f = 0; f < numSolvers; f++) {
			if (s1 != solvers[f].s1 || s2 != solvers[f].s2) {
				continue;
			}
			solvers[f].update(cs, num);
			found = true;
			break;
		}

		if (!found) {
			PSolver solver = new PSolver(s1, s2, cs, num);
			addSolver(solver);
		}
	}

	public PBody[] getBodies() {
		return (PBody[]) CollectionUtils.copyOf(bodies, numBodies);
	}

	public PBody[] inner_bodies() {
		return bodies;
	}
	
	public int size(){
		return numBodies;
	}
	
	public long getCollisionDetectionTime() {
		return collisionDetectionTime;
	}

	public long getCollisionSolveTime() {
		return collisionSolveTime;
	}

	public Vector2f getGravity() {
		return gravity.clone();
	}

	public float getIterations() {
		return iterations;
	}

	public PJoint[] getJoints() {
		return (PJoint[]) CollectionUtils.copyOf(joints, numJoints);
	}

	public long getPositionUpdateTime() {
		return positionUpdateTime;
	}

	public PShape[] getShapes() {
		return (PShape[]) CollectionUtils.copyOf(shapes, numShapes);
	}

	public PSolver[] getSolvers() {
		return (PSolver[]) CollectionUtils.copyOf(solvers, numSolvers);
	}

	public long getTotalStepTime() {
		return totalStepTime;
	}

	private void removeBody(int index) {
		for (int i = 0; i < bodies[index].numShapes; i++) {
			PShape s = bodies[index].shapes[i];
			if (s._type == PShapeType.CONCAVE_SHAPE) {
				PConcavePolygonShape c = (PConcavePolygonShape) s;
				for (int j = 0; j < c.numConvexes; j++) {
					c.convexes[j]._rem = true;
				}
			} else {
				s._rem = true;
			}
		}
		if (index != numBodies - 1) {
			System.arraycopy(bodies, index + 1, bodies, index, numBodies
					- index - 1);
		}
		numBodies--;
	}

	private void removeJoint(int index) {
		if (index != numJoints - 1) {
			System.arraycopy(joints, index + 1, joints, index, numJoints
					- index - 1);
		}
		numJoints--;
	}

	private void removeShape(int index) {
		if (shapes[index]._type == PShapeType.CONCAVE_SHAPE) {
			PConcavePolygonShape c = (PConcavePolygonShape) shapes[index];
			for (int i = 0; i < c.numConvexes; i++) {
				c.convexes[i]._rem = true;
			}
		}
		shapes[index]._sapAABB.remove();
		if (index != numShapes - 1) {
			System.arraycopy(shapes, index + 1, shapes, index, numShapes
					- index - 1);
		}
		numShapes--;
	}

	private void removeSolver(int index) {
		if (index != numSolvers - 1) {
			System.arraycopy(solvers, index + 1, solvers, index, numSolvers
					- index - 1);
		}
		numSolvers--;
	}

	public void setGravity(float gx, float gy) {
		gravity.set(gx, gy);
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	private void solve(float dt) {
		long st = System.nanoTime();
		for (int i = 0; i < numSolvers; i++) {
			solvers[i].preSolve();
		}
		for (int i = 0; i < numJoints; i++) {
			joints[i].preSolve(dt);
		}
		for (int j = 0; j < iterations; j++) {
			for (int i = 0; i < numJoints; i++) {
				joints[i].solveVelocity(dt);
			}
			for (int i = 0; i < numSolvers; i++) {
				solvers[i].solveVelocity();
			}
		}

		long en = System.nanoTime();
		collisionSolveTime = en - st;
		st = System.nanoTime();
		for (int i = 0; i < numBodies; i++)
			if (!bodies[i].fix) {
				PBody b = bodies[i];
				b.correctVel.x = b.vel.x * dt;
				b.correctVel.y = b.vel.y * dt;
				b.correctAngVel = b.angVel * dt;
			}

		en = System.nanoTime();
		positionUpdateTime += en - st;
		st = System.nanoTime();
		for (int j = 0; j < iterations; j++) {
			for (int i = 0; i < numJoints; i++){
				joints[i].solvePosition();
			}
			for (int i = 0; i < numSolvers; i++){
				solvers[i].solvePosition();
			}
		}

		en = System.nanoTime();
		collisionSolveTime += en - st;
		st = System.nanoTime();
		for (int i = 0; i < numBodies; i++) {
			PBody b = bodies[i];
			if (b.fix) {
				b.angVel = 0.0F;
				b.vel.set(0.0F, 0.0F);
			} else {
				b.pos.x += b.correctVel.x;
				b.pos.y += b.correctVel.y;
				b.ang += b.correctAngVel;
			}
			b.update();
		}

		for (int i = 0; i < numJoints; i++) {
			joints[i].update();
		}
		en = System.nanoTime();
		positionUpdateTime += en - st;
	}

	public void step(float dt) {
		long st = System.nanoTime();
		for (int i = 0; i < numBodies; i++)
			if (bodies[i].rem) {
				removeBody(i);
				i--;
			} else {
				bodies[i].update();
				if (!bodies[i].fix) {
					PBody b = bodies[i];
					b.vel.x += gravity.x * dt;
					b.vel.y += gravity.y * dt;
				}
			}

		for (int i = 0; i < numShapes; i++) {
			if (shapes[i]._rem) {
				removeShape(i);
				i--;
			}
		}
		for (int i = 0; i < numJoints; i++) {
			if (joints[i].rem) {
				removeJoint(i);
				i--;
			} else {
				joints[i].update();
			}
		}
		long en = System.nanoTime();
		positionUpdateTime = en - st;
		collide(en);
		solve(dt);
		long totalEn = System.nanoTime();
		totalStepTime = totalEn - st;
	}

	public void update() {
		for (int i = 0; i < numBodies; i++) {
			if (bodies[i].rem) {
				removeBody(i);
				i--;
			} else {
				bodies[i].update();
			}
		}
		for (int i = 0; i < numShapes; i++) {
			if (shapes[i]._rem) {
				removeShape(i);
				i--;
			}
		}
		for (int i = 0; i < numJoints; i++) {
			if (joints[i].rem) {
				removeJoint(i);
				i--;
			} else {
				joints[i].update();
			}
		}
	}
}

/// <summary>
/// Copyright 2013 The Loon Authors
/// Licensed under the Apache License, Version 2.0 (the "License"); you may not
/// use this file except in compliance with the License. You may obtain a copy of
/// the License at
/// http://www.apache.org/licenses/LICENSE-2.0
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
/// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
/// License for the specific language governing permissions and limitations under
/// the License.
/// </summary>
///
using Loon.Core.Geom;
using Loon.Utils;
namespace Loon.Physics {
	
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
	
		public bool RemoveBody(PBody b) {
			return RemoveBody(b, false);
		}
	
		public bool RemoveBody(PBody b, bool identity) {
			object[] items = this.bodies;
			if (identity || b == null) {
				for (int i = 0; i < numBodies; i++) {
					if (items[i] == (object) b) {
						RemoveBody(i);
						return true;
					}
				}
			} else {
				for (int i_0 = 0; i_0 < numBodies; i_0++) {
					if (b.Equals(items[i_0])) {
						RemoveBody(i_0);
						return true;
					}
				}
			}
			return false;
		}
	
		public void AddBody(PBody b) {
			if (numBodies + 1 >= bodies.Length) {
				bodies = (PBody[]) CollectionUtils
						.CopyOf(bodies, bodies.Length * 2);
			}
			b.w = this;
			for (int i = 0; i < b.numShapes; i++) {
				AddShape(b.shapes[i]);
			}
			bodies[numBodies] = b;
			numBodies++;
		}
	
		public void AddJoint(PJoint j) {
			if (numJoints + 1 >= joints.Length) {
				joints = (PJoint[]) CollectionUtils.CopyOf(joints,
						joints.Length * 2);
			}
			joints[numJoints] = j;
			numJoints++;
		}
	
		internal void AddShape(PShape s) {
			if (s._type == PShapeType.CONCAVE_SHAPE) {
				PConcavePolygonShape c = (PConcavePolygonShape) s;
				for (int i = 0; i < c.numConvexes; i++) {
					AddShape(((PShape) (c.convexes[i])));
				}
				return;
			}
			if (numShapes + 1 >= shapes.Length) {
				shapes = (PShape[]) CollectionUtils.CopyOf(shapes,
						shapes.Length * 2);
			}
			shapes[numShapes] = s;
			s._sapAABB.Set(sap, s, s._aabb);
			numShapes++;
		}
	
		private void AddSolver(PSolver s) {
			if (numSolvers + 1 >= solvers.Length) {
				solvers = (PSolver[]) CollectionUtils.CopyOf(solvers,
						solvers.Length * 2);
			}
			solvers[numSolvers] = s;
			numSolvers++;
		}
	
		private PCollisionChooser cc = new PCollisionChooser();
	
		private void Collide(long st) {
			PSortableObject[] obj = sap.Sort();
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
										&& s1._aabb.IsHit(s2._aabb))
									CollisionShape(s1, s2, cc);
							}
						} while (obj[j] != end && j < sap.numObject);
					}
	
			} else {
				for (int i_0 = 0; i_0 < sap.numObject; i_0++)
					if (obj[i_0].begin) {
						PSortableObject end_1 = obj[i_0].aabb.endY;
						PShape s1_2 = obj[i_0].parent;
						int j_3 = i_0;
						do {
							j_3++;
							if (obj[j_3].begin) {
								PShape s2_4 = obj[j_3].parent;
								if ((!s1_2._parent.fix || !s2_4._parent.fix)
										&& s1_2._parent != s2_4._parent
										&& s1_2._aabb.IsHit(s2_4._aabb))
									CollisionShape(s1_2, s2_4, cc);
							}
						} while (obj[j_3] != end_1 && j_3 < sap.numObject);
					}
			}
			long en = (System.DateTime.Now.Ticks*100);
			collisionDetectionTime = en - st;
			for (int i_5 = 0; i_5 < numSolvers; i_5++) {
				if (solvers[i_5].rem) {
					RemoveSolver(i_5);
					i_5--;
				}
			}
		}
	
		private void CollisionShape(PShape s1, PShape s2, PCollisionChooser cc_0) {
			PContact[] cs = new PContact[2];
			int num = cc_0.Collide(s1, s2, cs);
			if (num == 0) {
				return;
			}
			bool found = false;
			for (int f = 0; f < numSolvers; f++) {
				if (s1 != solvers[f].s1 || s2 != solvers[f].s2) {
					continue;
				}
				solvers[f].Update(cs, num);
				found = true;
				break;
			}
	
			if (!found) {
				PSolver solver = new PSolver(s1, s2, cs, num);
				AddSolver(solver);
			}
		}
	
		public PBody[] GetBodies() {
			return (PBody[]) CollectionUtils.CopyOf(bodies, numBodies);
		}
	
		public PBody[] Inner_bodies() {
			return bodies;
		}
	
		public int Size() {
			return numBodies;
		}
	
		public long GetCollisionDetectionTime() {
			return collisionDetectionTime;
		}
	
		public long GetCollisionSolveTime() {
			return collisionSolveTime;
		}
	
		public Vector2f GetGravity() {
			return gravity.Clone();
		}
	
		public float GetIterations() {
			return (float) iterations;
		}
	
		public PJoint[] GetJoints() {
			return (PJoint[]) CollectionUtils.CopyOf(joints, numJoints);
		}
	
		public long GetPositionUpdateTime() {
			return positionUpdateTime;
		}
	
		public PShape[] GetShapes() {
			return (PShape[]) CollectionUtils.CopyOf(shapes, numShapes);
		}
	
		public PSolver[] GetSolvers() {
			return (PSolver[]) CollectionUtils.CopyOf(solvers, numSolvers);
		}
	
		public long GetTotalStepTime() {
			return totalStepTime;
		}
	
		private void RemoveBody(int index) {
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
				System.Array.Copy((bodies),index + 1,(bodies),index,numBodies
									- index - 1);
			}
			numBodies--;
		}
	
		private void RemoveJoint(int index) {
			if (index != numJoints - 1) {
				System.Array.Copy((joints),index + 1,(joints),index,numJoints
									- index - 1);
			}
			numJoints--;
		}
	
		private void RemoveShape(int index) {
			if (shapes[index]._type == PShapeType.CONCAVE_SHAPE) {
				PConcavePolygonShape c = (PConcavePolygonShape) shapes[index];
				for (int i = 0; i < c.numConvexes; i++) {
					c.convexes[i]._rem = true;
				}
			}
			shapes[index]._sapAABB.Remove();
			if (index != numShapes - 1) {
				System.Array.Copy((shapes),index + 1,(shapes),index,numShapes
									- index - 1);
			}
			numShapes--;
		}
	
		private void RemoveSolver(int index) {
			if (index != numSolvers - 1) {
				System.Array.Copy((solvers),index + 1,(solvers),index,numSolvers
									- index - 1);
			}
			numSolvers--;
		}
	
		public void SetGravity(float gx, float gy) {
			gravity.Set(gx, gy);
		}
	
		public void SetIterations(int iterations_0) {
			this.iterations = iterations_0;
		}
	
		private void Solve(float dt) {
			long st = (System.DateTime.Now.Ticks*100);
			for (int i = 0; i < numSolvers; i++) {
				solvers[i].PreSolve();
			}
			for (int i_0 = 0; i_0 < numJoints; i_0++) {
				joints[i_0].PreSolve(dt);
			}
			for (int j = 0; j < iterations; j++) {
				for (int i_1 = 0; i_1 < numJoints; i_1++) {
					joints[i_1].SolveVelocity(dt);
				}
				for (int i_2 = 0; i_2 < numSolvers; i_2++) {
					solvers[i_2].SolveVelocity();
				}
			}
	
			long en = (System.DateTime.Now.Ticks*100);
			collisionSolveTime = en - st;
			st = (System.DateTime.Now.Ticks*100);
			for (int i_3 = 0; i_3 < numBodies; i_3++)
				if (!bodies[i_3].fix) {
					PBody b = bodies[i_3];
					b.correctVel.x = b.vel.x * dt;
					b.correctVel.y = b.vel.y * dt;
					b.correctAngVel = b.angVel * dt;
				}
	
			en = (System.DateTime.Now.Ticks*100);
			positionUpdateTime += en - st;
			st = (System.DateTime.Now.Ticks*100);
			for (int j_4 = 0; j_4 < iterations; j_4++) {
				for (int i_5 = 0; i_5 < numJoints; i_5++) {
					joints[i_5].SolvePosition();
				}
				for (int i_6 = 0; i_6 < numSolvers; i_6++) {
					solvers[i_6].SolvePosition();
				}
			}
	
			en = (System.DateTime.Now.Ticks*100);
			collisionSolveTime += en - st;
			st = (System.DateTime.Now.Ticks*100);
			for (int i_7 = 0; i_7 < numBodies; i_7++) {
				PBody b_8 = bodies[i_7];
				if (b_8.fix) {
					b_8.angVel = 0.0F;
					b_8.vel.Set(0.0F, 0.0F);
				} else {
					b_8.pos.x += b_8.correctVel.x;
					b_8.pos.y += b_8.correctVel.y;
					b_8.ang += b_8.correctAngVel;
				}
				b_8.Update();
			}
	
			for (int i_9 = 0; i_9 < numJoints; i_9++) {
				joints[i_9].Update();
			}
			en = (System.DateTime.Now.Ticks*100);
			positionUpdateTime += en - st;
		}
	
		public void Step(float dt) {
			long st = (System.DateTime.Now.Ticks*100);
			for (int i = 0; i < numBodies; i++)
				if (bodies[i].rem) {
					RemoveBody(i);
					i--;
				} else {
					bodies[i].Update();
					if (!bodies[i].fix) {
						PBody b = bodies[i];
						b.vel.x += gravity.x * dt;
						b.vel.y += gravity.y * dt;
					}
				}
	
			for (int i_0 = 0; i_0 < numShapes; i_0++) {
				if (shapes[i_0]._rem) {
					RemoveShape(i_0);
					i_0--;
				}
			}
			for (int i_1 = 0; i_1 < numJoints; i_1++) {
				if (joints[i_1].rem) {
					RemoveJoint(i_1);
					i_1--;
				} else {
					joints[i_1].Update();
				}
			}
			long en = (System.DateTime.Now.Ticks*100);
			positionUpdateTime = en - st;
			Collide(en);
			Solve(dt);
			long totalEn = (System.DateTime.Now.Ticks*100);
			totalStepTime = totalEn - st;
		}
	
		public void Update() {
			for (int i = 0; i < numBodies; i++) {
				if (bodies[i].rem) {
					RemoveBody(i);
					i--;
				} else {
					bodies[i].Update();
				}
			}
			for (int i_0 = 0; i_0 < numShapes; i_0++) {
				if (shapes[i_0]._rem) {
					RemoveShape(i_0);
					i_0--;
				}
			}
			for (int i_1 = 0; i_1 < numJoints; i_1++) {
				if (joints[i_1].rem) {
					RemoveJoint(i_1);
					i_1--;
				} else {
					joints[i_1].Update();
				}
			}
		}
	}
}

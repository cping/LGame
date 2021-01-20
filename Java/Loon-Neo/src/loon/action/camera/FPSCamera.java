/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.action.camera;

import loon.geom.Matrix4;
import loon.geom.Quaternion;
import loon.geom.Transforms;
import loon.geom.Vector3f;

public class FPSCamera extends EmptyCamera {

	private static final float ANGLE_LIMIT_X = 60;

	private Quaternion rotation;

	private Vector3f position;
	private Vector3f forward;
	private Vector3f right;
	private Vector3f up;

	private float angleX;

	public FPSCamera() {
		this(70, 1f, 0.01f, 100);
	}

	public FPSCamera(float fovy, float aspect, float zNear, float zFar) {
		_viewMatrix4 = Transforms.createPerspective(fovy, aspect, zNear, zFar);

		position = new Vector3f(0, 0, 1);
		rotation = new Quaternion();

		forward = new Vector3f();
		right = new Vector3f();
		up = new Vector3f();
	}

	public FPSCamera lookAt(Vector3f point) {
		return lookAt(point, getUp().normalizeSelf());
	}

	public FPSCamera lookAt(Vector3f point, Vector3f up) {
		rotation = Transforms.createLookAtQuaternion(position, point, up,
				rotation);
		return this;
	}

	public Vector3f getUp() {
		return rotation.multiply(Vector3f.AXIS_Y(), up);
	}

	public FPSCamera lookAt(Vector3f position, Vector3f point, Vector3f up) {
		return setPosition(position).lookAt(point, up);
	}

	public FPSCamera moveForward(float amount) {
		return move(getForward(), amount);
	}

	public FPSCamera move(Vector3f dir, float amount) {
		position.addSelf(dir.normalize().scale(amount));
		return this;
	}

	public Vector3f getForward() {
		return rotation.multiply(forward.set(Vector3f.AXIS_Z()).negateSelf(),
				forward);
	}

	public FPSCamera moveBackward(float amount) {
		return move(getForward().negate(), amount);
	}

	public FPSCamera moveLeft(float amount) {
		return move(getRight().negate(), amount);
	}

	public Vector3f getRight() {
		return rotation.multiply(Vector3f.AXIS_X(), right);
	}

	public FPSCamera moveRight(float amount) {
		return move(getRight(), amount);
	}

	public FPSCamera moveUp(float amount) {
		return move(getUp(), amount);
	}

	public FPSCamera moveDown(float amount) {
		return move(getUp().negateSelf(), amount);
	}

	public FPSCamera rotateX(float angle) {
		angleX += angle;

		if (angleX < -ANGLE_LIMIT_X || angleX > ANGLE_LIMIT_X) {
			angleX -= angle;
			return this;
		}

		Quaternion tempQuat = Quaternion.TMP();

		Quaternion xRot = tempQuat.set(Vector3f.AXIS_X(), angle);
		rotation.multiplySelf(xRot);

		return this;
	}

	public FPSCamera rotateY(float angle) {
		Quaternion tempQuat = Quaternion.TMP();

		Quaternion yRot = tempQuat.set(Vector3f.AXIS_Y(), angle);
		rotation.set(yRot.multiplySelf(rotation));

		return this;
	}

	public FPSCamera lerp(FPSCamera p, float alpha) {
		position.lerpSelf(p.position, alpha);
		rotation.lerpSelf(p.rotation, alpha);

		return this;
	}

	public FPSCamera slerp(FPSCamera p, float alpha) {
		position.lerpSelf(p.position, alpha);
		rotation.slerpSelf(p.rotation, alpha);

		return this;
	}

	public FPSCamera initProjection(float fovy, float aspect, float zNear,
			float zFar) {
		Transforms.createPerspective(fovy, aspect, zNear, zFar, _projMatrix4);
		return this;
	}

	public FPSCamera initProjection(float width, float height) {
		return initProjection(0, width, height, 0, 0.01f, 100f);
	}

	public FPSCamera initProjection(float left, float right, float bottom,
			float top, float zNear, float zFar) {
		Transforms.createFrustum(left, right, bottom, top, zNear, zFar,
				_projMatrix4);
		return this;
	}

	@Override
	public void setup() {
		super.setup();

		Vector3f tempVec3 = Vector3f.TMP();
		Matrix4 tempMat4 = Matrix4.TMP();

		Quaternion tempQuat = Quaternion.TMP();

		_viewMatrix4
				.idt()
				.mul(Transforms.createRotation(tempQuat.set(rotation)
						.invertSelf(), tempMat4))
				.mul(Transforms.createTranslation(tempVec3.set(position)
						.negateSelf(), tempMat4));

	}

	public Vector3f getPosition() {
		return position;
	}

	public FPSCamera setPosition(Vector3f position) {
		this.position.set(position);
		return this;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public FPSCamera setRotation(Quaternion rotation) {
		this.rotation.set(rotation);
		return this;
	}
}

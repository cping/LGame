package loon.action.camera;

import loon.geom.Matrix4;
import loon.geom.Quaternion;
import loon.geom.Transforms;
import loon.geom.Vector3f;

public class PerspectiveCamera extends EmptyCamera {

	private Vector3f position;
	private Quaternion rotation;

	private Vector3f forward;
	private Vector3f right;
	private Vector3f up;

	public PerspectiveCamera() {
		this(70, 1f, 0.01f, 100);
	}

	public PerspectiveCamera(float fovy, float aspect, float zNear, float zFar) {
		super();
		_viewMatrix4 = Transforms.createPerspective(fovy, aspect, zNear, zFar);

		position = new Vector3f(0, 0, 1);
		rotation = new Quaternion();

		forward = new Vector3f(0, 0, -1);
		right = new Vector3f(0, 0, 1);
		up = new Vector3f(0, 1, 0);
	}

	public PerspectiveCamera lookAt(Vector3f point) {
		return lookAt(point, getUp().normalizeSelf());
	}

	public PerspectiveCamera lookAt(Vector3f point, Vector3f up) {
		Transforms.createLookAtQuaternion(position, point, up, rotation);
		return this;
	}

	public Vector3f getUp() {
		return rotation.multiply(up.set(Vector3f.AXIS_Y()), up).normalizeSelf();
	}

	public PerspectiveCamera lookAt(Vector3f position, Vector3f point,
			Vector3f up) {
		return setPosition(position).lookAt(point, up);
	}

	public PerspectiveCamera moveForward(float amount) {
		return move(getForward(), amount);
	}

	public PerspectiveCamera move(Vector3f dir, float amount) {
		position.addSelf(dir.normalizeSelf().scaleSelf(amount));
		return this;
	}

	public Vector3f getForward() {
		return rotation.multiply(forward.set(Vector3f.AXIS_Z()).negateSelf(),
				forward).normalizeSelf();
	}

	public PerspectiveCamera moveBackward(float amount) {
		return move(getForward().negateSelf(), amount);
	}

	public PerspectiveCamera moveLeft(float amount) {
		return move(getRight().negateSelf(), amount);
	}

	public Vector3f getRight() {
		return rotation.multiply(Vector3f.AXIS_X(), right).normalizeSelf();
	}

	public PerspectiveCamera moveRight(float amount) {
		return move(getRight(), amount);
	}

	public PerspectiveCamera moveUp(float amount) {
		return move(getUp(), amount);
	}

	public PerspectiveCamera moveDown(float amount) {
		return move(getUp().negateSelf(), amount);
	}

	public PerspectiveCamera rotateX(float angle) {
		Quaternion tempQuat = Quaternion.TMP();

		Quaternion xRot = tempQuat.set(Vector3f.AXIS_X(), angle);
		rotation.multiplySelf(xRot);

		return this;
	}

	public PerspectiveCamera rotateY(float angle) {
		Quaternion tempQuat = Quaternion.TMP();

		Quaternion yRot = tempQuat.set(Vector3f.AXIS_Y(), angle);
		rotation.set(yRot.multiplySelf(rotation));
		return this;
	}

	public PerspectiveCamera lerp(PerspectiveCamera p, float alpha) {
		position.lerpSelf(p.position, alpha);
		rotation.lerpSelf(p.rotation, alpha);

		return this;
	}

	public PerspectiveCamera slerp(PerspectiveCamera p, float alpha) {
		position.lerpSelf(p.position, alpha);
		rotation.slerpSelf(p.rotation, alpha);

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

	public PerspectiveCamera setPosition(Vector3f position) {
		this.position.set(position);
		return this;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public PerspectiveCamera setRotation(Quaternion rotation) {
		this.rotation.set(rotation);
		return this;
	}

    public PerspectiveCamera initProjection(float fovy, float aspect, float zNear, float zFar)
    {
        Transforms.createPerspective(fovy, aspect, zNear, zFar, _projMatrix4);
        return this;
    }

    public PerspectiveCamera initProjection(float width, float height)
    {
        return initProjection(0, width, height, 0, 0.01f, 100f);
    }

    public PerspectiveCamera initProjection(float left, float right, float bottom, float top, float zNear, float zFar)
    {
        Transforms.createFrustum(left, right, bottom, top, zNear, zFar, _projMatrix4);
        return this;
    }

}

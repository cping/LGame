package loon.geom;

public class Transform {

	private Matrix4 _matrix4;

	public Transform() {
		_matrix4 = new Matrix4();
	}

	public Transform translate(Vector2f v) {
		return cpy().translateSelf(v);
	}

	public Transform translateSelf(Vector2f v) {
		return translateSelf(new Vector3f(v, 0));
	}

	public Transform cpy() {
		return new Transform().applySelf(_matrix4);
	}

	public Transform translateSelf(Vector3f v) {
		Matrix4 temp = Matrix4.TMP();
		_matrix4.set(Transforms.createTranslation(v, temp).mul(_matrix4));
		return this;
	}

	public Transform applySelf(Matrix4 matrix) {
		Matrix4 temp = Matrix4.TMP();
		_matrix4.set(temp.set(matrix).mul(_matrix4));
		return this;
	}

	public Transform translate(Vector3f v) {
		return cpy().translateSelf(v);
	}

	public Transform rotate(Vector3f axis, float angle) {
		return cpy().rotateSelf(axis, angle);
	}

	public Transform rotateSelf(Vector3f axis, float angle) {
		Matrix4 temp = Matrix4.TMP();
		_matrix4.set(Transforms.createRotation(axis, angle, temp).mul(_matrix4));
		return this;
	}

	public Transform rotate(float rx, float ry, float rz) {
		return cpy().rotateSelf(rx, ry, rz);
	}

	public Transform rotateSelf(float rx, float ry, float rz) {
		Quaternion temp = Quaternion.TMP();
		temp.set(rx, ry, rz);
		Matrix4 tMat = Matrix4.TMP();
		_matrix4.set(Transforms.createRotation(temp, tMat).mul(_matrix4));
		return this;
	}

	public Transform scale(Vector2f scale) {
		return cpy().scaleSelf(scale);
	}

	public Transform scaleSelf(Vector2f scale) {
		Vector3f temp = Vector3f.TMP();
		scaleSelf(temp.set(scale.x, scale.y, 0));

		return this;
	}

	public Transform scaleSelf(Vector3f scale) {
		Matrix4 temp = Matrix4.TMP();
		_matrix4.set(Transforms.createScaling(scale, temp).mul(_matrix4));
		return this;
	}

	public Transform scale(Vector3f scale) {
		return cpy().scaleSelf(scale);
	}

	public Transform apply(Transform transform) {
		return cpy().applySelf(transform);
	}

	public Transform applySelf(Transform transform) {
		return applySelf(transform.getMatrix());
	}

	public Matrix4 getMatrix() {
		return _matrix4;
	}

	public Transform apply(Matrix4 matrix) {
		return cpy().applySelf(matrix);
	}

	public Transform applyInverse(Matrix4 matrix) {
		return cpy().applyInverseSelf(matrix);
	}

	public Transform applyInverseSelf(Matrix4 matrix) {
		Matrix4 temp = Matrix4.TMP();
		temp.set(matrix).inv();
		applySelf(temp);
		return this;
	}

	public Transform apply(Quaternion q) {
		return cpy().applySelf(q);
	}

	public Transform applySelf(Quaternion q) {
		Matrix4 temp = Matrix4.TMP();
		applySelf(Transforms.createRotation(q, temp));
		return this;
	}

	public Transform applyInverse(Quaternion q) {
		return cpy().applyInverseSelf(q);
	}

	public Transform applyInverseSelf(Quaternion q) {
		Matrix4 temp = Matrix4.TMP();
		applyInverseSelf(Transforms.createRotation(q, temp));
		return this;
	}

	public Transform set(Transform t) {
		return reset().applySelf(t);
	}

	public Transform reset() {
		_matrix4.idt();
		return this;
	}

	public Transform invert() {
		return cpy().invertSelf();
	}

	public Transform invertSelf() {
		_matrix4.inv().inv();
		return this;
	}
}

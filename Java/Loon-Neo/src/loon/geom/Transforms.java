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
package loon.geom;

import loon.utils.MathUtils;

public final class Transforms {

	private Transforms() {
	}

	public static Matrix4 createTranslation(Vector3f translation) {
		return createTranslation(translation, null);
	}

	public static Matrix4 createTranslation(Vector3f translation, Matrix4 dest) {
		if (dest == null) {
			dest = new Matrix4();
		}
		Matrix4 result = dest.idt();

		result.set(3, 0, translation.getX()).set(3, 1, translation.getY()).set(3, 2, translation.getZ());

		return result;
	}

	public static Matrix4 createScaling(Vector3f scale) {
		return createScaling(scale, null);
	}

	public static Matrix4 createScaling(Vector3f scale, Matrix4 dest) {
		if (dest == null) {
			dest = new Matrix4();
		}

		Matrix4 result = dest.idt();

		result.set(0, 0, scale.getX()).set(1, 1, scale.getY()).set(2, 2, scale.getZ());

		return result;
	}

	public static Matrix4 createRotation(Vector3f axis, float angle) {
		return createRotation(axis, angle, null);
	}

	public static Matrix4 createRotation(Vector3f axis, float angle, Matrix4 dest) {
		if (dest == null) {
			dest = new Matrix4();
		}

		Matrix4 result = dest.idt();

		float c = MathUtils.cos(angle);
		float s = MathUtils.sin(angle);

		Vector3f nAxis = Vector3f.TMP().set(axis).normalizeSelf();
		Vector3f tempV = Vector3f.TMP().set(nAxis).scaleSelf(1f - c);

		result.set(0, 0, c + tempV.x * nAxis.x).set(0, 1, tempV.x * nAxis.y + s * nAxis.z).set(0, 2,
				tempV.x * nAxis.z - s * nAxis.y);

		result.set(1, 0, tempV.y * nAxis.x - s * nAxis.z).set(1, 1, c + tempV.y * nAxis.y).set(1, 2,
				tempV.y * nAxis.z + s * nAxis.x);

		result.set(2, 0, tempV.z * nAxis.x + s * nAxis.y).set(2, 1, tempV.z * nAxis.y - s * nAxis.x).set(2, 2,
				c + tempV.z * nAxis.z);

		return result;
	}

	public static Matrix4 createOrtho2d(float left, float right, float bottom, float top, float zNear, float zFar) {
		return createOrtho2d(left, right, bottom, top, zNear, zFar, null);
	}

	public static Matrix4 createOrtho2d(float left, float right, float bottom, float top, float zNear, float zFar,
			Matrix4 dest) {
		if (dest == null) {
			dest = new Matrix4();
		}
		Matrix4 result = dest.izero();
		result.set(0, 0, 2 / (right - left)).set(1, 1, 2 / (top - bottom)).set(2, 2, -2 / (zFar - zNear))
				.set(3, 0, -(right + left) / (right - left)).set(3, 1, -(top + bottom) / (top - bottom))
				.set(3, 2, -(zFar + zNear) / (zFar - zNear)).set(3, 3, 1);
		return result;
	}

	public static Matrix4 createFrustum(float left, float right, float bottom, float top, float zNear, float zFar) {
		return createFrustum(left, right, bottom, top, zNear, zFar, null);
	}

	public static Matrix4 createFrustum(float left, float right, float bottom, float top, float zNear, float zFar,
			Matrix4 dest) {
		assert zFar > zNear;

		if (dest == null) {
			dest = new Matrix4();
		}

		Matrix4 result = dest.izero();

		result.set(0, 0, (2 * zNear) / (right - left)).set(1, 1, (2 * zNear) / (top - bottom))
				.set(2, 0, (right + left) / (right - left)).set(2, 1, (top + bottom) / (top - bottom))
				.set(2, 2, (zFar + zNear) / (zNear - zFar)).set(2, 3, -1)
				.set(3, 2, (-2 * zFar * zNear) / (zFar - zNear));

		return result;
	}

	public static Matrix4 createPerspective(float fovy, float aspect, float zNear, float zFar) {
		return createPerspective(fovy, aspect, zNear, zFar, null);
	}

	public static Matrix4 createPerspective(float fovy, float aspect, float zNear, float zFar, Matrix4 dest) {
		if (dest == null) {
			dest = new Matrix4();
		}

		Matrix4 result = dest.izero();

		float tanHalfFovy = MathUtils.tan(fovy / 2);

		result.set(0, 0, 1 / (aspect * tanHalfFovy)).set(1, 1, 1 / tanHalfFovy)
				.set(2, 2, (zFar + zNear) / (zNear - zFar)).set(2, 3, -1)
				.set(3, 2, (-2 * zFar * zNear) / (zFar - zNear));

		return result;
	}

	public static Matrix4 createLookAtMatrix(Vector3f eye, Vector3f center, Vector3f up) {
		return createLookAtMatrix(eye, center, up, null);
	}

	public static Matrix4 createLookAtMatrix(Vector3f eye, Vector3f center, Vector3f up, Matrix4 dest) {
		if (dest == null) {
			dest = new Matrix4();
		}

		Matrix4 result = dest.idt();

		Vector3f f = Vector3f.TMP();
		Vector3f s = Vector3f.TMP();
		Vector3f u = Vector3f.TMP();

		f.set(center).subtractSelf(eye).normalizeSelf();
		s.set(f).crossSelf(up).normalizeSelf();
		u.set(s).crossSelf(f);

		result.set(0, 0, s.x).set(1, 0, s.y).set(2, 0, s.z);

		result.set(0, 1, u.x).set(1, 1, u.y).set(2, 1, u.z);

		result.set(0, 2, -f.x).set(1, 2, -f.y).set(2, 2, -f.z);

		result.set(3, 0, -s.dot(eye)).set(3, 1, -u.dot(eye)).set(3, 2, f.dot(eye));

		return result;
	}

	public static Quaternion createLookAtQuaternion(Vector3f eye, Vector3f center, Vector3f up) {
		return createLookAtQuaternion(eye, center, up, null);
	}

	public static Quaternion createLookAtQuaternion(Vector3f eye, Vector3f center, Vector3f up, Quaternion dest) {
		if (dest == null) {
			dest = new Quaternion();
		}

		Vector3f temp1 = Vector3f.TMP();
		Vector3f temp2 = Vector3f.TMP();

		Vector3f forward = temp1.set(center).subtractSelf(eye).normalizeSelf();
		Vector3f negativeZ = temp2.set(Vector3f.AXIS_Z()).negateSelf();

		float dot = negativeZ.dot(forward);

		if (MathUtils.abs(dot + 1) < 0.000001f) {
			return dest.set(up.x, up.y, up.z, MathUtils.PI);
		}

		if (MathUtils.abs(dot - 1) < 0.000001f) {

			return dest.set();
		}

		float rotAngle = MathUtils.acos(dot);
		Vector3f rotAxis = negativeZ.crossSelf(forward).normalizeSelf();

		dest.set(rotAxis, rotAngle);

		return dest;
	}

	public static Matrix4 createRotation(Quaternion q) {
		return createRotation(q, null);
	}

	public static Matrix4 createRotation(Quaternion q, Matrix4 dest) {
		if (dest == null) {
			dest = new Matrix4();
		}

		q.normalizeSelf();

		Matrix4 result = dest.idt();

		float x2 = q.x * q.x;
		float y2 = q.y * q.y;
		float z2 = q.z * q.z;
		float xy = q.x * q.y;
		float xz = q.x * q.z;
		float yz = q.y * q.z;
		float wx = q.w * q.x;
		float wy = q.w * q.y;
		float wz = q.w * q.z;

		result.set(0, 0, 1.0f - 2.0f * (y2 + z2)).set(0, 1, 2.0f * (xy + wz)).set(0, 2, 2.0f * (xz - wy));

		result.set(1, 0, 2.0f * (xy - wz)).set(1, 1, 1.0f - 2.0f * (x2 + z2)).set(1, 2, 2.0f * (yz + wx));

		result.set(2, 0, 2.0f * (xz + wy)).set(2, 1, 2.0f * (yz - wx)).set(2, 2, 1.0f - 2.0f * (x2 + y2));

		return result;
	}
}

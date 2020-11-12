using loon.utils;

namespace loon.geom
{
	public sealed class Transforms
	{

		private Transforms()
		{
		}

		public static Matrix4 CreateTranslation(Vector3f translation)
		{
			return CreateTranslation(translation, null);
		}

		public static Matrix4 CreateTranslation(Vector3f translation, Matrix4 dest)
		{
			if (dest == null)
			{
				dest = new Matrix4();
			}
			Matrix4 result = dest.Idt();

			result.Set(3, 0, translation.GetX()).Set(3, 1, translation.GetY())
					.Set(3, 2, translation.GetZ());

			return result;
		}

		public static Matrix4 CreateScaling(Vector3f scale)
		{
			return CreateScaling(scale, null);
		}

		public static Matrix4 CreateScaling(Vector3f scale, Matrix4 dest)
		{
			if (dest == null)
			{
				dest = new Matrix4();
			}

			Matrix4 result = dest.Idt();

			result.Set(0, 0, scale.GetX()).Set(1, 1, scale.GetY())
					.Set(2, 2, scale.GetZ());

			return result;
		}

		public static Matrix4 CreateRotation(Vector3f axis, float angle)
		{
			return CreateRotation(axis, angle, null);
		}

		public static Matrix4 CreateRotation(Vector3f axis, float angle,
				Matrix4 dest)
		{
			if (dest == null)
			{
				dest = new Matrix4();
			}

			Matrix4 result = dest.Idt();

			float c = MathUtils.Cos(angle);
			float s = MathUtils.Sin(angle);

			Vector3f nAxis = Vector3f.TMP().Set(axis).NormalizeSelf();
			Vector3f tempV = Vector3f.TMP().Set(nAxis).ScaleSelf(1f - c);

			result.Set(0, 0, c + tempV.x * nAxis.x)
					.Set(0, 1, tempV.x * nAxis.y + s * nAxis.z)
					.Set(0, 2, tempV.x * nAxis.z - s * nAxis.y);

			result.Set(1, 0, tempV.y * nAxis.x - s * nAxis.z)
					.Set(1, 1, c + tempV.y * nAxis.y)
					.Set(1, 2, tempV.y * nAxis.z + s * nAxis.x);

			result.Set(2, 0, tempV.z * nAxis.x + s * nAxis.y)
					.Set(2, 1, tempV.z * nAxis.y - s * nAxis.x)
					.Set(2, 2, c + tempV.z * nAxis.z);

			return result;
		}

		public static Matrix4 CreateOrtho2d(float left, float right, float bottom,
				float top, float zNear, float zFar)
		{
			return CreateOrtho2d(left, right, bottom, top, zNear, zFar, null);
		}

		public static Matrix4 CreateOrtho2d(float left, float right, float bottom,
				float top, float zNear, float zFar, Matrix4 dest)
		{
			if (dest == null)
			{
				dest = new Matrix4();
			}
			Matrix4 result = dest.Izero();
			result.Set(0, 0, 2 / (right - left)).Set(1, 1, 2 / (top - bottom))
					.Set(2, 2, -2 / (zFar - zNear))
					.Set(3, 0, -(right + left) / (right - left))
					.Set(3, 1, -(top + bottom) / (top - bottom))
					.Set(3, 2, -(zFar + zNear) / (zFar - zNear)).Set(3, 3, 1);
			return result;
		}

		public static Matrix4 CreateFrustum(float left, float right, float bottom,
				float top, float zNear, float zFar)
		{
			return CreateFrustum(left, right, bottom, top, zNear, zFar, null);
		}

		public static Matrix4 CreateFrustum(float left, float right, float bottom,
				float top, float zNear, float zFar, Matrix4 dest)
		{

			if (dest == null)
			{
				dest = new Matrix4();
			}

			Matrix4 result = dest.Izero();

			result.Set(0, 0, (2 * zNear) / (right - left))
					.Set(1, 1, (2 * zNear) / (top - bottom))
					.Set(2, 0, (right + left) / (right - left))
					.Set(2, 1, (top + bottom) / (top - bottom))
					.Set(2, 2, (zFar + zNear) / (zNear - zFar)).Set(2, 3, -1)
					.Set(3, 2, (-2 * zFar * zNear) / (zFar - zNear));

			return result;
		}

		public static Matrix4 CreatePerspective(float fovy, float aspect,
				float zNear, float zFar)
		{
			return CreatePerspective(fovy, aspect, zNear, zFar, null);
		}

		public static Matrix4 CreatePerspective(float fovy, float aspect,
				float zNear, float zFar, Matrix4 dest)
		{
			if (dest == null)
			{
				dest = new Matrix4();
			}

			Matrix4 result = dest.Izero();

			float tanHalfFovy = MathUtils.Tan(fovy / 2);

			result.Set(0, 0, 1 / (aspect * tanHalfFovy)).Set(1, 1, 1 / tanHalfFovy)
					.Set(2, 2, (zFar + zNear) / (zNear - zFar)).Set(2, 3, -1)
					.Set(3, 2, (-2 * zFar * zNear) / (zFar - zNear));

			return result;
		}

		public static Matrix4 CreateLookAtMatrix(Vector3f eye, Vector3f center,
				Vector3f up)
		{
			return CreateLookAtMatrix(eye, center, up, null);
		}

		public static Matrix4 CreateLookAtMatrix(Vector3f eye, Vector3f center,
				Vector3f up, Matrix4 dest)
		{
			if (dest == null)
			{
				dest = new Matrix4();
			}

			Matrix4 result = dest.Idt();

			Vector3f f = Vector3f.TMP();
			Vector3f s = Vector3f.TMP();
			Vector3f u = Vector3f.TMP();

			f.Set(center).SubtractSelf(eye).NormalizeSelf();
			s.Set(f).CrossSelf(up).NormalizeSelf();
			u.Set(s).CrossSelf(f);

			result.Set(0, 0, s.x).Set(1, 0, s.y).Set(2, 0, s.z);

			result.Set(0, 1, u.x).Set(1, 1, u.y).Set(2, 1, u.z);

			result.Set(0, 2, -f.x).Set(1, 2, -f.y).Set(2, 2, -f.z);

			result.Set(3, 0, -s.Dot(eye)).Set(3, 1, -u.Dot(eye))
					.Set(3, 2, f.Dot(eye));

			return result;
		}

		public static Quaternion CreateLookAtQuaternion(Vector3f eye,
				Vector3f center, Vector3f up)
		{
			return CreateLookAtQuaternion(eye, center, up, null);
		}

		public static Quaternion CreateLookAtQuaternion(Vector3f eye,
				Vector3f center, Vector3f up, Quaternion dest)
		{
			if (dest == null)
			{
				dest = new Quaternion();
			}

			Vector3f temp1 = Vector3f.TMP();
			Vector3f temp2 = Vector3f.TMP();

			Vector3f forward = temp1.Set(center).SubtractSelf(eye).NormalizeSelf();
			Vector3f negativeZ = temp2.Set(Vector3f.AXIS_Z()).NegateSelf();

			float dot = negativeZ.Dot(forward);

			if (MathUtils.Abs(dot + 1) < 0.000001f)
			{
				return dest.Set(up.x, up.y, up.z, MathUtils.PI);
			}

			if (MathUtils.Abs(dot - 1) < 0.000001f)
			{

				return dest.Set();
			}

			float rotAngle = MathUtils.Acos(dot);
			Vector3f rotAxis = negativeZ.CrossSelf(forward).NormalizeSelf();

			dest.Set(rotAxis, rotAngle);

			return dest;
		}

		public static Matrix4 CreateRotation(Quaternion q)
		{
			return CreateRotation(q, null);
		}

		public static Matrix4 CreateRotation(Quaternion q, Matrix4 dest)
		{
			if (dest == null)
			{
				dest = new Matrix4();
			}

			q.NormalizeSelf();

			Matrix4 result = dest.Idt();

			float x2 = q.x * q.x;
			float y2 = q.y * q.y;
			float z2 = q.z * q.z;
			float xy = q.x * q.y;
			float xz = q.x * q.z;
			float yz = q.y * q.z;
			float wx = q.w * q.x;
			float wy = q.w * q.y;
			float wz = q.w * q.z;

			result.Set(0, 0, 1.0f - 2.0f * (y2 + z2)).Set(0, 1, 2.0f * (xy + wz))
					.Set(0, 2, 2.0f * (xz - wy));

			result.Set(1, 0, 2.0f * (xy - wz)).Set(1, 1, 1.0f - 2.0f * (x2 + z2))
					.Set(1, 2, 2.0f * (yz + wx));

			result.Set(2, 0, 2.0f * (xz + wy)).Set(2, 1, 2.0f * (yz - wx))
					.Set(2, 2, 1.0f - 2.0f * (x2 + y2));

			return result;
		}
	}
}

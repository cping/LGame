using java.lang;
using loon.utils;

namespace loon.geom
{
    public class Matrix4
    {

		public  static Matrix4 TMP()
		{
			return new Matrix4();
		}

		public  static Matrix4 ZERO()
		{
			return new Matrix4();
		}

		public const  int M00 = 0;

		public const int M01 = 4;

		public const int M02 = 8;

		public const int M03 = 12;

		public const int M10 = 1;

		public const int M11 = 5;

		public const int M12 = 9;

		public const int M13 = 13;

		public const int M20 = 2;

		public const int M21 = 6;

		public const int M22 = 10;

		public const int M23 = 14;

		public const int M30 = 3;

		public const int M31 = 7;

		public const int M32 = 11;

		public const int M33 = 15;

		public readonly  float[] tmp = new float[16];

		public readonly float[] val = new float[16];

		//private Support //support;

		private void Init()
		{
		
		}

		public Matrix4()
		{
			val[M00] = 1f;
			val[M11] = 1f;
			val[M22] = 1f;
			val[M33] = 1f;
			Init();
		}

		public Matrix4(Matrix4 matrix)
		{
			Init();
			this.Set(matrix);
		}

		public Matrix4(float[] values)
		{
			Init();
			this.Set(values);
		}

		public Matrix4(Quaternion quaternion)
		{
			Init();
			this.Set(quaternion);
		}

		public Matrix4(Vector3f position, Quaternion rotation, Vector3f Scale)
		{
			Init();
			Set(position, rotation, Scale);
		}

		public Matrix4 Set(Matrix4 matrix)
		{
			return this.Set(matrix.val);
		}

		public Matrix4 Set(float[] values)
		{
			JavaSystem.Arraycopy(values, 0, val, 0, val.Length);
			return this;
		}

		public Matrix4 Set(Quaternion quaternion)
		{
			return Set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
		}

		public Matrix4 Set(float quaternionX, float quaternionY, float quaternionZ, float quaternionW)
		{
			return Set(0f, 0f, 0f, quaternionX, quaternionY, quaternionZ, quaternionW);
		}

		public Matrix4 Set(Vector3f position, Quaternion orientation)
		{
			return Set(position.x, position.y, position.z, orientation.x, orientation.y, orientation.z, orientation.w);
		}

		public Matrix4 Set(float TranslationX, float TranslationY, float TranslationZ, float quaternionX, float quaternionY,
				float quaternionZ, float quaternionW)
		{
			 float xs = quaternionX * 2f, ys = quaternionY * 2f, zs = quaternionZ * 2f;
			 float wx = quaternionW * xs, wy = quaternionW * ys, wz = quaternionW * zs;
			 float xx = quaternionX * xs, xy = quaternionX * ys, xz = quaternionX * zs;
			 float yy = quaternionY * ys, yz = quaternionY * zs, zz = quaternionZ * zs;

			val[M00] = (1.0f - (yy + zz));
			val[M01] = (xy - wz);
			val[M02] = (xz + wy);
			val[M03] = TranslationX;

			val[M10] = (xy + wz);
			val[M11] = (1.0f - (xx + zz));
			val[M12] = (yz - wx);
			val[M13] = TranslationY;

			val[M20] = (xz - wy);
			val[M21] = (yz + wx);
			val[M22] = (1.0f - (xx + yy));
			val[M23] = TranslationZ;

			val[M30] = 0;
			val[M31] = 0;
			val[M32] = 0;
			val[M33] = 1.0f;
			return this;
		}

		public Matrix4 Set(Vector3f position, Quaternion orientation, Vector3f Scale)
		{
			return Set(position.x, position.y, position.z, orientation.x, orientation.y, orientation.z, orientation.w,
					Scale.x, Scale.y, Scale.z);
		}

		public Matrix4 Set(float TranslationX, float TranslationY, float TranslationZ, float quaternionX, float quaternionY,
				float quaternionZ, float quaternionW, float ScaleX, float ScaleY, float ScaleZ)
		{

			 float xs = quaternionX * 2f, ys = quaternionY * 2f, zs = quaternionZ * 2f;
			 float wx = quaternionW * xs, wy = quaternionW * ys, wz = quaternionW * zs;
			 float xx = quaternionX * xs, xy = quaternionX * ys, xz = quaternionX * zs;
			 float yy = quaternionY * ys, yz = quaternionY * zs, zz = quaternionZ * zs;

			val[M00] = ScaleX * (1.0f - (yy + zz));
			val[M01] = ScaleY * (xy - wz);
			val[M02] = ScaleZ * (xz + wy);
			val[M03] = TranslationX;

			val[M10] = ScaleX * (xy + wz);
			val[M11] = ScaleY * (1.0f - (xx + zz));
			val[M12] = ScaleZ * (yz - wx);
			val[M13] = TranslationY;

			val[M20] = ScaleX * (xz - wy);
			val[M21] = ScaleY * (yz + wx);
			val[M22] = ScaleZ * (1.0f - (xx + yy));
			val[M23] = TranslationZ;

			val[M30] = 0;
			val[M31] = 0;
			val[M32] = 0;
			val[M33] = 1.0f;
			return this;
		}

		public Matrix4 Set(Vector3f xAxis, Vector3f yAxis, Vector3f zAxis, Vector3f pos)
		{
			val[M00] = xAxis.x;
			val[M01] = xAxis.y;
			val[M02] = xAxis.z;
			val[M10] = yAxis.x;
			val[M11] = yAxis.y;
			val[M12] = yAxis.z;
			val[M20] = zAxis.x;
			val[M21] = zAxis.y;
			val[M22] = zAxis.z;
			val[M03] = pos.x;
			val[M13] = pos.y;
			val[M23] = pos.z;
			val[M30] = 0;
			val[M31] = 0;
			val[M32] = 0;
			val[M33] = 1;
			return this;
		}

		public Matrix4 Set(int x, int y, float v)
		{
			val[y + x * 4] = v;
			return this;
		}

		public float Get(int x, int y)
		{
			return val[y + x * 4];
		}

		public Matrix4 Cpy()
		{
			return new Matrix4(this);
		}

		public Matrix4 Trn(Vector3f vector)
		{
			val[M03] += vector.x;
			val[M13] += vector.y;
			val[M23] += vector.z;
			return this;
		}

		public Matrix4 Trn(float x, float y, float z)
		{
			val[M03] += x;
			val[M13] += y;
			val[M23] += z;
			return this;
		}

		public float[] GetValues()
		{
			return val;
		}

		public Matrix4 Mul(Matrix4 matrix)
		{
			//support.Mul(val, matrix.val);
			return this;
		}

		public Matrix4 Mul(Affine2f aff)
		{
			Matrix4 m = new Matrix4();
			m.Set(aff);
			////support.Mul(val, m.val);
			return this;
		}

		public Matrix4 MulLeft(Matrix4 matrix)
		{
			tmpMat.Set(matrix);
			////support.Mul(tmpMat.val, this.val);
			return Set(tmpMat);
		}

		public Matrix4 Tra()
		{
			tmp[M00] = val[M00];
			tmp[M01] = val[M10];
			tmp[M02] = val[M20];
			tmp[M03] = val[M30];
			tmp[M10] = val[M01];
			tmp[M11] = val[M11];
			tmp[M12] = val[M21];
			tmp[M13] = val[M31];
			tmp[M20] = val[M02];
			tmp[M21] = val[M12];
			tmp[M22] = val[M22];
			tmp[M23] = val[M32];
			tmp[M30] = val[M03];
			tmp[M31] = val[M13];
			tmp[M32] = val[M23];
			tmp[M33] = val[M33];
			return Set(tmp);
		}

		public Matrix4 Idt()
		{
			val[M00] = 1;
			val[M01] = 0;
			val[M02] = 0;
			val[M03] = 0;
			val[M10] = 0;
			val[M11] = 1;
			val[M12] = 0;
			val[M13] = 0;
			val[M20] = 0;
			val[M21] = 0;
			val[M22] = 1;
			val[M23] = 0;
			val[M30] = 0;
			val[M31] = 0;
			val[M32] = 0;
			val[M33] = 1;
			return this;
		}

		public Matrix4 Izero()
		{
			val[M00] = 0;
			val[M01] = 0;
			val[M02] = 0;
			val[M03] = 0;
			val[M10] = 0;
			val[M11] = 0;
			val[M12] = 0;
			val[M13] = 0;
			val[M20] = 0;
			val[M21] = 0;
			val[M22] = 0;
			val[M23] = 0;
			val[M30] = 0;
			val[M31] = 0;
			val[M32] = 0;
			val[M33] = 0;
			return this;
		}

		public Matrix4 Inv()
		{
			float l_Det = val[M30] * val[M21] * val[M12] * val[M03] - val[M20] * val[M31] * val[M12] * val[M03]
					- val[M30] * val[M11] * val[M22] * val[M03] + val[M10] * val[M31] * val[M22] * val[M03]
					+ val[M20] * val[M11] * val[M32] * val[M03] - val[M10] * val[M21] * val[M32] * val[M03]
					- val[M30] * val[M21] * val[M02] * val[M13] + val[M20] * val[M31] * val[M02] * val[M13]
					+ val[M30] * val[M01] * val[M22] * val[M13] - val[M00] * val[M31] * val[M22] * val[M13]
					- val[M20] * val[M01] * val[M32] * val[M13] + val[M00] * val[M21] * val[M32] * val[M13]
					+ val[M30] * val[M11] * val[M02] * val[M23] - val[M10] * val[M31] * val[M02] * val[M23]
					- val[M30] * val[M01] * val[M12] * val[M23] + val[M00] * val[M31] * val[M12] * val[M23]
					+ val[M10] * val[M01] * val[M32] * val[M23] - val[M00] * val[M11] * val[M32] * val[M23]
					- val[M20] * val[M11] * val[M02] * val[M33] + val[M10] * val[M21] * val[M02] * val[M33]
					+ val[M20] * val[M01] * val[M12] * val[M33] - val[M00] * val[M21] * val[M12] * val[M33]
					- val[M10] * val[M01] * val[M22] * val[M33] + val[M00] * val[M11] * val[M22] * val[M33];
			if (l_Det == 0f)
			{
				throw new LSysException("non-invertible matrix");
			}
			float inv_Det = 1.0f / l_Det;
			tmp[M00] = val[M12] * val[M23] * val[M31] - val[M13] * val[M22] * val[M31] + val[M13] * val[M21] * val[M32]
					- val[M11] * val[M23] * val[M32] - val[M12] * val[M21] * val[M33] + val[M11] * val[M22] * val[M33];
			tmp[M01] = val[M03] * val[M22] * val[M31] - val[M02] * val[M23] * val[M31] - val[M03] * val[M21] * val[M32]
					+ val[M01] * val[M23] * val[M32] + val[M02] * val[M21] * val[M33] - val[M01] * val[M22] * val[M33];
			tmp[M02] = val[M02] * val[M13] * val[M31] - val[M03] * val[M12] * val[M31] + val[M03] * val[M11] * val[M32]
					- val[M01] * val[M13] * val[M32] - val[M02] * val[M11] * val[M33] + val[M01] * val[M12] * val[M33];
			tmp[M03] = val[M03] * val[M12] * val[M21] - val[M02] * val[M13] * val[M21] - val[M03] * val[M11] * val[M22]
					+ val[M01] * val[M13] * val[M22] + val[M02] * val[M11] * val[M23] - val[M01] * val[M12] * val[M23];
			tmp[M10] = val[M13] * val[M22] * val[M30] - val[M12] * val[M23] * val[M30] - val[M13] * val[M20] * val[M32]
					+ val[M10] * val[M23] * val[M32] + val[M12] * val[M20] * val[M33] - val[M10] * val[M22] * val[M33];
			tmp[M11] = val[M02] * val[M23] * val[M30] - val[M03] * val[M22] * val[M30] + val[M03] * val[M20] * val[M32]
					- val[M00] * val[M23] * val[M32] - val[M02] * val[M20] * val[M33] + val[M00] * val[M22] * val[M33];
			tmp[M12] = val[M03] * val[M12] * val[M30] - val[M02] * val[M13] * val[M30] - val[M03] * val[M10] * val[M32]
					+ val[M00] * val[M13] * val[M32] + val[M02] * val[M10] * val[M33] - val[M00] * val[M12] * val[M33];
			tmp[M13] = val[M02] * val[M13] * val[M20] - val[M03] * val[M12] * val[M20] + val[M03] * val[M10] * val[M22]
					- val[M00] * val[M13] * val[M22] - val[M02] * val[M10] * val[M23] + val[M00] * val[M12] * val[M23];
			tmp[M20] = val[M11] * val[M23] * val[M30] - val[M13] * val[M21] * val[M30] + val[M13] * val[M20] * val[M31]
					- val[M10] * val[M23] * val[M31] - val[M11] * val[M20] * val[M33] + val[M10] * val[M21] * val[M33];
			tmp[M21] = val[M03] * val[M21] * val[M30] - val[M01] * val[M23] * val[M30] - val[M03] * val[M20] * val[M31]
					+ val[M00] * val[M23] * val[M31] + val[M01] * val[M20] * val[M33] - val[M00] * val[M21] * val[M33];
			tmp[M22] = val[M01] * val[M13] * val[M30] - val[M03] * val[M11] * val[M30] + val[M03] * val[M10] * val[M31]
					- val[M00] * val[M13] * val[M31] - val[M01] * val[M10] * val[M33] + val[M00] * val[M11] * val[M33];
			tmp[M23] = val[M03] * val[M11] * val[M20] - val[M01] * val[M13] * val[M20] - val[M03] * val[M10] * val[M21]
					+ val[M00] * val[M13] * val[M21] + val[M01] * val[M10] * val[M23] - val[M00] * val[M11] * val[M23];
			tmp[M30] = val[M12] * val[M21] * val[M30] - val[M11] * val[M22] * val[M30] - val[M12] * val[M20] * val[M31]
					+ val[M10] * val[M22] * val[M31] + val[M11] * val[M20] * val[M32] - val[M10] * val[M21] * val[M32];
			tmp[M31] = val[M01] * val[M22] * val[M30] - val[M02] * val[M21] * val[M30] + val[M02] * val[M20] * val[M31]
					- val[M00] * val[M22] * val[M31] - val[M01] * val[M20] * val[M32] + val[M00] * val[M21] * val[M32];
			tmp[M32] = val[M02] * val[M11] * val[M30] - val[M01] * val[M12] * val[M30] - val[M02] * val[M10] * val[M31]
					+ val[M00] * val[M12] * val[M31] + val[M01] * val[M10] * val[M32] - val[M00] * val[M11] * val[M32];
			tmp[M33] = val[M01] * val[M12] * val[M20] - val[M02] * val[M11] * val[M20] + val[M02] * val[M10] * val[M21]
					- val[M00] * val[M12] * val[M21] - val[M01] * val[M10] * val[M22] + val[M00] * val[M11] * val[M22];
			val[M00] = tmp[M00] * inv_Det;
			val[M01] = tmp[M01] * inv_Det;
			val[M02] = tmp[M02] * inv_Det;
			val[M03] = tmp[M03] * inv_Det;
			val[M10] = tmp[M10] * inv_Det;
			val[M11] = tmp[M11] * inv_Det;
			val[M12] = tmp[M12] * inv_Det;
			val[M13] = tmp[M13] * inv_Det;
			val[M20] = tmp[M20] * inv_Det;
			val[M21] = tmp[M21] * inv_Det;
			val[M22] = tmp[M22] * inv_Det;
			val[M23] = tmp[M23] * inv_Det;
			val[M30] = tmp[M30] * inv_Det;
			val[M31] = tmp[M31] * inv_Det;
			val[M32] = tmp[M32] * inv_Det;
			val[M33] = tmp[M33] * inv_Det;
			return this;
		}

		public float Det()
		{
			return val[M30] * val[M21] * val[M12] * val[M03] - val[M20] * val[M31] * val[M12] * val[M03]
					- val[M30] * val[M11] * val[M22] * val[M03] + val[M10] * val[M31] * val[M22] * val[M03]
					+ val[M20] * val[M11] * val[M32] * val[M03] - val[M10] * val[M21] * val[M32] * val[M03]
					- val[M30] * val[M21] * val[M02] * val[M13] + val[M20] * val[M31] * val[M02] * val[M13]
					+ val[M30] * val[M01] * val[M22] * val[M13] - val[M00] * val[M31] * val[M22] * val[M13]
					- val[M20] * val[M01] * val[M32] * val[M13] + val[M00] * val[M21] * val[M32] * val[M13]
					+ val[M30] * val[M11] * val[M02] * val[M23] - val[M10] * val[M31] * val[M02] * val[M23]
					- val[M30] * val[M01] * val[M12] * val[M23] + val[M00] * val[M31] * val[M12] * val[M23]
					+ val[M10] * val[M01] * val[M32] * val[M23] - val[M00] * val[M11] * val[M32] * val[M23]
					- val[M20] * val[M11] * val[M02] * val[M33] + val[M10] * val[M21] * val[M02] * val[M33]
					+ val[M20] * val[M01] * val[M12] * val[M33] - val[M00] * val[M21] * val[M12] * val[M33]
					- val[M10] * val[M01] * val[M22] * val[M33] + val[M00] * val[M11] * val[M22] * val[M33];
		}

		public float Det3x3()
		{
			return val[M00] * val[M11] * val[M22] + val[M01] * val[M12] * val[M20] + val[M02] * val[M10] * val[M21]
					- val[M00] * val[M12] * val[M21] - val[M01] * val[M10] * val[M22] - val[M02] * val[M11] * val[M20];
		}

		public Matrix4 SetToProjection(float near, float far, float fovy, float aspecTratio)
		{

			float l_fd = (1f / MathUtils.Tan(fovy * MathUtils.DEG_TO_RAD) / 2f);
			float l_a1 = (far + near) / (near - far);
			float l_a2 = (2 * far * near) / (near - far);
			val[M00] = l_fd / aspecTratio;
			val[M10] = 0;
			val[M20] = 0;
			val[M30] = 0;
			val[M01] = 0;
			val[M11] = l_fd;
			val[M21] = 0;
			val[M31] = 0;
			val[M02] = 0;
			val[M12] = 0;
			val[M22] = l_a1;
			val[M32] = -1;
			val[M03] = 0;
			val[M13] = 0;
			val[M23] = l_a2;
			val[M33] = 0;

			return this;
		}

		public Matrix4 SetToOrtho2D(float x, float y, float wIdth, float height)
		{
			SetToOrtho(x, x + wIdth, y + height, y, 1f, -1f);
			return this;
		}

		public Matrix4 SetToOrtho2D(float x, float y, float wIdth, float height, float near, float far)
		{
			SetToOrtho(x, x + wIdth, y + height, y, near, far);
			return this;
		}

		public Matrix4 SetToOrtho(float left, float right, float bottom, float top, float near, float far)
		{

			float x_orth = 2 / (right - left);
			float y_orth = 2 / (top - bottom);
			float z_orth = -2 / (far - near);

			float tx = -(right + left) / (right - left);
			float ty = -(top + bottom) / (top - bottom);
			float tz = -(far + near) / (far - near);

			val[M00] = x_orth;
			val[M10] = 0;
			val[M20] = 0;
			val[M30] = 0;
			val[M01] = 0;
			val[M11] = y_orth;
			val[M21] = 0;
			val[M31] = 0;
			val[M02] = 0;
			val[M12] = 0;
			val[M22] = z_orth;
			val[M32] = 0;
			val[M03] = tx;
			val[M13] = ty;
			val[M23] = tz;
			val[M33] = 1;

			return this;
		}

		public Matrix4 SetTranslation(Vector3f vector)
		{
			val[M03] = vector.x;
			val[M13] = vector.y;
			val[M23] = vector.z;
			return this;
		}

		public Matrix4 SetTranslation(float x, float y, float z)
		{
			val[M03] = x;
			val[M13] = y;
			val[M23] = z;
			return this;
		}

		public Matrix4 SetToTranslation(Vector3f vector)
		{
			Idt();
			val[M03] = vector.x;
			val[M13] = vector.y;
			val[M23] = vector.z;
			return this;
		}

		public Matrix4 SetToTranslation(float x, float y, float z)
		{
			Idt();
			val[M03] = x;
			val[M13] = y;
			val[M23] = z;
			return this;
		}

		public Matrix4 SetToTranslationAndScaling(Vector3f Translation, Vector3f scaling)
		{
			Idt();
			val[M03] = Translation.x;
			val[M13] = Translation.y;
			val[M23] = Translation.z;
			val[M00] = scaling.x;
			val[M11] = scaling.y;
			val[M22] = scaling.z;
			return this;
		}

		public Matrix4 SetToTranslationAndScaling(float TranslationX, float TranslationY, float TranslationZ,
				float scalingX, float scalingY, float scalingZ)
		{
			Idt();
			val[M03] = TranslationX;
			val[M13] = TranslationY;
			val[M23] = TranslationZ;
			val[M00] = scalingX;
			val[M11] = scalingY;
			val[M22] = scalingZ;
			return this;
		}

		static readonly Quaternion quat = new Quaternion();
		static readonly Quaternion quat2 = new Quaternion();

		public Matrix4 SetToRotation(Vector3f axis, float degrees)
		{
			if (degrees == 0)
			{
				Idt();
				return this;
			}
			return Set(quat.Set(axis, degrees));
		}

		public Matrix4 SetToRotationRad(Vector3f axis, float radians)
		{
			if (radians == 0)
			{
				Idt();
				return this;
			}
			return Set(quat.SetFromAxisRad(axis, radians));
		}

		public Matrix4 SetToRotation(float axisX, float axisY, float axisZ, float degrees)
		{
			if (degrees == 0)
			{
				Idt();
				return this;
			}
			return Set(quat.SetFromAxis(axisX, axisY, axisZ, degrees));
		}

		public Matrix4 SetToRotationRad(float axisX, float axisY, float axisZ, float radians)
		{
			if (radians == 0)
			{
				Idt();
				return this;
			}
			return Set(quat.SetFromAxisRad(axisX, axisY, axisZ, radians));
		}

		public Matrix4 SetToRotation( Vector3f v1,  Vector3f v2)
		{
			return Set(quat.SetFromCross(v1, v2));
		}

		public Matrix4 SetToRotation( float x1,  float y1,  float z1,  float x2,  float y2,
				 float z2)
		{
			return Set(quat.SetFromCross(x1, y1, z1, x2, y2, z2));
		}

		public Matrix4 SetFromEulerAngles(float yaw, float pitch, float roll)
		{
			quat.SetEulerAnglesSelf(yaw, pitch, roll);
			return Set(quat);
		}

		public Matrix4 SetToScaling(Vector3f vector)
		{
			Idt();
			val[M00] = vector.x;
			val[M11] = vector.y;
			val[M22] = vector.z;
			return this;
		}

		public Matrix4 SetToScaling(float x, float y, float z)
		{
			Idt();
			val[M00] = x;
			val[M11] = y;
			val[M22] = z;
			return this;
		}

		static readonly Vector3f l_vez = new Vector3f();
		static readonly Vector3f l_vex = new Vector3f();
		static readonly Vector3f l_vey = new Vector3f();

		public Matrix4 SetToLookAt(Vector3f direction, Vector3f up)
		{
			l_vez.Set(direction).NorSelf();
			l_vex.Set(direction).NorSelf();
			l_vex.CrsSelf(up).NorSelf();
			l_vey.Set(l_vex).CrsSelf(l_vez).NorSelf();
			Idt();
			val[M00] = l_vex.x;
			val[M01] = l_vex.y;
			val[M02] = l_vex.z;
			val[M10] = l_vey.x;
			val[M11] = l_vey.y;
			val[M12] = l_vey.z;
			val[M20] = -l_vez.x;
			val[M21] = -l_vez.y;
			val[M22] = -l_vez.z;

			return this;
		}

		static readonly  Vector3f tmpVec = new Vector3f();
		static readonly  Matrix4 tmpMat = new Matrix4();

		public Matrix4 SetToLookAt(Vector3f position, Vector3f tarGet, Vector3f up)
		{
			tmpVec.Set(tarGet).SubtractSelf(position);
			SetToLookAt(tmpVec, up);
			this.Mul(tmpMat.SetToTranslation(-position.x, -position.y, -position.z));

			return this;
		}

		static readonly  Vector3f right = new Vector3f();
		static readonly Vector3f tmpForward = new Vector3f();
		static readonly Vector3f tmpUp = new Vector3f();

		public Matrix4 SetToWorld(Vector3f position, Vector3f forward, Vector3f up)
		{
			tmpForward.Set(forward).NorSelf();
			right.Set(tmpForward).CrsSelf(up).NorSelf();
			tmpUp.Set(right).CrsSelf(tmpForward).NorSelf();

			this.Set(right, tmpUp, tmpForward.ScaleSelf(-1), position);
			return this;
		}

		public Matrix4 Lerp(Matrix4 matrix, float alpha)
		{
			for (int i = 0; i < 16; i++)
				this.val[i] = this.val[i] * (1 - alpha) + matrix.val[i] * alpha;
			return this;
		}

		public Matrix4 Avg(Matrix4 other, float w)
		{

			GetScale(tmpVec);
			other.GetScale(tmpForward);

			GetRotation(quat);
			other.GetRotation(quat2);

			GetTranslation(tmpUp);
			other.GetTranslation(right);

			SetToScaling(tmpVec.ScaleSelf(w).AddSelf(tmpForward.ScaleSelf(1 - w)));

			Rotate(quat.SlerpSelf(quat2, 1 - w));

			SetTranslation(tmpUp.ScaleSelf(w).AddSelf(right.ScaleSelf(1 - w)));

			return this;
		}

		public Matrix4 Avg(Matrix4[] t)
		{
			 float w = 1.0f / t.Length;

			tmpVec.Set(t[0].GetScale(tmpUp).ScaleSelf(w));

			quat.Set(t[0].GetRotation(quat2).ExpSelf(w));

			tmpForward.Set(t[0].GetTranslation(tmpUp).ScaleSelf(w));

			for (int i = 1; i < t.Length; i++)
			{

				tmpVec.AddSelf(t[i].GetScale(tmpUp).ScaleSelf(w));

				quat.MulSelf(t[i].GetRotation(quat2).ExpSelf(w));

				tmpForward.AddSelf(t[i].GetTranslation(tmpUp).ScaleSelf(w));
			}
			quat.NorSelf();

			SetToScaling(tmpVec);
			Rotate(quat);
			SetTranslation(tmpForward);

			return this;
		}

		public Matrix4 Avg(Matrix4[] t, float[] w)
		{

			tmpVec.Set(t[0].GetScale(tmpUp).ScaleSelf(w[0]));

			quat.Set(t[0].GetRotation(quat2).ExpSelf(w[0]));

			tmpForward.Set(t[0].GetTranslation(tmpUp).ScaleSelf(w[0]));

			for (int i = 1; i < t.Length; i++)
			{

				tmpVec.AddSelf(t[i].GetScale(tmpUp).ScaleSelf(w[i]));

				quat.MulSelf(t[i].GetRotation(quat2).ExpSelf(w[i]));

				tmpForward.AddSelf(t[i].GetTranslation(tmpUp).ScaleSelf(w[i]));
			}
			quat.NorSelf();

			SetToScaling(tmpVec);
			Rotate(quat);
			SetTranslation(tmpForward);

			return this;
		}

		public Matrix4 Set(Matrix3 mat)
		{
			val[0] = mat.val[0];
			val[1] = mat.val[1];
			val[2] = mat.val[2];
			val[3] = 0;
			val[4] = mat.val[3];
			val[5] = mat.val[4];
			val[6] = mat.val[5];
			val[7] = 0;
			val[8] = 0;
			val[9] = 0;
			val[10] = 1;
			val[11] = 0;
			val[12] = mat.val[6];
			val[13] = mat.val[7];
			val[14] = 0;
			val[15] = mat.val[8];
			return this;
		}

		public Matrix4 SetAsAffine(Matrix4 mat)
		{
			val[M00] = mat.val[M00];
			val[M10] = mat.val[M10];
			val[M01] = mat.val[M01];
			val[M11] = mat.val[M11];
			val[M03] = mat.val[M03];
			val[M13] = mat.val[M13];
			return this;
		}

		public Matrix4 ScaleSelf(Vector3f Scale)
		{
			val[M00] *= Scale.x;
			val[M11] *= Scale.y;
			val[M22] *= Scale.z;
			return this;
		}

		public Matrix4 ScaleSelf(float x, float y, float z)
		{
			val[M00] *= x;
			val[M11] *= y;
			val[M22] *= z;
			return this;
		}

		public Matrix4 ScaleSelf(float Scale)
		{
			val[M00] *= Scale;
			val[M11] *= Scale;
			val[M22] *= Scale;
			return this;
		}

		public Vector3f GetTranslation(Vector3f position)
		{
			position.x = val[M03];
			position.y = val[M13];
			position.z = val[M23];
			return position;
		}

		public Quaternion GetRotation(Quaternion rotation, bool normalizeAxes)
		{
			return rotation.SetFromMatrix(normalizeAxes, this);
		}

		public Quaternion GetRotation(Quaternion rotation)
		{
			return rotation.SetFromMatrix(this);
		}

		public float GetScaleXSquared()
		{
			return val[Matrix4.M00] * val[Matrix4.M00] + val[Matrix4.M01] * val[Matrix4.M01]
					+ val[Matrix4.M02] * val[Matrix4.M02];
		}

		public float GetScaleYSquared()
		{
			return val[Matrix4.M10] * val[Matrix4.M10] + val[Matrix4.M11] * val[Matrix4.M11]
					+ val[Matrix4.M12] * val[Matrix4.M12];
		}

		public float GetScaleZSquared()
		{
			return val[Matrix4.M20] * val[Matrix4.M20] + val[Matrix4.M21] * val[Matrix4.M21]
					+ val[Matrix4.M22] * val[Matrix4.M22];
		}

		public float GetScaleX()
		{
			return (MathUtils.IsZero(val[Matrix4.M01]) && MathUtils.IsZero(val[Matrix4.M02]))
					? MathUtils.Abs(val[Matrix4.M00]) : MathUtils.Sqrt(GetScaleXSquared());
		}

		public float GetScaleY()
		{
			return (MathUtils.IsZero(val[Matrix4.M10]) && MathUtils.IsZero(val[Matrix4.M12]))
					? MathUtils.Abs(val[Matrix4.M11]) : MathUtils.Sqrt(GetScaleYSquared());
		}

		public float GetScaleZ()
		{
			return (MathUtils.IsZero(val[Matrix4.M20]) && MathUtils.IsZero(val[Matrix4.M21]))
					? MathUtils.Abs(val[Matrix4.M22]) : MathUtils.Sqrt(GetScaleZSquared());
		}

		public Vector3f GetScale(Vector3f Scale)
		{
			return Scale.Set(GetScaleX(), GetScaleY(), GetScaleZ());
		}

		public Matrix4 ToNormalMatrix()
		{
			val[M03] = 0;
			val[M13] = 0;
			val[M23] = 0;
			return Inv().Tra();
		}

		public Matrix4 Translate(Vector3f Translation)
		{
			return Translate(Translation.x, Translation.y, Translation.z);
		}

		public Matrix4 Translate(float x, float y, float z)
		{
			tmp[M00] = 1;
			tmp[M01] = 0;
			tmp[M02] = 0;
			tmp[M03] = x;
			tmp[M10] = 0;
			tmp[M11] = 1;
			tmp[M12] = 0;
			tmp[M13] = y;
			tmp[M20] = 0;
			tmp[M21] = 0;
			tmp[M22] = 1;
			tmp[M23] = z;
			tmp[M30] = 0;
			tmp[M31] = 0;
			tmp[M32] = 0;
			tmp[M33] = 1;

			//support.Mul(val, tmp);
			return this;
		}

		public Matrix4 Rotate(Vector3f axis, float degrees)
		{
			if (degrees == 0)
				return this;
			quat.Set(axis, degrees);
			return Rotate(quat);
		}

		public Matrix4 RotateRad(Vector3f axis, float radians)
		{
			if (radians == 0)
				return this;
			quat.SetFromAxisRad(axis, radians);
			return Rotate(quat);
		}

		public Matrix4 Rotate(float axisX, float axisY, float axisZ, float degrees)
		{
			if (degrees == 0)
				return this;
			quat.SetFromAxis(axisX, axisY, axisZ, degrees);
			return Rotate(quat);
		}

		public Matrix4 RotateRad(float axisX, float axisY, float axisZ, float radians)
		{
			if (radians == 0)
				return this;
			quat.SetFromAxisRad(axisX, axisY, axisZ, radians);
			return Rotate(quat);
		}

		public Matrix4 Rotate(Quaternion rotation)
		{
			rotation.ToMatrix(tmp);
			//support.Mul(val, tmp);
			return this;
		}

		public Matrix4 Rotate( Vector3f v1,  Vector3f v2)
		{
			return Rotate(quat.SetFromCross(v1, v2));
		}

		public Matrix4 Scale(float ScaleX, float ScaleY, float ScaleZ)
		{
			tmp[M00] = ScaleX;
			tmp[M01] = 0;
			tmp[M02] = 0;
			tmp[M03] = 0;
			tmp[M10] = 0;
			tmp[M11] = ScaleY;
			tmp[M12] = 0;
			tmp[M13] = 0;
			tmp[M20] = 0;
			tmp[M21] = 0;
			tmp[M22] = ScaleZ;
			tmp[M23] = 0;
			tmp[M30] = 0;
			tmp[M31] = 0;
			tmp[M32] = 0;
			tmp[M33] = 1;

			//support.Mul(val, tmp);
			return this;
		}

		public void ExTract4x3Matrix(float[] dst)
		{
			dst[0] = val[M00];
			dst[1] = val[M10];
			dst[2] = val[M20];
			dst[3] = val[M01];
			dst[4] = val[M11];
			dst[5] = val[M21];
			dst[6] = val[M02];
			dst[7] = val[M12];
			dst[8] = val[M22];
			dst[9] = val[M03];
			dst[10] = val[M13];
			dst[11] = val[M23];
		}

		public Matrix4 SetAsAffine(Affine2f affine)
		{
			val[M00] = affine.m00;
			val[M10] = affine.m10;
			val[M01] = affine.m01;
			val[M11] = affine.m11;
			val[M03] = affine.tx;
			val[M13] = affine.ty;
			return this;
		}

		public Matrix4 Set(Affine2f affine)
		{
			val[M00] = affine.m00;
			val[M10] = affine.m10;
			val[M20] = 0;
			val[M30] = 0;
			val[M01] = affine.m01;
			val[M11] = affine.m11;
			val[M21] = 0;
			val[M31] = 0;
			val[M02] = 0;
			val[M12] = 0;
			val[M22] = 1;
			val[M32] = 0;
			val[M03] = affine.tx;
			val[M13] = affine.ty;
			val[M23] = 0;
			val[M33] = 1;
			return this;
		}

		public Matrix4 NewCombine(Affine2f affine)
		{

			float m00 = affine.m00;
			float m10 = affine.m10;
			float m20 = 0;
			float m30 = 0;
			float m01 = affine.m01;
			float m11 = affine.m11;
			float m21 = 0;
			float m31 = 0;
			float m02 = 0;
			float m12 = 0;
			float m22 = 1;
			float m32 = 0;
			float m03 = affine.tx;
			float m13 = affine.ty;
			float m23 = 0;
			float m33 = 1;

			float nm00 = val[M00] * m00 + val[M01] * m10 + val[M02] * m20 + val[M03] * m30;
			float nm01 = val[M00] * m01 + val[M01] * m11 + val[M02] * m21 + val[M03] * m31;
			float nm02 = val[M00] * m02 + val[M01] * m12 + val[M02] * m22 + val[M03] * m32;
			float nm03 = val[M00] * m03 + val[M01] * m13 + val[M02] * m23 + val[M03] * m33;
			float nm10 = val[M10] * m00 + val[M11] * m10 + val[M12] * m20 + val[M13] * m30;
			float nm11 = val[M10] * m01 + val[M11] * m11 + val[M12] * m21 + val[M13] * m31;
			float nm12 = val[M10] * m02 + val[M11] * m12 + val[M12] * m22 + val[M13] * m32;
			float nm13 = val[M10] * m03 + val[M11] * m13 + val[M12] * m23 + val[M13] * m33;
			float nm20 = val[M20] * m00 + val[M21] * m10 + val[M22] * m20 + val[M23] * m30;
			float nm21 = val[M20] * m01 + val[M21] * m11 + val[M22] * m21 + val[M23] * m31;
			float nm22 = val[M20] * m02 + val[M21] * m12 + val[M22] * m22 + val[M23] * m32;
			float nm23 = val[M20] * m03 + val[M21] * m13 + val[M22] * m23 + val[M23] * m33;
			float nm30 = val[M30] * m00 + val[M31] * m10 + val[M32] * m20 + val[M33] * m30;
			float nm31 = val[M30] * m01 + val[M31] * m11 + val[M32] * m21 + val[M33] * m31;
			float nm32 = val[M30] * m02 + val[M31] * m12 + val[M32] * m22 + val[M33] * m32;
			float nm33 = val[M30] * m03 + val[M31] * m13 + val[M32] * m23 + val[M33] * m33;

			Matrix4 m = new Matrix4();

			m.val[M00] = nm00;
			m.val[M10] = nm10;
			m.val[M20] = nm20;
			m.val[M30] = nm30;
			m.val[M01] = nm01;
			m.val[M11] = nm11;
			m.val[M21] = nm21;
			m.val[M31] = nm31;
			m.val[M02] = nm02;
			m.val[M12] = nm12;
			m.val[M22] = nm22;
			m.val[M32] = nm32;
			m.val[M03] = nm03;
			m.val[M13] = nm13;
			m.val[M23] = nm23;
			m.val[M33] = nm33;

			return m;
		}

		public bool EqualsAffine(Affine2f affine)
		{
			int count = 0;
			if (affine.m00 == val[M00])
			{
				count++;
			}
			if (affine.m01 == val[M01])
			{
				count++;
			}
			if (affine.m10 == val[M10])
			{
				count++;
			}
			if (affine.m11 == val[M11])
			{
				count++;
			}
			if (affine.tx == val[M03])
			{
				count++;
			}
			if (affine.ty == val[M13])
			{
				count++;
			}
			return count == 6;
		}

		public Matrix4 ThisCombine(Affine2f affine)
		{

			 float m00 = affine.m00;
			 float m10 = affine.m10;
			 float m20 = 0;
			 float m30 = 0;
			 float m01 = affine.m01;
			 float m11 = affine.m11;
			 float m21 = 0;
			 float m31 = 0;
			 float m02 = 0;
			 float m12 = 0;
			 float m22 = 1;
			 float m32 = 0;
			 float m03 = affine.tx;
			 float m13 = affine.ty;
			 float m23 = 0;
			 float m33 = 1;

			 float nm00 = val[M00] * m00 + val[M01] * m10 + val[M02] * m20 + val[M03] * m30;
			 float nm01 = val[M00] * m01 + val[M01] * m11 + val[M02] * m21 + val[M03] * m31;
			 float nm02 = val[M00] * m02 + val[M01] * m12 + val[M02] * m22 + val[M03] * m32;
			 float nm03 = val[M00] * m03 + val[M01] * m13 + val[M02] * m23 + val[M03] * m33;
			 float nm10 = val[M10] * m00 + val[M11] * m10 + val[M12] * m20 + val[M13] * m30;
			 float nm11 = val[M10] * m01 + val[M11] * m11 + val[M12] * m21 + val[M13] * m31;
			 float nm12 = val[M10] * m02 + val[M11] * m12 + val[M12] * m22 + val[M13] * m32;
			 float nm13 = val[M10] * m03 + val[M11] * m13 + val[M12] * m23 + val[M13] * m33;
			 float nm20 = val[M20] * m00 + val[M21] * m10 + val[M22] * m20 + val[M23] * m30;
			 float nm21 = val[M20] * m01 + val[M21] * m11 + val[M22] * m21 + val[M23] * m31;
			 float nm22 = val[M20] * m02 + val[M21] * m12 + val[M22] * m22 + val[M23] * m32;
			 float nm23 = val[M20] * m03 + val[M21] * m13 + val[M22] * m23 + val[M23] * m33;
			 float nm30 = val[M30] * m00 + val[M31] * m10 + val[M32] * m20 + val[M33] * m30;
			 float nm31 = val[M30] * m01 + val[M31] * m11 + val[M32] * m21 + val[M33] * m31;
			 float nm32 = val[M30] * m02 + val[M31] * m12 + val[M32] * m22 + val[M33] * m32;
			 float nm33 = val[M30] * m03 + val[M31] * m13 + val[M32] * m23 + val[M33] * m33;

			this.val[M00] = nm00;
			this.val[M10] = nm10;
			this.val[M20] = nm20;
			this.val[M30] = nm30;
			this.val[M01] = nm01;
			this.val[M11] = nm11;
			this.val[M21] = nm21;
			this.val[M31] = nm31;
			this.val[M02] = nm02;
			this.val[M12] = nm12;
			this.val[M22] = nm22;
			this.val[M32] = nm32;
			this.val[M03] = nm03;
			this.val[M13] = nm13;
			this.val[M23] = nm23;
			this.val[M33] = nm33;

			return this;
		}

		public static Matrix4 NewCombine(Matrix4 m1, Matrix4 m2)
		{
			float m00 = m1.val[M00] * m2.val[M00] + m1.val[M01] * m2.val[M10] + m1.val[M02] * m2.val[M20]
					+ m1.val[M03] * m2.val[M30];
			float m01 = m1.val[M00] * m2.val[M01] + m1.val[M01] * m2.val[M11] + m1.val[M02] * m2.val[M21]
					+ m1.val[M03] * m2.val[M31];
			float m02 = m1.val[M00] * m2.val[M02] + m1.val[M01] * m2.val[M12] + m1.val[M02] * m2.val[M22]
					+ m1.val[M03] * m2.val[M32];
			float m03 = m1.val[M00] * m2.val[M03] + m1.val[M01] * m2.val[M13] + m1.val[M02] * m2.val[M23]
					+ m1.val[M03] * m2.val[M33];
			float m10 = m1.val[M10] * m2.val[M00] + m1.val[M11] * m2.val[M10] + m1.val[M12] * m2.val[M20]
					+ m1.val[M13] * m2.val[M30];
			float m11 = m1.val[M10] * m2.val[M01] + m1.val[M11] * m2.val[M11] + m1.val[M12] * m2.val[M21]
					+ m1.val[M13] * m2.val[M31];
			float m12 = m1.val[M10] * m2.val[M02] + m1.val[M11] * m2.val[M12] + m1.val[M12] * m2.val[M22]
					+ m1.val[M13] * m2.val[M32];
			float m13 = m1.val[M10] * m2.val[M03] + m1.val[M11] * m2.val[M13] + m1.val[M12] * m2.val[M23]
					+ m1.val[M13] * m2.val[M33];
			float m20 = m1.val[M20] * m2.val[M00] + m1.val[M21] * m2.val[M10] + m1.val[M22] * m2.val[M20]
					+ m1.val[M23] * m2.val[M30];
			float m21 = m1.val[M20] * m2.val[M01] + m1.val[M21] * m2.val[M11] + m1.val[M22] * m2.val[M21]
					+ m1.val[M23] * m2.val[M31];
			float m22 = m1.val[M20] * m2.val[M02] + m1.val[M21] * m2.val[M12] + m1.val[M22] * m2.val[M22]
					+ m1.val[M23] * m2.val[M32];
			float m23 = m1.val[M20] * m2.val[M03] + m1.val[M21] * m2.val[M13] + m1.val[M22] * m2.val[M23]
					+ m1.val[M23] * m2.val[M33];
			float m30 = m1.val[M30] * m2.val[M00] + m1.val[M31] * m2.val[M10] + m1.val[M32] * m2.val[M20]
					+ m1.val[M33] * m2.val[M30];
			float m31 = m1.val[M30] * m2.val[M01] + m1.val[M31] * m2.val[M11] + m1.val[M32] * m2.val[M21]
					+ m1.val[M33] * m2.val[M31];
			float m32 = m1.val[M30] * m2.val[M02] + m1.val[M31] * m2.val[M12] + m1.val[M32] * m2.val[M22]
					+ m1.val[M33] * m2.val[M32];
			float m33 = m1.val[M30] * m2.val[M03] + m1.val[M31] * m2.val[M13] + m1.val[M32] * m2.val[M23]
					+ m1.val[M33] * m2.val[M33];

			Matrix4 m = new Matrix4();

			m.val[M00] = m00;
			m.val[M10] = m10;
			m.val[M20] = m20;
			m.val[M30] = m30;
			m.val[M01] = m01;
			m.val[M11] = m11;
			m.val[M21] = m21;
			m.val[M31] = m31;
			m.val[M02] = m02;
			m.val[M12] = m12;
			m.val[M22] = m22;
			m.val[M32] = m32;
			m.val[M03] = m03;
			m.val[M13] = m13;
			m.val[M23] = m23;
			m.val[M33] = m33;

			return m;
		}


	public override bool Equals(object o)
		{
			if (!(o is Matrix3) || o == null) {
				return false;
			}
			if (this == o)
			{
				return true;
			}
			Matrix3 comp = (Matrix3)o;
			for (int i = 0; i < 16; i++)
			{
				if (NumberUtils.Compare(this.val[i], comp.val[i]) != 0)
				{
					return false;
				}
			}
			return true;
		}

	public float GetX()
		{
			return val[M13];
		}

	public float GetY()
		{
			return val[M03];
		}

	public override int GetHashCode()
		{
			uint result = 17;
			for (int j = 0; j < 16; j++)
			{
				 uint val = NumberUtils.FloatToIntBits(this.val[j]);
				result += 31 * result + (uint)(val ^ (val >> 32));
			}
			return (int)result;
		}

		
	public override string ToString()
		{
			StringKeyValue builder = new StringKeyValue("Matrix4");
			builder.NewLine().AddValue("[{0},{1},{2},{3}]").NewLine().AddValue("[{4},{5}.{6},{7}]").NewLine()
					.AddValue("[{8},{9},{10},{11}]").NewLine().AddValue("[{12},{13},{14},{15}]").NewLine();
			return StringUtils.Format(builder.ToString(), val[M00], val[M01], val[M02], val[M03], val[M10], val[M11],
					val[M12], val[M13], val[M20], val[M21], val[M22], val[M23], val[M30], val[M31], val[M32], val[M33]);
		}

	}
}

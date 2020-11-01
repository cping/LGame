using loon.utils;
using System;
using System.Collections.Generic;
using System.Text;

namespace loon.geom
{

	public class Quaternion : XY {

	public static Quaternion CreateFromAxisAngle(Vector3f axis, float angle)
	{
		float half = angle * 0.5f;
		float Sin = MathUtils.Sin(half);
		float Cos = MathUtils.Cos(half);
		return new Quaternion(axis.x * Sin, axis.y * Sin, axis.z * Sin, Cos);
	}

	private static readonly Quaternion tmp1 = new Quaternion(0, 0, 0, 0);
	private static readonly Quaternion tmp2 = new Quaternion(0, 0, 0, 0);

	private static readonly Array<Quaternion> _quan_cache = new Array<Quaternion>();

	public  static Quaternion TMP()
	{
		Quaternion temp = _quan_cache.Pop();
		if (temp == null)
		{
			_quan_cache.Add(temp = new Quaternion(0, 0, 0, 0));
		}
		return temp;
	}

	public  static Quaternion ZERO()
	{
		return new Quaternion(0, 0, 0, 0);
	}

	public float x;
	public float y;
	public float z;
	public float w;

	public Quaternion(float x, float y, float z, float w)
	{
		this.Set(x, y, z, w);
	}

	public Quaternion()
	{
		Idt();
	}

	public Quaternion(Quaternion quaternion)
	{
		this.Set(quaternion);
	}

	public Quaternion(Vector3f axis, float angle)
	{
		this.Set(axis, angle);
	}

	public Quaternion(float pitch, float yaw, float roll)
	{
		Set(pitch, yaw, roll);
	}

	public Quaternion Set(float x, float y, float z, float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	public Quaternion Set(Quaternion quaternion)
	{
		return this.Set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
	}

	public Quaternion Set(Vector3f axis, float angle)
	{
		return SetFromAxis(axis.x, axis.y, axis.z, angle);
	}

	public Quaternion Cpy()
	{
		return new Quaternion(this);
	}

	public  static float Len( float x,  float y,  float z,  float w)
	{
		return MathUtils.Sqrt(x * x + y * y + z * z + w * w);
	}

	public float Len()
	{
		return MathUtils.Sqrt(x * x + y * y + z * z + w * w);
	}

	public Quaternion SetEulerAnglesSelf(float yaw, float pitch, float roll)
	{
		return SetEulerAnglesRadSelf(yaw * MathUtils.DEG_TO_RAD, pitch * MathUtils.DEG_TO_RAD,
				roll * MathUtils.DEG_TO_RAD);
	}

	public Quaternion SetEulerAnglesRadSelf(float yaw, float pitch, float roll)
	{
		 float hr = roll * 0.5f;
		 float shr = MathUtils.Sin(hr);
		 float chr = MathUtils.Cos(hr);
		 float hp = pitch * 0.5f;
		 float shp = MathUtils.Sin(hp);
		 float chp = MathUtils.Cos(hp);
		 float hy = yaw * 0.5f;
		 float shy = MathUtils.Sin(hy);
		 float chy = MathUtils.Cos(hy);
		 float chy_shp = chy * shp;
		 float shy_chp = shy * chp;
		 float chy_chp = chy * chp;
		 float shy_shp = shy * shp;

		x = (chy_shp * chr) + (shy_chp * shr);
		y = (shy_chp * chr) - (chy_shp * shr);
		z = (chy_chp * shr) - (shy_shp * chr);
		w = (chy_chp * chr) + (shy_shp * shr);
		return this;
	}

	public int GetGimbalPole()
	{
		 float t = y * x + z * w;
		return t > 0.499f ? 1 : (t < -0.499f ? -1 : 0);
	}

	public float GetRollRad()
	{
		 int pole = GetGimbalPole();
		return pole == 0 ? MathUtils.Atan2(2f * (w * z + y * x), 1f - 2f * (x * x + z * z))
				: (float)pole * 2f * MathUtils.Atan2(y, w);
	}

	public float GetRoll()
	{
		return GetRollRad() * MathUtils.RAD_TO_DEG;
	}

	public float GetPitchRad()
	{
		 int pole = GetGimbalPole();
		return pole == 0 ? MathUtils.Asin(MathUtils.Clamp(2f * (w * x - z * y), -1f, 1f))
				: (float)pole * MathUtils.PI * 0.5f;
	}

	public float GetPitch()
	{
		return GetPitchRad() * MathUtils.RAD_TO_DEG;
	}

	public float GetYawRad()
	{
		return GetGimbalPole() == 0 ? MathUtils.Atan2(2f * (y * w + x * z), 1f - 2f * (y * y + x * x)) : 0f;
	}

	public float GetYaw()
	{
		return GetYawRad() * MathUtils.RAD_TO_DEG;
	}

	public  static float Len2( float x,  float y,  float z,  float w)
	{
		return x * x + y * y + z * z + w * w;
	}

	public float Len2()
	{
		return x * x + y * y + z * z + w * w;
	}

	public Quaternion NorSelf()
	{
		float len = Len2();
		if (len != 0 && !MathUtils.IsEqual(len, 1f))
		{
			len = MathUtils.Sqrt(len);
			w /= len;
			x /= len;
			y /= len;
			z /= len;
		}
		return this;
	}

	public Quaternion ConjugateSelf()
	{
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	public Vector3f TransformSelf(Vector3f v)
	{
		tmp2.Set(this);
		tmp2.ConjugateSelf();
		tmp2.MulLeftSelf(tmp1.Set(v.x, v.y, v.z, 0)).MulLeftSelf(this);

		v.x = tmp2.x;
		v.y = tmp2.y;
		v.z = tmp2.z;
		return v;
	}

	public Quaternion MulSelf( Quaternion other)
	{
		 float newX = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
		 float newY = this.w * other.y + this.y * other.w + this.z * other.x - this.x * other.z;
		 float newZ = this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x;
		 float newW = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		this.w = newW;
		return this;
	}

	public Quaternion MulSelf( float x,  float y,  float z,  float w)
	{
		 float newX = this.w * x + this.x * w + this.y * z - this.z * y;
		 float newY = this.w * y + this.y * w + this.z * x - this.x * z;
		 float newZ = this.w * z + this.z * w + this.x * y - this.y * x;
		 float newW = this.w * w - this.x * x - this.y * y - this.z * z;
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		this.w = newW;
		return this;
	}

	public Quaternion MulLeftSelf(Quaternion other)
	{
		 float newX = other.w * this.x + other.x * this.w + other.y * this.z - other.z * y;
		 float newY = other.w * this.y + other.y * this.w + other.z * this.x - other.x * z;
		 float newZ = other.w * this.z + other.z * this.w + other.x * this.y - other.y * x;
		 float newW = other.w * this.w - other.x * this.x - other.y * this.y - other.z * z;
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		this.w = newW;
		return this;
	}

	public Quaternion MulLeftSelf( float x,  float y,  float z,  float w)
	{
		 float newX = w * this.x + x * this.w + y * this.z - z * y;
		 float newY = w * this.y + y * this.w + z * this.x - x * z;
		 float newZ = w * this.z + z * this.w + x * this.y - y * x;
		 float newW = w * this.w - x * this.x - y * this.y - z * z;
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		this.w = newW;
		return this;
	}

	public Quaternion AddSelf(Quaternion quaternion)
	{
		this.x += quaternion.x;
		this.y += quaternion.y;
		this.z += quaternion.z;
		this.w += quaternion.w;
		return this;
	}

	public Quaternion AddSelf(float qx, float qy, float qz, float qw)
	{
		this.x += qx;
		this.y += qy;
		this.z += qz;
		this.w += qw;
		return this;
	}

	public void ToMatrix( float[] matrix)
	{
		 float xx = x * x;
		 float xy = x * y;
		 float xz = x * z;
		 float xw = x * w;
		 float yy = y * y;
		 float yz = y * z;
		 float yw = y * w;
		 float zz = z * z;
		 float zw = z * w;

		matrix[Matrix4.M00] = 1 - 2 * (yy + zz);
		matrix[Matrix4.M01] = 2 * (xy - zw);
		matrix[Matrix4.M02] = 2 * (xz + yw);
		matrix[Matrix4.M03] = 0;
		matrix[Matrix4.M10] = 2 * (xy + zw);
		matrix[Matrix4.M11] = 1 - 2 * (xx + zz);
		matrix[Matrix4.M12] = 2 * (yz - xw);
		matrix[Matrix4.M13] = 0;
		matrix[Matrix4.M20] = 2 * (xz - yw);
		matrix[Matrix4.M21] = 2 * (yz + xw);
		matrix[Matrix4.M22] = 1 - 2 * (xx + yy);
		matrix[Matrix4.M23] = 0;
		matrix[Matrix4.M30] = 0;
		matrix[Matrix4.M31] = 0;
		matrix[Matrix4.M32] = 0;
		matrix[Matrix4.M33] = 1;
	}

	public Quaternion Idt()
	{
		return this.Set(0, 0, 0, 1);
	}

	public bool IsIdentity()
	{
		return MathUtils.IsZero(x) && MathUtils.IsZero(y) && MathUtils.IsZero(z) && MathUtils.IsEqual(w, 1f);
	}

	public bool IsIdentity( float tolerance)
	{
		return MathUtils.IsZero(x, tolerance) && MathUtils.IsZero(y, tolerance) && MathUtils.IsZero(z, tolerance)
				&& MathUtils.IsEqual(w, 1f, tolerance);
	}

	public Quaternion SetFromAxis( Vector3f axis,  float degrees)
	{
		return SetFromAxis(axis.x, axis.y, axis.z, degrees);
	}

	public Quaternion SetFromAxisRad( Vector3f axis,  float radians)
	{
		return SetFromAxisRad(axis.x, axis.y, axis.z, radians);
	}

	public Quaternion SetFromAxis( float x,  float y,  float z,  float degrees)
	{
		return SetFromAxisRad(x, y, z, degrees * MathUtils.DEG_TO_RAD);
	}

	public Quaternion SetFromAxisRad( float x,  float y,  float z,  float radians)
	{
		float d = Vector3f.Len(x, y, z);
		if (d == 0f)
			return Idt();
		d = 1f / d;
		float l_ang = radians < 0 ? MathUtils.TWO_PI - (-radians % MathUtils.TWO_PI) : radians % MathUtils.TWO_PI;
		float l_sin = MathUtils.Sin(l_ang / 2);
		float l_cos = MathUtils.Cos(l_ang / 2);
		return this.Set(d * x * l_sin, d * y * l_sin, d * z * l_sin, l_cos).NorSelf();
	}

	public Quaternion SetFromMatrix(bool NormalizeAxes, Matrix4 matrix)
	{
		return SetFromAxes(NormalizeAxes, matrix.val[Matrix4.M00], matrix.val[Matrix4.M01], matrix.val[Matrix4.M02],
				matrix.val[Matrix4.M10], matrix.val[Matrix4.M11], matrix.val[Matrix4.M12], matrix.val[Matrix4.M20],
				matrix.val[Matrix4.M21], matrix.val[Matrix4.M22]);
	}

	public Quaternion SetFromMatrix(Matrix4 matrix)
	{
		return SetFromMatrix(false, matrix);
	}

	public Quaternion SetFromMatrix(bool NormalizeAxes, Matrix3 matrix)
	{
		return SetFromAxes(NormalizeAxes, matrix.val[Matrix3.M00], matrix.val[Matrix3.M01], matrix.val[Matrix3.M02],
				matrix.val[Matrix3.M10], matrix.val[Matrix3.M11], matrix.val[Matrix3.M12], matrix.val[Matrix3.M20],
				matrix.val[Matrix3.M21], matrix.val[Matrix3.M22]);
	}

	public Quaternion SetFromMatrix(Matrix3 matrix)
	{
		return SetFromMatrix(false, matrix);
	}

	public Quaternion SetFromAxes(float xx, float xy, float xz, float yx, float yy, float yz, float zx, float zy,
			float zz)
	{
		return SetFromAxes(false, xx, xy, xz, yx, yy, yz, zx, zy, zz);
	}

	public Quaternion SetFromAxes(bool NormalizeAxes, float xx, float xy, float xz, float yx, float yy, float yz,
			float zx, float zy, float zz)
	{
		if (NormalizeAxes)
		{
			 float lx = 1f / Vector3f.Len(xx, xy, xz);
			 float ly = 1f / Vector3f.Len(yx, yy, yz);
			 float lz = 1f / Vector3f.Len(zx, zy, zz);
			xx *= lx;
			xy *= lx;
			xz *= lx;
			yz *= ly;
			yy *= ly;
			yz *= ly;
			zx *= lz;
			zy *= lz;
			zz *= lz;
		}

		 float t = xx + yy + zz;

		if (t >= 0)
		{
			float s = MathUtils.Sqrt(t + 1);
			w = 0.5f * s;
			s = 0.5f / s;
			x = (zy - yz) * s;
			y = (xz - zx) * s;
			z = (yx - xy) * s;
		}
		else if ((xx > yy) && (xx > zz))
		{
			float s = MathUtils.Sqrt(1f + xx - yy - zz);
			x = s * 0.5f;
			s = 0.5f / s;
			y = (yx + xy) * s;
			z = (xz + zx) * s;
			w = (zy - yz) * s;
		}
		else if (yy > zz)
		{
			float s = MathUtils.Sqrt(1f + yy - xx - zz);
			y = s * 0.5f;
			s = 0.5f / s;
			x = (yx + xy) * s;
			z = (zy + yz) * s;
			w = (xz - zx) * s;
		}
		else
		{
			float s = MathUtils.Sqrt(1f + zz - xx - yy);
			z = s * 0.5f;
			s = 0.5f / s;
			x = (xz + zx) * s;
			y = (zy + yz) * s;
			w = (yx - xy) * s;
		}

		return this;
	}

	public Quaternion SetFromCross( Vector3f v1,  Vector3f v2)
	{
		 float Dot = MathUtils.Clamp(v1.Dot(v2), -1f, 1f);
		 float angle = MathUtils.Acos(Dot);
		return SetFromAxisRad(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x, angle);
	}

	public Quaternion SetFromCross( float x1,  float y1,  float z1,  float x2,  float y2,
			 float z2)
	{
		 float Dot = MathUtils.Clamp(Vector3f.Dot(x1, y1, z1, x2, y2, z2), -1f, 1f);
		 float angle = MathUtils.Acos(Dot);
		return SetFromAxisRad(y1 * z2 - z1 * y2, z1 * x2 - x1 * z2, x1 * y2 - y1 * x2, angle);
	}

	public Quaternion SlerpSelf(Quaternion end, float alpha)
	{
		 float dot = Dot(end);
		float absDot = dot < 0 ? -dot : dot;
		float scale0 = 1 - alpha;
		float scale1 = alpha;
		if ((1 - absDot) > 0.1)
		{
			 float angle = MathUtils.Acos(absDot);
			 float invSinTheta = 1f / MathUtils.Sin(angle);

			scale0 = (MathUtils.Sin((1 - alpha) * angle) * invSinTheta);
			scale1 = (MathUtils.Sin((alpha * angle)) * invSinTheta);
		}

		if (dot < 0)
		{
			scale1 = -scale1;
		}

		x = (scale0 * x) + (scale1 * end.x);
		y = (scale0 * y) + (scale1 * end.y);
		z = (scale0 * z) + (scale1 * end.z);
		w = (scale0 * w) + (scale1 * end.w);

		return this;
	}

	public Quaternion SlerpSelf(Quaternion[] q)
	{

		 float w = 1.0f / q.Length;
		Set(q[0]).ExpSelf(w);
		for (int i = 1; i < q.Length; i++)
		{
			MulSelf(tmp1.Set(q[i]).ExpSelf(w));
		}
		NorSelf();
		return this;
	}

	public Quaternion SlerpSelf(Quaternion[] q, float[] w)
	{

		Set(q[0]).ExpSelf(w[0]);
		for (int i = 1; i < q.Length; i++)
		{
			MulSelf(tmp1.Set(q[i]).ExpSelf(w[i]));
		}
		NorSelf();
		return this;
	}

	public Quaternion ExpSelf(float alpha)
	{

		float norm = Len();
		float normExp = MathUtils.Pow(norm, alpha);

		float theta = MathUtils.Acos(w / norm);

		float coeff;
		if (MathUtils.Abs(theta) < 0.001)
		{
			coeff = normExp * alpha / norm;
		}
		else
		{
			coeff = (float)(normExp * MathUtils.Sin(alpha * theta) / (norm * MathUtils.Sin(theta)));
		}

		w = (float)(normExp * MathUtils.Cos(alpha * theta));
		x *= coeff;
		y *= coeff;
		z *= coeff;

		NorSelf();

		return this;
	}

	public override int GetHashCode()
	{
		 uint prime = 31;
		uint result = 1;
		result = prime * result + NumberUtils.FloatToRawIntBits(w);
		result = prime * result + NumberUtils.FloatToRawIntBits(x);
		result = prime * result + NumberUtils.FloatToRawIntBits(y);
		result = prime * result + NumberUtils.FloatToRawIntBits(z);
		return (int)result;
	}

	
	public override bool Equals(object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj is Quaternion)) {
			return false;
		}
		Quaternion other = (Quaternion)obj;
		return (NumberUtils.FloatToRawIntBits(w) == NumberUtils.FloatToRawIntBits(other.w))
				&& (NumberUtils.FloatToRawIntBits(x) == NumberUtils.FloatToRawIntBits(other.x))
				&& (NumberUtils.FloatToRawIntBits(y) == NumberUtils.FloatToRawIntBits(other.y))
				&& (NumberUtils.FloatToRawIntBits(z) == NumberUtils.FloatToRawIntBits(other.z));
	}

	public  static float Dot( float x1,  float y1,  float z1,  float w1,  float x2,
			 float y2,  float z2,  float w2)
	{
		return x1 * x2 + y1 * y2 + z1 * z2 + w1 * w2;
	}

	public float Dot( Quaternion other)
	{
		return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
	}

	public float Dot( float x,  float y,  float z,  float w)
	{
		return this.x * x + this.y * y + this.z * z + this.w * w;
	}

	public Quaternion MulSelf(float scalar)
	{
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
		this.w *= scalar;
		return this;
	}

	public float GetAxisAngle(Vector3f axis)
	{
		return GetAxisAngleRad(axis) * MathUtils.RAD_TO_DEG;
	}

	public float GetAxisAngleRad(Vector3f axis)
	{
		if (this.w > 1)
		{
			this.NorSelf();
		}
		float angle = (float)(2.0 * MathUtils.Acos(this.w));
		double s = MathUtils.Sqrt(1 - this.w * this.w);
		if (s < MathUtils.FLOAT_ROUNDING_ERROR)
		{
			axis.x = this.x;
			axis.y = this.y;
			axis.z = this.z;
		}
		else
		{
			axis.x = (float)(this.x / s);
			axis.y = (float)(this.y / s);
			axis.z = (float)(this.z / s);
		}

		return angle;
	}

	public float GetAngleRad()
	{
		return (float)(2.0 * MathUtils.Acos((this.w > 1) ? (this.w / Len()) : this.w));
	}

	public float GetAngle()
	{
		return GetAngleRad() * MathUtils.RAD_TO_DEG;
	}

	public void GetSwingTwistSelf( float axisX,  float axisY,  float axisZ,  Quaternion swing,
			 Quaternion twist)
	{
		 float d = Vector3f.Dot(this.x, this.y, this.z, axisX, axisY, axisZ);
		twist.Set(axisX * d, axisY * d, axisZ * d, this.w).NorSelf();
		swing.Set(twist).ConjugateSelf().MulLeftSelf(this);
	}

	public void GetSwingTwistSelf( Vector3f axis,  Quaternion swing,  Quaternion twist)
	{
		GetSwingTwistSelf(axis.x, axis.y, axis.z, swing, twist);
	}

	public float GetAngleAroundRad( float axisX,  float axisY,  float axisZ)
	{
		 float d = Vector3f.Dot(this.x, this.y, this.z, axisX, axisY, axisZ);
		 float l2 = Quaternion.Len2(axisX * d, axisY * d, axisZ * d, this.w);
		return MathUtils.IsZero(l2) ? 0f
				: (float)(2.0 * MathUtils.Acos(MathUtils.Clamp((float)(this.w / MathUtils.Sqrt(l2)), -1f, 1f)));
	}

	public float GetAngleAroundRad( Vector3f axis)
	{
		return GetAngleAroundRad(axis.x, axis.y, axis.z);
	}

	public float GetAngleAround( float axisX,  float axisY,  float axisZ)
	{
		return GetAngleAroundRad(axisX, axisY, axisZ) * MathUtils.RAD_TO_DEG;
	}

	public float GetAngleAround( Vector3f axis)
	{
		return GetAngleAround(axis.x, axis.y, axis.z);
	}

	public Quaternion Set(float pitch, float yaw, float roll)
	{
		pitch = MathUtils.ToRadians(pitch) * 0.5f;
		yaw = MathUtils.ToRadians(yaw) * 0.5f;
		roll = MathUtils.ToRadians(roll) * 0.5f;

		float sinP = MathUtils.Sin(pitch);
		float sinY = MathUtils.Sin(yaw);
		float sinR = MathUtils.Sin(roll);
		float cosP = MathUtils.Cos(pitch);
		float cosY = MathUtils.Cos(yaw);
		float cosR = MathUtils.Cos(roll);

		x = sinP * cosY * cosR - cosP * sinY * sinR;
		y = cosP * sinY * cosR + sinP * cosY * sinR;
		z = cosP * cosY * sinR - sinP * sinY * cosR;
		w = cosP * cosY * cosR + sinP * sinY * sinR;

		return this;
	}

	public Quaternion Add(Quaternion q)
	{
		return Add(q.x, q.y, q.z, q.w);
	}

	public Quaternion Add(float x, float y, float z, float w)
	{
		return Cpy().AddSelf(x, y, z, w);
	}

	public Quaternion Subtract(Quaternion q)
	{
		return Subtract(q.x, q.y, q.z, q.w);
	}

	public Quaternion Subtract(float x, float y, float z, float w)
	{
		return Add(-x, -y, -z, -w);
	}

	public Quaternion SubtractSelf(Quaternion q)
	{
		return SubtractSelf(q.x, q.y, q.z, q.w);
	}

	public Quaternion SubtractSelf(float x, float y, float z, float w)
	{
		return AddSelf(-x, -y, -z, -w);
	}

	public Quaternion Normalize()
	{
		return Cpy().NormalizeSelf();
	}

	public float Length()
	{
		return MathUtils.Sqrt(LengthSquared());
	}

	public float LengthSquared()
	{
		return x * x + y * y + z * z + w * w;
	}

	public Quaternion Multiply(Quaternion q)
	{
		return Cpy().MultiplySelf(q);
	}

	public Quaternion NormalizeSelf()
	{
		float length = Length();

		if (length == 0 || length == 1)
		{
			return this;
		}

		return Set(x / length, y / length, z / length, w / length);
	}

	public Quaternion MultiplySelf(Quaternion q)
	{
		float nx = w * q.x + x * q.w + y * q.z - z * q.y;
		float ny = w * q.y + y * q.w + z * q.x - x * q.z;
		float nz = w * q.z + z * q.w + x * q.y - y * q.x;
		float nw = w * q.w - x * q.x - y * q.y - z * q.z;

		return Set(nx, ny, nz, nw).NormalizeSelf();
	}

	public Vector3f MultiplyInverse(Vector3f v)
	{
		return MultiplyInverse(v, new Vector3f());
	}

	public Vector3f MultiplyInverse(Vector3f v, Vector3f dest)
	{
		InvertSelf().Multiply(v, dest);
		InvertSelf();

		return dest;
	}

	public Vector3f Multiply(Vector3f v)
	{
		return Multiply(v, new Vector3f());
	}

	public Vector3f Multiply(Vector3f v, Vector3f dest)
	{
		Vector3f temp = Vector3f.TMP();

		Quaternion temp1 = Quaternion.TMP();
		Quaternion temp2 = Quaternion.TMP();
		Quaternion temp3 = Quaternion.TMP();

		float length = v.Length();
		v = temp.Set(v).NormalizeSelf();

		Quaternion q1 = temp1.Set(this).ConjugateSelf().NormalizeSelf();
		Quaternion qv = temp2.Set(v.x, v.y, v.z, 0);
		Quaternion q = this;

		Quaternion res = temp3.Set(q).NormalizeSelf().MultiplySelf(qv.MultiplySelf(q1).NormalizeSelf());

		dest.x = res.x;
		dest.y = res.y;
		dest.z = res.z;

		return dest.NormalizeSelf().ScaleSelf(length);
	}

	public Quaternion Invert()
	{
		return Cpy().InvertSelf();
	}

	public Quaternion InvertSelf()
	{
		float norm = LengthSquared();

		if (norm == 0)
		{
			return ConjugateSelf();
		}

		x = -x / norm;
		y = -y / norm;
		z = -z / norm;
		w = +w / norm;

		return this;
	}

	public Quaternion Lerp(Quaternion tarGet, float alpha)
	{
		return Cpy().LerpSelf(tarGet, alpha);
	}

	public Quaternion LerpSelf(Quaternion tarGet, float alpha)
	{
			//
		/*Vector4f temp1 = Vector4f.TMP();
		Vector4f temp2 = Vector4f.TMP();

		Vector4f start = temp1.Set(x, y, z, w);
		Vector4f end = temp2.Set(tarGet.x, tarGet.y, tarGet.z, tarGet.w);
		Vector4f lerp = start.LerpSelf(end, alpha).NormalizeSelf();

		Set(lerp.x, lerp.y, lerp.z, lerp.w);*/

		return this;
	}

	public float GetX()
	{
		return x;
	}

	public void SetX(float x)
	{
		this.x = x;
	}

	public float GetY()
	{
		return y;
	}

	public void SetY(float y)
	{
		this.y = y;
	}

	public float GetZ()
	{
		return z;
	}

	public void SetZ(float z)
	{
		this.z = z;
	}

	public float GetW()
	{
		return w;
	}

	public void SetW(float w)
	{
		this.w = w;
	}

	public Quaternion Set()
	{
		return Set(0, 0, 0, 1);
	}

	public override string ToString()
	{
		return "(" + x + "," + y + "," + z + "," + w + ")";
	}

}

}

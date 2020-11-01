using loon.utils;

namespace loon.geom
{
    public class Vector3f : XYZ
    {

		public float x;

		public float y;

		public float z;

		private static readonly Array<Vector3f> _VEC3_CACHE = new Array<Vector3f>();

		public static Vector3f Set(Vector3f v1, Vector3f v2)
		{
			return Set(v1, v2.x, v2.y, v2.z);
		}

		public static Vector3f Set(Vector3f v1, float[] values)
		{
			return Set(v1, values[0], values[1], values[2]);
		}

		public static Vector3f Set(Vector3f v1, float x, float y, float z)
		{
			v1.x = x;
			v1.y = y;
			v1.z = z;
			return v1;
		}

		public static Vector3f Cpy(Vector3f v1)
		{
            Vector3f newSVector = new Vector3f
            {
                x = v1.x,
                y = v1.y,
                z = v1.z
            };

            return newSVector;
		}

		public static Vector3f Add(Vector3f v1, Vector3f v2)
		{
			v1.x += v2.x;
			v1.y += v2.y;
			v1.z += v2.z;

			return v1;
		}

		public static Vector3f Add(Vector3f v1, float x, float y, float z)
		{
			v1.x += x;
			v1.y += y;
			v1.z += z;

			return v1;
		}

		public static Vector3f Add(Vector3f v1, float v)
		{
			v1.x += v;
			v1.y += v;
			v1.z += v;

			return v1;
		}

		public static Vector3f Sub(Vector3f v1, Vector3f v2)
		{
			v1.x -= v2.x;
			v1.y -= v2.y;
			v1.z -= v2.z;

			return v1;
		}

		public static Vector3f Sub(Vector3f v1, float x, float y, float z)
		{
			v1.x -= x;
			v1.y -= y;
			v1.z -= z;

			return v1;
		}

		public static Vector3f Sub(Vector3f v1, float v)
		{
			v1.x -= v;
			v1.y -= v;
			v1.z -= v;

			return v1;
		}

		public static Vector3f Mul(Vector3f v1, Vector3f v2)
		{
			v1.x = v2.x * v1.x;
			v1.y = v2.y * v1.y;
			v1.z = v2.z * v1.z;

			return v1;
		}

		public static Vector3f Mul(Vector3f v1, float v)
		{
			v1.x = v * v1.x;
			v1.y = v * v1.y;
			v1.z = v * v1.z;

			return v1;
		}

		public static Vector3f Div(Vector3f v1, float v)
		{
			float d = 1 / v;
			v1.x = d * v1.x;
			v1.y = d * v1.y;
			v1.z = d * v1.z;

			return v1;
		}

		public static float Len(Vector3f v1)
		{
			return MathUtils.Sqrt(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z);
		}

		public static float Len2(Vector3f v1)
		{
			return v1.x * v1.x + v1.y * v1.y + v1.z * v1.z;
		}

		public static bool Idt(Vector3f v1, Vector3f v2)
		{
			return v1.x == v2.x && v1.y == v2.y && v1.z == v2.z;
		}

		public static float Dst(Vector3f v1, Vector3f v2)
		{
			float a = v2.x - v1.x;
			float b = v2.y - v1.y;
			float c = v2.z - v1.z;

			a *= a;
			b *= b;
			c *= c;

			return MathUtils.Sqrt(a + b + c);
		}

		public static float Dst(Vector3f v1, float x, float y, float z)
		{
			float a = x - v1.x;
			float b = y - v1.y;
			float c = z - v1.z;

			a *= a;
			b *= b;
			c *= c;

			return MathUtils.Sqrt(a + b + c);
		}

		public static Vector3f Crs(Vector3f v1, Vector3f v2)
		{
			v1.x = v1.y * v2.z - v1.z * v2.y;
			v1.y = v1.z * v2.x - v1.x * v2.z;
			v1.z = v1.x * v2.y - v1.y * v2.x;

			return v1;
		}

		public static Vector3f Crs(Vector3f v1, float x, float y, float z)
		{
			v1.x = v1.y * z - v1.z * y;
			v1.y = v1.z * x - v1.x * z;
			v1.z = v1.x * y - v1.y * x;

			return v1;
		}

		public static bool IsZero(Vector3f v1)
		{
			return v1.x == 0 && v1.y == 0 && v1.z == 0;
		}

		public static Vector3f Lerp(Vector3f v1, Vector3f tarGet, float alpha)
		{
			Vector3f r = Mul(v1, 1.0f - alpha);
			Add(r, Mul(Cpy(v1), alpha));
			return r;
		}

		public static float Dot(Vector3f v1, float x, float y, float z)
		{
			return v1.x * x + v1.y * y + v1.z * z;
		}

		public static float Dst2(Vector3f v1, Vector3f v2)
		{
			float a = v2.x - v1.x;
			float b = v2.y - v1.y;
			float c = v2.z - v1.z;

			a *= a;
			b *= b;
			c *= c;

			return a + b + c;
		}

		public static float Dst2(Vector3f v1, float x, float y, float z)
		{
			float a = x - v1.x;
			float b = y - v1.y;
			float c = z - v1.z;

			a *= a;
			b *= b;
			c *= c;

			return a + b + c;
		}

		public static Vector3f ScaleTo(Vector3f v1, float scalarX, float scalarY, float scalarZ)
		{
			v1.x *= scalarX;
			v1.y *= scalarY;
			v1.z *= scalarZ;
			return v1;
		}

		public static float AngleBetween(Vector3f v1, Vector3f other)
		{
			float angle;

			float dot = Dot(v1, other);

			float len1 = Len(v1);
			float Len2 = Len(other);

			if (len1 == 0 && Len2 == 0)
			{
				return 0;
			}

			angle = MathUtils.Acos(dot / (len1 * Len2));

			return angle;
		}

		public static float AngleBetween(Vector3f v1, float x, float y, float z)
		{
			float angle;

			float dot = Dot(v1, x, y, z);

			float len1 = Len(v1);
			float Len2 = MathUtils.Sqrt(x * x + y * y + z * z);

			if (len1 == 0 || Len2 == 0)
			{
				return 0;
			}

			angle = MathUtils.Acos(dot / (len1 * Len2));

			return angle;
		}

		public static float AngleBetweenXY(Vector3f v1, float x, float y)
		{
			float angle;

			float Dot = v1.x * x + v1.y * y;

			float len1 = MathUtils.Sqrt(v1.x * v1.x + v1.y * v1.y);
			float Len2 = MathUtils.Sqrt(x * x + y * y);

			if (len1 == 0 || Len2 == 0)
			{
				return 0;
			}

			angle = MathUtils.Acos(Dot / (len1 * Len2));

			return angle;
		}

		public static float AngleBetweenXZ(Vector3f v1, float x, float z)
		{
			float angle;

			float Dot = v1.x * x + v1.z * z;

			float len1 = MathUtils.Sqrt(v1.x * v1.x + v1.z * v1.z);
			float Len2 = MathUtils.Sqrt(x * x + z * z);

			if (len1 == 0 || Len2 == 0)
			{
				return 0;
			}

			angle = MathUtils.Acos(Dot / (len1 * Len2));

			return angle;
		}

		public static float AngleBetweenYZ(Vector3f v1, float y, float z)
		{
			float angle;

			float Dot = v1.y * y + v1.z * z;

			float len1 = MathUtils.Sqrt(v1.y * v1.y + v1.z * v1.z);
			float Len2 = MathUtils.Sqrt(y * y + z * z);

			if (len1 == 0 || Len2 == 0)
			{
				return 0;
			}

			angle = MathUtils.Acos(Dot / (len1 * Len2));

			return angle;
		}

		public static float[] ToArray(Vector3f v1)
		{
			return new float[] { v1.x, v1.y, v1.z };
		}

		public static Vector3f Cross(Vector3f a, Vector3f b)
		{
			return new Vector3f(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
		}

		public static Vector3f VectorSquareEquation(Vector3f a, Vector3f b, Vector3f c)
		{
			float baz = -1 * a.z / b.z;
			b.ScaleSelf(baz).AddSelf(a);
			float caz = -1 * a.z / c.z;
			c.ScaleSelf(caz).AddSelf(a);
			float cby = -1 * b.y / c.y;
			c.ScaleSelf(cby).AddSelf(b);
			float X = c.x;
			float Y = -1 * X * b.x / b.y;
			float Z = -1 * (X * a.x + Y * a.y) / a.z;
			return new Vector3f(X, Y, Z);
		}

		public static Vector3f CalcNormal(Vector3f zero, Vector3f one, Vector3f two)
		{
			tmpNormal1.Set(one.x - zero.x, one.y - zero.y, one.z - zero.z);
			tmpNormal2.Set(two.x - zero.x, two.y - zero.y, two.z - zero.z);
			Vector3f res = new Vector3f();
			return CalcVectorNormal(tmpNormal1, tmpNormal2, res);
		}

		public  static Vector3f CalcVectorNormal(Vector3f one, Vector3f two, Vector3f result)
		{
			result.x = two.y * one.z - two.z * one.y;
			result.y = two.z * one.x - two.x * one.z;
			result.z = two.x * one.y - two.y * one.x;
			return result.NorSelf();
		}

		public  static Vector3f TMP()
		{
			Vector3f temp = _VEC3_CACHE.Pop();
			if (temp == null)
			{
				_VEC3_CACHE.Add(temp = new Vector3f(0, 0, 0));
			}
			return temp;
		}

		public  static Vector3f ZERO()
		{
			return new Vector3f(0);
		}

		public  static Vector3f ONE()
		{
			return new Vector3f(1);
		}

		public  static Vector3f AXIS_X()
		{
			return new Vector3f(1, 0, 0);
		}

		public  static Vector3f AXIS_Y()
		{
			return new Vector3f(0, 1, 0);
		}

		public  static Vector3f AXIS_Z()
		{
			return new Vector3f(0, 0, 1);
		}

		public  static Vector3f At(float x, float y, float z)
		{
			return new Vector3f(x, y, z);
		}

		public  static Vector3f SmoothStep(Vector3f a, Vector3f b, float amount)
		{
			return new Vector3f(MathUtils.SmoothStep(a.x, b.x, amount), MathUtils.SmoothStep(a.y, b.y, amount),
					MathUtils.SmoothStep(a.z, b.z, amount));
		}

		private  static readonly Matrix4 tmpMat = new Matrix4();

		private  static readonly Vector3f tmpNormal1 = new Vector3f();

		private  static readonly Vector3f tmpNormal2 = new Vector3f();

		public Vector3f(): this(0, 0, 0)
		{
			
		}

		public Vector3f(float x, float y, float z)
		{
			this.Set(x, y, z);
		}

		public Vector3f( Vector3f v)
		{
			this.Set(v);
		}

		public Vector3f( float[] values)
		{
			this.Set(values[0], values[1], values[2]);
		}

		public Vector3f( Vector2f v, float z)
		{
			this.Set(v.x, v.y, z);
		}

		public Vector3f(float v): this(v, v, v)
		{
			
		}

		public Vector3f(float x, Vector2f v): this(x, v.GetX(), v.GetY())
		{
			
		}

		public Vector3f Set(float x, float y, float z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}

		public Vector3f Set( Vector3f v)
		{
			return this.Set(v.x, v.y, v.z);
		}

		public Vector3f Set( float[] values)
		{
			return this.Set(values[0], values[1], values[2]);
		}

		public Vector3f Set( Vector2f v, float z)
		{
			return this.Set(v.x, v.y, z);
		}

		public Vector3f Cpy()
		{
			return new Vector3f(this);
		}

		public Vector3f AddSelf( Vector3f v)
		{
			return this.AddSelf(v.x, v.y, v.z);
		}

		public Vector3f AddSelf(float x, float y, float z)
		{
			return this.Set(this.x + x, this.y + y, this.z + z);
		}

		public Vector3f AddSelf(float v)
		{
			return this.Set(this.x + v, this.y + v, this.z + v);
		}

		public Vector3f MulAddSelf(Vector3f vec, float scalar)
		{
			this.x += vec.x * scalar;
			this.y += vec.y * scalar;
			this.z += vec.z * scalar;
			return this;
		}

		public Vector3f MulAddSelf(Vector3f vec, Vector3f MulVec)
		{
			this.x += vec.x * MulVec.x;
			this.y += vec.y * MulVec.y;
			this.z += vec.z * MulVec.z;
			return this;
		}

		public static float Len( float x,  float y,  float z)
		{
			return MathUtils.Sqrt(x * x + y * y + z * z);
		}

		public float Len()
		{
			return MathUtils.Sqrt(x * x + y * y + z * z);
		}

		public static float Len2( float x,  float y,  float z)
		{
			return x * x + y * y + z * z;
		}

		public float Len2()
		{
			return x * x + y * y + z * z;
		}

		public bool Idt( Vector3f v)
		{
			return x == v.x && y == v.y && z == v.z;
		}

		public static float Dst( float x1,  float y1,  float z1,  float x2,  float y2,
				 float z2)
		{
			 float a = x2 - x1;
			 float b = y2 - y1;
			 float c = z2 - z1;
			return MathUtils.Sqrt(a * a + b * b + c * c);
		}

		public float Dst( Vector3f v)
		{
			 float a = v.x - x;
			 float b = v.y - y;
			 float c = v.z - z;
			return MathUtils.Sqrt(a * a + b * b + c * c);
		}

		public float Dst(float x, float y, float z)
		{
			 float a = x - this.x;
			 float b = y - this.y;
			 float c = z - this.z;
			return MathUtils.Sqrt(a * a + b * b + c * c);
		}

		public static float Dst2( float x1,  float y1,  float z1,  float x2,  float y2,
				 float z2)
		{
			 float a = x2 - x1;
			 float b = y2 - y1;
			 float c = z2 - z1;
			return a * a + b * b + c * c;
		}

		public float Dst2(Vector3f point)
		{
			 float a = point.x - x;
			 float b = point.y - y;
			 float c = point.z - z;
			return a * a + b * b + c * c;
		}

		public float Dst2(float x, float y, float z)
		{
			 float a = x - this.x;
			 float b = y - this.y;
			 float c = z - this.z;
			return a * a + b * b + c * c;
		}

		public Vector3f NorSelf()
		{
			 float Len2 = this.Len2();
			if (Len2 == 0f || Len2 == 1f)
			{
				return this;
			}
			return this.ScaleSelf(1f / MathUtils.Sqrt(Len2));
		}

		public static float Dot(float x1, float y1, float z1, float x2, float y2, float z2)
		{
			return x1 * x2 + y1 * y2 + z1 * z2;
		}

		public float Dot( Vector3f v)
		{
			return x * v.x + y * v.y + z * v.z;
		}

		public float Dot(float x, float y, float z)
		{
			return this.x * x + this.y * y + this.z * z;
		}

		public  static float Dot(Vector3f a, Vector3f b)
		{
			return a.x * b.x + a.y * b.y + a.z * b.z;
		}

		public Vector3f CrsSelf( Vector3f v)
		{
			return this.Set(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
		}

		public Vector3f CrsSelf(float x, float y, float z)
		{
			return this.Set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
		}

		public Vector3f Mul4x3(float[] matrix)
		{
			return Set(x * matrix[0] + y * matrix[3] + z * matrix[6] + matrix[9],
					x * matrix[1] + y * matrix[4] + z * matrix[7] + matrix[10],
					x * matrix[2] + y * matrix[5] + z * matrix[8] + matrix[11]);
		}

		public Vector3f MulSelf( Matrix4 matrix)
		{
			 float[] l_mat = matrix.val;
			return this.Set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02] + l_mat[Matrix4.M03],
					x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12] + l_mat[Matrix4.M13],
					x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M23]);
		}

		public Vector3f TraMul( Matrix4 matrix)
		{
			 float[] l_mat = matrix.val;
			return this.Set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20] + l_mat[Matrix4.M30],
					x * l_mat[Matrix4.M01] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21] + l_mat[Matrix4.M31],
					x * l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M32]);
		}

		public Vector3f MulSelf(Matrix3 matrix)
		{
			 float[] l_mat = matrix.val;
			return Set(x * l_mat[Matrix3.M00] + y * l_mat[Matrix3.M01] + z * l_mat[Matrix3.M02],
					x * l_mat[Matrix3.M10] + y * l_mat[Matrix3.M11] + z * l_mat[Matrix3.M12],
					x * l_mat[Matrix3.M20] + y * l_mat[Matrix3.M21] + z * l_mat[Matrix3.M22]);
		}

		public Vector3f TraMul(Matrix3 matrix)
			
		{
			 float[] l_mat = matrix.val;
			return Set(x * l_mat[Matrix3.M00] + y * l_mat[Matrix3.M10] + z * l_mat[Matrix3.M20],
					x * l_mat[Matrix3.M01] + y * l_mat[Matrix3.M11] + z * l_mat[Matrix3.M21],
					x * l_mat[Matrix3.M02] + y * l_mat[Matrix3.M12] + z * l_mat[Matrix3.M22]);
		}

		public Vector3f MulSelf( Quaternion quat)
		{
			return quat.TransformSelf(this);
		}

		public Vector3f PrjSelf( Matrix4 matrix)
		{
			 float[] l_mat = matrix.val;
			 float l_w = 1f
					/ (x * l_mat[Matrix4.M30] + y * l_mat[Matrix4.M31] + z * l_mat[Matrix4.M32] + l_mat[Matrix4.M33]);
			return this.Set(
					(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02] + l_mat[Matrix4.M03]) * l_w,
					(x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12] + l_mat[Matrix4.M13]) * l_w,
					(x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M23]) * l_w);
		}

		public Vector3f RotSelf( Matrix4 matrix)
		{
			 float[] l_mat = matrix.val;
			return this.Set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02],
					x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12],
					x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22]);
		}

		public Vector3f UnRotateSelf( Matrix4 matrix)
		{
			 float[] l_mat = matrix.val;
			return this.Set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20],
					x * l_mat[Matrix4.M01] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21],
					x * l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22]);
		}

		public Vector3f UntransformSelf( Matrix4 matrix)
		{
			 float[] l_mat = matrix.val;
			x -= l_mat[Matrix4.M03];
			y -= l_mat[Matrix4.M03];
			z -= l_mat[Matrix4.M03];
			return this.Set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20],
					x * l_mat[Matrix4.M01] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21],
					x * l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22]);
		}

		public Vector3f RotateSelf(float degrees, float axisX, float axisY, float axisZ)
		{
			return this.MulSelf(tmpMat.SetToRotation(axisX, axisY, axisZ, degrees));
		}

		public Vector3f RotateRadSelf(float radians, float axisX, float axisY, float axisZ)
		{
			return this.MulSelf(tmpMat.SetToRotationRad(axisX, axisY, axisZ, radians));
		}

		public Vector3f RotateSelf( Vector3f axis, float degrees)
		{
			tmpMat.SetToRotation(axis, degrees);
			return this.MulSelf(tmpMat);
		}

		public Vector3f RotateRadSelf( Vector3f axis, float radians)
		{
			tmpMat.SetToRotationRad(axis, radians);
			return this.MulSelf(tmpMat);
		}

		public bool IsUnit()
		{
			return IsUnit(0.000000001f);
		}

		public bool IsUnit( float margin)
		{
			return MathUtils.Abs(Len2() - 1f) < margin;
		}

		public bool IsZero()
		{
			return x == 0 && y == 0 && z == 0;
		}

		public bool IsZero( float margin)
		{
			return Len2() < margin;
		}

		public bool IsOnLine(Vector3f other, float epsilon)
		{
			return Len2(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x) <= epsilon;
		}

		public bool IsOnLine(Vector3f other)
		{
			return Len2(y * other.z - z * other.y, z * other.x - x * other.z,
					x * other.y - y * other.x) <= MathUtils.FLOAT_ROUNDING_ERROR;
		}

		public bool IsCollinear(Vector3f other, float epsilon)
		{
			return IsOnLine(other, epsilon) && HasSameDirection(other);
		}

		public bool IsCollinear(Vector3f other)
		{
			return IsOnLine(other) && HasSameDirection(other);
		}

		public bool IsCollinearOpposite(Vector3f other, float epsilon)
		{
			return IsOnLine(other, epsilon) && HasOppositeDirection(other);
		}

		public bool IsCollinearOpposite(Vector3f other)
		{
			return IsOnLine(other) && HasOppositeDirection(other);
		}

		public bool IsPerpendicular(Vector3f v)
		{
			return MathUtils.IsZero(Dot(v));
		}

		public bool IsPerpendicular(Vector3f v, float epsilon)
		{
			return MathUtils.IsZero(Dot(v), epsilon);
		}

		public bool HasSameDirection(Vector3f v)
		{
			return Dot(v) > 0;
		}

		public bool HasOppositeDirection(Vector3f v)
		{
			return Dot(v) < 0;
		}

		public Vector3f LerpSelf(float x, float y, float z, float alpha)
		{
			this.x += alpha * (x - this.x);
			this.y += alpha * (y - this.y);
			this.z += alpha * (z - this.z);
			return this;
		}

		public Vector3f Lerp(float x, float y, float z, float alpha)
		{
			return Cpy().LerpSelf(x, y, z, alpha);
		}

		public Vector3f Lerp( Vector3f tarGet, float alpha)
		{
			return Cpy().LerpSelf(tarGet, alpha);
		}

		public Vector3f Slerp( Vector3f tarGet, float alpha)
		{
			return Cpy().SlerpSelf(tarGet, alpha);
		}

		public Vector3f LerpSelf( Vector3f tarGet, float alpha)
		{
			ScaleSelf(1.0f - alpha);
			AddSelf(tarGet.x * alpha, tarGet.y * alpha, tarGet.z * alpha);
			return this;
		}

		public Vector3f SlerpSelf( Vector3f tarGet, float alpha)
		{
			 float dot = Dot(tarGet);
			if (dot > 0.9995 || dot < -0.9995)
			{
				return LerpSelf(tarGet, alpha);
			}

			 float theta0 = MathUtils.Acos(dot);
			 float theta = theta0 * alpha;

			 float st = MathUtils.Sin(theta);
			 float tx = tarGet.x - x * dot;
			 float ty = tarGet.y - y * dot;
			 float tz = tarGet.z - z * dot;
			 float l2 = tx * tx + ty * ty + tz * tz;
			 float dl = st * ((l2 < 0.0001f) ? 1f : 1f / MathUtils.Sqrt(l2));

			return ScaleSelf(MathUtils.Cos(theta)).AddSelf(tx * dl, ty * dl, tz * dl).NorSelf();
		}

		public Vector3f LimitSelf(float limit)
		{
			return Limit2Self(limit * limit);
		}

		public Vector3f Limit2Self(float limit2)
		{
			float len2 = Len2();
			if (len2 > limit2)
			{
				ScaleSelf(limit2 / len2);
			}
			return this;
		}

		public Vector3f SetLengthSelf(float len)
		{
			return SetLength2Self(len * len);
		}

		public Vector3f SetLength2Self(float len2)
		{
			float oldLen2 = Len2();
			return (oldLen2 == 0 || oldLen2 == len2) ? this : ScaleSelf(MathUtils.Sqrt(len2 / oldLen2));
		}

		public Vector3f ClampSelf(float min, float max)
		{
			 float len2 = Len2();
			if (len2 == 0f)
				return this;
			float max2 = max * max;
			if (len2 > max2)
				return ScaleSelf(MathUtils.Sqrt(max2 / len2));
			float min2 = min * min;
			if (len2 < min2)
				return ScaleSelf(MathUtils.Sqrt(min2 / len2));
			return this;
		}

	public override int GetHashCode()
		{
			 uint prime = 31;
			uint result = 1;
			result = prime * result + NumberUtils.FloatToIntBits(x);
			result = prime * result + NumberUtils.FloatToIntBits(y);
			result = prime * result + NumberUtils.FloatToIntBits(z);
			return (int)result;
		}


	public override bool Equals(object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (GetType() != obj.GetType())
				return false;
			Vector3f other = (Vector3f)obj;
			if (NumberUtils.FloatToIntBits(x) != NumberUtils.FloatToIntBits(other.x))
				return false;
			if (NumberUtils.FloatToIntBits(y) != NumberUtils.FloatToIntBits(other.y))
				return false;
			if (NumberUtils.FloatToIntBits(z) != NumberUtils.FloatToIntBits(other.z))
				return false;
			return true;
		}

		public bool EpsilonEquals( Vector3f other, float epsilon)
		{
			if (other == null)
				return false;
			if (MathUtils.Abs(other.x - x) > epsilon)
				return false;
			if (MathUtils.Abs(other.y - y) > epsilon)
				return false;
			if (MathUtils.Abs(other.z - z) > epsilon)
				return false;
			return true;
		}

		public bool EpsilonEquals(float x, float y, float z, float epsilon)
		{
			if (MathUtils.Abs(x - this.x) > epsilon)
				return false;
			if (MathUtils.Abs(y - this.y) > epsilon)
				return false;
			if (MathUtils.Abs(z - this.z) > epsilon)
				return false;
			return true;
		}

		public Vector3f ZeroSelf()
		{
			this.x = 0;
			this.y = 0;
			this.z = 0;
			return this;
		}

		public Vector3f Add(Vector3f v)
		{
			return Add(v.x, v.y, v.z);
		}

		public Vector3f Add(float x, float y, float z)
		{
			return Cpy().AddSelf(x, y, z);
		}

		public Vector3f Add(Vector2f v, float z)
		{
			return Add(v.x, v.y, z);
		}

		public Vector3f AddSelf(Vector2f v, float z)
		{
			return AddSelf(v.x, v.y, z);
		}

		public Vector3f Add(float x, Vector2f v)
		{
			return Add(x, v.x, v.y);
		}

		public Vector3f AddSelf(float x, Vector2f v)
		{
			return AddSelf(x, v.x, v.y);
		}

		public Vector3f SubtractSelf(float x, float y, float z)
		{
			return AddSelf(-x, -y, -z);
		}

		public Vector3f Subtract(Vector3f v)
		{
			return Add(-v.x, -v.y, -v.z);
		}

		public Vector3f SubtractSelf(Vector3f v)
		{
			return AddSelf(-v.x, -v.y, -v.z);
		}

		public Vector3f Subtract(Vector2f v, float z)
		{
			return Subtract(v.x, v.y, z);
		}

		public Vector3f Subtract(float x, float y, float z)
		{
			return Add(-x, -y, -z);
		}

		public Vector3f SubtractSelf(Vector2f v, float z)
		{
			return AddSelf(-v.x, -v.y, z);
		}

		public Vector3f Subtract(float x, Vector2f v)
		{
			return Subtract(x, v.x, v.y);
		}

		public Vector3f SubtractSelf(float x, Vector2f v)
		{
			return AddSelf(-x, -v.x, -v.y);
		}

		public Vector3f Scale(float s)
		{
			return Scale(s, s, s);
		}

		public Vector3f Scale(float sx, float sy, float sz)
		{
			return Cpy().ScaleSelf(sx, sy, sz);
		}

		public Vector3f Scale(Vector3f origin, float dx, float dy, float dz)
		{
			return Cpy().Set((this.x - origin.x) * dx + origin.x, (this.y - origin.y) * dy + origin.y,
					(this.z - origin.z) * dz + origin.z);
		}

		public Vector3f Scale(Vector3f origin, float dx)
		{
			return Scale(origin, dx, dx, dx);
		}

		public Vector3f Scale(Vector3f origin, float dx, float dy)
		{
			return Scale(origin, dx, dy, 1f);
		}

		public Vector3f Cross(Vector3f v)
		{
			return Cross(v.x, v.y, v.z);
		}

		public Vector3f Cross(float vx, float vy, float vz)
		{
			return Cpy().CrossSelf(vx, vy, vz);
		}

		public Vector3f CrossSelf(float vx, float vy, float vz)
		{
			float x = this.x * vz - this.z * vy;
			float y = this.z * vx - this.x * vz;
			float z = this.x * vy - this.y * vx;

			return Set(x, y, z);
		}

		public Vector3f CrossSelf(Vector3f v)
		{
			return CrossSelf(v.x, v.y, v.z);
		}

		public Vector3f Normalize()
		{
			return Cpy().NormalizeSelf();
		}

		public Vector3f NormalizeSelf()
		{
			float l = Length();

			if (l == 0 || l == 1)
				return this;

			return Set(x / l, y / l, z / l);
		}

		public float Length()
		{
			return MathUtils.Sqrt(LengthSquared());
		}

		public float LengthSquared()
		{
			return x * x + y * y + z * z;
		}

		public Vector3f Negate()
		{
			return new Vector3f(-x, -y, -z);
		}

		public Vector3f NegateSelf()
		{
			return Set(-x, -y, -z);
		}

		public float Distance(float x, float y, float z)
		{
			return MathUtils.Sqrt(DistanceSquared(x, y, z));
		}

		public float DistanceSquared(float x, float y, float z)
		{
			 float x2 = (x - this.x) * (x - this.x);
			 float y2 = (y - this.y) * (y - this.y);
			 float z2 = (z - this.z) * (z - this.z);

			return x2 + y2 + z2;
		}

		public float Distance(Vector3f v)
		{
			return MathUtils.Sqrt(DistanceSquared(v));
		}

		public float DistanceSquared(Vector3f v)
		{
			return DistanceSquared(v.x, v.y, v.z);
		}

		public float Distance(Vector2f v)
		{
			return MathUtils.Sqrt(DistanceSquared(v));
		}

		public float DistanceSquared(Vector2f v)
		{
			return DistanceSquared(v.x, v.y, 0);
		}

		public Vector3f Rotate(Vector3f axis, float angle)
		{
			return Cpy().RotateSelf(axis, angle);
		}

		public Vector3f RotateX(Vector3f origin, float angle)
		{
			float pY = this.y - origin.y;
			float pZ = this.z - origin.z;
			float Cos = MathUtils.Cos(angle);
			float Sin = MathUtils.Sin(angle);
			float z = pZ * Cos - pY * Sin;
			float y = pZ * Sin + pY * Cos;
			pZ = z;
			pY = y;
			return Cpy().Set(this.x, pY + origin.y, pZ + origin.z);
		}

		public Vector3f RotateY(Vector3f origin, float angle)
		{
			float pX = this.x - origin.x;
			float pZ = this.z - origin.z;
			float Cos = MathUtils.Cos(angle);
			float Sin = MathUtils.Sin(angle);
			float x = pX * Cos - pZ * Sin;
			float z = pX * Sin + pZ * Cos;
			pX = x;
			pZ = z;
			return Cpy().Set(pX + origin.x, this.y, pZ + origin.z);
		}

		public Vector3f RotateZ(Point origin, float angle)
		{
			float pX = this.x - origin.x;
			float pY = this.y - origin.y;
			float Cos = MathUtils.Cos(angle);
			float Sin = MathUtils.Sin(angle);
			float x = pX * Cos - pY * Sin;
			float y = pX * Sin + pY * Cos;
			pX = x;
			pY = y;
			return Cpy().Set(pX + origin.x, pY + origin.y, this.z);
		}

		public Vector3f ScaleSelf(float s)
		{
			return ScaleSelf(s, s, s);
		}

		public Vector3f ScaleSelf(float sx, float sy, float sz)
		{
			return Set(x * sx, y * sy, z * sz);
		}

		public Vector3f Translate(float dx, float dy, float dz)
		{
			return Cpy().TranslateSelf(dx, dy, dz);
		}

		public Vector3f TranslateSelf(float dx, float dy, float dz)
		{
			return Set(this.x + dx, this.y + dy, this.z + dz);
		}

		public Vector3f Multiply(Matrix3 m)
		{
			return Cpy().MultiplySelf(m);
		}

		public Vector3f MultiplySelf(Matrix3 m)
		{
			float rx = x * m.Get(0, 0) + y * m.Get(0, 1) + z * m.Get(0, 2);
			float ry = x * m.Get(1, 0) + y * m.Get(1, 1) + z * m.Get(1, 2);
			float rz = x * m.Get(2, 0) + y * m.Get(2, 1) + z * m.Get(2, 2);

			return Set(rx, ry, rz);
		}

		public Vector3f Multiply(Matrix4 m)
		{
			return Cpy().MultiplySelf(m);
		}

		public Vector3f MultiplySelf(Matrix4 m)
		{
			float rx = x * m.Get(0, 0) + y * m.Get(1, 0) + z * m.Get(2, 0) + 1 * m.Get(3, 0);
			float ry = x * m.Get(0, 1) + y * m.Get(1, 1) + z * m.Get(2, 1) + 1 * m.Get(3, 1);
			float rz = x * m.Get(0, 2) + y * m.Get(1, 2) + z * m.Get(2, 2) + 1 * m.Get(3, 2);

			return Set(rx, ry, rz);
		}

		public Vector3f Set(float v)
		{
			return Set(v, v, v);
		}

		public float GetR()
		{
			return x;
		}

		public Vector3f SetR(float r)
		{
			x = r;
			return this;
		}

		public float GetG()
		{
			return y;
		}

		public Vector3f SetG(float g)
		{
			y = g;
			return this;
		}

		public float GetB()
		{
			return z;
		}

		public Vector3f SetB(float b)
		{
			z = b;
			return this;
		}

		public Vector2f GetXX()
		{
			return new Vector2f(x, x);
		}

		public Vector2f GetXY()
		{
			return new Vector2f(x, y);
		}

		public Vector2f GetXZ()
		{
			return new Vector2f(x, z);
		}

		public Vector2f GetYX()
		{
			return new Vector2f(y, x);
		}

		public Vector2f GetYY()
		{
			return new Vector2f(y, y);
		}

		public Vector2f GetYZ()
		{
			return new Vector2f(y, z);
		}

		public Vector2f GetZX()
		{
			return new Vector2f(z, x);
		}

		public Vector2f GetZY()
		{
			return new Vector2f(z, y);
		}

		public Vector2f GetZZ()
		{
			return new Vector2f(z, z);
		}

		
	public float GetX()
		{
			return this.x;
		}

		
	public float GetY()
		{
			return this.y;
		}

		
	public float GetZ()
		{
			return this.z;
		}

		public Vector3f Nor()
		{
			float len = this.Len();
			if (len == 0)
			{
				return this;
			}
			else
			{
				return this.Div(len);
			}
		}

		public Vector3f Div(float v)
		{
			float d = 1 / v;
			return this.Set(this.x * d, this.y * d, this.z * d);
		}

		public Vector3f Crs(Vector3f v)
		{
			return this.Set(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
		}

		public Vector3f Crs(float x, float y, float z)
		{
			return this.Set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
		}

		public Vector3f Sub(float x, float y, float z)
		{
			return this.Set(this.x - x, this.y - y, this.z - z);
		}

		public Vector3f Sub(float v)
		{
			return this.Set(this.x - v, this.y - v, this.z - v);
		}

		public Vector3f Sub(Vector3f argVec)
		{
			return new Vector3f(x - argVec.x, y - argVec.y, z - argVec.z);
		}

		public Vector3f SmoothStep(Vector3f v, float amount)
		{
			return SmoothStep(this, v, amount);
		}

		public Vector3f Mul(float argScalar)
		{
			return new Vector3f(x * argScalar, y * argScalar, z * argScalar);
		}

		public Vector3f Mul(Vector3f v)
		{
			return new Vector3f(x * v.x, y * v.y, z * v.z);
		}

		public Vector3f Random()
		{
			this.x = MathUtils.Random(0f, LSystem.viewSize.GetWidth());
			this.y = MathUtils.Random(0f, LSystem.viewSize.GetHeight());
			this.z = MathUtils.Random();
			return this;
		}

	public override string ToString()
		{
			return "(" + x + ", " + y + ", " + z + ")";
		}

	}
}

using System;
using Loon.Utils;
using Loon.Action.Map;
namespace Loon.Core.Geom {
	
	public class Vector2f {
	
		public static Vector2f TMP() {
			return new Vector2f();
		}
	
		public static Vector2f X1() {
			return new Vector2f(1, 0);
		}
	
		public static Vector2f Y1() {
			return new Vector2f(0, 1);
		}
	
		public static Vector2f ZERO() {
			return new Vector2f(0, 0);
		}
	
		public static readonly Vector2f Zero = new Vector2f();
	
		public float x;
	
		public float y;
	
		public Vector2f() {
	
		}
	
		public Vector2f(float x_0, float y_1) {
			this.x = x_0;
			this.y = y_1;
		}
	
		public Vector2f(Vector2f v) {
			Set(v);
		}
	
		public Vector2f Cpy() {
			return new Vector2f(this);
		}
	
		public float Len() {
			return MathUtils.Sqrt(x * x + y * y);
		}
	
		public float Len2() {
			return x * x + y * y;
		}
	
		public Vector2f Nor() {
			float len = Len();
			if (len != 0) {
				x /= len;
				y /= len;
			}
			return this;
		}
	
		public Vector2f Mul(float a) {
			return new Vector2f(x * a, y * a);
		}
	
		public Vector2f Sub(float x_0, float y_1) {
			return new Vector2f(this.x - x_0, this.y - y_1);
		}
	
		public Vector2f Div() {
			return new Vector2f(x / 2, y / 2);
		}
	
		public Vector2f Div(float v) {
			return new Vector2f(x / v, y / v);
		}
	
		public Vector2f Div(Vector2f v) {
			return new Vector2f(x / v.x, y / v.y);
		}
	
		public Vector2f Add(float x_0, float y_1) {
			return new Vector2f(this.x + x_0, this.y + y_1);
		}
	
		public float Dot(Vector2f v) {
			return x * v.x + y * v.y;
		}
	
		public float Dst(Vector2f v) {
			float x_d = v.x - x;
			float y_d = v.y - y;
			return MathUtils.Sqrt(x_d * x_d + y_d * y_d);
		}
	
		public float Dst(float x_0, float y_1) {
			float x_d = x_0 - this.x;
			float y_d = y_1 - this.y;
			return MathUtils.Sqrt(x_d * x_d + y_d * y_d);
		}
	
		public float Dst2(Vector2f v) {
			float x_d = v.x - x;
			float y_d = v.y - y;
			return x_d * x_d + y_d * y_d;
		}
	
		public float Dst2(float x_0, float y_1) {
			float x_d = x_0 - this.x;
			float y_d = y_1 - this.y;
			return x_d * x_d + y_d * y_d;
		}
	
		public Vector2f Tmp() {
			return TMP().Set(this);
		}
	
		public float Crs(Vector2f v) {
			return this.x * v.y - this.y * v.x;
		}
	
		public float Crs(float x_0, float y_1) {
			return this.x * y_1 - this.y * x_0;
		}
	
		public float Angle() {
			float angle = MathUtils.Atan2(y, x) * MathUtils.RAD_TO_DEG;
			if (angle < 0) {
				angle += 360;
			}
			return angle;
		}
	
		public Vector2f Rotate(float angle) {
			float rad = angle * MathUtils.DEG_TO_RAD;
			float cos = MathUtils.Cos(rad);
			float sin = MathUtils.Sin(rad);
	
			float newX = this.x * cos - this.y * sin;
			float newY = this.x * sin + this.y * cos;
	
			this.x = newX;
			this.y = newY;
	
			return this;
		}
	
		public Vector2f Lerp(Vector2f target, float alpha) {
			Vector2f r = this.Mul(1.0f - alpha);
			r.Add(target.Tmp().Mul(alpha));
			return r;
		}
	
		public Vector2f(float value_ren):this(value_ren, value_ren) {
		
		}
	
		public Vector2f(float[] coords) {
			x = coords[0];
			y = coords[1];
		}
	
		public void Move(Vector2f vector2D) {
			this.x += vector2D.x;
			this.y += vector2D.y;
		}
	
		public void Move_multiples(int direction, int multiples) {
			if (multiples <= 0) {
				multiples = 1;
			}
			Vector2f v = Field2D.GetDirection(direction);
			Move(v.X() * multiples, v.Y() * multiples);
		}
	
		public void MoveX(int x_0) {
			this.x += x_0;
		}
	
		public void MoveY(int y_0) {
			this.y += y_0;
		}
	
		public void MoveByAngle(int degAngle, float distance) {
			if (distance == 0) {
				return;
			}
			float Angle = MathUtils.ToRadians(degAngle);
			float dX = (MathUtils.Cos(Angle) * distance);
			float dY = (-MathUtils.Sin(Angle) * distance);
			int idX = MathUtils.Round(dX);
			int idY = MathUtils.Round(dY);
			Move(idX, idY);
		}
	
		public void Move(float x_0, float y_1) {
			this.x += x_0;
			this.y += y_1;
		}
	
		public void Move(float distance) {
			float angle = MathUtils.ToRadians(GetAngle());
			int x_0 = MathUtils.Round(GetX() + MathUtils.Cos(angle) * distance);
			int y_1 = MathUtils.Round(GetY() + MathUtils.Sin(angle) * distance);
			SetLocation(x_0, y_1);
		}
	
		public bool NearlyCompare(Vector2f v, int range) {
			int dX = MathUtils.Abs(X() - v.X());
			int dY = MathUtils.Abs(Y() - v.Y());
			return (dX <= range) && (dY <= range);
		}
	
		public int Angle(Vector2f v) {
			int dx = v.X() - X();
			int dy = v.Y() - Y();
			int adx = MathUtils.Abs(dx);
			int ady = MathUtils.Abs(dy);
			if ((dy == 0) && (dx == 0)) {
				return 0;
			}
			if ((dy == 0) && (dx > 0)) {
				return 0;
			}
			if ((dy == 0) && (dx < 0)) {
				return 180;
			}
			if ((dy > 0) && (dx == 0)) {
				return 90;
			}
			if ((dy < 0) && (dx == 0)) {
				return 270;
			}
			float rwinkel = MathUtils.Atan(ady / adx);
			float dwinkel = 0.0f;
			if ((dx > 0) && (dy > 0)) {
				dwinkel = MathUtils.ToDegrees(rwinkel);
			} else if ((dx < 0) && (dy > 0)) {
				dwinkel = (180.0f - MathUtils.ToDegrees(rwinkel));
			} else if ((dx > 0) && (dy < 0)) {
				dwinkel = (360.0f - MathUtils.ToDegrees(rwinkel));
			} else if ((dx < 0) && (dy < 0)) {
				dwinkel = (180.0f + MathUtils.ToDegrees(rwinkel));
			}
			int iwinkel = (int) dwinkel;
			if (iwinkel == 360) {
				iwinkel = 0;
			}
			return iwinkel;
		}
	
		public float GetAngle() {
			float theta = MathUtils.ToDegrees(MathUtils.Atan2(y, x));
			if ((theta < -360) || (theta > 360)) {
				theta = theta % 360;
			}
			if (theta < 0) {
				theta = 360 + theta;
			}
			return theta;
		}
	
		public float[] GetCoords() {
			return (new float[] { x, y });
		}
	
		public void SetLocation(float x_0, float y_1) {
			this.x = x_0;
			this.y = y_1;
		}
	
		public void SetX(float x_0) {
			this.x = x_0;
		}
	
		public void SetY(float y_0) {
			this.y = y_0;
		}
	
		public float GetX() {
			return x;
		}
	
		public float GetY() {
			return y;
		}
	
		public int X() {
			return (int) x;
		}
	
		public int Y() {
			return (int) y;
		}
	
		public Vector2f Reverse() {
			x = -x;
			y = -y;
			return this;
		}
	
		public static Vector2f Sum(Vector2f a, Vector2f b) {
			Vector2f answer = new Vector2f(a);
			return answer.Add(b);
		}
	
		public static Vector2f Mult(Vector2f vector, float scalar) {
			Vector2f answer = new Vector2f(vector);
			return answer.Mul(scalar);
		}
	
		public float Cross(Vector2f v) {
			return this.x * v.y - v.x * this.y;
		}
	
		public float LenManhattan() {
			return Math.Abs(this.x) + Math.Abs(this.y);
		}
	
		public static Vector2f Cpy(Vector2f vectorA) {
			Vector2f newSVector2 = new Vector2f();
	
			newSVector2.x = vectorA.x;
			newSVector2.y = vectorA.y;
	
			return newSVector2;
		}
	
		public static float Len(Vector2f vectorA) {
			return MathUtils.Sqrt(vectorA.x * vectorA.x + vectorA.y * vectorA.y);
		}
	
		public static float Len2(Vector2f vectorA) {
			return vectorA.x * vectorA.x + vectorA.y * vectorA.y;
		}
	
		public static Vector2f Set(Vector2f vectorA, Vector2f vectorB) {
			vectorA.x = vectorB.x;
			vectorA.y = vectorB.y;
			return vectorA;
		}
	
		public static Vector2f Set(Vector2f vectorA, float x_0, float y_1) {
			vectorA.x = x_0;
			vectorA.y = y_1;
			return vectorA;
		}
	
		public static Vector2f Sub(Vector2f vectorA, Vector2f vectorB) {
			vectorA.x -= vectorB.x;
			vectorA.y -= vectorB.y;
			return vectorA;
		}
	
		public Vector2f Nor(float n) {
			return new Vector2f((x == 0) ? (float) (0) : (float) (x / n), (y == 0) ? (float) (0) : (float) (y / n));
		}
	
		public static Vector2f Nor(Vector2f vectorA) {
			float len = Len(vectorA);
			if (len != 0) {
				vectorA.x /= len;
				vectorA.y /= len;
			}
			return vectorA;
		}
	
		public static Vector2f Add(Vector2f vectorA, Vector2f vectorB) {
			vectorA.x += vectorB.x;
			vectorA.y += vectorB.y;
			return vectorA;
		}
	
		public static Vector2f Add(Vector2f vectorA, float x_0, float y_1) {
			vectorA.x += x_0;
			vectorA.y += y_1;
			return vectorA;
		}
	
		public Vector2f Mul(Vector2f vectorA) {
			return new Vector2f(x * vectorA.x, y * vectorA.y);
		}
	
		public static Vector2f Mul(Vector2f vectorA, float scalar) {
			vectorA.x *= scalar;
			vectorA.y *= scalar;
			return vectorA;
		}
	
		public static float Dst(Vector2f vectorA, Vector2f vectorB) {
			float x_d = vectorB.x - vectorA.x;
			float y_d = vectorB.y - vectorA.y;
			return MathUtils.Sqrt(x_d * x_d + y_d * y_d);
		}
	
		public static float Dst(Vector2f vectorA, float x_0, float y_1) {
			float x_d = x_0 - vectorA.x;
			float y_d = y_1 - vectorA.y;
			return MathUtils.Sqrt(x_d * x_d + y_d * y_d);
		}
	
		public static float Dst2(Vector2f vectorA, Vector2f vectorB) {
			float x_d = vectorB.x - vectorA.x;
			float y_d = vectorB.y - vectorA.y;
			return x_d * x_d + y_d * y_d;
		}
	
		public static Vector2f Sub(Vector2f vectorA, float x_0, float y_1) {
			vectorA.x -= x_0;
			vectorA.y -= y_1;
			return vectorA;
		}
	
		public static float Crs(Vector2f vectorA, Vector2f vectorB) {
			return vectorA.x * vectorB.y - vectorA.y * vectorB.x;
		}
	
		public static float Crs(Vector2f vectorA, float x_0, float y_1) {
			return vectorA.x * y_1 - vectorA.y * x_0;
		}
	
		public static float AngleTo(Vector2f vectorA) {
			float angle = MathUtils.Atan2(vectorA.y, vectorA.x)
					* MathUtils.RAD_TO_DEG;
			if (angle < 0) {
				angle += 360;
			}
			return angle;
		}
	
		public static Vector2f Rotate(Vector2f vectorA, float angle) {
			float rad = angle * MathUtils.DEG_TO_RAD;
			float cos = MathUtils.Cos(rad);
			float sin = MathUtils.Sin(rad);
	
			float newX = vectorA.x * cos - vectorA.y * sin;
			float newY = vectorA.x * sin + vectorA.y * cos;
	
			vectorA.x = newX;
			vectorA.y = newY;
	
			return vectorA;
		}
	
		public static Vector2f Lerp(Vector2f vectorA, Vector2f target, float alpha) {
			Vector2f r = Mul(vectorA, 1.0f - alpha);
			Add(r, Mul(Cpy(target), alpha));
			return r;
		}
	
		public static float Dst2(float x1, float y1, float x2, float y2) {
			float x_d = x2 - x1;
			float y_d = y2 - y1;
			return x_d * x_d + y_d * y_d;
		}
	
		public void SetZero() {
			x = 0.0f;
			y = 0.0f;
		}
	
		public Vector2f Set(float x_0, float y_1) {
			this.x = x_0;
			this.y = y_1;
			return this;
		}
	
		public Vector2f Set(Vector2f v) {
			this.x = v.x;
			this.y = v.y;
			return this;
		}
	
		public Vector2f Add(Vector2f v) {
			return new Vector2f(x + v.x, y + v.y);
		}
	
		public Vector2f Sub(Vector2f v) {
			return new Vector2f(x - v.x, y - v.y);
		}
	
		public Vector2f Negate() {
			return new Vector2f(-x, -y);
		}
	
		public Vector2f NegateLocal() {
			x = -x;
			y = -y;
			return this;
		}
	
		public Vector2f AddLocal(Vector2f v) {
			x += v.x;
			y += v.y;
			return this;
		}
	
		public Vector2f AddLocal(float x_0, float y_1) {
			this.x += x_0;
			this.y += y_1;
			return this;
		}
	
		public Vector2f SubLocal(Vector2f v) {
			x -= v.x;
			y -= v.y;
			return this;
		}
	
		public Vector2f MulLocal(float a) {
			x *= a;
			y *= a;
			return this;
		}
	
		public float Length() {
			return MathUtils.Sqrt(x * x + y * y);
		}
	
		public float LengthSquared() {
			return (x * x + y * y);
		}
	
		public float Normalize() {
			float length = Length();
			if (length < MathUtils.EPSILON) {
				return 0f;
			}
	
			float invLength = 1.0f / length;
			x *= invLength;
			y *= invLength;
			return length;
		}
	
		public bool IsValid() {
			return x != System.Single.NaN && x != System.Single.NegativeInfinity
					&& x != System.Single.PositiveInfinity && y != System.Single.NaN
					&& y != System.Single.NegativeInfinity && y != System.Single.PositiveInfinity;
		}
	
		public Vector2f Abs() {
			return new Vector2f(MathUtils.Abs(x), MathUtils.Abs(y));
		}
	
		public void AbsLocal() {
			x = MathUtils.Abs(x);
			y = MathUtils.Abs(y);
		}
	
		public Vector2f Clone() {
			return new Vector2f(x, y);
		}
	
		public sealed override string ToString() {
			return "(" + x + "," + y + ")";
		}
	
		public static Vector2f Abs(Vector2f a) {
			return new Vector2f(MathUtils.Abs(a.x), MathUtils.Abs(a.y));
		}
	
		public static void AbsToOut(Vector2f a, Vector2f xout) {
			xout.x = MathUtils.Abs(a.x);
			xout.y = MathUtils.Abs(a.y);
		}
	
		public static float Dot(Vector2f a, Vector2f b) {
			return a.x * b.x + a.y * b.y;
		}
	
		public static float Cross(Vector2f a, Vector2f b) {
			return a.x * b.y - a.y * b.x;
		}
	
		public static Vector2f Cross(Vector2f a, float s) {
			return new Vector2f(s * a.y, -s * a.x);
		}
	
		public static void CrossToOut(Vector2f a, float s, Vector2f xout) {
			float tempy = -s * a.x;
			xout.x = s * a.y;
			xout.y = tempy;
		}
	
		public static Vector2f Cross(float s, Vector2f a) {
			return new Vector2f(-s * a.y, s * a.x);
		}
	
		public static void CrossToOut(float s, Vector2f a, Vector2f xout) {
			float tempY = s * a.x;
			xout.x = -s * a.y;
			xout.y = tempY;
		}
	
		public static void NegateToOut(Vector2f a, Vector2f xout) {
			xout.x = -a.x;
			xout.y = -a.y;
		}
	
		public static Vector2f Min(Vector2f a, Vector2f b) {
			return new Vector2f((a.x < b.x) ? a.x : b.x, (a.y < b.y) ? a.y : b.y);
		}
	
		public static Vector2f Max(Vector2f a, Vector2f b) {
			return new Vector2f((a.x > b.x) ? a.x : b.x, (a.y > b.y) ? a.y : b.y);
		}
	
		public static void MinToOut(Vector2f a, Vector2f b, Vector2f xout) {
			xout.x = (a.x < b.x) ? a.x : b.x;
			xout.y = (a.y < b.y) ? a.y : b.y;
		}
	
		public static void MaxToOut(Vector2f a, Vector2f b, Vector2f xout) {
			xout.x = (a.x > b.x) ? a.x : b.x;
			xout.y = (a.y > b.y) ? a.y : b.y;
		}
	
		public override int GetHashCode() {
            int prime = 31;
            int result = 1;
            result = (int)(prime * result + BitConverter.Int64BitsToDouble((long)x));
            result = (int)(prime * result + BitConverter.Int64BitsToDouble((long)y));
            return result;
		}

        public bool Equals(Vector2f v)
        {
            return this.x == v.x && this.y == v.y;
        }

		public bool Equals(float x, float y) {
			return this.x == x && this.y == y;
		}
	
		public override bool Equals(object obj) {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (GetType() != obj.GetType())
            {
                return false;
            }
			Vector2f other = (Vector2f) obj;
            if (x != other.x)
            {
                return false;
            }
            if (y != other.y)
            {
                return false;
            }
			return true;
		}
	}
}

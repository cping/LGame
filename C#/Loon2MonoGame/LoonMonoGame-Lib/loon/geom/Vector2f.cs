using loon.action.map;
using loon.utils;

namespace loon.geom
{
    public class Vector2f : XY
    {

        private static readonly Array<Vector2f> _VEC2_CACHE = new Array<Vector2f>();

        public readonly static Vector2f STATIC_ZERO = new Vector2f();

        public static Vector2f TMP()
        {
            Vector2f temp = _VEC2_CACHE.Pop();
            if (temp == null)
            {
                _VEC2_CACHE.Add(temp = new Vector2f(0, 0));
            }
            return temp;
        }

        public static Vector2f ZERO()
        {
            return new Vector2f(0);
        }

        public static Vector2f HALF()
        {
            return new Vector2f(0.5f, 0.5f);
        }

        public static Vector2f ONE()
        {
            return new Vector2f(1);
        }

        public static Vector2f AXIS_X()
        {
            return new Vector2f(1, 0);
        }

        public static Vector2f AXIS_Y()
        {
            return new Vector2f(0, 1);
        }

        public static float AngleTo(Vector2f pos)
        {
            float theta = MathUtils.ToDegrees(MathUtils.Atan2(pos.y, pos.x));
            if ((theta < -360) || (theta > 360))
            {
                theta %= 360;
            }
            if (theta < 0)
            {
                theta = 360 + theta;
            }
            return theta;
        }

        public static Vector2f At(float x, float y)
        {
            return new Vector2f(x, y);
        }

        public static Vector2f At(XY xy)
        {
            return new Vector2f(xy.GetX(), xy.GetY());
        }

        public static Vector2f FromAngle(float angle)
        {
            return new Vector2f(MathUtils.Cos(angle), MathUtils.Sin(angle));
        }

        public static Vector2f Sum(Vector2f a, Vector2f b)
        {
            Vector2f answer = new Vector2f(a);
            return answer.Add(b);
        }

        public static Vector2f Mult(Vector2f vector, float scalar)
        {
            Vector2f answer = new Vector2f(vector);
            return answer.Mul(scalar);
        }

        public static Vector2f Cpy(Vector2f pos)
        {
            Vector2f newSVector2 = new Vector2f
            {
                x = pos.x,
                y = pos.y
            };

            return newSVector2;
        }

        public static float Len(Vector2f pos)
        {
            return MathUtils.Sqrt(pos.x * pos.x + pos.y * pos.y);
        }

        public static float Len2(Vector2f pos)
        {
            return pos.x * pos.x + pos.y * pos.y;
        }

        public static Vector2f Set(Vector2f pos, Vector2f vectorB)
        {
            pos.x = vectorB.x;
            pos.y = vectorB.y;
            return pos;
        }

        public static Vector2f Set(Vector2f pos, float x, float y)
        {
            pos.x = x;
            pos.y = y;
            return pos;
        }

        public Vector2f SubNew(Vector2f vectorB)
        {
            return SubNew(this, vectorB);
        }

        public static Vector2f SubNew(Vector2f pos, Vector2f vectorB)
        {
            return At(pos.x - vectorB.x, pos.y - vectorB.y);
        }

        public static Vector2f Sub(Vector2f pos, Vector2f vectorB)
        {
            pos.x -= vectorB.x;
            pos.y -= vectorB.y;
            return pos;
        }

        public static Vector2f Nor(Vector2f pos)
        {
            float len = Len(pos);
            if (len != 0)
            {
                pos.x /= len;
                pos.y /= len;
            }
            return pos;
        }

        public static Vector2f AddNew(Vector2f pos, Vector2f vectorB)
        {
            return At(pos.x + vectorB.x, pos.y + vectorB.y);
        }

        public static Vector2f Add(Vector2f pos, Vector2f vectorB)
        {
            pos.x += vectorB.x;
            pos.y += vectorB.y;
            return pos;
        }

        public static Vector2f Add(Vector2f pos, float x, float y)
        {
            pos.x += x;
            pos.y += y;
            return pos;
        }

        public static Vector2f SmoothStep(Vector2f a, Vector2f b, float amount)
        {
            return new Vector2f(MathUtils.SmoothStep(a.x, b.x, amount), MathUtils.SmoothStep(a.y, b.y, amount));
        }

        public static Vector2f Transform(Vector2f value, Quaternion rotation)
        {
            return Transform(value, rotation, null);
        }

        public static Vector2f Transform(Vector2f value, Quaternion rotation, Vector2f result)
        {
            if (result == null)
            {
                result = new Vector2f();
            }
            Vector3f rot1 = new Vector3f(rotation.x + rotation.x, rotation.y + rotation.y, rotation.z + rotation.z);
            Vector3f rot2 = new Vector3f(rotation.x, rotation.x, rotation.w);
            Vector3f rot3 = new Vector3f(1, rotation.y, rotation.z);
            Vector3f rot4 = rot1.Mul(rot2);
            Vector3f rot5 = rot1.Mul(rot3);
            Vector2f v = new Vector2f
            {
                x = (value.x * (1f - rot5.y - rot5.z) + value.y * (rot4.y - rot4.z)),
                y = (value.x * (rot4.y + rot4.z) + value.y * (1f - rot4.x - rot5.z))
            };
            result.x = v.x;
            result.y = v.y;
            return result;
        }

        public static Vector2f Abs(Vector2f a)
        {
            return new Vector2f(MathUtils.Abs(a.x), MathUtils.Abs(a.y));
        }

        public static void AbsToOut(Vector2f a, Vector2f outs)
        {
            outs.x = MathUtils.Abs(a.x);
            outs.y = MathUtils.Abs(a.y);
        }

        public static float Dot(Vector2f a, Vector2f b)
        {
            return a.x * b.x + a.y * b.y;
        }

        public static float Cross(Vector2f a, Vector2f b)
        {
            return a.x * b.y - a.y * b.x;
        }

        public static Vector2f Cross(Vector2f a, float s)
        {
            return new Vector2f(s * a.y, -s * a.x);
        }

        public static void CrossToOut(Vector2f a, float s, Vector2f outs)
        {
            float tempy = -s * a.x;
            outs.x = s * a.y;
            outs.y = tempy;
        }

        public static Vector2f Cross(float s, Vector2f a)
        {
            return new Vector2f(-s * a.y, s * a.x);
        }

        public static void CrossToOut(float s, Vector2f a, Vector2f outs)
        {
            float tempY = s * a.x;
            outs.x = -s * a.y;
            outs.y = tempY;
        }

        public static void NegateToOut(Vector2f a, Vector2f outs)
        {
            outs.x = -a.x;
            outs.y = -a.y;
        }

        public static Vector2f Min(Vector2f a, Vector2f b)
        {
            return new Vector2f(a.x < b.x ? a.x : b.x, a.y < b.y ? a.y : b.y);
        }

        public static Vector2f Max(Vector2f a, Vector2f b)
        {
            return new Vector2f(a.x > b.x ? a.x : b.x, a.y > b.y ? a.y : b.y);
        }

        public static void MinToOut(Vector2f a, Vector2f b, Vector2f outs)
        {
            outs.x = a.x < b.x ? a.x : b.x;
            outs.y = a.y < b.y ? a.y : b.y;
        }

        public static void MaxToOut(Vector2f a, Vector2f b, Vector2f outs)
        {
            outs.x = a.x > b.x ? a.x : b.x;
            outs.y = a.y > b.y ? a.y : b.y;
        }

        public static Vector2f Mul(Vector2f pos, float scalar)
        {
            pos.x *= scalar;
            pos.y *= scalar;
            return pos;
        }

        public static Vector2f Direction(Vector2f v1, Vector2f v2)
        {
            Vector2f vector = v2.Sub(v1);
            vector.NormalizeSelf();
            return vector;
        }

        public static float Dst(Vector2f pos, Vector2f vectorB)
        {
            float x_d = vectorB.x - pos.x;
            float y_d = vectorB.y - pos.y;
            return MathUtils.Sqrt(x_d * x_d + y_d * y_d);
        }

        public static float Dst(Vector2f pos, float x, float y)
        {
            float x_d = x - pos.x;
            float y_d = y - pos.y;
            return MathUtils.Sqrt(x_d * x_d + y_d * y_d);
        }

        public static float Dst2(Vector2f pos, Vector2f vectorB)
        {
            float x_d = vectorB.x - pos.x;
            float y_d = vectorB.y - pos.y;
            return x_d * x_d + y_d * y_d;
        }

        public static Vector2f Sub(Vector2f pos, float x, float y)
        {
            pos.x -= x;
            pos.y -= y;
            return pos;
        }

        public static float Crs(Vector2f pos, Vector2f vectorB)
        {
            return pos.x * vectorB.y - pos.y * vectorB.x;
        }

        public static float Crs(Vector2f pos, float x, float y)
        {
            return pos.x * y - pos.y * x;
        }

        public static Vector2f Rotate(Vector2f pos, Vector2f origin, float angle)
        {
            float rad = MathUtils.ToRadians(angle);

            Vector2f newVector = new Vector2f();

            pos.x += -origin.x;
            pos.y += -origin.y;
            newVector.x = (pos.x * MathUtils.Cos(rad) - (pos.y * MathUtils.Sin(rad)));
            newVector.y = (MathUtils.Sin(rad * pos.x) + (MathUtils.Cos(rad * pos.y)));

            newVector.x += origin.x;
            newVector.y += origin.y;

            return newVector;
        }

        public static Vector2f Rotate(Vector2f pos, float angle)
        {
            float rad = MathUtils.ToRadians(angle);
            float Cos = MathUtils.Cos(rad);
            float Sin = MathUtils.Sin(rad);

            float newX = pos.x * Cos - pos.y * Sin;
            float newY = pos.x * Sin + pos.y * Cos;

            pos.x = newX;
            pos.y = newY;

            return pos;
        }

        public static Vector2f Lerp(Vector2f pos, Vector2f target, float alpha)
        {
            Vector2f r = Mul(pos, 1.0f - alpha);
            Add(r, Mul(Cpy(target), alpha));
            return r;
        }

        public static float Dst2(float x1, float y1, float x2, float y2)
        {
            float x_d = x2 - x1;
            float y_d = y2 - y1;
            return x_d * x_d + y_d * y_d;
        }

        public static Vector2f Polar(float len, float angle)
        {
            return new Vector2f(len * MathUtils.Cos(angle / MathUtils.DEG_TO_RAD),
                    len * MathUtils.Sin(angle / MathUtils.DEG_TO_RAD));
        }

        public float x;

        public float y;

        public Vector2f() : this(0, 0)
        {

        }

        public Vector2f(float x, float y)
        {
            this.x = x;
            this.y = y;
        }

        public Vector2f(XY v)
        {
            Set(v);
        }

        public Vector2f Cpy()
        {
            return new Vector2f(this);
        }

        public float Length()
        {
            return MathUtils.Sqrt(x * x + y * y);
        }

        public float LengthSquared()
        {
            return (x * x + y * y);
        }

        public float Len()
        {
            return Length();
        }

        public float Len2()
        {
            return LengthSquared();
        }

        public Vector2f NormalizeSelf()
        {
            float l = Length();
            if (l == 0 || l == 1)
            {
                return this;
            }
            return Set(x / l, y / l);
        }

        public Vector2f NormalizeNew()
        {
            return Nor(Len());
        }

        public Vector2f NorSelf()
        {
            return NormalizeSelf();
        }

        public Vector2f Nor()
        {
            return NormalizeNew();
        }

        public Vector2f Nor(float n)
        {
            return new Vector2f(this.x == 0 ? 0 : this.x / n, this.y == 0 ? 0 : this.y / n);
        }

        public Vector2f Mul(float s)
        {
            return new Vector2f(this.x * s, this.y * s);
        }

        public Vector2f Mul(float sx, float sy)
        {
            return new Vector2f(this.x * sx, this.y * sy);
        }

        public Vector2f MulSelf(float scale)
        {
            return MulSelf(scale);
        }

        public Vector2f MulSelf(float sx, float sy)
        {
            this.x *= sx;
            this.y *= sy;
            return this;
        }

        public Vector2f MulSelf(Affine2f mat)
        {
            float nx = this.x * mat.m00 + this.y * mat.m01 + mat.tx;
            float ny = this.x * mat.m10 + this.y * mat.m11 + mat.ty;
            this.x = nx;
            this.y = ny;
            return this;
        }

        public Vector2f MulSelf(Matrix3 mat)
        {
            float nx = this.x * mat.val[0] + this.y * mat.val[3] + mat.val[6];
            float ny = this.x * mat.val[1] + this.y * mat.val[4] + mat.val[7];
            this.x = nx;
            this.y = ny;
            return this;
        }

        public Vector2f SmoothStep(Vector2f v, float amount)
        {
            return SmoothStep(this, v, amount);
        }

        public Vector2f Sub(float x, float y)
        {
            return new Vector2f(this.x - x, this.y - y);
        }

        public Vector2f Div()
        {
            return new Vector2f(x / 2, y / 2);
        }

        public Vector2f Div(float v)
        {
            return new Vector2f(x / v, y / v);
        }

        public Vector2f Div(Vector2f v)
        {
            return new Vector2f(x / v.x, y / v.y);
        }

        public Vector2f Add(float x, float y)
        {
            return new Vector2f(this.x + x, this.y + y);
        }

        public Vector2f AddSelfX(float x)
        {
            this.x += x;
            return this;
        }

        public Vector2f AddSelfY(float y)
        {
            this.y += y;
            return this;
        }

        public float Dot(Vector2f v)
        {
            return x * v.x + y * v.y;
        }

        public float Dst(Vector2f v)
        {
            float x_d = v.x - x;
            float y_d = v.y - y;
            return MathUtils.Sqrt(x_d * x_d + y_d * y_d);
        }

        public float Dst(float x, float y)
        {
            float x_d = x - this.x;
            float y_d = y - this.y;
            return MathUtils.Sqrt(x_d * x_d + y_d * y_d);
        }

        public float Dst2(Vector2f v)
        {
            float x_d = v.x - x;
            float y_d = v.y - y;
            return x_d * x_d + y_d * y_d;
        }

        public float Dst2(float x, float y)
        {
            float x_d = x - this.x;
            float y_d = y - this.y;
            return x_d * x_d + y_d * y_d;
        }

        public Vector2f Tmp()
        {
            return TMP().Set(this);
        }

        public float Crs(Vector2f v)
        {
            return this.x * v.y - this.y * v.x;
        }

        public float Crs(float x, float y)
        {
            return this.x * y - this.y * x;
        }

        public float GetAngle()
        {
            return AngleTo(this);
        }

        public float Angle()
        {
            return GetAngle();
        }

        public float AngleRad(Vector2f v)
        {
            if (null == v)
            {
                return 0f;
            }
            return MathUtils.Atan2(v.Crs(this), v.Dot(this));
        }

        public float AngleDeg(Vector2f v)
        {
            if (null == v)
            {
                return GetAngle();
            }
            float theta = MathUtils.ToDegrees(MathUtils.Atan2(v.Crs(this), v.Dot(this)));
            if ((theta < -360) || (theta > 360))
            {
                theta %= 360;
            }
            if (theta < 0)
            {
                theta = 360 + theta;
            }
            return theta;
        }

        public int Angle(Vector2f v)
        {
            int dx = v.X() - X();
            int dy = v.Y() - Y();
            int adx = MathUtils.Abs(dx);
            int ady = MathUtils.Abs(dy);
            if ((dy == 0) && (dx == 0))
            {
                return 0;
            }
            if ((dy == 0) && (dx > 0))
            {
                return 0;
            }
            if ((dy == 0) && (dx < 0))
            {
                return 180;
            }
            if ((dy > 0) && (dx == 0))
            {
                return 90;
            }
            if ((dy < 0) && (dx == 0))
            {
                return 270;
            }
            float rwinkel = MathUtils.Atan(ady / adx);
            float dwinkel = 0.0f;
            if ((dx > 0) && (dy > 0))
            {
                dwinkel = MathUtils.ToDegrees(rwinkel);
            }
            else if ((dx < 0) && (dy > 0))
            {
                dwinkel = (180.0f - MathUtils.ToDegrees(rwinkel));
            }
            else if ((dx > 0) && (dy < 0))
            {
                dwinkel = (360.0f - MathUtils.ToDegrees(rwinkel));
            }
            else if ((dx < 0) && (dy < 0))
            {
                dwinkel = (180.0f + MathUtils.ToDegrees(rwinkel));
            }
            int iwinkel = (int)dwinkel;
            if (iwinkel == 360)
            {
                iwinkel = 0;
            }
            return iwinkel;
        }

        public Vector2f Rotate(float angle)
        {
            return Cpy().RotateSelf(angle);
        }

        public Vector2f RotateX(float angle)
        {
            return Cpy().RotateSelfX(angle);
        }

        public Vector2f RotateY(float angle)
        {
            return Cpy().RotateSelfY(angle);
        }

        public Vector2f Rotate(float cx, float cy, float angle)
        {
            return Cpy().RotateSelf(cx, cy, angle);
        }

        public Vector2f RotateSelf(float cx, float cy, float angle)
        {
            if (angle != 0)
            {

                float rad = MathUtils.ToRadians(angle);
                float Cos = MathUtils.Cos(rad);
                float Sin = MathUtils.Sin(rad);

                float nx = cx + (this.x - cx) * MathUtils.Cos(rad) - (this.y - cy) * Sin;
                float ny = cy + (this.x - cx) * MathUtils.Sin(rad) + (this.y - cy) * Cos;

                return Set(nx, ny);
            }
            return this;
        }

        public Vector2f RotateSelf(float angle)
        {
            if (angle != 0)
            {
                float rad = MathUtils.ToRadians(angle);
                float Cos = MathUtils.Cos(rad);
                float Sin = MathUtils.Sin(rad);

                float newX = this.x * Cos - this.y * Sin;
                float newY = this.x * Sin + this.y * Cos;

                this.x = newX;
                this.y = newY;
            }
            return this;
        }

        public Vector2f RotateSelfX(float angle)
        {
            if (angle != 0)
            {
                float rad = MathUtils.ToRadians(angle);
                float Cos = MathUtils.Cos(rad);
                float Sin = MathUtils.Sin(rad);
                this.x = this.x * Cos - this.y * Sin;
            }
            return this;
        }

        public Vector2f RotateSelfY(float angle)
        {
            if (angle != 0)
            {
                float rad = MathUtils.ToRadians(angle);
                float Cos = MathUtils.Cos(rad);
                float Sin = MathUtils.Sin(rad);
                this.y = this.x * Sin + this.y * Cos;
            }
            return this;
        }

        public Vector2f(float value) : this(value, value)
        {

        }

        public Vector2f(float[] coords)
        {
            x = coords[0];
            y = coords[1];
        }

        public Vector2f Move_45D_up()
        {
            return Move_45D_up(1);
        }

        public Vector2f Move_45D_up(int multiples)
        {
            return Move_multiples(Field2D.UP, multiples);
        }

        public Vector2f Move_45D_left()
        {
            return Move_45D_left(1);
        }

        public Vector2f Move_45D_left(int multiples)
        {
            return Move_multiples(Field2D.LEFT, multiples);
        }

        public Vector2f Move_45D_right()
        {
            return Move_45D_right(1);
        }

        public Vector2f Move_45D_right(int multiples)
        {
            return Move_multiples(Field2D.RIGHT, multiples);
        }

        public Vector2f Move_45D_down()
        {
            return Move_45D_down(1);
        }

        public Vector2f Move_45D_down(int multiples)
        {
            return Move_multiples(Field2D.DOWN, multiples);
        }

        public Vector2f Move_up()
        {
            return Move_up(1);
        }

        public Vector2f Move_up(int multiples)
        {
            return Move_multiples(Field2D.TUP, multiples);
        }

        public Vector2f Move_left()
        {
            return Move_left(1);
        }

        public Vector2f Move_left(int multiples)
        {
            return Move_multiples(Field2D.TLEFT, multiples);
        }

        public Vector2f Move_right()
        {
            return Move_right(1);
        }

        public Vector2f Move_right(int multiples)
        {
            return Move_multiples(Field2D.TRIGHT, multiples);
        }

        public Vector2f Move_down()
        {
            return Move_down(1);
        }

        public Vector2f Move_down(int multiples)
        {
            return Move_multiples(Field2D.TDOWN, multiples);
        }

        public Vector2f Up(float v)
        {
            return Cpy().Set(this.x, this.y - v);
        }

        public Vector2f Down(float v)
        {
            return Cpy().Set(this.x, this.y + v);
        }

        public Vector2f Left(float v)
        {
            return Cpy().Set(this.x - v, this.y);
        }

        public Vector2f Right(float v)
        {
            return Cpy().Set(this.x + v, this.y);
        }

        public Vector2f Translate(float dx, float dy)
        {
            return Cpy().Set(this.x + dx, this.y + dy);
        }

        public Vector2f Up()
        {
            return Up(1f);
        }

        public Vector2f Down()
        {
            return Down(1f);
        }

        public Vector2f Left()
        {
            return Left(1f);
        }

        public Vector2f Right()
        {
            return Right(1f);
        }

        public Vector2f TranslateSelf(float dx, float dy)
        {
            return Move(dx, dy);
        }

        public Vector2f Move(float dx, float dy)
        {
            this.x += dx;
            this.y += dy;
            return this;
        }

        public Vector2f Move(Vector2f pos)
        {
            this.x += pos.x;
            this.y += pos.y;
            return this;
        }

        public Vector2f Move_multiples(int direction, int multiples)
        {
            if (multiples <= 0)
            {
                multiples = 1;
            }
            Vector2f v = Field2D.GetDirection(direction);
            return Move(v.X() * multiples, v.Y() * multiples);
        }

        public Vector2f MoveX(float x)
        {
            this.x += x;
            return this;
        }

        public Vector2f MoveY(float y)
        {
            this.y += y;
            return this;
        }

        public Vector2f MoveByAngle(int degAngle, float distance)
        {
            if (distance == 0)
            {
                return this;
            }
            float Angle = MathUtils.ToRadians(degAngle);
            float dX = (MathUtils.Cos(Angle) * distance);
            float dY = (-MathUtils.Sin(Angle) * distance);
            int idX = MathUtils.Round(dX);
            int idY = MathUtils.Round(dY);
            return Move(idX, idY);
        }

        public Vector2f Move(float distance)
        {
            float angle = MathUtils.ToRadians(GetAngle());
            int x = MathUtils.Round(GetX() + MathUtils.Cos(angle) * distance);
            int y = MathUtils.Round(GetY() + MathUtils.Sin(angle) * distance);
            return SetLocation(x, y);
        }

        public bool NearlyCompare(Vector2f v, int range)
        {
            int dX = MathUtils.Abs(X() - v.X());
            int dY = MathUtils.Abs(Y() - v.Y());
            return (dX <= range) && (dY <= range);
        }

        public float[] GetCoords()
        {
            return (new float[] { x, y });
        }

        public Vector2f SetLocation(float x, float y)
        {
            return Set(x, y);
        }

        public Vector2f SetX(float x)
        {
            this.x = x;
            return this;
        }

        public Vector2f SetY(float y)
        {
            this.y = y;
            return this;
        }


        public float GetX()
        {
            return x;
        }


        public float GetY()
        {
            return y;
        }

        public int X()
        {
            return (int)x;
        }

        public int Y()
        {
            return (int)y;
        }

        public Vector2f Reverse()
        {
            x = -x;
            y = -y;
            return this;
        }

        public Vector2f Mul(Vector2f pos)
        {
            return new Vector2f(x * pos.x, y * pos.y);
        }

        public void SetZero()
        {
            x = 0f;
            y = 0f;
        }

        public Vector2f Set(float v)
        {
            return Set(v, v);
        }

        public Vector2f Set(float x, float y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        public Vector2f Set(XY v)
        {
            this.x = v.GetX();
            this.y = v.GetY();
            return this;
        }

        public Vector2f Set(Vector2f v)
        {
            this.x = v.x;
            this.y = v.y;
            return this;
        }

        public Vector2f Add(float v)
        {
            return new Vector2f(x + v, y + v);
        }

        public Vector2f Add(Vector2f v)
        {
            return new Vector2f(x + v.x, y + v.y);
        }

        public Vector2f Sub(Vector2f v)
        {
            return new Vector2f(x - v.x, y - v.y);
        }

        public Vector2f Negate()
        {
            return new Vector2f(-x, -y);
        }

        public Vector2f NegateLocal()
        {
            x = -x;
            y = -y;
            return this;
        }

        public Vector2f SubLocal(Vector2f v)
        {
            x -= v.x;
            y -= v.y;
            return this;
        }

        public Vector2f MulLocal(float a)
        {
            x *= a;
            y *= a;
            return this;
        }

        public Vector2f SetLength(float len)
        {
            len *= len;
            float oldLength = LengthSquared();
            return (oldLength == 0 || oldLength == len) ? this : ScaleSelf(MathUtils.Sqrt(len / oldLength));
        }

        public Vector2f SetAngle(float radians)
        {
            this.Set(Length(), 0f);
            this.RotateSelf(radians);
            return this;
        }

        public float Normalize()
        {
            float length = Length();
            if (length < MathUtils.EPSILON)
            {
                return 0f;
            }
            float invLength = 1.0f / length;
            x *= invLength;
            y *= invLength;
            return length;
        }

        public Vector2f Lerp(Vector2f target, float alpha)
        {
            Vector2f r = this.Mul(1f - alpha);
            r.Add(target.Tmp().Mul(alpha));
            return r;
        }

        public Vector2f LerpSelf(Vector2f target, float alpha)
        {
            float oneMinusAlpha = 1f - alpha;
            float x = (this.x * oneMinusAlpha) + (target.x * alpha);
            float y = (this.y * oneMinusAlpha) + (target.y * alpha);
            return Set(x, y);
        }

        public Vector2f LerpSelf(float x, float y, float alpha)
        {
            this.x += alpha * (x - this.x);
            this.y += alpha * (y - this.y);
            return this;
        }

        public Vector2f Lerp(float x, float y, float alpha)
        {
            return Cpy().LerpSelf(x, y, alpha);
        }

        public Vector2f Abs()
        {
            return new Vector2f(MathUtils.Abs(x), MathUtils.Abs(y));
        }

        public void AbsLocal()
        {
            x = MathUtils.Abs(x);
            y = MathUtils.Abs(y);
        }

        public Vector2f Random()
        {
            this.x = MathUtils.Random(0f, LSystem.viewSize.GetWidth());
            this.y = MathUtils.Random(0f, LSystem.viewSize.GetHeight());
            return this;
        }


        public override int GetHashCode()
        {
            uint prime = 31;
            uint result = 1;
            result = prime * result + NumberUtils.FloatToIntBits(x);
            result = prime * result + NumberUtils.FloatToIntBits(y);
            return (int)result;
        }

        public bool Equals(float x, float y)
        {
            return this.x == x && this.y == y;
        }


        public override bool Equals(object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (GetType() != obj.GetType())
                return false;
            Vector2f other = (Vector2f)obj;
            if (NumberUtils.FloatToIntBits(x) != NumberUtils.FloatToIntBits(other.x))
                return false;
            if (NumberUtils.FloatToIntBits(y) != NumberUtils.FloatToIntBits(other.y))
                return false;
            return true;
        }

        public bool EpsilonEquals(Vector2f other, float epsilon)
        {
            if (other == null)
                return false;
            if (MathUtils.Abs(other.x - x) > epsilon)
                return false;
            if (MathUtils.Abs(other.y - y) > epsilon)
                return false;
            return true;
        }

        public bool EpsilonEquals(float x, float y, float epsilon)
        {
            if (MathUtils.Abs(x - this.x) > epsilon)
                return false;
            if (MathUtils.Abs(y - this.y) > epsilon)
                return false;
            return true;
        }

        public bool IsUnit()
        {
            return IsUnit(0.000000001f);
        }

        public bool IsUnit(float margin)
        {
            return MathUtils.Abs(Len2() - 1f) < margin;
        }

        public bool IsZero()
        {
            return x == 0 && y == 0;
        }

        public bool IsZero(float margin)
        {
            return Len2() < margin;
        }

        public bool IsOnLine(Vector2f other)
        {
            return MathUtils.IsZero(x * other.y - y * other.x);
        }

        public bool IsOnLine(Vector2f other, float epsilon)
        {
            return MathUtils.IsZero(x * other.y - y * other.x, epsilon);
        }

        public bool IsCollinear(Vector2f other, float epsilon)
        {
            return IsOnLine(other, epsilon) && Dot(other) > 0f;
        }

        public bool IsCollinear(Vector2f other)
        {
            return IsOnLine(other) && Dot(other) > 0f;
        }

        public bool IsCollinearOpposite(Vector2f other, float epsilon)
        {
            return IsOnLine(other, epsilon) && Dot(other) < 0f;
        }

        public bool IsCollinearOpposite(Vector2f other)
        {
            return IsOnLine(other) && Dot(other) < 0f;
        }

        public bool IsPerpendicular(Vector2f vector)
        {
            return MathUtils.IsZero(Dot(vector));
        }

        public bool IsPerpendicular(Vector2f vector, float epsilon)
        {
            return MathUtils.IsZero(Dot(vector), epsilon);
        }

        public bool HasSameDirection(Vector2f vector)
        {
            return Dot(vector) > 0;
        }

        public bool HasOppositeDirection(Vector2f vector)
        {
            return Dot(vector) < 0;
        }

        public static string PointToString(float x, float y)
        {
            return MathUtils.ToString(x) + "," + MathUtils.ToString(y);
        }

        public Vector2f Inverse()
        {
            return new Vector2f(-this.x, -this.y);
        }

        public Vector2f InverseSelf()
        {
            this.x = -this.x;
            this.y = -this.y;
            return this;
        }

        public Vector2f AddSelf(Vector2f v)
        {
            this.x += v.x;
            this.y += v.y;
            return this;
        }

        public Vector2f AddSelf(float x, float y)
        {
            this.x += x;
            this.y += y;
            return this;
        }

        public Vector2f Subtract(float x, float y)
        {
            return Add(-x, -y);
        }

        public Vector2f NegateSelf()
        {
            return Set(-x, -y);
        }

        public float Zcross(float zx, float zy)
        {
            return (this.x * zy) - (this.y * zx);
        }

        public float Zcross(Vector2f v)
        {
            return (this.x * v.y) - (this.y * v.x);
        }

        public float Dot(float x, float y)
        {
            return this.x * x + this.y * y;
        }

        public float Distance(Vector2f v)
        {
            return MathUtils.Sqrt(DistanceSquared(v));
        }

        public float DistanceSquared(Vector2f v)
        {
            return (v.x - x) * (v.x - x) + (v.y - y) * (v.y - y);
        }

        public Vector2f Perpendicular()
        {
            return new Vector2f(y, -x);
        }

        public Vector2f PerpendicularSelf()
        {
            return Set(y, x);
        }

        public Vector2f ProjectSelf(Vector2f v)
        {
            return ScaleSelf(Dot(v) / v.LengthSquared());
        }

        public Vector2f ScaleSelf(float s)
        {
            return ScaleSelf(s, s);
        }

        public Vector2f ScaleSelf(float sx, float sy)
        {
            return Set(x * sx, y * sy);
        }

        public Vector2f Reflect(Vector2f axis)
        {
            return Project(axis).Scale(2).Subtract(this);
        }

        public Vector2f Subtract(Vector2f v)
        {
            return Add(-v.x, -v.y);
        }

        public Vector2f Scale(float s)
        {
            return Mul(s);
        }

        public Vector2f Scale(float sx, float sy)
        {
            return Mul(x * sx, y * sy);
        }

        public Vector2f Project(Vector2f v)
        {
            return Mul(Dot(v) / v.LengthSquared());
        }

        public Vector2f ReflectSelf(Vector2f axis)
        {
            return Set(Project(axis).ScaleSelf(2).SubtractSelf(this));
        }

        public Vector2f SubtractSelf(Vector2f v)
        {
            return SubtractSelf(v.x, v.y);
        }

        public Vector2f SubtractSelf(float x, float y)
        {
            return AddSelf(-x, -y);
        }

        public float Cross(Vector2f v)
        {
            return this.x * v.y - v.x * this.y;
        }

        public float LenManhattan()
        {
            return MathUtils.Abs(this.x) + MathUtils.Abs(this.y);
        }

        public float[] ToFloat()
        {
            return new float[] { x, y };
        }

        public int[] ToInt()
        {
            return new int[] { X(), Y() };
        }

        public string ToCSS()
        {
            return this.x + "px " + this.y + "px";
        }


        public override string ToString()
        {
            return "(" + x + "," + y + ")";
        }
    }
}

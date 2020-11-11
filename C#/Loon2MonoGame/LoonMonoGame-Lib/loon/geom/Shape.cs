
using loon.utils;

namespace loon.geom
{
    public abstract class Shape : IArray, XY
    {

        public float x;

        public float y;

        protected internal float rotation;

        protected internal float[] points;

        protected internal float[] center;

        protected internal float scaleX, scaleY;

        protected internal float maxX, maxY;

        protected internal float minX, minY;

        protected internal float boundingCircleRadius;

        protected internal bool pointsDirty;

        protected internal Triangle triangle;

        protected internal bool trianglesDirty;

        public Shape()
        {
            pointsDirty = true;
            scaleX = scaleY = 1f;
        }

        public virtual void SetLocation(float x, float y)
        {
            SetX(x);
            SetY(y);
        }


        public virtual float GetCenterX()
        {
            CheckPoints();
            return center[0];
        }

        public virtual void SetCenterX(float centerX)
        {
            if ((points == null) || (center == null))
            {
                CheckPoints();
            }

            float xDiff = centerX - GetCenterX();
            SetX(x + xDiff);
        }

        public virtual float GetCenterY()
        {
            CheckPoints();

            return center[1];
        }

        public virtual void SetCenterY(float centerY)
        {
            if ((points == null) || (center == null))
            {
                CheckPoints();
            }

            float yDiff = centerY - GetCenterY();
            SetY(y + yDiff);
        }

        public virtual void SetCenter(Vector2f pos)
        {
            SetCenterX(pos.x);
            SetCenterY(pos.y);
        }

        public virtual float GetMaxX()
        {
            CheckPoints();
            return maxX;
        }

        public virtual float GetMaxY()
        {
            CheckPoints();
            return maxY;
        }

        public virtual float GetMinX()
        {
            CheckPoints();
            return minX;
        }

        public virtual float GetMinY()
        {
            CheckPoints();
            return minY;
        }

        public virtual float GetBoundingCircleRadius()
        {
            CheckPoints();
            return boundingCircleRadius;
        }

        public virtual float[] GetCenter()
        {
            CheckPoints();
            return center;
        }

        public virtual float[] GetPoints()
        {
            CheckPoints();
            return points;
        }

        public virtual int GetPointCount()
        {
            CheckPoints();
            return points.Length / 2;
        }

        public virtual float[] GetPoint(int index)
        {
            CheckPoints();

            float[] result = new float[2];

            result[0] = points[index * 2];
            result[1] = points[index * 2 + 1];

            return result;
        }

        private float[] GetNormal(float[] start, float[] end)
        {
            float dx = start[0] - end[0];
            float dy = start[1] - end[1];
            float len = MathUtils.Sqrt((dx * dx) + (dy * dy));
            dx /= len;
            dy /= len;
            return new float[] { -dy, dx };
        }

        public virtual bool Closed()
        {
            return true;
        }

        public virtual float[] GetNormal(int index)
        {
            float[] current = GetPoint(index);
            float[] prev = GetPoint(index - 1 < 0 ? GetPointCount() - 1 : index - 1);
            float[] next = GetPoint(index + 1 >= GetPointCount() ? 0 : index + 1);

            float[] t1 = GetNormal(prev, current);
            float[] t2 = GetNormal(current, next);

            if ((index == 0) && (!Closed()))
            {
                return t2;
            }
            if ((index == GetPointCount() - 1) && (!Closed()))
            {
                return t1;
            }

            float tx = (t1[0] + t2[0]) / 2;
            float ty = (t1[1] + t2[1]) / 2;
            float len = MathUtils.Sqrt((tx * tx) + (ty * ty));
            return new float[] { tx / len, ty / len };
        }

        public virtual int IndexOf(float x, float y)
        {
            for (int i = 0; i < points.Length; i += 2)
            {
                if ((points[i] == x) && (points[i + 1] == y))
                {
                    return i / 2;
                }
            }

            return -1;
        }

        public virtual bool Contains(Shape other)
        {
            for (int i = 0; i < other.GetPointCount(); i++)
            {
                float[] pt = other.GetPoint(i);
                if (!Contains(pt[0], pt[1]))
                {
                    return false;
                }
            }
            return true;
        }

        public virtual bool Contains(float x, float y)
        {

            CheckPoints();
            if (points.Length == 0)
            {
                return false;
            }

            bool result = false;
            float xnew, ynew;
            float xold, yold;
            float x1, y1;
            float x2, y2;
            int npoints = points.Length;

            xold = points[npoints - 2];
            yold = points[npoints - 1];
            for (int i = 0; i < npoints; i += 2)
            {
                xnew = points[i];
                ynew = points[i + 1];
                if (xnew > xold)
                {
                    x1 = xold;
                    x2 = xnew;
                    y1 = yold;
                    y2 = ynew;
                }
                else
                {
                    x1 = xnew;
                    x2 = xold;
                    y1 = ynew;
                    y2 = yold;
                }
                if ((xnew < x) == (x <= xold) && (y - y1) * (x2 - x1) < (y2 - y1) * (x - x1))
                {
                    result = !result;
                }
                xold = xnew;
                yold = ynew;
            }

            return result;
        }

        public virtual bool Intersects(Shape shape)
        {
            if (shape == null)
            {
                return false;
            }

            CheckPoints();

            bool result = false;
            float[] points = GetPoints();
            float[] thatPoints = shape.GetPoints();
            int length = points.Length;
            int thatLength = thatPoints.Length;
            float unknownA;
            float unknownB;

            if (!Closed())
            {
                length -= 2;
            }
            if (!shape.Closed())
            {
                thatLength -= 2;
            }

            for (int i = 0; i < length; i += 2)
            {
                int iNext = i + 2;
                if (iNext >= points.Length)
                {
                    iNext = 0;
                }

                for (int j = 0; j < thatLength; j += 2)
                {
                    int jNext = j + 2;
                    if (jNext >= thatPoints.Length)
                    {
                        jNext = 0;
                    }

                    unknownA = (((points[iNext] - points[i]) * (float)(thatPoints[j + 1] - points[i + 1]))
                            - ((points[iNext + 1] - points[i + 1]) * (thatPoints[j] - points[i])))
                            / (((points[iNext + 1] - points[i + 1]) * (thatPoints[jNext] - thatPoints[j]))
                                    - ((points[iNext] - points[i]) * (thatPoints[jNext + 1] - thatPoints[j + 1])));
                    unknownB = (((thatPoints[jNext] - thatPoints[j]) * (float)(thatPoints[j + 1] - points[i + 1]))
                            - ((thatPoints[jNext + 1] - thatPoints[j + 1]) * (thatPoints[j] - points[i])))
                            / (((points[iNext + 1] - points[i + 1]) * (thatPoints[jNext] - thatPoints[j]))
                                    - ((points[iNext] - points[i]) * (thatPoints[jNext + 1] - thatPoints[j + 1])));

                    if (unknownA >= 0 && unknownA <= 1 && unknownB >= 0 && unknownB <= 1)
                    {
                        result = true;
                        break;
                    }
                }
                if (result)
                {
                    break;
                }
            }

            return result;
        }

        public virtual bool HasVertex(float x, float y)
        {
            if (points.Length == 0)
            {
                return false;
            }

            CheckPoints();

            for (int i = 0; i < points.Length; i += 2)
            {
                if ((points[i] == x) && (points[i + 1] == y))
                {
                    return true;
                }
            }

            return false;
        }

        protected internal virtual void FindCenter()
        {
            center = new float[] { 0, 0 };
            int length = points.Length;
            for (int i = 0; i < length; i += 2)
            {
                center[0] += points[i];
                center[1] += points[i + 1];
            }
            center[0] /= (length / 2);
            center[1] /= (length / 2);
        }


        protected virtual void CalculateRadius()
        {
            boundingCircleRadius = 0;

            for (int i = 0; i < points.Length; i += 2)
            {
                float temp = ((points[i] - center[0]) * (points[i] - center[0]))
                        + ((points[i + 1] - center[1]) * (points[i + 1] - center[1]));
                boundingCircleRadius = (boundingCircleRadius > temp) ? boundingCircleRadius : temp;
            }
            boundingCircleRadius = MathUtils.Sqrt(boundingCircleRadius);
        }

        protected virtual void CalculateTriangles()
        {
            if ((!trianglesDirty) && (triangle != null))
            {
                return;
            }
            if (points.Length >= 6)
            {
                triangle = new TriangleNeat();
                for (int i = 0; i < points.Length; i += 2)
                {
                    triangle.AddPolyPoint(points[i], points[i + 1]);
                }
                triangle.Triangulate();
            }

            trianglesDirty = false;
        }

        private void CallTransform(Matrix3 m)
        {
            if (points != null)
            {
                float[] result = new float[points.Length];
                m.Transform(points, 0, result, 0, points.Length / 2);
                this.points = result;
                this.CheckPoints();
            }
        }

        public virtual void SetScale(float s)
        {
            this.SetScale(s, s);
        }

        public virtual void SetScale(float sx, float sy)
        {
            if (scaleX != sx || scaleY != sy)
            {
                Matrix3 m = new Matrix3();
                m.Scale(scaleX = sx, scaleY = sy);
                this.CallTransform(m);
            }
        }

        public virtual float GetScaleX()
        {
            return scaleX;
        }

        public virtual float GetScaleY()
        {
            return scaleY;
        }

        public virtual void SetRotation(float r)
        {
            if (rotation != r)
            {
                this.CallTransform(Matrix3.CreateRotateTransform(rotation = (r / 180f * MathUtils.PI), this.center[0],
                        this.center[1]));
            }
        }

        public virtual void SetRotation(float r, float x, float y)
        {
            if (rotation != r)
            {
                this.CallTransform(Matrix3.CreateRotateTransform(rotation = (r / 180f * MathUtils.PI), x, y));
            }
        }

        public virtual float GetRotation()
        {
            return (rotation * 180f / MathUtils.PI);
        }

        public virtual void IncreaseTriangulation()
        {
            CheckPoints();
            CalculateTriangles();

            triangle = new TriangleOver(triangle);
        }

        public virtual Triangle GetTriangles()
        {
            CheckPoints();
            CalculateTriangles();
            return triangle;
        }

        public virtual void PreCache()
        {
            CheckPoints();
            GetTriangles();
        }

        protected internal virtual void CheckPoints()
        {
            lock (this)
            {
                if (pointsDirty)
                {
                    CreatePoints();
                    FindCenter();
                    CalculateRadius();
                    if (points == null)
                    {
                        return;
                    }
                    lock (points)
                    {
                        int size = points.Length;
                        if (size > 0)
                        {
                            maxX = points[0];
                            maxY = points[1];
                            minX = points[0];
                            minY = points[1];
                            for (int i = 0; i < size / 2; i++)
                            {
                                maxX = MathUtils.Max(points[i * 2], maxX);
                                maxY = MathUtils.Max(points[(i * 2) + 1], maxY);
                                minX = MathUtils.Min(points[i * 2], minX);
                                minY = MathUtils.Min(points[(i * 2) + 1], minY);
                            }
                        }
                        pointsDirty = false;
                        trianglesDirty = true;
                    }
                }
            }
        }


        public abstract Shape Transform(Matrix3 transform);

        protected abstract void CreatePoints();

        public virtual void Translate(int deltaX, int deltaY)
        {
            SetX(x + deltaX);
            SetY(y + deltaY);
        }

        public virtual int VertexCount()
        {
            return points.Length / 2;
        }

        public virtual Vector2f GetPosition()
        {
            return new Vector2f(GetX(), GetY());
        }

        public virtual Vector2f GetCenterPos()
        {
            return new Vector2f(GetCenterX(), GetCenterY());
        }


        public virtual void Clear()
        {
            points = new float[0];
            center = new float[0];
            x = 0;
            y = 0;
            rotation = 0;
            scaleX = 1f;
            scaleY = 1f;
            maxX = maxY = 0;
            minX = minY = 0;
            pointsDirty = true;
        }

        public virtual void SetX(float x)
        {
            if (x != this.x || x == 0)
            {
                float dx = x - this.x;
                this.x = x;
                if ((points == null) || (center == null))
                {
                    CheckPoints();
                }
                for (int i = 0; i < points.Length / 2; i++)
                {
                    points[i * 2] += dx;
                }
                center[0] += dx;
                maxX += dx;
                minX += dx;
                trianglesDirty = true;
            }
        }

        public virtual void SetY(float y)
        {
            if (y != this.y || y == 0)
            {
                float dy = y - this.y;
                this.y = y;
                if ((points == null) || (center == null))
                {
                    CheckPoints();
                }
                for (int i = 0; i < points.Length / 2; i++)
                {
                    points[(i * 2) + 1] += dy;
                }
                center[1] += dy;
                maxY += dy;
                minY += dy;
                trianglesDirty = true;
            }
        }

        public virtual void SetLocation(Vector2f loc)
        {
            SetX(loc.x);
            SetY(loc.y);
        }

        public virtual float GetX()
        {
            return this.x;
        }

        public virtual float GetY()
        {
            return this.y;
        }
        public virtual float Length()
        {
            return MathUtils.Sqrt(x * x + y * y);
        }

        public virtual bool IsEmpty()
        {
            return Size() == 0;
        }

        public virtual int Size()
        {
            return points == null ? 0 : points.Length;
        }

        public virtual float GetWidth()
        {
            return maxX - minX;
        }

        public virtual float GetHeight()
        {
            return maxY - minY;
        }

        public override int GetHashCode()
        {
            uint prime = 31;
            uint result = 17;
            result = prime * result + NumberUtils.FloatToIntBits(x);
            result = prime * result + NumberUtils.FloatToIntBits(y);
            result = prime * result + NumberUtils.FloatToIntBits(scaleX);
            result = prime * result + NumberUtils.FloatToIntBits(scaleY);
            for (int j = 0; j < points.Length; j++)
            {
                long val = NumberUtils.FloatToIntBits(this.points[j]);
                result += 31 * result + (uint)(val ^ (val >> 32));
            }
            return (int)result;
        }


        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("Shape");
            builder.Kv("pos", x + "," + y).Comma().Kv("scale", scaleX + "," + scaleY).Comma()
                    .Kv("points", "[" + StringUtils.Join(',', points) + "]").Comma()
                    .Kv("center", "[" + StringUtils.Join(',', center) + "]").Comma().Kv("rotation", rotation).Comma()
                    .Kv("minX", minX).Comma().Kv("minY", minY).Comma().Kv("maxX", maxX).Comma().Kv("maxY", maxY);
            return builder.ToString();
        }
    }
}

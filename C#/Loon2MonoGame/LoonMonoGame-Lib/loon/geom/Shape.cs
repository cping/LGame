
using loon.utils;

namespace loon.geom
{
    public abstract class Shape : IArray, XY
    {

        public float x;

        public float y;

        protected float rotation;

        protected float[] points;

        protected float[] center;

        protected float scaleX, scaleY;

        protected float maxX, maxY;

        protected float minX, minY;

        protected float boundingCircleRadius;

        protected bool pointsDirty;

        protected Triangle triangle;

        protected bool trianglesDirty;

        public Shape()
        {
            pointsDirty = true;
            scaleX = scaleY = 1f;
        }

        public void SetLocation(float x, float y)
        {
            SetX(x);
            SetY(y);
        }
        protected void FindCenter()
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
        protected void CalculateRadius()
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

        protected void CheckPoints()
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

        public void Translate(int deltaX, int deltaY)
        {
            SetX(x + deltaX);
            SetY(y + deltaY);
        }

        public int VertexCount()
        {
            return points.Length / 2;
        }

        public Vector2f GetPosition()
        {
            return new Vector2f(GetX(), GetY());
        }

        public Vector2f GetCenterPos()
        {
            return new Vector2f(GetCenterX(), GetCenterY());
        }
        public float GetCenterX()
        {
            CheckPoints();
            return center[0];
        }
        public float GetCenterY()
        {
            CheckPoints();

            return center[1];
        }


        public void Clear()
        {
            throw new System.NotImplementedException();
        }

        public void SetX(float x)
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

        public void SetY(float y)
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

        public void SetLocation(Vector2f loc)
        {
            SetX(loc.x);
            SetY(loc.y);
        }

        public float GetX()
        {
            return this.x;
        }

        public float GetY()
        {
            return this.y;
        }
        public float Length()
        {
            return MathUtils.Sqrt(x * x + y * y);
        }

        public bool IsEmpty()
        {
            throw new System.NotImplementedException();
        }

        public int Size()
        {
            throw new System.NotImplementedException();
        }
    }
}

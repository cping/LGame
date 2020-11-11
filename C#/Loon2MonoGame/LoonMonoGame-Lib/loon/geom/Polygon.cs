using java.lang;
using loon.utils;

namespace loon.geom
{
    public class Polygon : Shape, BoxSize
    {

        private bool allowDups = false;

        private bool closed = true;

        public Polygon(float[] points)
        {
            int length = points.Length;

            this.points = new float[length];
            maxX = -Float.MIN_VALUE_JAVA;
            maxY = -Float.MIN_VALUE_JAVA;
            minX = Float.MAX_VALUE_JAVA;
            minY = Float.MAX_VALUE_JAVA;
            x = Float.MAX_VALUE_JAVA;
            y = Float.MAX_VALUE_JAVA;

            for (int i = 0; i < length; i++)
            {
                this.points[i] = points[i];
                if (i % 2 == 0)
                {
                    if (points[i] > maxX)
                    {
                        maxX = points[i];
                    }
                    if (points[i] < minX)
                    {
                        minX = points[i];
                    }
                    if (points[i] < x)
                    {
                        x = points[i];
                    }
                }
                else
                {
                    if (points[i] > maxY)
                    {
                        maxY = points[i];
                    }
                    if (points[i] < minY)
                    {
                        minY = points[i];
                    }
                    if (points[i] < y)
                    {
                        y = points[i];
                    }
                }
            }

            FindCenter();
            CalculateRadius();
            pointsDirty = true;
        }

        public Polygon()
        {
            points = new float[0];
            maxX = -Float.MIN_VALUE_JAVA;
            maxY = -Float.MIN_VALUE_JAVA;
            minX = Float.MAX_VALUE_JAVA;
            minY = Float.MAX_VALUE_JAVA;
        }

        public Polygon(float[] xpoints, float[] ypoints, int npoints)
        {
            if (npoints > xpoints.Length || npoints > ypoints.Length)
            {
                throw new LSysException("npoints > xpoints.length || " + "npoints > ypoints.length");
            }
            if (npoints < 0)
            {
                throw new LSysException("npoints < 0");
            }
            points = new float[0];
            maxX = -Float.MIN_VALUE_JAVA;
            maxY = -Float.MIN_VALUE_JAVA;
            minX = Float.MAX_VALUE_JAVA;
            minY = Float.MAX_VALUE_JAVA;
            for (int i = 0; i < npoints; i++)
            {
                AddPoint(xpoints[i], ypoints[i]);
            }
        }

        public Polygon(int[] xpoints, int[] ypoints, int npoints)
        {
            if (npoints > xpoints.Length || npoints > ypoints.Length)
            {
                throw new LSysException("npoints > xpoints.length || " + "npoints > ypoints.length");
            }
            if (npoints < 0)
            {
                throw new LSysException("npoints < 0");
            }
            points = new float[0];
            maxX = -Float.MIN_VALUE_JAVA;
            maxY = -Float.MIN_VALUE_JAVA;
            minX = Float.MAX_VALUE_JAVA;
            minY = Float.MAX_VALUE_JAVA;
            for (int i = 0; i < npoints; i++)
            {
                AddPoint(xpoints[i], ypoints[i]);
            }
        }

        public void SetAllowDuplicatePoints(bool allowDups)
        {
            this.allowDups = allowDups;
        }

        public void AddPoint(float x, float y)
        {
            if (HasVertex(x, y) && (!allowDups))
            {
                return;
            }
            int size = points.Length;
            TArray<float> tempPoints = new TArray<float>();
            for (int i = 0; i < size; i++)
            {
                tempPoints.Add(points[i]);
            }
            tempPoints.Add(x);
            tempPoints.Add(y);
            int length = tempPoints.size;
            this.points = new float[length];
            for (int i = 0; i < length; i++)
            {
                points[i] = tempPoints.Get(i);
            }
            if (x > maxX)
            {
                maxX = x;
            }
            if (y > maxY)
            {
                maxY = y;
            }
            if (x < minX)
            {
                minX = x;
            }
            if (y < minY)
            {
                minY = y;
            }
            FindCenter();
            CalculateRadius();

            pointsDirty = true;
        }

        public override Shape Transform(Matrix3 transform)
        {
            CheckPoints();

            Polygon resultPolygon = new Polygon();

            float[] result = new float[points.Length];
            transform.Transform(points, 0, result, 0, points.Length / 2);
            resultPolygon.points = result;
            resultPolygon.FindCenter();
            resultPolygon.closed = closed;

            return resultPolygon;
        }


        public override void SetX(float x)
        {
            base.SetX(x);
            pointsDirty = false;
        }


        public override void SetY(float y)
        {
            base.SetY(y);
            pointsDirty = false;
        }

        public void AddVertex(float x, float y)
        {
            AddPoint(x, y);
        }

        public void AddVertex(Vector2f v)
        {
            AddVertex(v.x, v.y);
        }

        public TArray<Vector2f> GetVertices()
        {
            int size = points.Length;
            TArray<Vector2f> vertices = new TArray<Vector2f>();
            for (int i = 0; i < size; i += 2)
            {
                vertices.Add(new Vector2f(points[i], points[i + 1]));
            }
            return vertices;
        }


        protected override void CreatePoints()
        {

        }

        public override bool Closed()
        {
            return closed;
        }

        public void SetClosed(bool closed)
        {
            this.closed = closed;
        }

        public Polygon Cpy()
        {
            float[] copyPoints = new float[points.Length];
            JavaSystem.Arraycopy(points, 0, copyPoints, 0, copyPoints.Length);
            return new Polygon(copyPoints);
        }


        public void SetWidth(float w)
        {
            this.maxX = w;
        }


        public void SetHeight(float h)
        {
            this.maxY = h;
        }

        public RectBox GetBox()
        {
            TArray<Vector2f> v = GetVertices();
            float miX = this.minX;
            float miY = this.minY;
            float maX = this.maxX;
            float maY = this.maxY;
            for (int i = 0; i < v.size; i++)
            {
                Vector2f p = v.Get(i);
                miX = MathUtils.Min(miX, p.x);
                miY = MathUtils.Min(miY, p.y);
                maX = MathUtils.Max(maX, p.x);
                maY = MathUtils.Max(maY, p.y);
            }
            return new RectBox(miX, miY, maX - miX, maY - miY);
        }


        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("Polygon");
            builder.Kv("points", "[" + StringUtils.Join(',', points) + "]").Comma()
                    .Kv("center", "[" + StringUtils.Join(',', center) + "]").Comma().Kv("rotation", rotation).Comma()
                    .Kv("minX", minX).Comma().Kv("minY", minY).Comma().Kv("maxX", maxX).Comma().Kv("maxY", maxY);
            return builder.ToString();
        }
    }
}

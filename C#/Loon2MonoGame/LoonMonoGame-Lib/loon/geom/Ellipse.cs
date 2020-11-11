using java.lang;
using loon.utils;

namespace loon.geom
{
    public class Ellipse : Shape
    {

        protected const int DEFAULT_SEGMENT_MAX_COUNT = 50;

        private int segmentCount;

        private float radius1;

        private float radius2;

        private float _start = 0;

        private float _end = 359;

        public Ellipse(float centerPointX, float centerPointY, float radius1, float radius2)
        {
            this.Set(centerPointX, centerPointY, radius1, radius2);
        }

        public Ellipse(float centerPointX, float centerPointY, float radius1, float radius2, int segmentCount)
        {
            this.Set(centerPointX, centerPointY, radius1, radius2, segmentCount);
        }

        public Ellipse(float centerPointX, float centerPointY, float radius1, float radius2, float start, float end,
                int segmentCount)
        {
            _start = start;
            _end = end;
            Set(centerPointX, centerPointY, radius1, radius2, segmentCount);
        }

        public void Set(float centerPointX, float centerPointY, float radius1, float radius2)
        {
            Set(centerPointX, centerPointY, radius1, radius2, DEFAULT_SEGMENT_MAX_COUNT);
        }

        public void Set(float centerPointX, float centerPointY, float radius1, float radius2, int segmentCount)
        {
            this.x = centerPointX - radius1;
            this.y = centerPointY - radius2;
            this.radius1 = radius1;
            this.radius2 = radius2;
            this.segmentCount = segmentCount;
            CheckPoints();
        }

        public void SetRadii(float radius1, float radius2)
        {
            SetRadius1(radius1);
            SetRadius2(radius2);
        }

        public float GetRadius1()
        {
            return radius1;
        }

        public void SetRadius1(float radius1)
        {
            if (radius1 != this.radius1)
            {
                this.radius1 = radius1;
                pointsDirty = true;
            }
        }

        public float GetRadius2()
        {
            return radius2;
        }

        public void SetRadius2(float radius2)
        {
            if (radius2 != this.radius2)
            {
                this.radius2 = radius2;
                pointsDirty = true;
            }
        }

        protected override void CreatePoints()
        {
            TArray<float> tempPoints = new TArray<float>();

            maxX = -Float.MIN_VALUE_JAVA;
            maxY = -Float.MIN_VALUE_JAVA;
            minX = Float.MAX_VALUE_JAVA;
            minY = Float.MAX_VALUE_JAVA;

            float start = _start;
            float end = _end;

            float cx = x + radius1;
            float cy = y + radius2;

            int step = 360 / segmentCount;

            for (float a = start; a <= end + step; a += step)
            {
                float ang = a;
                if (ang > end)
                {
                    ang = end;
                }

                float newX = (cx + (MathUtils.Cos(MathUtils.ToRadians(ang)) * radius1));
                float newY = (cy + (MathUtils.Sin(MathUtils.ToRadians(ang)) * radius2));

                if (newX > maxX)
                {
                    maxX = newX;
                }
                if (newY > maxY)
                {
                    maxY = newY;
                }
                if (newX < minX)
                {
                    minX = newX;
                }
                if (newY < minY)
                {
                    minY = newY;
                }

                tempPoints.Add(newX);
                tempPoints.Add(newY);
            }
            points = new float[tempPoints.size];
            for (int i = 0; i < points.Length; i++)
            {
                points[i] = tempPoints.Get(i);
            }
        }

        protected internal override void FindCenter()
        {
            center = new float[2];
            center[0] = x + radius1;
            center[1] = y + radius2;
        }


        protected override void CalculateRadius()
        {
            boundingCircleRadius = MathUtils.Max(radius1, radius2);
        }


        public override int GetHashCode()
        {
            uint bits = NumberUtils.FloatToIntBits(GetX());
            bits += NumberUtils.FloatToIntBits(GetY()) * 37;
            bits += NumberUtils.FloatToIntBits(GetWidth()) * 43;
            bits += NumberUtils.FloatToIntBits(GetHeight()) * 47;
            return (int)(bits ^ ((bits >> 32)));
        }

        public float GetStart()
        {
            return _start;
        }

        public void SetStart(float start)
        {
            this._start = start;
        }

        public float GetEnd()
        {
            return _end;
        }

        public void SetEnd(float end)
        {
            this._end = end;
        }

        public float GetMinorRadius()
        {
            return MathUtils.Min(this.radius1, this.radius2) / 2f;
        }

        public float GetMajorRadius()
        {
            return MathUtils.Max(this.radius1, this.radius2) / 2f;
        }


        public override Shape Transform(Matrix3 transform)
        {
            CheckPoints();

            Polygon resultPolygon = new Polygon();

            float[] result = new float[points.Length];
            transform.Transform(points, 0, result, 0, points.Length / 2);
            resultPolygon.points = result;
            resultPolygon.CheckPoints();

            return resultPolygon;
        }

    }
}

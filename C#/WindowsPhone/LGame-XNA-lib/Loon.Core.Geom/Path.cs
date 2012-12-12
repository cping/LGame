namespace Loon.Core.Geom
{

    using System.Collections.Generic;
    using Loon.Utils;

    public class Path : Shape
    {

        private List<float[]> localPoints;

        private float cx;

        private float cy;

        private bool closed;

        private List<List<float[]>> holes;

        private List<float[]> hole;

        public Path(float sx, float sy)
        {
            if (holes == null)
            {
                holes = new List<List<float[]>>(10);
            }
            if (localPoints == null)
            {
                localPoints = new List<float[]>(10);
            }
            this.Set(sx, sy);
            this.type = Loon.Core.Geom.ShapeType.PATH_SHAPE;
        }

        public void Set(float sx, float sy)
        {
            CollectionUtils.Add(localPoints, new float[] { sx, sy });
            cx = sx;
            cy = sy;
            pointsDirty = true;
        }

        public void MoveTo(float sx, float sy)
        {
            hole = new List<float[]>();
            CollectionUtils.Add(holes, hole);
        }

        public void AddPath(Path p, float px, float py)
        {
            for (IEnumerator<float[]> it = p.localPoints.GetEnumerator(); it.MoveNext(); )
            {
                float[] pos = it.Current;
                if (hole != null)
                {
                    hole.Add(new float[] { px + pos[0], py + pos[1] });
                }
                else
                {
                    localPoints.Add(new float[] { px + pos[0], py + pos[1] });
                }
            }
            pointsDirty = true;
        }

        public void QuadTo(float x1, float y1, float x2, float y2)
        {
            if (hole != null)
            {
                hole.Add(new float[] { x1, y1, x2, y2 });
            }
            else
            {
                localPoints.Add(new float[] { x1, y1, x2, y2 });
            }
            cx = x2;
            cy = y2;
            pointsDirty = true;
        }

        public void Clear()
        {
            if (hole != null)
            {
                hole.Clear();
            }
            if (localPoints != null)
            {
                localPoints.Clear();
            }
            pointsDirty = true;
        }

        public void LineTo(float x, float y)
        {
            if (hole != null)
            {
                hole.Add(new float[] { x, y });
            }
            else
            {
                localPoints.Add(new float[] { x, y });
            }
            cx = x;
            cy = y;
            pointsDirty = true;
        }

        public void Close()
        {
            closed = true;
        }

        public void CurveTo(float x, float y, float cx1, float cy1, float cx2,
                float cy2)
        {
            CurveTo(x, y, cx1, cy1, cx2, cy2, 10);
        }

        public void CurveTo(float x, float y, float cx1, float cy1, float cx2,
                float cy2, int segments)
        {
            if ((cx == x) && (cy == y))
            {
                return;
            }
            Curve curve = new Curve(new Vector2f(cx, cy), new Vector2f(cx1, cy1),
                    new Vector2f(cx2, cy2), new Vector2f(x, y));
            float step = 1.0f / segments;

            for (int i = 1; i < segments + 1; i++)
            {
                float t = i * step;
                Vector2f p = curve.PointAt(t);
                if (hole != null)
                {
                    hole.Add(new float[] { p.x, p.y });
                }
                else
                {
                    localPoints.Add(new float[] { p.x, p.y });
                }
                cx = p.x;
                cy = p.y;
            }
            pointsDirty = true;
        }

        protected internal override void CreatePoints()
        {
            points = new float[localPoints.Count * 2];
            for (int i = 0; i < localPoints.Count; i++)
            {
                float[] p = (float[])localPoints[i];
                points[(i * 2)] = p[0];
                points[(i * 2) + 1] = p[1];
            }
        }

        public override Shape Transform(Matrix transform)
        {
            Path p = new Path(cx, cy);
            p.localPoints = Transform(localPoints, transform);
            for (int i = 0; i < holes.Count; i++)
            {
                CollectionUtils.Add(p.holes, Transform((List<float[]>)holes[i], transform));
            }
            p.closed = this.closed;
            return p;
        }

        private List<float[]> Transform(List<float[]> pts, Matrix t)
        {
            float[] ins0 = new float[pts.Count * 2];
            float[] xout = new float[pts.Count * 2];

            for (int i = 0; i < pts.Count; i++)
            {
                ins0[i * 2] = ((float[])pts[i])[0];
                ins0[(i * 2) + 1] = ((float[])pts[i])[1];
            }
            t.Transform(ins0, 0, xout, 0, pts.Count);
            List<float[]> outList = new List<float[]>();
            for (int i_0 = 0; i_0 < pts.Count; i_0++)
            {
                outList.Add(new float[] { xout[(i_0 * 2)], xout[(i_0 * 2) + 1] });
            }
            return outList;
        }

        public override bool Closed()
        {
            return closed;
        }
    }
}

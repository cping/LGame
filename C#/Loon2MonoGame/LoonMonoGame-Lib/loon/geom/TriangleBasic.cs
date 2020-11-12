using loon.utils;

namespace loon.geom
{

    public class TriangleBasic : Triangle
    {

        private const float EPSILON = 0.0000000001f;

        private readonly TArray<PointF> poly = new TArray<PointF>();

        private readonly TArray<PointF> tris = new TArray<PointF>();

        private bool tried;

        public TriangleBasic()
        {
        }


        public void AddPolyPoint(float x, float y)
        {
            PointF p = new PointF(x, y);
            if (!poly.Contains(p))
            {
                poly.Add(p);
            }
        }

        public int GetPolyPointCount()
        {
            return poly.Size();
        }

        public float[] GetPolyPoint(int index)
        {
            return new float[] { poly.Get(index).x, poly.Get(index).y };
        }

        public bool Triangulate()
        {
            tried = true;

            bool worked = Process(poly, tris);
            return worked;
        }


        public int GetTriangleCount()
        {
            if (!tried)
            {
                throw new LSysException("this not Triangle !");
            }
            return tris.Size() / 3;
        }


        public float[] GetTrianglePoint(int t, int i)
        {
            if (!tried)
            {
                throw new LSysException("this not Triangle !");
            }

            return tris.Get((t * 3) + i).ToArray();
        }

        private float Area(TArray<PointF> contour)
        {
            int n = contour.Size();

            float sA = 0.0f;

            for (int p = n - 1, q = 0; q < n; p = q++)
            {
                PointF contourP = contour.Get(p);
                PointF contourQ = contour.Get(q);

                sA += contourP.GetX() * contourQ.GetY() - contourQ.GetX()
                        * contourP.GetY();
            }
            return sA * 0.5f;
        }

        private bool InsideTriangle(float Ax, float Ay, float Bx, float By,
                float Cx, float Cy, float Px, float Py)
        {
            float ax, ay, bx, by, cx, cy, apx, apy, bpx, bpy, cpx, cpy;
            float cCROSSap, bCROSScp, aCROSSbp;

            ax = Cx - Bx;
            ay = Cy - By;
            bx = Ax - Cx;
            by = Ay - Cy;
            cx = Bx - Ax;
            cy = By - Ay;
            apx = Px - Ax;
            apy = Py - Ay;
            bpx = Px - Bx;
            bpy = Py - By;
            cpx = Px - Cx;
            cpy = Py - Cy;

            aCROSSbp = ax * bpy - ay * bpx;
            cCROSSap = cx * apy - cy * apx;
            bCROSScp = bx * cpy - by * cpx;

            return ((aCROSSbp >= 0.0f) && (bCROSScp >= 0.0f) && (cCROSSap >= 0.0f));
        }

        private bool Snip(TArray<PointF> contour, int u, int v, int w, int n, int[] V)
        {
            int p;
            float Ax, Ay, Bx, By, Cx, Cy, Px, Py;

            Ax = contour.Get(V[u]).GetX();
            Ay = contour.Get(V[u]).GetY();

            Bx = contour.Get(V[v]).GetX();
            By = contour.Get(V[v]).GetY();

            Cx = contour.Get(V[w]).GetX();
            Cy = contour.Get(V[w]).GetY();

            if (EPSILON > (((Bx - Ax) * (Cy - Ay)) - ((By - Ay) * (Cx - Ax))))
            {
                return false;
            }

            for (p = 0; p < n; p++)
            {
                if ((p == u) || (p == v) || (p == w))
                {
                    continue;
                }

                Px = contour.Get(V[p]).GetX();
                Py = contour.Get(V[p]).GetY();

                if (InsideTriangle(Ax, Ay, Bx, By, Cx, Cy, Px, Py))
                {
                    return false;
                }
            }

            return true;
        }

        private bool Process(TArray<PointF> contour, TArray<PointF> result)
        {
            result.Clear();

            int n = contour.Size();
            if (n < 3)
            {
                return false;
            }
            int[] sV = new int[n];

            if (0.0f < Area(contour))
            {
                for (int v = 0; v < n; v++)
                {
                    sV[v] = v;
                }
            }
            else
            {
                for (int v = 0; v < n; v++)
                {
                    sV[v] = (n - 1) - v;
                }
            }

            int nv = n;

            int count = 2 * nv;

            for (int v = nv - 1; nv > 2;)
            {

                if (0 >= (count--))
                {
                    return false;
                }

                int u = v;
                if (nv <= u)
                {
                    u = 0;
                }
                v = u + 1;
                if (nv <= v)
                {
                    v = 0;
                }
                int w = v + 1;
                if (nv <= w)
                {
                    w = 0;
                }
                if (Snip(contour, u, v, w, nv, sV))
                {
                    int a, b, c, s, t;

                    a = sV[u];
                    b = sV[v];
                    c = sV[w];

                    result.Add(contour.Get(a));
                    result.Add(contour.Get(b));
                    result.Add(contour.Get(c));

                    for (s = v, t = v + 1; t < nv; s++, t++)
                    {
                        sV[s] = sV[t];
                    }
                    nv--;

                    count = 2 * nv;
                }
            }

            return true;
        }


        public void StartHole()
        {

        }
    }

}

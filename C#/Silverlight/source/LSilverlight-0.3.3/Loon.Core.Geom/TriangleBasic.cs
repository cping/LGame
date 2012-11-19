namespace Loon.Core.Geom {

    using System.Collections.Generic;
    using Loon.Utils;

	public class TriangleBasic : Triangle {
	
		private const float EPSILON = 0.0000000001f;
	
		private TriangleBasic.PointList poly;
	
		private TriangleBasic.PointList tris;
	
		private bool tried;
	
		public TriangleBasic() {
			this.poly = new TriangleBasic.PointList ();
			this.tris = new TriangleBasic.PointList ();
		}
	
		public virtual void AddPolyPoint(float x, float y) {
			TriangleBasic.Point  p = new TriangleBasic.Point (x, y);
			if (!poly.Contains(p)) {
				poly.Add(p);
			}
		}
	
		public int GetPolyPointCount() {
			return poly.Size();
		}
	
		public float[] GetPolyPoint(int index) {
			return new float[] { poly.Get(index).x, poly.Get(index).y };
		}
	
		public virtual bool Triangulate() {
			tried = true;
	
			bool worked = Process(poly, tris);
			return worked;
		}
	
		public virtual int GetTriangleCount() {
			if (!tried) {
				throw new System.Exception("this not Triangle !");
			}
			return tris.Size() / 3;
		}
	
		public virtual float[] GetTrianglePoint(int t, int i) {
			if (!tried) {
				throw new System.Exception("this not Triangle !");
			}
	
			return tris.Get((t * 3) + i).ToArray();
		}
	
		private float Area(TriangleBasic.PointList  contour) {
			int n = contour.Size();
	
			float sA = 0.0f;
	
			for (int p = n - 1, q = 0; q < n; p = q++) {
				TriangleBasic.Point  contourP = contour.Get(p);
				TriangleBasic.Point  contourQ = contour.Get(q);
	
				sA += contourP.GetX() * contourQ.GetY() - contourQ.GetX()
						* contourP.GetY();
			}
			return sA * 0.5f;
		}
	
		private bool InsideTriangle(float Ax, float Ay, float Bx, float By,
				float Cx, float Cy, float Px, float Py) {
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
	
		private bool Snip(TriangleBasic.PointList  contour, int u, int v, int w, int n, int[] V) {
			int p;
			float Ax, Ay, Bx, By, Cx, Cy, Px, Py;
	
			Ax = contour.Get(V[u]).GetX();
			Ay = contour.Get(V[u]).GetY();
	
			Bx = contour.Get(V[v]).GetX();
			By = contour.Get(V[v]).GetY();
	
			Cx = contour.Get(V[w]).GetX();
			Cy = contour.Get(V[w]).GetY();
	
			if (EPSILON > (((Bx - Ax) * (Cy - Ay)) - ((By - Ay) * (Cx - Ax)))) {
				return false;
			}
	
			for (p = 0; p < n; p++) {
				if ((p == u) || (p == v) || (p == w)) {
					continue;
				}
	
				Px = contour.Get(V[p]).GetX();
				Py = contour.Get(V[p]).GetY();
	
				if (InsideTriangle(Ax, Ay, Bx, By, Cx, Cy, Px, Py)) {
					return false;
				}
			}
	
			return true;
		}
	
		private bool Process(TriangleBasic.PointList  contour, TriangleBasic.PointList  result) {
			result.Clear();
	
			int n = contour.Size();
			if (n < 3) {
				return false;
			}
			int[] sV = new int[n];
	
			if (0.0f < Area(contour)) {
				for (int v = 0; v < n; v++) {
					sV[v] = v;
				}
			} else {
				for (int v_0 = 0; v_0 < n; v_0++) {
					sV[v_0] = (n - 1) - v_0;
				}
			}
	
			int nv = n;
	
			int count = 2 * nv;
	
			for (int v_1 = nv - 1; nv > 2;) {
	
				if (0 >= (count--)) {
					return false;
				}
	
				int u = v_1;
				if (nv <= u) {
					u = 0;
				}
				v_1 = u + 1;
				if (nv <= v_1) {
					v_1 = 0;
				}
				int w = v_1 + 1;
				if (nv <= w) {
					w = 0;
				}
				if (Snip(contour, u, v_1, w, nv, sV)) {
					int a, b, c, s, t;
	
					a = sV[u];
					b = sV[v_1];
					c = sV[w];
	
					result.Add(contour.Get(a));
					result.Add(contour.Get(b));
					result.Add(contour.Get(c));
	
					for (s = v_1, t = v_1 + 1; t < nv; s++, t++) {
						sV[s] = sV[t];
					}
					nv--;
	
					count = 2 * nv;
				}
			}
	
			return true;
		}
	
		private class Point {
	
			public float x;

            public float y;
	
			private float[] array;
	
			public Point(float x_0, float y_1) {
				this.x = x_0;
				this.y = y_1;
				array = new float[] { x_0, y_1 };
			}
	
			public float GetX() {
				return x;
			}
	
			public float GetY() {
				return y;
			}
	
			public float[] ToArray() {
				return array;
			}
	
			public override int GetHashCode() {
				return (int) (x * y * 31);
			}
	
			public override bool Equals(object other) {
				if (other  is  TriangleBasic.Point ) {
					TriangleBasic.Point  p = (TriangleBasic.Point ) other;
					return (p.x == x) && (p.y == y);
				}
	
				return false;
			}
		}
	
		private class PointList {
	
			private List<Point> points;
	
			public PointList() {
				this.points = new List<Point>();
			}
	
			public bool Contains(TriangleBasic.Point  p) {
				return points.Contains(p);
			}
	
			public void Add(TriangleBasic.Point  point) {
                CollectionUtils.Add(points, point);
			}
	
			public int Size() {
				return points.Count;
			}
	
			public TriangleBasic.Point  Get(int i) {
				return (TriangleBasic.Point ) points[i];
			}
	
			public void Clear() {
				points.Clear();
			}
		}
	
		public virtual void StartHole() {
	
		}
	}
}

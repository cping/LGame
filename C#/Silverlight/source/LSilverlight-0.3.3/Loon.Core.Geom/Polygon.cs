namespace Loon.Core.Geom {

    using System;
    using System.Collections.Generic;
    using Loon.Utils;

	public class Polygon : Shape {

		public class Polygon2i {
	
			public int npoints;
	
			public int[] xpoints;
	
			public int[] ypoints;
	
			private const int MIN_LENGTH = 4;
	
			public Polygon2i() {
				xpoints = new int[MIN_LENGTH];
				ypoints = new int[MIN_LENGTH];
			}
	
			public Polygon2i(int[] xs, int[] ys, int np) {
				if (np > xs.Length || np > ys.Length) {
					throw new IndexOutOfRangeException("npoints > xpoints.length || "
													+ "npoints > ypoints.length".ToString());
				}
				if (np < 0) {
					throw new IndexOutOfRangeException("npoints < 0");
				}
				this.npoints = np;
				this.xpoints = Loon.Utils.CollectionUtils.CopyOf(xs, np);
				this.ypoints = Loon.Utils.CollectionUtils.CopyOf(ys, np);
			}

            public static int HighestOneBit(int i)
            {
                i |= (i >> 1);
                i |= (i >> 2);
                i |= (i >> 4);
                i |= (i >> 8);
                i |= (i >> 16);
                return i - ((int)((uint)i >> 1));
            }
	   
			public void AddPoint(int x, int y) {
				if (npoints >= xpoints.Length || npoints >= ypoints.Length) {
					int newLength = (npoints * 2);
					if (newLength < MIN_LENGTH) {
						newLength = MIN_LENGTH;
					} else if ((newLength & (newLength - 1)) != 0) {
                        newLength = HighestOneBit(newLength);
					}
					xpoints = Loon.Utils.CollectionUtils.CopyOf(xpoints, newLength);
					ypoints = Loon.Utils.CollectionUtils.CopyOf(ypoints, newLength);
				}
				xpoints[npoints] = x;
				ypoints[npoints] = y;
				npoints++;
			}
	
			public int[] GetVertices() {
				int vertice_size = xpoints.Length * 2;
				int[] verts = new int[vertice_size];
				for (int i = 0, j = 0; i < vertice_size; i += 2, j++) {
					verts[i] = xpoints[j];
					verts[i + 1] = ypoints[j];
				}
				return verts;
			}
	
			public void Reset() {
				npoints = 0;
				xpoints = new int[MIN_LENGTH];
				ypoints = new int[MIN_LENGTH];
			}
		}
	
		private bool allowDups;
	
		private bool closed;
	
		public Polygon(float[] points) {
			this.allowDups = false;
			this.closed = true;
			int length = points.Length;
	
			this.points = new float[length];
			maxX = -System.Single.MinValue;
			maxY = -System.Single.MinValue;
			minX = System.Single.MaxValue;
			minY = System.Single.MaxValue;
			x = System.Single.MaxValue;
			y = System.Single.MaxValue;
	
			for (int i = 0; i < length; i++) {
				this.points[i] = points[i];
				if (i % 2 == 0) {
					if (points[i] > maxX) {
						maxX = points[i];
					}
					if (points[i] < minX) {
						minX = points[i];
					}
					if (points[i] < x) {
						x = points[i];
					}
				} else {
					if (points[i] > maxY) {
						maxY = points[i];
					}
					if (points[i] < minY) {
						minY = points[i];
					}
					if (points[i] < y) {
						y = points[i];
					}
				}
			}
	
			FindCenter();
			CalculateRadius();
			pointsDirty = true;
		}
	
		public Polygon() {
			this.allowDups = false;
			this.closed = true;
			points = new float[0];
			maxX = -System.Single.MinValue;
			maxY = -System.Single.MinValue;
			minX = System.Single.MaxValue;
			minY = System.Single.MaxValue;
		}
	
		public Polygon(float[] xs, float[] ys, int np) {
			this.allowDups = false;
			this.closed = true;
			if (np > xs.Length || np > ys.Length) {
				throw new IndexOutOfRangeException("npoints > xpoints.length || "
									+ "npoints > ypoints.length".ToString());
			}
			if (np < 0) {
				throw new IndexOutOfRangeException("npoints < 0");
			}
			points = new float[0];
			maxX = -System.Single.MinValue;
			maxY = -System.Single.MinValue;
			minX = System.Single.MaxValue;
			minY = System.Single.MaxValue;
			for (int i = 0; i < np; i++) {
				AddPoint(xs[i], ys[i]);
			}
		}

        public Polygon(int[] xs, int[] ys, int np)
        {
            this.allowDups = false;
            this.closed = true;
            if (np > xs.Length || np > ys.Length)
            {
                throw new IndexOutOfRangeException("npoints > xpoints.length || "
                                    + "npoints > ypoints.length".ToString());
            }
            if (np < 0)
            {
                throw new IndexOutOfRangeException("npoints < 0");
            }
            points = new float[0];
            maxX = -System.Single.MinValue;
            maxY = -System.Single.MinValue;
            minX = System.Single.MaxValue;
            minY = System.Single.MaxValue;
            for (int i = 0; i < np; i++)
            {
                AddPoint(xs[i], ys[i]);
            }
        }

		public Point[] GetVertexs() {
			int size = GetPointCount();
			Point[] verts = new Point[size / 2];
			for (int i = 0, j = 0; i < size; i += 2, j++) {
				verts[j] = new Point(points[i], points[i + 1]);
			}
			return verts;
		}
	
		public void SetAllowDuplicatePoints(bool allowDups_0) {
			this.allowDups = allowDups_0;
		}
	
		public void AddPoint(float x, float y) {
			if (HasVertex(x, y) && (!allowDups)) {
				return;
			}
			List<Single> tempPoints = new List<Single>();
			for (int i = 0; i < points.Length; i++) {
                CollectionUtils.Add(tempPoints, points[i]);
			}
            CollectionUtils.Add(tempPoints, x);
            CollectionUtils.Add(tempPoints, y);
			int length = tempPoints.Count;
			this.points = new float[length];
			for (int i_0 = 0; i_0 < length; i_0++) {
				points[i_0] = (tempPoints[i_0]);
			}
			if (x > maxX) {
				maxX = x;
			}
			if (y > maxY) {
				maxY = y;
			}
			if (x < minX) {
				minX = x;
			}
			if (y < minY) {
				minY = y;
			}
			FindCenter();
			CalculateRadius();
	
			pointsDirty = true;
		}

        public override Shape Transform(Matrix transform)
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
	
		public override void SetX(float x) {
			base.SetX(x);
	
			pointsDirty = false;
		}
	
		public override void SetY(float y) {
			base.SetY(y);
	
			pointsDirty = false;
		}
	
		protected internal override void CreatePoints() {
	
		}
	
		public override bool Closed() {
			return closed;
		}
	
		public void SetClosed(bool closed_0) {
			this.closed = closed_0;
		}
	
		public Polygon Copy() {
			float[] copyPoints = new float[points.Length];
			System.Array.Copy((Array)(points),0,(Array)(copyPoints),0,copyPoints.Length);
			return new Polygon(copyPoints);
		}
	}
}

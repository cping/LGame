using Loon.Utils;
using Loon.Java;
namespace Loon.Core.Geom {
	
	public class Point : Shape {
	
		public class Point2i {
	
			public int x;
	
			public int y;
	
			public Point2i() {
			}
	
			public Point2i(int xa, int ya) {
				this.x = xa;
				this.y = ya;
			}
	
			public Point2i(float xa, float ya) {
				this.x = MathUtils.FromFloat(xa);
				this.y = MathUtils.FromFloat(ya);
			}
	
			public Point2i(Point2i p) {
				this.x = p.x;
				this.y = p.y;
			}
	
			public bool Equals(int xa, int ya) {
				return MathUtils.Equal(xa, this.x) && MathUtils.Equal(ya, this.y);
			}
	
			public int Length() {
				return MathUtils.Sqrt(MathUtils.Mul(x, x) + MathUtils.Mul(y, y));
			}
	
			public void Negate() {
				x = -x;
				y = -y;
			}
	
			public void Offset(int xa, int ya) {
				this.x += xa;
				this.y += ya;
			}
	
			public void Set(int xa, int ya) {
				this.x = xa;
				this.y = ya;
			}
	
			public void Set(Point2i p) {
				this.x = p.x;
				this.y = p.y;
			}
	
			public int DistanceTo(Point2i p) {
				int tx = this.x - p.x;
				int ty = this.y - p.y;
				return MathUtils
						.Sqrt(MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
			}
	
			public int DistanceTo(int xa, int ya) {
				int tx = this.x - xa;
				int ty = this.y - ya;
				return MathUtils
						.Sqrt(MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
			}

            public override int GetHashCode()
            {
                return base.GetHashCode();
            }
	
			public int DistanceTo(Point2i p1, Point2i p2) {
				int tx = p2.x - p1.x;
				int ty = p2.y - p1.y;
				int u = MathUtils.Div(
						MathUtils.Mul(x - p1.x, tx) + MathUtils.Mul(y - p1.y, ty),
						MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
				int ix = p1.x + MathUtils.Mul(u, tx);
				int iy = p1.y + MathUtils.Mul(u, ty);
				int dx = ix - x;
				int dy = iy - y;
				return MathUtils
						.Sqrt(MathUtils.Mul(dx, dx) + MathUtils.Mul(dy, dy));
			}
	
		}
	
		public int clazz;
	
		public const int POINT_CONVEX = 1;
	
		public const int POINT_CONCAVE = 2;
	
		public Point(float xa, float ya) {
			this.CheckPoints();
			this.SetLocation(xa, ya);
			this.type = ShapeType.POINT_SHAPE;
		}
	
		public Point(Point p) {
			this.CheckPoints();
			this.SetLocation(p);
			this.type = ShapeType.POINT_SHAPE;
		}
	
		public override Shape Transform(Matrix transform) {
			float[] result = new float[points.Length];
			transform.Transform(points, 0, result, 0, points.Length / 2);
			return new Point(points[0], points[1]);
		}
	
		protected internal override void CreatePoints() {
			if (points == null) {
				points = new float[2];
			}
			points[0] = GetX();
			points[1] = GetY();
	
			maxX = x;
			maxY = y;
			minX = x;
			minY = y;
	
			FindCenter();
			CalculateRadius();
		}

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

		protected internal override void FindCenter() {
			if (center == null) {
				center = new float[2];
			}
			center[0] = points[0];
			center[1] = points[1];
		}
	
		protected internal override void CalculateRadius() {
			boundingCircleRadius = 0;
		}
	
		public void Set(int xa, int ya) {
			this.x = xa;
			this.y = ya;
		}
	
		public override void SetLocation(float xa, float ya) {
			this.x = xa;
			this.y = ya;
		}
	
		public void SetLocation(Point p) {
			this.x = p.GetX();
			this.y = p.GetY();
		}
	
		public void Translate(float dx, float dy) {
			this.x += dx;
			this.y += dy;
		}
	
		public void Translate(Point p) {
			this.x += p.x;
			this.y += p.y;
		}
	
		public void Untranslate(Point p) {
			this.x -= p.x;
			this.y -= p.y;
		}
	
		public int DistanceTo(Point p) {
			int tx = (int) (this.x - p.x);
			int ty = (int) (this.y - p.y);
			return MathUtils.Sqrt(MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
		}
	
		public int DistanceTo(int xa, int ya) {
			int tx = (int) (this.x - xa);
			int ty = (int) (this.y - ya);
			return MathUtils.Sqrt(MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
		}
	
		public void GetLocation(Point dest) {
			dest.SetLocation(this.x, this.y);
		}
	
		public override bool Equals(object obj) {
			Point p = (Point) obj;
            return p.x == this.x && p.y == this.y && p.clazz == this.clazz;
		}
	}}

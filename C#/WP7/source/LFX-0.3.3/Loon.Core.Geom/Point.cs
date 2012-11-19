using Loon.Java;
namespace Loon.Core.Geom {
	
	public class Point : Shape {


        public override int GetHashCode()
        {
            return JavaRuntime.IdentityHashCode(this);
        }

		public class Point2i {
	
			public int x;
	
			public int y;

            public override int GetHashCode()
            {
                return JavaRuntime.IdentityHashCode(this);
            }
	
			public Point2i() {
			}
	
			public Point2i(int x_0, int y_1) {
				this.x = x_0;
				this.y = y_1;
			}
	
			public Point2i(float x_0, float y_1) {
				this.x = Loon.Utils.MathUtils.FromFloat(x_0);
				this.y = Loon.Utils.MathUtils.FromFloat(y_1);
			}
	
			public Point2i(Point.Point2i  p) {
				this.x = p.x;
				this.y = p.y;
			}
	
			public bool Equals(int x_0, int y_1) {
				return Loon.Utils.MathUtils.Equal(x_0, this.x) && Loon.Utils.MathUtils.Equal(y_1, this.y);
			}
	
			public int Length() {
				return Loon.Utils.MathUtils.Sqrt(Loon.Utils.MathUtils.Mul(x, x) + Loon.Utils.MathUtils.Mul(y, y));
			}
	
			public void Negate() {
				x = -x;
				y = -y;
			}
	
			public void Offset(int x_0, int y_1) {
				this.x += x_0;
				this.y += y_1;
			}
	
			public void Set(int x_0, int y_1) {
				this.x = x_0;
				this.y = y_1;
			}
	
			public void Set(Point.Point2i  p) {
				this.x = p.x;
				this.y = p.y;
			}
	
			public int DistanceTo(Point.Point2i  p) {
				int tx = this.x - p.x;
				int ty = this.y - p.y;
				return Loon.Utils.MathUtils
						.Sqrt(Loon.Utils.MathUtils.Mul(tx, tx) + Loon.Utils.MathUtils.Mul(ty, ty));
			}
	
			public int DistanceTo(int x_0, int y_1) {
				int tx = this.x - x_0;
				int ty = this.y - y_1;
				return Loon.Utils.MathUtils
						.Sqrt(Loon.Utils.MathUtils.Mul(tx, tx) + Loon.Utils.MathUtils.Mul(ty, ty));
			}
	
			public int DistanceTo(Point.Point2i  p1, Point.Point2i  p2) {
				int tx = p2.x - p1.x;
				int ty = p2.y - p1.y;
				int u = Loon.Utils.MathUtils.Div(
						Loon.Utils.MathUtils.Mul(x - p1.x, tx) + Loon.Utils.MathUtils.Mul(y - p1.y, ty),
						Loon.Utils.MathUtils.Mul(tx, tx) + Loon.Utils.MathUtils.Mul(ty, ty));
				int ix = p1.x + Loon.Utils.MathUtils.Mul(u, tx);
				int iy = p1.y + Loon.Utils.MathUtils.Mul(u, ty);
				int dx = ix - x;
				int dy = iy - y;
				return Loon.Utils.MathUtils
						.Sqrt(Loon.Utils.MathUtils.Mul(dx, dx) + Loon.Utils.MathUtils.Mul(dy, dy));
			}
	
		}
	
		public int clazz;
	
		public const int POINT_CONVEX = 1;
	
		public const int POINT_CONCAVE = 2;
	
		public Point(float x_0, float y_1) {
			this.CheckPoints();
			this.SetLocation(x_0, y_1);
			this.type = Loon.Core.Geom.ShapeType.POINT_SHAPE;
		}
	
		public Point(Point p) {
			this.CheckPoints();
			this.SetLocation(p);
			this.type = Loon.Core.Geom.ShapeType.POINT_SHAPE;
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
	
		public void Set(int x_0, int y_1) {
			this.x = x_0;
			this.y = y_1;
		}
	
		public override void SetLocation(float x_0, float y_1) {
			this.x = x_0;
			this.y = y_1;
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
			return Loon.Utils.MathUtils.Sqrt(Loon.Utils.MathUtils.Mul(tx, tx) + Loon.Utils.MathUtils.Mul(ty, ty));
		}
	
		public int DistanceTo(int x_0, int y_1) {
			int tx = (int) (this.x - x_0);
			int ty = (int) (this.y - y_1);
			return Loon.Utils.MathUtils.Sqrt(Loon.Utils.MathUtils.Mul(tx, tx) + Loon.Utils.MathUtils.Mul(ty, ty));
		}
	
		public void GetLocation(Point dest) {
			dest.SetLocation(this.x, this.y);
		}
	
		public override bool Equals(object obj) {
			Point p = (Point) obj;
			if (p.x == this.x && p.y == this.y && p.clazz == this.clazz) {
				return true;
			} else {
				return false;
			}
		}
	}}

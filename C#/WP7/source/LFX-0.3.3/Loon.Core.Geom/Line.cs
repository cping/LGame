using Loon.Utils;
namespace Loon.Core.Geom {
	

	public class Line : Shape {
	
		private Vector2f start;
	
		private Vector2f end;
	
		private Vector2f vec;
	
		private Vector2f loc;
	
		private Vector2f closest;

        public Line(float x, float y, bool inner, bool outer)
            : this(0, 0, x, y)
        {
			
		}
	
		public Line(float x, float y):this(x, y, true, true) {
			
		}
	
		public Line(Point p1, Point p2):	this(p1.x, p1.y, p2.x, p2.y) {
		
		}
	
		public Line(float x1, float y1, float x2, float y2):this(new Vector2f(x1, y1), new Vector2f(x2, y2)) {
			
		}
	
		public Line(float x1, float y1, float dx, float dy, bool dummy):this(new Vector2f(x1, y1), new Vector2f(x1 + dx, y1 + dy)) {
			
		}
	
		public Line(float[] start_0, float[] end_1):base() {
			
			this.loc = new Vector2f(0, 0);
			this.closest = new Vector2f(0, 0);
			this.type = Loon.Core.Geom.ShapeType.LINE_SHAPE;
			Set(start_0, end_1);
		}
	
		public Line(Vector2f start_0, Vector2f end_1):base() {
			this.loc = new Vector2f(0, 0);
			this.closest = new Vector2f(0, 0);
			this.type = Loon.Core.Geom.ShapeType.LINE_SHAPE;
			Set(start_0, end_1);
		}
	
		public void Set(float[] start_0, float[] end_1) {
			Set(start_0[0], start_0[1], end_1[0], end_1[1]);
		}
	
		public Vector2f GetStart() {
			return start;
		}
	
		public Vector2f GetEnd() {
			return end;
		}
	
		public override float Length() {
			return vec.Len();
		}
	
		public float LengthSquared() {
			return vec.LengthSquared();
		}
	
		public void Set(Vector2f start_0, Vector2f end_1) {
			base.pointsDirty = true;
			if (this.start == null) {
				this.start = new Vector2f();
			}
			this.start.Set(start_0);
	
			if (this.end == null) {
				this.end = new Vector2f();
			}
			this.end.Set(end_1);
	
			vec = new Vector2f(end_1);
			vec.Sub(start_0);
	
		}
	
		public void Set(float sx, float sy, float ex, float ey) {
			base.pointsDirty = true;
			start.Set(sx, sy);
			end.Set(ex, ey);
			float dx = (ex - sx);
			float dy = (ey - sy);
			vec.Set(dx, dy);
	
		}
	
		public float GetDX() {
			return end.GetX() - start.GetX();
		}
	
		public float GetDY() {
			return end.GetY() - start.GetY();
		}
	
		public override float GetX() {
			return GetX1();
		}
	
		public override float GetY() {
			return GetY1();
		}
	
		public float GetX1() {
			return start.GetX();
		}
	
		public float GetY1() {
			return start.GetY();
		}
	
		public float GetX2() {
			return end.GetX();
		}
	
		public float GetY2() {
			return end.GetY();
		}
	
		public float Distance(Vector2f point) {
			return MathUtils.Sqrt(DistanceSquared(point));
		}
	
		public bool On(Vector2f point) {
			GetClosestPoint(point, closest);
	
			return point.Equals(closest);
		}
	
		public float DistanceSquared(Vector2f point) {
			GetClosestPoint(point, closest);
			closest.Sub(point);
	
			float result = closest.LengthSquared();
	
			return result;
		}
	
		public void GetClosestPoint(Vector2f point, Vector2f result) {
			loc.Set(point);
			loc.Sub(start);
	
			float projDistance = vec.Dot(loc);
	
			projDistance /= vec.LengthSquared();
	
			if (projDistance < 0) {
				result.Set(start);
				return;
			}
			if (projDistance > 1) {
				result.Set(end);
				return;
			}
	
			result.x = start.GetX() + projDistance * vec.GetX();
			result.y = start.GetY() + projDistance * vec.GetY();
		}
	
		public Vector2f Intersect(Line other) {
			return Intersect(other, false);
		}
	
		public Vector2f Intersect(Line other, bool limit) {
			Vector2f temp = new Vector2f();
	
			if (!Intersect(other, limit, temp)) {
				return null;
			}
	
			return temp;
		}
	
		public bool Intersect(Line other, bool limit, Vector2f result) {
			float dx1 = end.GetX() - start.GetX();
			float dx2 = other.end.GetX() - other.start.GetX();
			float dy1 = end.GetY() - start.GetY();
			float dy2 = other.end.GetY() - other.start.GetY();
			float denom = (dy2 * dx1) - (dx2 * dy1);
	
			if (denom == 0) {
				return false;
			}
	
			float ua = (dx2 * (start.GetY() - other.start.GetY()))
					- (dy2 * (start.GetX() - other.start.GetX()));
			ua /= denom;
			float ub = (dx1 * (start.GetY() - other.start.GetY()))
					- (dy1 * (start.GetX() - other.start.GetX()));
			ub /= denom;
	
			if ((limit) && ((ua < 0) || (ua > 1) || (ub < 0) || (ub > 1))) {
				return false;
			}
	
			float u = ua;
	
			float ix = start.GetX() + (u * (end.GetX() - start.GetX()));
			float iy = start.GetY() + (u * (end.GetY() - start.GetY()));
	
			result.Set(ix, iy);
			return true;
		}
	
		protected internal override void CreatePoints() {
			points = new float[4];
			points[0] = GetX1();
			points[1] = GetY1();
			points[2] = GetX2();
			points[3] = GetY2();
		}
	
		public override bool Intersects(Shape shape) {
			if (shape  is  Circle) {
				return shape.Intersects(this);
			}
			return base.Intersects(shape);
		}
	
		public float PtSegDistSq(Point pt) {
			return PtSegDistSq(GetX1(), GetY1(), GetX2(), GetY2(), pt.GetX(),
					pt.GetY());
		}
	
		public float PtSegDistSq(float px, float py) {
			return PtSegDistSq(GetX1(), GetY1(), GetX2(), GetY2(), px, py);
		}

        public static float PtSegDist(float x1, float y1, float x2, float y2,
                float px, float py)
        {
            return MathUtils.Sqrt(PtSegDistSq(x1, y1, x2, y2, px, py));
        }
	
		public static float PtSegDistSq(float x1, float y1, float x2, float y2,
				float px, float py) {
			x2 -= x1;
			y2 -= y1;
			px -= x1;
			py -= y1;
			float dotprod = px * x2 + py * y2;
			float projlenSq;
			if (dotprod <= 0.0d) {
				projlenSq = 0.0f;
			} else {
				px = x2 - px;
				py = y2 - py;
				dotprod = px * x2 + py * y2;
				if (dotprod <= 0.0d) {
					projlenSq = 0.0f;
				} else {
					projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
				}
			}
			float lenSq = px * px + py * py - projlenSq;
			if (lenSq < 0) {
				lenSq = 0;
			}
			return lenSq;
		}
	
		public static float PtLineDist(float x1, float y1, float x2, float y2,
				float px, float py) {
			return MathUtils.Sqrt(PtLineDistSq(x1, y1, x2, y2, px, py));
		}
	
		public float PtLineDist(Point pt) {
			return PtLineDist(GetX1(), GetY1(), GetX2(), GetY2(), pt.GetX(),
					pt.GetY());
		}
	
		public float PtLineDistSq(float px, float py) {
			return PtLineDistSq(GetX1(), GetY1(), GetX2(), GetY2(), px, py);
		}
	
		public float PtLineDistSq(Point pt) {
			return PtLineDistSq(GetX1(), GetY1(), GetX2(), GetY2(), pt.GetX(),
					pt.GetY());
		}
	
		public static float PtLineDistSq(float x1, float y1, float x2, float y2,
				float px, float py) {
			x2 -= x1;
			y2 -= y1;
			px -= x1;
			py -= y1;
			float dotprod = px * x2 + py * y2;
			float projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
			float lenSq = px * px + py * py - projlenSq;
			if (lenSq < 0) {
				lenSq = 0;
			}
			return lenSq;
		}

        public override Shape Transform(Matrix transform)
        {
			float[] temp = new float[4];
			CreatePoints();
			transform.Transform(points, 0, temp, 0, 2);
			return new Line(temp[0], temp[1], temp[2], temp[3]);
		}
	
		public override bool Closed() {
			return false;
		}
	
	}
}

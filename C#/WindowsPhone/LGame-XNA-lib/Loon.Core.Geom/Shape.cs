using Loon.Utils;
using System.Runtime.CompilerServices;
namespace Loon.Core.Geom {
	
	public abstract class Shape {
	
		internal ShapeType type;
	
		public float x;
	
		public float y;
	
		protected internal float rotation;
	
		protected internal float[] points;
	
		protected internal float[] center;
	
		protected internal float scaleX, scaleY;
	
		protected internal float maxX, maxY;
	
		protected internal float minX, minY;
	
		protected internal float boundingCircleRadius;
	
		protected internal bool pointsDirty;
	
		protected internal Triangle triangle;
	
		protected internal bool trianglesDirty;
	
		protected internal AABB aabb;
	
		protected internal RectBox rect;
	
		public Shape() {
			pointsDirty = true;
			type = ShapeType.DEFAULT_SHAPE;
			scaleX = scaleY = 1f;
		}
	
		public virtual void SetLocation(float x_0, float y_1) {
			SetX(x_0);
			SetY(y_1);
		}
	
		public abstract Shape Transform(Matrix transform);
	
		protected abstract internal void CreatePoints();
	
		public void Translate(int deltaX, int deltaY) {
			SetX(x + deltaX);
			SetY(y + deltaY);
		}
	
		public virtual float GetX() {
			return x;
		}
	
		public virtual void SetX(float x_0) {
			if (x_0 != this.x || x_0 == 0) {
				float dx = x_0 - this.x;
				this.x = x_0;
				if ((points == null) || (center == null)) {
					CheckPoints();
				}
				for (int i = 0; i < points.Length / 2; i++) {
					points[i * 2] += dx;
				}
				center[0] += dx;
				x_0 += dx;
				maxX += dx;
				minX += dx;
				trianglesDirty = true;
			}
		}
	
		public virtual void SetY(float y_0) {
			if (y_0 != this.y || y_0 == 0) {
				float dy = y_0 - this.y;
				this.y = y_0;
				if ((points == null) || (center == null)) {
					CheckPoints();
				}
				for (int i = 0; i < points.Length / 2; i++) {
					points[(i * 2) + 1] += dy;
				}
				center[1] += dy;
				y_0 += dy;
				maxY += dy;
				minY += dy;
				trianglesDirty = true;
			}
		}
	
		public virtual float GetY() {
			return y;
		}
	
		public virtual float Length() {
			return MathUtils.Sqrt(x * x + y * y);
		}
	
		public void SetLocation(Vector2f loc) {
			SetX(loc.x);
			SetY(loc.y);
		}
	
		public virtual float GetCenterX() {
			CheckPoints();
			return center[0];
		}
	
		public void SetCenterX(float centerX) {
			if ((points == null) || (center == null)) {
				CheckPoints();
			}
	
			float xDiff = centerX - GetCenterX();
			SetX(x + xDiff);
		}
	
		public virtual float GetCenterY() {
			CheckPoints();
	
			return center[1];
		}
	
		public void SetCenterY(float centerY) {
			if ((points == null) || (center == null)) {
				CheckPoints();
			}
	
			float yDiff = centerY - GetCenterY();
			SetY(y + yDiff);
		}
	
		public virtual float GetMaxX() {
			CheckPoints();
			return maxX;
		}
	
		public virtual float GetMaxY() {
			CheckPoints();
			return maxY;
		}
	
		public virtual float GetMinX() {
			CheckPoints();
			return minX;
		}
	
		public virtual float GetMinY() {
			CheckPoints();
			return minY;
		}
	
		public float GetBoundingCircleRadius() {
			CheckPoints();
			return boundingCircleRadius;
		}
	
		public float[] GetCenter() {
			CheckPoints();
			return center;
		}
	
		public float[] GetPoints() {
			CheckPoints();
			return points;
		}
	
		public int GetPointCount() {
			CheckPoints();
			return points.Length / 2;
		}
	
		public float[] GetPoint(int index) {
			CheckPoints();
	
			float[] result = new float[2];
	
			result[0] = points[index * 2];
			result[1] = points[index * 2 + 1];
	
			return result;
		}
	
		public float[] GetNormal(int index) {
			float[] current = GetPoint(index);
			float[] prev = GetPoint((index - 1 < 0) ? GetPointCount() - 1 : index - 1);
			float[] next = GetPoint((index + 1 >= GetPointCount()) ? 0 : index + 1);
	
			float[] t1 = GetNormal(prev, current);
			float[] t2 = GetNormal(current, next);
	
			if ((index == 0) && (!Closed())) {
				return t2;
			}
			if ((index == GetPointCount() - 1) && (!Closed())) {
				return t1;
			}
	
			float tx = (t1[0] + t2[0]) / 2;
			float ty = (t1[1] + t2[1]) / 2;
			float len = MathUtils.Sqrt((tx * tx) + (ty * ty));
			return new float[] { tx / len, ty / len };
		}
	
		public bool Contains(Shape other) {
			if (other.Intersects(this)) {
				return false;
			}
	
			for (int i = 0; i < other.GetPointCount(); i++) {
				float[] pt = other.GetPoint(i);
				if (!Contains(pt[0], pt[1])) {
					return false;
				}
			}
	
			return true;
		}
	
		private float[] GetNormal(float[] start, float[] end) {
			float dx = start[0] - end[0];
			float dy = start[1] - end[1];
			float len = MathUtils.Sqrt((dx * dx) + (dy * dy));
			dx /= len;
			dy /= len;
			return new float[] { -dy, dx };
		}
	
		public bool Includes(float x_0, float y_1) {
			if (points.Length == 0) {
				return false;
			}
	
			CheckPoints();
	
			Line testLine = new Line(0, 0, 0, 0);
			Vector2f pt = new Vector2f(x_0, y_1);
	
			for (int i = 0; i < points.Length; i += 2) {
				int n = i + 2;
				if (n >= points.Length) {
					n = 0;
				}
				testLine.Set(points[i], points[i + 1], points[n], points[n + 1]);
	
				if (testLine.On(pt)) {
					return true;
				}
			}
	
			return false;
		}
	
		public int IndexOf(float x_0, float y_1) {
			for (int i = 0; i < points.Length; i += 2) {
				if ((points[i] == x_0) && (points[i + 1] == y_1)) {
					return i / 2;
				}
			}
	
			return -1;
		}
	
		public virtual bool Contains(float x_0, float y_1) {
	
			CheckPoints();
			if (points.Length == 0) {
				return false;
			}
	
			bool result = false;
			float xnew, ynew;
			float xold, yold;
			float x1, y1;
			float x2, y2;
			int npoints = points.Length;
	
			xold = points[npoints - 2];
			yold = points[npoints - 1];
			for (int i = 0; i < npoints; i += 2) {
				xnew = points[i];
				ynew = points[i + 1];
				if (xnew > xold) {
					x1 = xold;
					x2 = xnew;
					y1 = yold;
					y2 = ynew;
				} else {
					x1 = xnew;
					x2 = xold;
					y1 = ynew;
					y2 = yold;
				}
				if ((xnew < x_0) == (x_0 <= xold)
						&& ((double) y_1 - (double) y1) * (x2 - x1) < ((double) y2 - (double) y1)
								* (x_0 - x1)) {
					result = !result;
				}
				xold = xnew;
				yold = ynew;
			}
	
			return result;
		}
	
		public virtual bool Intersects(Shape shape) {
			if (shape == null) {
				return false;
			}
	
			CheckPoints();
	
			bool result = false;
			float[] points_0 = GetPoints();
			float[] thatPoints = shape.GetPoints();
			int length = points_0.Length;
			int thatLength = thatPoints.Length;
			double unknownA;
			double unknownB;
	
			if (!Closed()) {
				length -= 2;
			}
			if (!shape.Closed()) {
				thatLength -= 2;
			}
	
			for (int i = 0; i < length; i += 2) {
				int iNext = i + 2;
				if (iNext >= points_0.Length) {
					iNext = 0;
				}
	
				for (int j = 0; j < thatLength; j += 2) {
					int jNext = j + 2;
					if (jNext >= thatPoints.Length) {
						jNext = 0;
					}
	
					unknownA = (((points_0[iNext] - points_0[i]) * (double) (thatPoints[j + 1] - points_0[i + 1])) - ((points_0[iNext + 1] - points_0[i + 1]) * (thatPoints[j] - points_0[i])))
							/ (((points_0[iNext + 1] - points_0[i + 1]) * (thatPoints[jNext] - thatPoints[j])) - ((points_0[iNext] - points_0[i]) * (thatPoints[jNext + 1] - thatPoints[j + 1])));
					unknownB = (((thatPoints[jNext] - thatPoints[j]) * (double) (thatPoints[j + 1] - points_0[i + 1])) - ((thatPoints[jNext + 1] - thatPoints[j + 1]) * (thatPoints[j] - points_0[i])))
							/ (((points_0[iNext + 1] - points_0[i + 1]) * (thatPoints[jNext] - thatPoints[j])) - ((points_0[iNext] - points_0[i]) * (thatPoints[jNext + 1] - thatPoints[j + 1])));
	
					if (unknownA >= 0 && unknownA <= 1 && unknownB >= 0
							&& unknownB <= 1) {
						result = true;
						break;
					}
				}
				if (result) {
					break;
				}
			}
	
			return result;
		}
	
		public bool HasVertex(float x_0, float y_1) {
			if (points.Length == 0) {
				return false;
			}
	
			CheckPoints();
	
			for (int i = 0; i < points.Length; i += 2) {
				if ((points[i] == x_0) && (points[i + 1] == y_1)) {
					return true;
				}
			}
	
			return false;
		}
	
		protected internal virtual void FindCenter() {
			center = new float[] { 0, 0 };
			int length = points.Length;
			for (int i = 0; i < length; i += 2) {
				center[0] += points[i];
				center[1] += points[i + 1];
			}
			center[0] /= (length / 2);
			center[1] /= (length / 2);
		}
	
		protected internal virtual void CalculateRadius() {
			boundingCircleRadius = 0;
	
			for (int i = 0; i < points.Length; i += 2) {
				float temp = ((points[i] - center[0]) * (points[i] - center[0]))
						+ ((points[i + 1] - center[1]) * (points[i + 1] - center[1]));
				boundingCircleRadius = (boundingCircleRadius > temp) ? boundingCircleRadius
						: temp;
			}
			boundingCircleRadius = MathUtils.Sqrt(boundingCircleRadius);
		}
	
		protected internal void CalculateTriangles() {
			if ((!trianglesDirty) && (triangle != null)) {
				return;
			}
			if (points.Length >= 6) {
				triangle = new TriangleNeat();
				for (int i = 0; i < points.Length; i += 2) {
					triangle.AddPolyPoint(points[i], points[i + 1]);
				}
				triangle.Triangulate();
			}
	
			trianglesDirty = false;
		}
	
		private void CallTransform(Matrix m) {
			if (points != null) {
				float[] result = new float[points.Length];
				m.Transform(points, 0, result, 0, points.Length / 2);
				this.points = result;
				this.CheckPoints();
			}
		}
	
		public void SetScale(float s) {
			this.SetScale(s, s);
		}
	
		public virtual void SetScale(float sx, float sy) {
			if (scaleX != sx || scaleY != sy) {
				Matrix m = new Matrix();
				m.Scale(scaleX = sx, scaleY = sy);
				this.CallTransform(m);
			}
		}
	
		public float GetScaleX() {
			return scaleX;
		}
	
		public float GetScaleY() {
			return scaleY;
		}
	
		public void SetRotation(float r) {
			if (rotation != r) {
				this.CallTransform(Matrix.CreateRotateTransform(
						rotation = (r / 180f * MathUtils.PI), this.center[0],
						this.center[1]));
			}
		}
	
		public void SetRotation(float r, float x_0, float y_1) {
			if (rotation != r) {
				this.CallTransform(Matrix.CreateRotateTransform(
						rotation = (r / 180f * MathUtils.PI), x_0, y_1));
			}
		}
	
		public float GetRotation() {
			return (rotation * 180f / MathUtils.PI);
		}
	
		public void IncreaseTriangulation() {
			CheckPoints();
			CalculateTriangles();
	
			triangle = new TriangleOver(triangle);
		}
	
		public Triangle GetTriangles() {
			CheckPoints();
			CalculateTriangles();
			return triangle;
		}
	
		[MethodImpl(MethodImplOptions.Synchronized)]
		protected internal void CheckPoints() {
			if (pointsDirty) {
				CreatePoints();
				FindCenter();
				CalculateRadius();
				if (points == null) {
					return;
				}
				 lock (points) {
								int size = points.Length;
								if (size > 0) {
									maxX = points[0];
									maxY = points[1];
									minX = points[0];
									minY = points[1];
									for (int i = 0; i < size / 2; i++) {
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
	
		public void PreCache() {
			CheckPoints();
			GetTriangles();
		}
	
		public virtual bool Closed() {
			return true;
		}
	
		public Shape Prune() {
			Polygon result = new Polygon();
	
			for (int i = 0; i < GetPointCount(); i++) {
				int next = (i + 1 >= GetPointCount()) ? 0 : i + 1;
				int prev = (i - 1 < 0) ? GetPointCount() - 1 : i - 1;
	
				float dx1 = GetPoint(i)[0] - GetPoint(prev)[0];
				float dy1 = GetPoint(i)[1] - GetPoint(prev)[1];
				float dx2 = GetPoint(next)[0] - GetPoint(i)[0];
				float dy2 = GetPoint(next)[1] - GetPoint(i)[1];
	
				float len1 = MathUtils.Sqrt((dx1 * dx1) + (dy1 * dy1));
				float len2 = MathUtils.Sqrt((dx2 * dx2) + (dy2 * dy2));
				dx1 /= len1;
				dy1 /= len1;
				dx2 /= len2;
				dy2 /= len2;
	
				if ((dx1 != dx2) || (dy1 != dy2)) {
					result.AddPoint(GetPoint(i)[0], GetPoint(i)[1]);
				}
			}
	
			return result;
		}
	
		public virtual float GetWidth() {
			return maxX - minX;
		}
	
		public virtual float GetHeight() {
			return maxY - minY;
		}
	
		public ShapeType GetShapeType() {
			return this.type;
		}
	
		public virtual RectBox GetRect() {
			if (rect == null) {
				rect = new RectBox(x, y, GetWidth(), GetHeight());
			} else {
				rect.SetBounds(x, y, GetWidth(), GetHeight());
			}
			return rect;
		}
	
		public AABB GetAABB() {
			if (aabb == null) {
				aabb = new AABB(minX, minY, maxX, maxY);
			} else {
				aabb.Set(minX, minY, maxX, maxY);
			}
			return aabb;
		}
	
	}
}

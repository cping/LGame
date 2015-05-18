using Loon.Utils;
using System.Collections.Generic;
namespace Loon.Core.Geom {
	
	public class ShapeUtils {
	
		public static Vector2f CalculateVector(float angle, float magnitude) {
			Vector2f v = new Vector2f();
			v.x = MathUtils.Sin(MathUtils.ToRadians(angle));
			v.x *= magnitude;
			v.y = -MathUtils.Cos(MathUtils.ToRadians(angle));
			v.y *= magnitude;
			return v;
		}
	
		public static float CalculateAngle(float x, float y, float x1, float y1) {
			float angle = MathUtils.Atan2(y - y1, x - x1);
			return (float) (MathUtils.ToDegrees(angle) - 90);
		}
	
		public static float UpdateAngle(float currentAngle, float targetAngle,
				float step) {
			float pi = MathUtils.PI;
	
			currentAngle = (currentAngle + pi * 2) % (pi * 2);
			targetAngle = (targetAngle + pi * 2) % (pi * 2);
	
			if (MathUtils.Abs(currentAngle - targetAngle) < step) {
				return targetAngle;
			}
	
			if (2 * pi - currentAngle + targetAngle < pi
					|| 2 * pi - targetAngle + currentAngle < pi) {
				if (currentAngle < targetAngle) {
					currentAngle -= step;
				} else {
					currentAngle += step;
				}
			} else {
				if (currentAngle < targetAngle) {
					currentAngle += step;
				} else {
					currentAngle -= step;
				}
			}
			return (2 * pi + currentAngle) % (2 * pi);
		}
	
		public static float UpdateLine(float value_ren, float target, float step) {
			if (MathUtils.Abs(value_ren - target) < step)
				return target;
			if (value_ren > target) {
				return value_ren - step;
			}
			return value_ren + step;
		}
	
		public static float GetAngleDiff(float currentAngle, float targetAngle) {
			float pi = MathUtils.PI;
			currentAngle = (currentAngle + pi * 2) % (pi * 2);
			targetAngle = (targetAngle + pi * 2) % (pi * 2);
	
			float diff = MathUtils.Abs(currentAngle - targetAngle);
			float v = MathUtils.Abs(2 * pi - currentAngle + targetAngle);
			if (v < diff) {
				diff = v;
			}
			v = MathUtils.Abs(2 * pi - targetAngle + currentAngle);
			if (v < diff) {
				diff = v;
			}
			return diff;
		}
	
		public static Vector2f RotateVector(Vector2f v, Vector2f center, float angle) {
			Vector2f result = new Vector2f();
			float x = v.x - center.x;
			float y = v.y - center.y;
			result.x = MathUtils.Cos(angle) * x - MathUtils.Sin(angle) * y
					+ center.x;
			result.y = MathUtils.Sin(angle) * x + MathUtils.Cos(angle) * y
					+ center.y;
			return result;
		}
	
		public static Triangle Triangulate(Vector2f[] vertices) {
			return Triangulate(new TriangleBasic(), vertices);
		}
	
		public static Triangle Triangulate(Triangle triangulator,
				Vector2f[] vertices) {
			int size = vertices.Length;
			for (int i = 0; i < size; i++) {
				triangulator.AddPolyPoint(vertices[i].x, vertices[i].y);
			}
			triangulator.Triangulate();
			return triangulator;
		}
	
		public static void CalculateCenter(Vector2f[] vertices, Vector2f center) {
			center.x = 0f;
			center.y = 0f;
			for (int i = 0; i < vertices.Length; i++) {
				center.x += vertices[i].x;
				center.y += vertices[i].y;
			}
			center.x /= vertices.Length;
			center.y /= vertices.Length;
		}
	
		public static void TranslateVertices(Vector2f[] vertices, Vector2f tx) {
			for (int i = 0; i < vertices.Length; i++) {
				vertices[i].Add(tx.x, tx.y);
			}
		}
	
		public static void CalculateBounds(Vector2f[] vertices, RectBox bounds) {
			bounds.x = System.Single.MaxValue;
			bounds.y = System.Single.MaxValue;

            bounds.width = (int)-System.Int32.MaxValue;
            bounds.height = (int)-System.Int32.MaxValue;

			for (int i = 0; i < vertices.Length; i++) {
				Vector2f v = vertices[i];
	
				if (v.x < bounds.x)
					bounds.x = v.x;
	
				if (v.y < bounds.y)
					bounds.y = v.y;
	
				if (v.x > bounds.x + bounds.width) {
					bounds.width = (int) (v.x - bounds.x);
				}
	
				if (v.y > bounds.y + bounds.height) {
					bounds.height = (int) (v.y - bounds.y);
				}
			}
		}
	
		public void Rotate(Vector2f[] vertices, float angle) {
			for (int i = 0; i < vertices.Length; i++) {
				vertices[i].Rotate(angle);
			}
		}
	
		public static void CalculateConvexHull(List<Vector2f> points,
                List<Vector2f> convexHullPoints)
        {
			if (points.Count <= 1) {
				return;
			}
			Vector2f p;
			Vector2f bot = points[0];
			for (int i = 1; i < points.Count; i++) {
				Vector2f point = points[i];
				if (point.y < bot.y)
					bot = point;
			}
			CollectionUtils.Add(convexHullPoints,bot);
			p = bot;
			do {
				int i_0;
				i_0 = (points[0] == p) ? 1 : 0;
				Vector2f cand = points[i_0];
	
				for (i_0 = i_0 + 1; i_0 < points.Count; i_0++) {
					Vector2f point_1 = points[i_0];
					if (point_1 != p && Area(p, cand, point_1) > 0)
						cand = points[i_0];
				}
                CollectionUtils.Add(convexHullPoints, cand);
				p = cand;
			} while (p != bot);
		}
	
		public static float Area(Vector2f a, Vector2f b, Vector2f c) {
			return Area(a.x, a.y, b.x, b.y, c.x, c.y);
		}
	
		public static float Area(float x0, float y0, float x1, float y1, float x2,
				float y2) {
			return x1 * y2 - y1 * x2 + x2 * y0 - y2 * x0 + x0 * y1 - y0 * x1;
		}
	}
}

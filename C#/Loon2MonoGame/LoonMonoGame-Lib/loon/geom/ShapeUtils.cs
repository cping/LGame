using java.lang;
using loon.utils;


namespace loon.geom
{

	public class ShapeUtils
	{

		public static Vector2f CalculateVector(float angle, float magnitude)
		{
            Vector2f v = new Vector2f
            {
                x = MathUtils.Sin(MathUtils.ToRadians(angle)),
				y = -MathUtils.Cos(MathUtils.ToRadians(angle))
		     };
            v.x *= magnitude;
			v.y *= magnitude;
			return v;
		}

		public static float CalculateAngle(float x, float y, float x1, float y1)
		{
			float angle = MathUtils.Atan2(y - y1, x - x1);
			return (MathUtils.ToDegrees(angle) - 90);
		}

		public static float UpdateAngle(float currentAngle, float targetAngle, float step)
		{
			float pi = MathUtils.PI;

			currentAngle = (currentAngle + pi * 2) % (pi * 2);
			targetAngle = (targetAngle + pi * 2) % (pi * 2);

			if (MathUtils.Abs(currentAngle - targetAngle) < step)
			{
				return targetAngle;
			}

			if (2 * pi - currentAngle + targetAngle < pi || 2 * pi - targetAngle + currentAngle < pi)
			{
				if (currentAngle < targetAngle)
				{
					currentAngle -= step;
				}
				else
				{
					currentAngle += step;
				}
			}
			else
			{
				if (currentAngle < targetAngle)
				{
					currentAngle += step;
				}
				else
				{
					currentAngle -= step;
				}
			}
			return (2 * pi + currentAngle) % (2 * pi);
		}

		public static float UpdateLine(float value, float target, float step)
		{
			if (MathUtils.Abs(value - target) < step)
				return target;
			if (value > target)
			{
				return value - step;
			}
			return value + step;
		}

		public static float GetAngleDiff(float currentAngle, float targetAngle)
		{
			float pi = MathUtils.PI;
			currentAngle = (currentAngle + pi * 2) % (pi * 2);
			targetAngle = (targetAngle + pi * 2) % (pi * 2);

			float diff = MathUtils.Abs(currentAngle - targetAngle);
			float v = MathUtils.Abs(2 * pi - currentAngle + targetAngle);
			if (v < diff)
			{
				diff = v;
			}
			v = MathUtils.Abs(2 * pi - targetAngle + currentAngle);
			if (v < diff)
			{
				diff = v;
			}
			return diff;
		}

		public static  Vector2f RotateVector(Vector2f v, Vector2f center, float angle)
		{
			Vector2f result = new Vector2f();
			float x = v.x - center.x;
			float y = v.y - center.y;
			result.x = MathUtils.Cos(angle) * x - MathUtils.Sin(angle) * y + center.x;
			result.y = MathUtils.Sin(angle) * x + MathUtils.Cos(angle) * y + center.y;
			return result;
		}

		public static Triangle Triangulate(Vector2f[] vertices)
		{
			return Triangulate(new TriangleBasic(), vertices);
		}

		public static  Triangle Triangulate(Triangle triangulator, Vector2f[] vertices)
		{
			int size = vertices.Length;
			for (int i = 0; i < size; i++)
			{
				triangulator.AddPolyPoint(vertices[i].x, vertices[i].y);
			}
			triangulator.Triangulate();
			return triangulator;
		}

		public static  void CalculateCenter(Vector2f[] vertices, Vector2f center)
		{
			center.x = 0f;
			center.y = 0f;
			for (int i = 0; i < vertices.Length; i++)
			{
				center.x += vertices[i].x;
				center.y += vertices[i].y;
			}
			center.x /= vertices.Length;
			center.y /= vertices.Length;
		}

		public static  void TranslateVertices(Vector2f[] vertices, Vector2f tx)
		{
			for (int i = 0; i < vertices.Length; i++)
			{
				vertices[i].AddSelf(tx.x, tx.y);
			}
		}

		public static  void CalculateBounds(Vector2f[] vertices, RectBox bounds)
		{
			bounds.x = Integer.MAX_VALUE_JAVA;
			bounds.y = Integer.MAX_VALUE_JAVA;

			bounds.width = -Integer.MAX_VALUE_JAVA;
			bounds.height = -Integer.MAX_VALUE_JAVA;

			for (int i = 0; i < vertices.Length; i++)
			{
				Vector2f v = vertices[i];

				if (v.x < bounds.x)
					bounds.x = v.x;

				if (v.y < bounds.y)
					bounds.y = v.y;

				if (v.x > bounds.x + bounds.width)
				{
					bounds.width = (int)(v.x - bounds.x);
				}

				if (v.y > bounds.y + bounds.height)
				{
					bounds.height = (int)(v.y - bounds.y);
				}
			}
		}

		public void Rotate(Vector2f[] vertices, float angle)
		{
			for (int i = 0; i < vertices.Length; i++)
			{
				vertices[i].RotateSelf(angle);
			}
		}

		public static  void CalculateConvexHull(TArray<Vector2f> points, TArray<Vector2f> convexHullPoints)
		{
			if (points.size <= 1)
			{
				return;
			}
			Vector2f p;
			Vector2f bot = points.Get(0);
			for (int i = 1; i < points.size; i++)
			{
				Vector2f point = points.Get(i);
				if (point.y < bot.y)
					bot = point;
			}
			convexHullPoints.Add(bot);
			p = bot;
			do
			{
				int i;
				i = points.Get(0) == p ? 1 : 0;
				Vector2f cand = points.Get(i);

				for (i += 1; i < points.size; i++)
				{
					Vector2f point = points.Get(i);
					if (point != p && Area(p, cand, point) > 0)
						cand = points.Get(i);
				}
				convexHullPoints.Add(cand);
				p = cand;
			} while (p != bot);
		}

		public static  float Area(Vector2f a, Vector2f b, Vector2f c)
		{
			return Area(a.x, a.y, b.x, b.y, c.x, c.y);
		}

		public static  float Area(float x0, float y0, float x1, float y1, float x2, float y2)
		{
			return x1 * y2 - y1 * x2 + x2 * y0 - y2 * x0 + x0 * y1 - y0 * x1;
		}

		public static  float GetScaleFactor(float srcSize, float dstSize)
		{
            float dScale;
            if (srcSize > dstSize)
			{
				dScale = dstSize / srcSize;
			}
			else
			{
				dScale = dstSize / srcSize;
			}
			return dScale;
		}

		public static  float GetScaleFactorToFit(float ox, float oy, float nx, float ny)
		{
			float dScaleWidth = GetScaleFactor(ox, nx);
			float dScaleHeight = GetScaleFactor(oy, ny);
			return MathUtils.Min(dScaleHeight, dScaleWidth);
		}

		public static  float SnapToNearest(float number, float interval)
		{
			interval = MathUtils.Abs(interval);
			if (interval == 0)
			{
				return number;
			}
			return MathUtils.Round(number / interval) * interval;
		}

		public static  float LockAtIntervals(float number, float interval)
		{
			interval = MathUtils.Abs(interval);
			if (interval == 0)
			{
				return number;
			}
			return ((int)(number / interval)) * interval;
		}

		public static  float CalcRotationAngleInDegrees(float x, float y, float tx, float ty)
		{
			float theta = MathUtils.Atan2(tx - x, ty - y);
			float angle = theta * MathUtils.RAD_TO_DEG;
			if (angle < 0)
			{
				angle += 360;
			}
			angle += 180;
			return angle;
		}

		public static  float CalcRotationAngleInRadians(float x, float y, float tx, float ty)
		{
			return CalcRotationAngleInDegrees(x, y, tx, ty) * MathUtils.DEG_TO_RAD;
		}

		public static  float CalcRadiansDiff(float x, float y, float tx, float ty)
		{
			float d = CalcRotationAngleInDegrees(x, y, tx, ty);
			d -= 90;
			d %= 360;
			return MathUtils.ToRadians(d);
		}

		public static  int Dot(Vector2f v1s, Vector2f v1e, Vector2f v2s, Vector2f v2e)
		{
			return (int)((v1e.x - v1s.x) * (v2e.x - v2s.x) + (v1e.y - v1s.y) * (v2e.y - v2s.y));
		}

		public static  int Dot(int v1sx, int v1sy, int v1ex, int v1ey, int v2sx, int v2sy, int v2ex, int v2ey)
		{
			return ((v1ex - v1sx) * (v2ex - v2sx) + (v1ey - v1sy) * (v2ey - v2sy));
		}

		public static  int Dot(Vector2f vs, Vector2f v1e, Vector2f v2e)
		{
			return (int)((v1e.x - vs.x) * (v2e.x - vs.x) + (v1e.y - vs.y) * (v2e.y - vs.y));
		}

		public static  int Dot(int vsx, int vsy, int v1ex, int v1ey, int v2ex, int v2ey)
		{
			return ((v1ex - vsx) * (v2ex - vsx) + (v1ey - vsy) * (v2ey - vsy));
		}

		public static  float Dotf(float vsx, float vsy, float v1ex, float v1ey, float v2ex, float v2ey)
		{
			return ((v1ex - vsx) * (v2ex - vsx) + (v1ey - vsy) * (v2ey - vsy));
		}

		public static  void TransPointList(float[] points, float x, float y)
		{
			int len = points.Length;
			for (int i = 0; i < len; i += 2)
			{
				points[i] += x;
				points[i + 1] += y;
			}
		}

		public static  void TransPointList(int[] points, int x, int y)
		{
			int len = points.Length;
			for (int i = 0; i < len; i += 2)
			{
				points[i] += x;
				points[i + 1] += y;
			}
		}

		public static  float PtSegDist(float x1, float y1, float x2, float y2, float px, float py)
		{
			return MathUtils.Sqrt(PtSegDistSq(x1, y1, x2, y2, px, py));
		}

		public static  float PtSegDistSq(float x1, float y1, float x2, float y2, float px, float py)
		{
			x2 -= x1;
			y2 -= y1;
			px -= x1;
			py -= y1;
			float dotprod = px * x2 + py * y2;
			float projlenSq;
			if (dotprod <= 0.0)
			{
				projlenSq = 0.0f;
			}
			else
			{
				px = x2 - px;
				py = y2 - py;
				dotprod = px * x2 + py * y2;
				if (dotprod <= 0.0)
				{
					projlenSq = 0.0f;
				}
				else
				{
					projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
				}
			}
			float lenSq = px * px + py * py - projlenSq;
			if (lenSq < 0)
			{
				lenSq = 0;
			}
			return lenSq;
		}

		public static  float PtLineDist(float x1, float y1, float x2, float y2, float px, float py)
		{
			return MathUtils.Sqrt(PtLineDistSq(x1, y1, x2, y2, px, py));
		}

		public static  float PtLineDistSq(float x1, float y1, float x2, float y2, float px, float py)
		{
			x2 -= x1;
			y2 -= y1;
			px -= x1;
			py -= y1;
			float dotprod = px * x2 + py * y2;
			float projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
			float lenSq = px * px + py * py - projlenSq;
			if (lenSq < 0)
			{
				lenSq = 0;
			}
			return lenSq;
		}

	}
}

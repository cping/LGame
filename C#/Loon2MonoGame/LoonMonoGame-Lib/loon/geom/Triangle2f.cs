
using loon.utils;

namespace loon.geom
{

	public class Triangle2f : Shape , Triangle
	{

	public static Triangle2f At(float x, float y, float w, float h)
	{
		return new Triangle2f(x + w / 2, y + h / 2, w, h);
	}

	public float[] xpoints;

	public float[] ypoints;

	public Triangle2f()
	{
		xpoints = new float[3];
		ypoints = new float[3];
	}

	public Triangle2f(float w, float h):this()
	{
		Set(w, h);
	}

	public Triangle2f(float x, float y, float w, float h): this()
	{
		Set(x, y, w, h);
	}

	public Triangle2f(Vector2f t1, Vector2f t2, Vector2f t3): this(t1.x, t1.y, t2.x, t2.y, t3.x, t3.y)
	{
		
	}

	public Triangle2f(float x1, float y1, float x2, float y2, float x3, float y3) : this()
	{
		float dx1 = x2 - x1;
		float dx2 = x3 - x1;
		float dy1 = y2 - y1;
		float dy2 = y3 - y1;
		float cross = dx1 * dy2 - dx2 * dy1;
		bool ccw = (cross > 0);
		if (ccw)
		{
			xpoints[0] = x1;
			xpoints[1] = x2;
			xpoints[2] = x3;
			ypoints[0] = y1;
			ypoints[1] = y2;
			ypoints[2] = y3;
		}
		else
		{
			xpoints[0] = x1;
			xpoints[1] = x3;
			xpoints[2] = x2;
			ypoints[0] = y1;
			ypoints[1] = y3;
			ypoints[2] = y2;
		}
	}

	public float GetX1()
	{
		return xpoints[0];
	}

	public float GetX2()
	{
		return xpoints[1];
	}

	public float GetX3()
	{
		return xpoints[2];
	}

	public float GetY1()
	{
		return ypoints[0];
	}

	public float GetY2()
	{
		return ypoints[1];
	}

	public float GetY3()
	{
		return ypoints[2];
	}

	protected void ConvertPoints(float[] points)
	{
		int size = points.Length / 2;
		for (int i = 0, j = 0; i < size; i += 2, j++)
		{
			xpoints[j] = points[i];
			ypoints[j] = points[i + 1];
		}
	}

	public float[] GetVertexs()
	{
		int vertice_size = xpoints.Length * 2;
		float[] verts = new float[vertice_size];
		for (int i = 0, j = 0; i < vertice_size; i += 2, j++)
		{
			verts[i] = xpoints[j];
			verts[i + 1] = ypoints[j];
		}
		return verts;
	}

	public void Set(float w, float h)
	{
		Set(w / 2 - 1, h / 2 - 1, w - 1, h - 1);
	}

	public void Set(float x, float y, float w, float h)
	{
		float halfWidth = w / 2;
		float halfHeight = h / 2;
		float top = -halfWidth;
		float bottom = halfHeight;
		float left = -halfHeight;
		float center = 0;
		float right = halfWidth;

		xpoints[0] = x + center;
		xpoints[1] = x + right;
		xpoints[2] = x + left;
		ypoints[0] = y + top;
		ypoints[1] = y + bottom;
		ypoints[2] = y + bottom;

		UpdateTriangle(6);
	}

	public void Set(Triangle2f t)
	{
		xpoints[0] = t.xpoints[0];
		xpoints[1] = t.xpoints[1];
		xpoints[2] = t.xpoints[2];
		ypoints[0] = t.ypoints[0];
		ypoints[1] = t.ypoints[1];
		ypoints[2] = t.ypoints[2];
		UpdateTriangle(6);
	}

	protected void UpdateTriangle(int length)
	{

		if (points == null || points.Length != length)
		{
			this.points = new float[length];
		}

		for (int i = 0, j = 0; i < length; i += 2, j++)
		{
			points[i] = xpoints[j];
			points[i + 1] = ypoints[j];
		}

		for (int i = 0; i < length; i++)
		{
			this.points[i] = points[i];
			if (i % 2 == 0)
			{
				if (points[i] > maxX)
				{
					maxX = points[i];
				}
				if (points[i] < minX)
				{
					minX = points[i];
				}
				if (points[i] < x)
				{
					x = points[i];
				}
			}
			else
			{
				if (points[i] > maxY)
				{
					maxY = points[i];
				}
				if (points[i] < minY)
				{
					minY = points[i];
				}
				if (points[i] < y)
				{
					y = points[i];
				}
			}
		}

		FindCenter();
		CalculateRadius();
		pointsDirty = true;
	}

	public bool IsInside(XY p)
	{
		return IsInside(new Vector2f(GetX1(), GetY1()), new Vector2f(GetX2(), GetY2()), new Vector2f(GetX3(), GetY3()),
				p);
	}

	public static bool IsInside(XY x, XY y, XY z, XY p)
	{
		Vector2f v1 = new Vector2f(y.GetX() - x.GetX(), y.GetY() - x.GetY());
		Vector2f v2 = new Vector2f(z.GetX() - x.GetX(), z.GetY() - x.GetY());

		float det = v1.x * v2.y - v2.x * v1.y;
		Vector2f tmp = new Vector2f(p.GetX() - x.GetX(), p.GetY() - x.GetY());
		float lambda = (tmp.x * v2.y - v2.x * tmp.y) / det;
		float mue = (v1.x * tmp.y - tmp.x * v1.y) / det;

		return (lambda > 0 && mue > 0 && (lambda + mue) < 1);
	}

	public bool IsInside(float nx, float ny)
	{
		float vx2 = nx - xpoints[0];
		float vy2 = ny - ypoints[0];
		float vx1 = xpoints[1] - xpoints[0];
		float vy1 = ypoints[1] - ypoints[0];
		float vx0 = xpoints[2] - xpoints[0];
		float vy0 = ypoints[2] - ypoints[0];

		float dot00 = vx0 * vx0 + vy0 * vy0;
		float dot01 = vx0 * vx1 + vy0 * vy1;
		float dot02 = vx0 * vx2 + vy0 * vy2;
		float dot11 = vx1 * vx1 + vy1 * vy1;
		float dot12 = vx1 * vx2 + vy1 * vy2;
		float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
		float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		return ((u > 0) && (v > 0) && (u + v < 1));
	}

	public bool ContainsPoint(Vector2f v)
	{
		if (v == null)
		{
			return false;
		}
		return ContainsPoint(v.x, v.y);
	}

	public bool ContainsPoint(float nx, float ny)
	{
		float vx2 = nx - xpoints[0];
		float vy2 = ny - ypoints[0];
		float vx1 = xpoints[1] - xpoints[0];
		float vy1 = ypoints[1] - ypoints[0];
		float vx0 = xpoints[2] - xpoints[0];
		float vy0 = ypoints[2] - ypoints[0];

		float dot00 = vx0 * vx0 + vy0 * vy0;
		float dot01 = vx0 * vx1 + vy0 * vy1;
		float dot02 = vx0 * vx2 + vy0 * vy2;
		float dot11 = vx1 * vx1 + vy1 * vy1;
		float dot12 = vx1 * vx2 + vy1 * vy2;
		float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
		float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		return ((u >= 0) && (v >= 0) && (u + v <= 1));
	}

	public PointF GetPointCenter()
	{
		return new PointF((xpoints[0] + xpoints[1] + xpoints[2]) / 3, (ypoints[0] + ypoints[1] + ypoints[2]) / 3);
	}

	protected override void CreatePoints()
	{
	}

	public bool Triangulate()
	{
		return true;
	}

	public int GetTriangleCount()
	{
		return 1;
	}

	public float[] GetTrianglePoint(int t, int i)
	{
		return null;
	}

	public void AddPolyPoint(float x, float y)
	{
	}

	public void StartHole()
	{
	}

	
	public override Shape Transform(Matrix3 transform)
	{
		CheckPoints();
		Triangle2f resultTriangle = new Triangle2f();
		float[] result = new float[points.Length];
		transform.Transform(points, 0, result, 0, points.Length / 2);
		resultTriangle.points = result;
		resultTriangle.FindCenter();
		resultTriangle.ConvertPoints(points);
		return resultTriangle;
	}

	
	public override string ToString()
	{
		StringKeyValue builder = new StringKeyValue("Triangle");
		builder.Kv("xpoints", "[" + StringUtils.Join(',', xpoints) + "]").Comma()
				.Kv("ypoints", "[" + StringUtils.Join(',', ypoints) + "]").Comma()
				.Kv("center", "[" + StringUtils.Join(',', center) + "]").Comma().Kv("rotation", rotation).Comma()
				.Kv("minX", minX).Comma().Kv("minY", minY).Comma().Kv("maxX", maxX).Comma().Kv("maxY", maxY);
		return builder.ToString();
	}

}
}

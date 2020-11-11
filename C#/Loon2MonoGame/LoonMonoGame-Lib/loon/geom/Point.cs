using java.lang;
using loon.utils;

namespace loon.geom
{
	public class Point : Shape
	{

	public static Point At(string v)
	{
		if (StringUtils.IsEmpty(v))
		{
			return new Point();
		}
		string[] result = StringUtils.Split(v, ',');
		int len = result.Length;
		if (len > 1)
		{
			try
			{
				float x = Float.ParseFloat(result[0].Trim());
				float y = Float.ParseFloat(result[1].Trim());
				return new Point(x, y);
			}
			catch (Exception)
			{
			}
		}
		return new Point();
	}

	public static Point At(float x, float y)
	{
		return new Point(x, y);
	}

	public int clazz;

	public const int POINT_CONVEX = 1;

	public const int POINT_CONCAVE = 2;

	public Point(): this(0f, 0f)
	{
		
	}

	public Point(float x, float y)
	{
		this.CheckPoints();
		this.SetLocation(x, y);
	}

	public Point(Point p)
	{
		this.CheckPoints();
		this.SetLocation(p);
	}

	public override Shape Transform(Matrix3 transform)
	{
		float[] result = new float[points.Length];
		transform.Transform(points, 0, result, 0, points.Length / 2);
		return new Point(points[0], points[1]);
	}

	protected override void CreatePoints()
	{
		if (points == null)
		{
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

	protected internal override void FindCenter()
	{
		if (center == null)
		{
			center = new float[2];
		}
		center[0] = points[0];
		center[1] = points[1];
	}

	protected override void CalculateRadius()
	{
		boundingCircleRadius = 0;
	}

	public void Set(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public override void SetLocation(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public void SetLocation(Point p)
	{
		this.x = p.GetX();
		this.y = p.GetY();
	}

	public void Translate(float dx, float dy)
	{
		this.x += dx;
		this.y += dy;
	}

	public void Translate(Point p)
	{
		this.x += p.x;
		this.y += p.y;
	}

	public void Untranslate(Point p)
	{
		this.x -= p.x;
		this.y -= p.y;
	}

	public int DistanceTo(Point p)
	{
		float tx = (this.x - p.x);
		float ty = (this.y - p.y);
		return (int)MathUtils.Sqrt(MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
	}

	public int DistanceTo(int x, int y)
	{
		float tx = (int)(this.x - x);
		float ty = (int)(this.y - y);
		return (int)MathUtils.Sqrt(MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
	}

	public void GetLocation(Point dest)
	{
		dest.SetLocation(this.x, this.y);
	}

	public Point Random()
	{
		this.x = MathUtils.Random(0f, LSystem.viewSize.GetWidth());
		this.y = MathUtils.Random(0f, LSystem.viewSize.GetHeight());
		return this;
	}

	public override int GetHashCode()
	{
		uint prime = 31;
		uint result = 1;
		result = prime * result + NumberUtils.FloatToIntBits(x);
		result = prime * result + NumberUtils.FloatToIntBits(y);
		return (int)result;
	}

	public override bool Equals(object obj)
	{
		Point p = (Point)obj;
		return p.x == this.x && p.y == this.y && p.clazz == this.clazz;
	}

	public override string ToString()
	{
		return "(" + x + "," + y + ")";
	}

}

}

using java.lang;
using loon.action.map;
using loon.utils;

namespace loon.geom
{
	public class Line : Shape
	{

	public static Line At(string v)
	{
		if (StringUtils.IsEmpty(v))
		{
			return new Line();
		}
		string[] result = StringUtils.Split(v, ',');
		int len = result.Length;
		if (len > 3)
		{
			try
			{
				float x = Float.ParseFloat(result[0].Trim());
				float y = Float.ParseFloat(result[1].Trim());
				float x1 = Float.ParseFloat(result[2].Trim());
				float y1 = Float.ParseFloat(result[3].Trim());
				return new Line(x, y, x1, y1);
			}
			catch (Exception)
			{
			}
		}
		return new Line();
	}

	public static Line At(float x1, float y1, float x2, float y2)
	{
		return new Line(x1, y1, x2, y2);
	}

	private Vector2f start;

	private Vector2f end;

	private Vector2f vec;

	private readonly Vector2f loc = new Vector2f(0, 0);

	private readonly Vector2f closest = new Vector2f(0, 0);

	public Line(): this(0f, 0f)
	{
		
	}

	public Line(float x, float y, bool inner, bool outer): this(0, 0, x, y)
	{
		
	}

	public Line(float x, float y): this(x, y, true, true)
	{
		
	}

	public Line(XY p1, XY p2): this(p1.GetX(), p1.GetY(), p2.GetX(), p2.GetY())
	{
		
	}

	public Line(float x1, float y1, float x2, float y2): this(new Vector2f(x1, y1), new Vector2f(x2, y2))
	{
		
	}

	public Line(float x1, float y1, float dx, float dy, bool dummy): this(new Vector2f(x1, y1), new Vector2f(x1 + dx, y1 + dy))
	{
		
	}

	public Line(float[] start, float[] end): base()
	{
		
		Set(start, end);
	}

	public Line(Vector2f start, Vector2f end):base()
	{
		Set(start, end);
	}

	public void Set(float[] start, float[] end)
	{
		Set(start[0], start[1], end[0], end[1]);
	}

	public Vector2f GetStart()
	{
		return start.Cpy();
	}

	public Vector2f GetEnd()
	{
		return end.Cpy();
	}

	public Vector2f GetDirectionValue()
	{
		return Field2D.GetDirection(GetDirection());
	}

	public int GetDirection()
	{
		return Field2D.GetDirection(end.X() - start.X(), end.Y() - start.Y(), Config.EMPTY);
	}

	public override float Length()
	{
		return (int)MathUtils
				.Sqrt((GetX2() - GetX1()) * (GetX2() - GetX2()) + (GetY2() - GetY1()) * (GetY2() - GetY1()));
	}

	public float LengthSquared()
	{
		return vec.LengthSquared();
	}

	public void Set(Vector2f start, Vector2f end)
	{
		base.pointsDirty = true;
		if (this.start == null)
		{
			this.start = new Vector2f();
		}
		this.start.Set(start);

		if (this.end == null)
		{
			this.end = new Vector2f();
		}
		this.end.Set(end);

		vec = new Vector2f(end);
		vec.Sub(start);

		this.SetLocation(start.x, start.y);
	}

	public void Set(float sx, float sy, float ex, float ey)
	{
		base.pointsDirty = true;
		start.Set(sx, sy);
		end.Set(ex, ey);
		float dx = (ex - sx);
		float dy = (ey - sy);
		vec.Set(dx, dy);
	}

	public float GetDX()
	{
		return end.GetX() - start.GetX();
	}

	public float GetDY()
	{
		return end.GetY() - start.GetY();
	}

	
	public override float GetX()
	{
		return GetX1();
	}

	
	public override float GetY()
	{
		return GetY1();
	}

	public float GetX1()
	{
		return start.GetX();
	}

	public float GetY1()
	{
		return start.GetY();
	}

	public float GetX2()
	{
		return end.GetX();
	}

	public float GetY2()
	{
		return end.GetY();
	}

	public TArray<Point> GetBresenhamPoints(int stepRate)
	{
		return GetBresenhamPoints(stepRate, null);
	}

	public TArray<Point> GetBresenhamPoints(int stepRate, TArray<Point> points)
	{

		stepRate = MathUtils.Max(1, stepRate);
		if (points == null)
		{
			points = new TArray<Point>();
		}

		int x1 = MathUtils.Round(GetX1());
		int y1 = MathUtils.Round(GetY1());
		int x2 = MathUtils.Round(GetX2());
		int y2 = MathUtils.Round(GetY2());

		int dx = MathUtils.Abs(x2 - x1);
		int dy = MathUtils.Abs(y2 - y1);
		int sx = (x1 < x2) ? 1 : -1;
		int sy = (y1 < y2) ? 1 : -1;
		int err = (dx - dy);

		points.Add(Point.At(x1, y1));

		float count = 1;

		for (; !((x1 == x2) && (y1 == y2));)
		{
			int e2 = err * 2;
			if (e2 > -dy)
			{
				err -= dy;
				x1 += sx;
			}
			if (e2 < dx)
			{
				err += dx;
				y1 += sy;
			}
			if (count % stepRate == 0)
			{
				points.Add(Point.At(x1, y1));
			}
			count++;
		}
		return points;
	}

	public float Distance(Vector2f point)
	{
		return MathUtils.Sqrt(DistanceSquared(point));
	}

	public bool On(Vector2f point)
	{
		GetClosestPoint(point, closest);
		return point.Equals(closest);
	}

	public float DistanceSquared(Vector2f point)
	{
		GetClosestPoint(point, closest);
		closest.Sub(point);
		float result = closest.LengthSquared();
		return result;
	}

	public void GetClosestPoint(Vector2f point, Vector2f result)
	{
		loc.Set(point);
		loc.Sub(start);

		float projDistance = vec.Dot(loc);

		projDistance /= vec.LengthSquared();

		if (projDistance < 0)
		{
			result.Set(start);
			return;
		}
		if (projDistance > 1)
		{
			result.Set(end);
			return;
		}

		result.x = start.GetX() + projDistance * vec.GetX();
		result.y = start.GetY() + projDistance * vec.GetY();
	}

	public bool Intersects(Line other)
	{
		return Intersects(other, null);
	}

	public bool Intersects(Line other, Vector2f result)
	{
		if (other == null)
		{
			return false;
		}

		float x1 = GetX1();
		float y1 = GetY1();
		float x2 = GetX2();
		float y2 = GetY2();

		float x3 = other.GetX1();
		float y3 = other.GetY1();
		float x4 = other.GetX2();
		float y4 = other.GetY2();

		float numA = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
		float numB = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);
		float denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

		if (denom == 0)
		{
			return false;
		}

		float uA = numA / denom;
		float uB = numB / denom;

		if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1)
		{
			if (result != null)
			{
				result.x = x1 + (uA * (x2 - x1));
				result.y = y1 + (uA * (y2 - y1));
			}
			return true;
		}

		return false;
	}

	public bool Intersects(RectBox rect)
	{
		if (rect == null)
		{
			return false;
		}

		float x1 = start.GetX();
		float y1 = start.GetY();

		float x2 = end.GetX() + start.GetX();
		float y2 = end.GetY() + start.GetY();

		float bx1 = rect.x;
		float by1 = rect.y;
		float bx2 = rect.GetRight();
		float by2 = rect.GetBottom();

		float t = 0;
		if ((x1 >= bx1 && x1 <= bx2 && y1 >= by1 && y1 <= by2) || (x2 >= bx1 && x2 <= bx2 && y2 >= by1 && y2 <= by2))
		{
			return true;
		}

		if (x1 < bx1 && x2 >= bx1)
		{
			t = y1 + (y2 - y1) * (bx1 - x1) / (x2 - x1);
			if (t > by1 && t <= by2)
			{
				return rect.Intersects(this);
			}
		}
		else if (x1 > bx2 && x2 <= bx2)
		{
			t = y1 + (y2 - y1) * (bx2 - x1) / (x2 - x1);
			if (t >= by1 && t <= by2)
			{
				return rect.Intersects(this);
			}
		}

		if (y1 < by1 && y2 >= by1)
		{
			t = x1 + (x2 - x1) * (by1 - y1) / (y2 - y1);
			if (t >= bx1 && t <= bx2)
			{
				return rect.Intersects(this);
			}

		}
		else if (y1 > by2 && y2 <= by2)
		{
			t = x1 + (x2 - x1) * (by2 - y1) / (y2 - y1);
			if (t >= bx1 && t <= bx2)
			{
				return rect.Intersects(this);
			}
		}

		return false;
	}

	public float Side(XY v)
	{
		if (v == null)
		{
			return 0f;
		}
		return Side(v.GetX(), v.GetY());
	}

	public float Side(float x, float y)
	{
		return (end.x - start.x) * (y - start.y) - (end.y - start.y) * (x - start.x);
	}

	public Vector2f GetMidPoint()
	{
		Vector2f outs = new Vector2f();
		outs.x = (GetX1() + GetX2()) / 2f;
		outs.y = (GetY1() + GetY2()) / 2f;
		return outs;
	}

	public Vector2f Project(Vector2f v)
	{
		if (v == null)
		{
			return Vector2f.ZERO();
		}
		return Project(v.x, v.y);
	}

	public Vector2f Project(float x, float y)
	{
		float dx = end.x - start.x;
		float dy = end.y - start.y;
		float k = ((x - start.x) * dx + (y - start.y) * dy) / (dx * dx + dy * dy);
		return new Vector2f(dx * k + start.x, dy * k + start.y);
	}

	protected override void CreatePoints()
	{
		points = new float[4];
		points[0] = GetX1();
		points[1] = GetY1();
		points[2] = GetX2();
		points[3] = GetY2();
	}

	public float Slope()
	{
		return (this.GetY2() - this.GetY1()) / (this.GetX2() - this.GetX1());
	}

	public float PerpSlope()
	{
		return -((this.GetX2() - this.GetX1()) / (this.GetY2() - this.GetY1()));
	}

	public float Angle()
	{
		return MathUtils.Atan2(this.GetY2() - this.GetY1(), this.GetX2() - this.GetX1());
	}

	public bool IsPointOnLine(float x, float y)
	{
		return (x - this.GetX1()) * (this.GetY2() - this.GetY1()) == (this.GetX2() - this.GetX1()) * (y - this.GetY1());
	}

	public bool IsPointOnLineSegment(float x, float y)
	{
		float xMin = MathUtils.Min(this.GetX1(), this.GetX2());
		float xMax = MathUtils.Max(this.GetX1(), this.GetX2());
		float yMin = MathUtils.Min(this.GetY1(), this.GetY2());
		float yMax = MathUtils.Max(this.GetY1(), this.GetY2());
		return this.IsPointOnLine(x, y) && (x >= xMin && x <= xMax) && (y >= yMin && y <= yMax);
	}

	public bool IsPointOnRay(float x, float y)
	{
		if ((x - this.GetX1()) * (this.GetY2() - this.GetY1()) == (this.GetX2() - this.GetX1()) * (y - this.GetY1()))
		{
			if (MathUtils.Atan2(y - this.GetY1(), x - this.GetX1()) == MathUtils.Atan2(this.GetY2() - this.GetY1(),
					this.GetX2() - this.GetX1()))
			{
				return true;
			}
		}
		return false;
	}

	public override Shape Transform(Matrix3 transform)
	{
		float[] temp = new float[4];
		CreatePoints();
		transform.Transform(points, 0, temp, 0, 2);
		return new Line(temp[0], temp[1], temp[2], temp[3]);
	}

	public override bool Closed()
	{
		return false;
	}


	public override int GetHashCode()
	{
		uint prime = 31;
		uint result = 1;
		result = prime * result + NumberUtils.FloatToIntBits(start.GetHashCode());
		result = prime * result + NumberUtils.FloatToIntBits(end.GetHashCode());
		return (int)result;
	}

	public override string ToString()
	{
		return "(" + GetX1() + "," + GetY1() + "," + GetX2() + "," + GetY2() + ")";
	}
}

}

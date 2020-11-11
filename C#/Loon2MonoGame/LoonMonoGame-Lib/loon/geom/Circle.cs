using java.lang;
using loon.utils;

namespace loon.geom
{

	public class Circle : Ellipse
	{

	public static Circle At(string v)
	{
		if (StringUtils.IsEmpty(v))
		{
			return new Circle();
		}
		string[] result = StringUtils.Split(v, ',');
		int len = result.Length;
		if (len > 2)
		{
			try
			{
				float x = Float.ParseFloat(result[0].Trim());
				float y = Float.ParseFloat(result[1].Trim());
				float r = Float.ParseFloat(result[2].Trim());
				return new Circle(x, y, r);
			}
			catch (Exception)
			{
			}
		}
		return new Circle();
	}

	public static Circle At(float centerPointX, float centerPointY, float r)
	{
		return new Circle(centerPointX, centerPointY, r);
	}

	public static Circle At(float centerPointX, float centerPointY, float w, float h)
	{
		float radius = MathUtils.Max(w, h);
		return new Circle(centerPointX, centerPointY, radius);
	}

	public Circle(): this(0f, 0f, 0f)
	{
		
	}

	public Circle(float centerPointX, float centerPointY, float boundingCircleRadius): this(centerPointX, centerPointY, boundingCircleRadius, DEFAULT_SEGMENT_MAX_COUNT)
	{
		
	}

	public Circle(float centerPointX, float centerPointY, float boundingCircleRadius, int segment):base(centerPointX, centerPointY, boundingCircleRadius, boundingCircleRadius, segment)
	{
		
		this.x = centerPointX;
		this.y = centerPointY;
		this.boundingCircleRadius = boundingCircleRadius;
		this.SetLocation(x, y);
		this.CheckPoints();
	}

	public override float GetCenterX()
	{
		return GetX() + boundingCircleRadius;
	}

	public override float GetCenterY()
	{
		return GetY() + boundingCircleRadius;
	}

	public void SetRadius(float boundingCircleRadius)
	{
		if (boundingCircleRadius != this.boundingCircleRadius)
		{
			pointsDirty = true;
			this.boundingCircleRadius = boundingCircleRadius;
			SetRadii(boundingCircleRadius, boundingCircleRadius);
		}
	}

	public float GetRadius()
	{
		return boundingCircleRadius;
	}

	public override bool Intersects(Shape shape)
	{
		if (shape is Circle) {
			return CollideCircle((Circle)shape);
		} else if (shape is RectBox) {
			return Intersects((RectBox)shape);
		} else
		{
			return base.Intersects(shape);
		}
	}

	public bool Intersects(RectBox other)
	{
		RectBox box = other;
		if (box.Contains(x + boundingCircleRadius, y + boundingCircleRadius))
		{
			return true;
		}
		return CollideBounds(other);
	}

	public bool Contains(XY xy)
	{
		if (xy == null)
		{
			return false;
		}
		return Contains(xy.GetX(), xy.GetY());
	}

	public bool Contains(Line line)
	{
		if (line == null)
		{
			return false;
		}
		return Contains(line.GetX1(), line.GetY1()) && Contains(line.GetX2(), line.GetY2());
	}

	
	protected internal override void FindCenter()
	{
		center = new float[2];
		center[0] = x + boundingCircleRadius;
		center[1] = y + boundingCircleRadius;
	}

	public float Side(Vector2f v)
	{
		if (v == null)
		{
			return 0f;
		}
		return Side(v.x, v.y);
	}

	public float Side(float px, float py)
	{
		float dx = px - x;
		float dy = py - y;
		return boundingCircleRadius * boundingCircleRadius - (dx * dx + dy * dy);
	}

	public bool CollideCircle(Circle c)
	{
		float dx = x - c.x;
		float dy = y - c.y;
		return dx * dx + dy * dy < (boundingCircleRadius + c.boundingCircleRadius)
				* (boundingCircleRadius + c.boundingCircleRadius);
	}

	public bool CollideBounds(RectBox size)
	{
		float radiusDouble = boundingCircleRadius * boundingCircleRadius;
		if (x < size.GetX() - boundingCircleRadius)
		{
			return false;
		}
		if (x > size.GetBottom() + boundingCircleRadius)
		{
			return false;
		}
		if (y < size.GetY() - boundingCircleRadius)
		{
			return false;
		}
		if (y > size.GetBottom() + boundingCircleRadius)
		{
			return false;
		}
		if (x < size.GetX() && y < size.GetY() && MathUtils.Distance(x - size.GetX(), y - size.GetY()) > radiusDouble)
		{
			return false;
		}
		if (x > size.GetRight() && y < size.GetY()
				&& MathUtils.Distance(x - size.GetRight(), y - size.GetY()) > radiusDouble)
		{
			return false;
		}
		if (x < size.GetX() && y > size.GetBottom()
				&& MathUtils.Distance(x - size.GetX(), y - size.GetBottom()) > radiusDouble)
		{
			return false;
		}
		if (x > size.GetRight() && y > size.GetBottom()
				&& MathUtils.Distance(x - size.GetRight(), y - size.GetBottom()) > radiusDouble)
		{
			return false;
		}
		return true;
	}

	public bool Intersects(Line other)
	{
		Vector2f lineSegmentStart = new Vector2f(other.GetX1(), other.GetY1());
		Vector2f lineSegmentEnd = new Vector2f(other.GetX2(), other.GetY2());
		Vector2f circleCenter = new Vector2f(GetCenterX(), GetCenterY());
		Vector2f closest;
		Vector2f segv = lineSegmentEnd.Sub(lineSegmentStart);
		Vector2f ptv = circleCenter.Sub(lineSegmentStart);
		float segvLength = segv.Len();
		float projvl = ptv.Dot(segv) / segvLength;
		if (projvl < 0)
		{
			closest = lineSegmentStart;
		}
		else if (projvl > segvLength)
		{
			closest = lineSegmentEnd;
		}
		else
		{
			Vector2f projv = segv.Mul(projvl / segvLength);
			closest = lineSegmentStart.Add(projv);
		}
		bool Intersects = circleCenter.Sub(closest).LengthSquared() <= GetRadius() * GetRadius();
		return Intersects;
	}

	
	public override bool Contains(Shape other)
	{
		if (other is Circle) {
			return Contains((Circle)other);
		}
		return base.Contains(other);
	}

	public float GetLeft()
	{
		return this.x - this.boundingCircleRadius;
	}

	public float GetRight()
	{
		return this.x + this.boundingCircleRadius;
	}

	public float GetTop()
	{
		return this.y - this.boundingCircleRadius;
	}

	public float GetBottom()
	{
		return this.y + this.boundingCircleRadius;
	}

	public bool Contains(Circle c)
	{
		 float radiusDiff = boundingCircleRadius - c.boundingCircleRadius;
		if (radiusDiff < 0f)
		{
			return false;
		}
		 float dx = x - c.x;
		 float dy = y - c.y;
		 float dst = dx * dx + dy * dy;
		 float radiusSum = boundingCircleRadius + c.boundingCircleRadius;
		return (!(radiusDiff * radiusDiff < dst) && (dst < radiusSum * radiusSum));
	}

	public float DistanceTo(XY tarGet)
	{
		return DistanceTo(tarGet, false);
	}

	public float DistanceTo(XY tarGet, bool round)
	{
		if (tarGet == null)
		{
			return 0f;
		}
		float dx = this.x - tarGet.GetX();
		float dy = this.y - tarGet.GetY();
		if (round)
		{
			return MathUtils.Round(MathUtils.Sqrt(dx * dx + dy * dy));
		}
		else
		{
			return MathUtils.Sqrt(dx * dx + dy * dy);
		}
	}

	public float CircumferenceFloat()
	{
		return 2f * (MathUtils.PI * this.boundingCircleRadius);
	}

	public Vector2f CircumferencePoint(float angle, bool asDegrees)
	{
		return CircumferencePoint(angle, asDegrees, null);
	}

	public Vector2f CircumferencePoint(float angle, bool asDegrees, Vector2f output)
	{
		if (asDegrees)
		{
			angle = MathUtils.ToDegrees(angle);
		}
		if (output == null)
		{
			output = new Vector2f();
		}
		output.x = this.x + this.boundingCircleRadius * MathUtils.Cos(angle);
		output.y = this.y + this.boundingCircleRadius * MathUtils.Sin(angle);
		return output;
	}

	public float Area()
	{
		return (this.boundingCircleRadius > 0) ? MathUtils.PI * this.boundingCircleRadius * this.boundingCircleRadius
				: 0f;
	}

	public bool Equals(Circle other)
	{
		if (other == null)
		{
			return false;
		}
		if (other == this)
		{
			return true;
		}
		if (this.x == other.x && this.y == other.y && this.boundingCircleRadius == other.boundingCircleRadius)
		{
			return true;
		}
		return false;
	}
}

}

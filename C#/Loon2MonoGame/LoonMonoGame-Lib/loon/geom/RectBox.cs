using java.lang;
using loon.action;
using loon.utils;

namespace loon.geom
{

	public class RectBox :  Shape , BoxSize, XYZW {


	public  static RectBox At(string v)
	{
		if (StringUtils.IsEmpty(v))
		{
			return new RectBox();
		}
		string[] result = StringUtils.Split(v, ',');
		int len = result.Length;
		if (len > 3)
		{
			try
			{
				float x = Float.ParseFloat(result[0].Trim());
				float y = Float.ParseFloat(result[1].Trim());
				float width = Float.ParseFloat(result[2].Trim());
				float height = Float.ParseFloat(result[3].Trim());
				return new RectBox(x, y, width, height);
			}
			catch (Exception)
			{
			}
		}
		return new RectBox();
	}

	public  static RectBox At(int x, int y, int w, int h)
	{
		return new RectBox(x, y, w, h);
	}

	public  static RectBox At(float x, float y, float w, float h)
	{
		return new RectBox(x, y, w, h);
	}

	public  static RectBox FromActor(ActionBind bind)
	{
		return new RectBox(bind.GetX(), bind.GetY(), bind.GetWidth(), bind.GetHeight());
	}

	public  static RectBox Inflate(RectBox src, int xScale, int yScale)
	{
		float destWidth = src.width + xScale;
		float destHeight = src.height + yScale;
		float destX = src.x - xScale / 2;
		float destY = src.y - yScale / 2;
		return new RectBox(destX, destY, destWidth, destHeight);
	}

	public  static RectBox Intersect(RectBox src1, RectBox src2, RectBox dest)
	{
		if (dest == null)
		{
			dest = new RectBox();
		}
		float x1 = MathUtils.Max(src1.GetMinX(), src2.GetMinX());
		float y1 = MathUtils.Max(src1.GetMinY(), src2.GetMinY());
		float x2 = MathUtils.Min(src1.GetMaxX(), src2.GetMaxX());
		float y2 = MathUtils.Min(src1.GetMaxY(), src2.GetMaxY());
		dest.SetBounds(x1, y1, x2 - x1, y2 - y1);
		return dest;
	}

	public  static RectBox GetIntersection(RectBox a, RectBox b)
	{
		float a_x = a.GetX();
		float a_r = a.GetRight();
		float a_y = a.GetY();
		float a_t = a.GetBottom();
		float b_x = b.GetX();
		float b_r = b.GetRight();
		float b_y = b.GetY();
		float b_t = b.GetBottom();
		float i_x = MathUtils.Max(a_x, b_x);
		float i_r = MathUtils.Min(a_r, b_r);
		float i_y = MathUtils.Max(a_y, b_y);
		float i_t = MathUtils.Min(a_t, b_t);
		return i_x < i_r && i_y < i_t ? new RectBox(i_x, i_y, i_r - i_x, i_t - i_y) : null;
	}

	public  static RectBox GetIntersection(RectBox a, RectBox b, RectBox result)
	{
		float a_x = a.GetX();
		float a_r = a.GetRight();
		float a_y = a.GetY();
		float a_t = a.GetBottom();
		float b_x = b.GetX();
		float b_r = b.GetRight();
		float b_y = b.GetY();
		float b_t = b.GetBottom();
		float i_x = MathUtils.Max(a_x, b_x);
		float i_r = MathUtils.Min(a_r, b_r);
		float i_y = MathUtils.Max(a_y, b_y);
		float i_t = MathUtils.Min(a_t, b_t);
		if (i_x < i_r && i_y < i_t)
		{
			result.SetBounds(i_x, i_y, i_r - i_x, i_t - i_y);
			return result;
		}
		return null;
	}

	public int width;

	public int height;

	private Matrix4 _matrix;

	public RectBox()
	{
		SetBounds(0, 0, 0, 0);
	}

	public RectBox(int width, int height)
	{
		SetBounds(0, 0, width, height);
	}

	public RectBox(int x, int y, int width, int height)
	{
		SetBounds(x, y, width, height);
	}

	public RectBox(float x, float y, float width, float height)
	{
		SetBounds(x, y, width, height);
	}

	public RectBox(double x, double y, double width, double height)
	{
		SetBounds(x, y, width, height);
	}

	public RectBox(RectBox rect)
	{
		SetBounds(rect.x, rect.y, rect.width, rect.height);
	}

	public RectBox OffSet(Vector2f offset)
	{
		this.x += offset.x;
		this.y += offset.y;
		return this;
	}

	public RectBox Offset(int offSetX, int offSetY)
	{
		this.x += offSetX;
		this.y += offSetY;
		return this;
	}

	public RectBox SetBoundsFromCenter(float centerX, float centerY, float cornerX, float cornerY)
	{
		float halfW = MathUtils.Abs(cornerX - centerX);
		float halfH = MathUtils.Abs(cornerY - centerY);
		SetBounds(centerX - halfW, centerY - halfH, halfW * 2.0, halfH * 2.0);
		return this;
	}

	public RectBox SetBounds(RectBox rect)
	{
		SetBounds(rect.x, rect.y, rect.width, rect.height);
		return this;
	}

	public RectBox SetBounds(double x, double y, double width, double height)
	{
		SetBounds((float)x, (float)y, (float)width, (float)height);
		return this;
	}

	public RectBox SetBounds(float x, float y, float width, float height)
	{
		this.x = x;
		this.y = y;
		this.width = (int)width;
		this.height = (int)height;
		this.minX = x;
		this.minY = y;
		this.maxX = x + width;
		this.maxY = y + height;
		this.pointsDirty = true;
		this.CheckPoints();
		return this;
	}

	public RectBox Inflate(int horizontalValue, int verticalValue)
	{
		this.x -= horizontalValue;
		this.y -= verticalValue;
		this.width += horizontalValue * 2;
		this.height += verticalValue * 2;
		return this;
	}

	public RectBox SetLocation(RectBox r)
	{
		this.x = r.x;
		this.y = r.y;
		return this;
	}

	public RectBox SetLocation(Point r)
	{
		this.x = r.x;
		this.y = r.y;
		return this;
	}

	public RectBox SetLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
		return this;
	}

	public RectBox Grow(float h, float v)
	{
		SetX(GetX() - h);
		SetY(GetY() - v);
		SetWidth(GetWidth() + (h * 2));
		SetHeight(GetHeight() + (v * 2));
		return this;
	}

	public RectBox ScaleGrow(float h, float v)
	{
		Grow(GetWidth() * (h - 1), GetHeight() * (v - 1));
		return this;
	}

	public override void SetScale(float sx, float sy)
	{
		if (scaleX != sx || scaleY != sy)
		{
			SetSize(width * (scaleX = sx), height * (scaleY * sy));
		}
	}

	public RectBox SetSize(float width, float height)
	{
		SetWidth(width);
		SetHeight(height);
		return this;
	}

	public bool Overlaps(RectBox rectangle)
	{
		return !(x > rectangle.x + rectangle.width || x + width < rectangle.x || y > rectangle.y + rectangle.height
				|| y + height < rectangle.y);
	}

	public Matrix4 GetMatrix()
	{
		if (_matrix == null)
		{
			_matrix = new Matrix4();
		}
		return _matrix.SetToOrtho2D(this.x, this.y, this.width, this.height);
	}

	public int X()
	{
		return (int)x;
	}

	public int Y()
	{
		return (int)y;
	}

	public int Width()
	{
		return width;
	}

	public int Height()
	{
		return height;
	}

	
	public override float GetX()
	{
		return x;
	}

	
	public override void SetX(float x)
	{
		this.x = x;
	}

	
	public override float GetY()
	{
		return y;
	}

	
	public override void SetY(float y)
	{
		this.y = y;
	}

	
	public float GetZ()
	{
		return GetWidth();
	}

	
	public float GetW()
	{
		return GetHeight();
	}

	public RectBox Copy(RectBox other)
	{
		this.x = other.x;
		this.y = other.y;
		this.width = other.width;
		this.height = other.height;
		return this;
	}

	
	public override float GetMinX()
	{
		return GetX();
	}

	
	public override float GetMinY()
	{
		return GetY();
	}

	
	public override float GetMaxX()
	{
		return this.x + this.width;
	}

	
	public override float GetMaxY()
	{
		return this.y + this.height;
	}

	public float GetMiddleX()
	{
		return GetCenterX();
	}

	public float GetMiddleY()
	{
		return GetCenterY();
	}

	
	public override float GetCenterX()
	{
		return x + width / 2f;
	}

	
	public override float GetCenterY()
	{
		return y + height / 2f;
	}

	public float GetLeft()
	{
		return this.GetMinX();
	}

	public RectBox SetLeft(float value)
	{
		this.width += (int)(this.x - value);
		this.x = value;
		return this;
	}

	public float GetRight()
	{
		return GetMaxX();
	}

	public RectBox SetRight(float v)
	{
		this.width = (int)(v - this.x);
		return this;
	}

	public float GetTop()
	{
		return GetMinY();
	}

	public RectBox SetTop(float value)
	{
		this.height += (int)(this.y - value);
		this.y = value;
		return this;
	}

	public float GetBottom()
	{
		return GetMaxY();
	}

	public RectBox SetBottom(float v)
	{
		this.height = (int)(v - this.y);
		return this;
	}

	public int Left()
	{
		return this.X();
	}

	public int Right()
	{
		return (int)GetMaxX();
	}

	public int Top()
	{
		return this.Y();
	}

	public int Bottom()
	{
		return (int)GetMaxY();
	}

	public Vector2f TopLeft()
	{
		return new Vector2f(this.GetLeft(), this.GetTop());
	}

	public Vector2f BottomRight()
	{
		return new Vector2f(this.GetRight(), this.GetBottom());
	}

	public RectBox Normalize()
	{
		return Normalize(this);
	}

	public RectBox Normalize(RectBox r)
	{
		if (r.width < 0)
		{
			r.width = MathUtils.Abs(r.width);
			r.x -= r.width;
		}
		if (r.height < 0)
		{
			r.height = MathUtils.Abs(r.height);
			r.y -= r.height;
		}
		return this;
	}

	public float[] ToFloat()
	{
		return new float[] { x, y, width, height };
	}

	
	public RectBox GetRect()
	{
		return this;
	}

	
	public override float GetHeight()
	{
		return height;
	}

	
	public void SetHeight(float height)
	{
		this.height = (int)height;
	}

	
	public override float GetWidth()
	{
		return width;
	}

	
	public void SetWidth(float width)
	{
		this.width = (int)width;
	}

	
	public override bool Equals(object obj)
	{
		if (obj is RectBox) {
			RectBox rect = (RectBox)obj;
			return Equals(rect.x, rect.y, rect.width, rect.height);
		} else
		{
			return false;
		}
	}

	public bool Equals(float x, float y, float width, float height)
	{
		return (this.x == x && this.y == y && this.width == width && this.height == height);
	}

	public int GetArea()
	{
		return width * height;
	}

	
	public override bool Contains(float x, float y)
	{
		return Contains(x, y, 0, 0);
	}

	public bool Contains(float x, float y, float width, float height)
	{
		return (x >= this.x && y >= this.y && ((x + width) <= (this.x + this.width))
				&& ((y + height) <= (this.y + this.height)));
	}

	public bool Contains(RectBox rect)
	{
		return Contains(rect.x, rect.y, rect.width, rect.height);
	}


	public bool Contains(Circle circle)
	{
		float xmin = circle.x - circle.boundingCircleRadius;
		float xmax = xmin + 2f * circle.boundingCircleRadius;
		float ymin = circle.y - circle.boundingCircleRadius;
		float ymax = ymin + 2f * circle.boundingCircleRadius;
		return ((xmin > x && xmin < x + width) && (xmax > x && xmax < x + width))
				&& ((ymin > y && ymin < y + height) && (ymax > y && ymax < y + height));
	}

	public bool Contains(Vector2f v)
	{
		return Contains(v.x, v.y);
	}

	public bool Contains(Point point)
	{
		if (this.x < point.x && this.x + this.width > point.x && this.y < point.y && this.y + this.height > point.y)
		{
			return true;
		}
		return false;
	}

	public bool Contains(PointF point)
	{
		if (this.x < point.x && this.x + this.width > point.x && this.y < point.y && this.y + this.height > point.y)
		{
			return true;
		}
		return false;
	}

	public bool Contains(PointI point)
	{
		if (this.x < point.x && this.x + this.width > point.x && this.y < point.y && this.y + this.height > point.y)
		{
			return true;
		}
		return false;
	}


	public bool Intersects(RectBox rect)
	{
		return Intersects(rect.x, rect.y, rect.width, rect.height);
	}

	public bool Intersects(float x, float y)
	{
		return Intersects(0, 0, width, height);
	}

	public bool Intersects(float x, float y, float width, float height)
	{
		return x + width > this.x && x < this.x + this.width && y + height > this.y && y < this.y + this.height;
	}

	public RectBox Intersection(RectBox rect)
	{
		return Intersection(rect.x, rect.y, rect.width, rect.height);
	}

	public RectBox Intersection(float x, float y, float width, float height)
	{
		int x1 = (int)MathUtils.Max(this.x, x);
		int y1 = (int)MathUtils.Max(this.y, y);
		int x2 = (int)MathUtils.Min(this.x + this.width - 1, x + width - 1);
		int y2 = (int)MathUtils.Min(this.y + this.height - 1, y + height - 1);
		return SetBounds(x1, y1, MathUtils.Max(0, x2 - x1 + 1), MathUtils.Max(0, y2 - y1 + 1));
	}

	public bool Inside(int x, int y)
	{
		return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y - this.y) < this.height);
	}

	public RectBox GetIntersection(RectBox rect)
	{
		int x1 = (int)MathUtils.Max(x, rect.x);
		int x2 = (int)MathUtils.Min(x + width, rect.x + rect.width);
		int y1 = (int)MathUtils.Max(y, rect.y);
		int y2 = (int)MathUtils.Min(y + height, rect.y + rect.height);
		return new RectBox(x1, y1, x2 - x1, y2 - y1);
	}

	public RectBox Union(RectBox rect)
	{
		return Union(rect.x, rect.y, rect.width, rect.height);
	}

	public RectBox Union(float x, float y, float width, float height)
	{
		int x1 = (int)MathUtils.Min(this.x, x);
		int y1 = (int)MathUtils.Min(this.y, y);
		int x2 = (int)MathUtils.Max(this.x + this.width - 1, x + width - 1);
		int y2 = (int)MathUtils.Max(this.y + this.height - 1, y + height - 1);
		SetBounds(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
		return this;
	}
	protected override void CreatePoints()
	{

		float useWidth = width;
		float useHeight = height;
		points = new float[8];

		points[0] = x;
		points[1] = y;

		points[2] = x + useWidth;
		points[3] = y;

		points[4] = x + useWidth;
		points[5] = y + useHeight;

		points[6] = x;
		points[7] = y + useHeight;

		maxX = points[2];
		maxY = points[5];
		minX = points[0];
		minY = points[1];
		FindCenter();
		CalculateRadius();
	}

	public override Shape Transform(Matrix3 transform)
	{
		CheckPoints();
		Polygon resultPolygon = new Polygon();
		float[] result = new float[points.Length];
		transform.Transform(points, 0, result, 0, points.Length / 2);
		resultPolygon.points = result;
		resultPolygon.FindCenter();
		resultPolygon.CheckPoints();
		return resultPolygon;
	}

	public  RectBox ModX(float xMod)
	{
		x += xMod;
		return this;
	}

	public  RectBox ModY(float yMod)
	{
		y += yMod;
		return this;
	}

	public RectBox ModWidth(float w)
	{
		this.width += (int)w;
		return this;
	}

	public RectBox ModHeight(float h)
	{
		this.height += (int)h;
		return this;
	}

	public  bool IntersectsLine( float x1,  float y1,  float x2,  float y2)
	{
		return Contains(x1, y1) || Contains(x2, y2);
	}

	public bool Inside(float x, float y)
	{
		return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y - this.y) < this.height);
	}

	public RectBox Cpy()
	{
		return new RectBox(this.x, this.y, this.width, this.height);
	}

	public RectBox CreateIntersection(RectBox rectBox)
	{
		RectBox dest = new RectBox();
		dest.Intersection(rectBox);
		Intersect(this, rectBox, dest);
		return dest;
	}

	public float MaxX()
	{
		return X() + Width();
	}

	public float MaxY()
	{
		return Y() + Height();
	}

	public override bool IsEmpty()
	{
		return GetWidth() <= 0 || Height() <= 0;
	}

	public RectBox SetEmpty()
	{
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
		return this;
	}

	public RectBox Offset(Point point)
	{
		x += point.x;
		y += point.y;
		return this;
	}

	public RectBox Offset(PointF point)
	{
		x += point.x;
		y += point.y;
		return this;
	}

	public RectBox Offset(PointI point)
	{
		x += point.x;
		y += point.y;
		return this;
	}

	public RectBox Inc(RectBox view)
	{
		if (view == null)
		{
			return Cpy();
		}
		return new RectBox(x + view.x, y + view.y, width + view.width, height + view.height);
	}

	public RectBox Sub(RectBox view)
	{
		if (view == null)
		{
			return Cpy();
		}
		return new RectBox(x - view.x, y - view.y, width - view.width, height - view.height);
	}

	public RectBox Mul(RectBox view)
	{
		if (view == null)
		{
			return Cpy();
		}
		return new RectBox(x * view.x, y * view.y, width * view.width, height * view.height);
	}

	public RectBox Div(RectBox view)
	{
		if (view == null)
		{
			return Cpy();
		}
		return new RectBox(x / view.x, y / view.y, width / view.width, height / view.height);
	}

	public RectBox Inc(float v)
	{
		return new RectBox(x + v, y + v, width + v, height + v);
	}

	public RectBox Sub(float v)
	{
		return new RectBox(x - v, y - v, width - v, height - v);
	}

	public RectBox Mul(float v)
	{
		return new RectBox(x * v, y * v, width * v, height * v);
	}

	public RectBox Div(float v)
	{
		return new RectBox(x / v, y / v, width / v, height / v);
	}

	public RectBox Add(float px, float py)
	{
		float x1 = MathUtils.Min(x, px);
		float x2 = MathUtils.Max(x + width, px);
		float y1 = MathUtils.Min(y, py);
		float y2 = MathUtils.Max(y + height, py);
		SetBounds(x1, y1, x2 - x1, y2 - y1);
		return this;
	}

	public RectBox Add(Vector2f v)
	{
		return Add(v.x, v.y);
	}

	public RectBox Add(RectBox r)
	{
		int tx2 = this.width;
		int ty2 = this.height;
		if ((tx2 | ty2) < 0)
		{
			SetBounds(r.x, r.y, r.width, r.height);
		}
		int rx2 = r.width;
		int ry2 = r.height;
		if ((rx2 | ry2) < 0)
		{
			return this;
		}
		float tx1 = this.x;
		float ty1 = this.y;
		tx2 += (int)tx1;
		ty2 += (int)ty1;
		float rx1 = r.x;
		float ry1 = r.y;
		rx2 += (int)rx1;
		ry2 += (int)ry1;
		if (tx1 > rx1)
		{
			tx1 = rx1;
		}
		if (ty1 > ry1)
		{
			ty1 = ry1;
		}
		if (tx2 < rx2)
		{
			tx2 = rx2;
		}
		if (ty2 < ry2)
		{
			ty2 = ry2;
		}
		tx2 -= (int)tx1;
		ty2 -= (int)ty1;
		if (tx2 > Integer.MAX_VALUE_JAVA)
		{
			tx2 = Integer.MAX_VALUE_JAVA;
		}
		if (ty2 > Integer.MAX_VALUE_JAVA)
		{
			ty2 = Integer.MAX_VALUE_JAVA;
		}
		SetBounds(tx1, ty1, tx2, ty2);
		return this;
	}

	public float GetAspectRatio()
	{
		return (height == 0) ? MathUtils.NaN : (float)width / (float)height;
	}

	public float Area()
	{
		return this.width * this.height;
	}

	public float Perimeter()
	{
		return 2f * (this.width + this.height);
	}

	public RectBox Random()
	{
		this.x = MathUtils.Random(0f, LSystem.viewSize.GetWidth());
		this.y = MathUtils.Random(0f, LSystem.viewSize.GetHeight());
		this.width = MathUtils.Random(0, LSystem.viewSize.GetWidth());
		this.height = MathUtils.Random(0, LSystem.viewSize.GetHeight());
		return this;
	}

	
	public override int GetHashCode()
	{
		uint prime = 31;
		uint result = 1;
		result = prime * result + NumberUtils.FloatToIntBits(x);
		result = prime * result + NumberUtils.FloatToIntBits(y);
		result = prime * result + NumberUtils.FloatToIntBits(width);
		result = prime * result + NumberUtils.FloatToIntBits(height);
		return (int)result;
	}

	
	public override string ToString()
	{
		StringKeyValue builder = new StringKeyValue("RectBox");
		builder.Kv("x", x).Comma().Kv("y", y).Comma().Kv("width", width).Comma().Kv("height", height).Comma()
				.Kv("left", Left()).Comma().Kv("right", Right()).Comma().Kv("top", Top()).Comma()
				.Kv("bottom", Bottom());
		return builder.ToString();
	}

}

}

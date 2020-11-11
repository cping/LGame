using java.lang;
using loon.utils;

namespace loon.geom
{
	public class AABB : XY, BoxSize {

	public static AABB At(string v)
	{
		if (StringUtils.IsEmpty(v))
		{
			return new AABB();
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
				return new AABB(x, y, width, height);
			}
			catch (Exception)
			{
			}
		}
		return new AABB();
	}

	public static AABB At(int x, int y, int w, int h)
	{
		return new AABB(x, y, w, h);
	}

	public static AABB At(float x, float y, float w, float h)
	{
		return new AABB(x, y, w, h);
	}
/*
	public  static AABB fromActor(ActionBind bind)
	{
		return new AABB(bind.GetX(), bind.GetY(), bind.GetWidth(), bind.GetHeight());
	}*/

	public float minX, minY;

	public float maxX, maxY;

	public AABB(): this(0.0F, 0.0F, 0.0F, 0.0F)
	{
		
	}

	public AABB(float minX, float minY, float maxX, float maxY)
	{
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public AABB SetCentered(float x, float y, float size)
	{
		return Set(x - size / 2f, y - size / 2f, size, size);
	}

	public AABB SetCentered(float x, float y, float width, float height)
	{
		return Set(x - width / 2f, y - height / 2f, width, height);
	}

	public int Width()
	{
		return (int)this.maxX;
	}

	public int Height()
	{
		return (int)this.maxY;
	}

	
	public float GetWidth()
	{
		return this.maxX;
	}


	public float GetHeight()
	{
		return this.maxY;
	}

	public AABB Cpy()
	{
		return new AABB(this.minX, this.minY, this.maxX, this.maxY);
	}

	public bool IsHit(AABB b)
	{
		return this.minX < b.maxX && b.minX < this.maxX && this.minY < b.maxY && b.minY < this.maxY;
	}

	public AABB Set(float minX, float minY, float maxX, float maxY)
	{
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		return this;
	}

	public AABB Move(float cx, float cy)
	{
		this.minX += cx;
		this.minY += cy;
		return this;
	}

	public float Distance(Vector2f other)
	{
		float dx = GetX() - other.x;
		float dy = GetY() - other.y;
		return MathUtils.Sqrt(dx * dx + dy * dy);
	}

	public AABB Merge(AABB other)
	{
		float minX = MathUtils.Min(this.GetX(), other.GetX());
		float minY = MathUtils.Min(this.GetY(), other.GetY());

		float maxW = MathUtils.Max(this.GetWidth(), other.GetWidth());
		float maxH = MathUtils.Max(this.GetHeight(), other.GetHeight());

		return new AABB(minX, minY, maxW, maxH);
	}

	public Vector2f GetPosition(Vector2f pos)
	{
		return pos.Set(GetX(), GetY());
	}

	public AABB SetPosition(XY pos)
	{
		if (pos == null)
		{
			return this;
		}
		SetPosition(pos.GetX(), pos.GetY());
		return this;
	}

	public AABB SetPosition(float x, float y)
	{
		SetX(x);
		SetY(y);
		return this;
	}

	public AABB SetSize(float width, float height)
	{
		SetWidth(width);
		SetHeight(height);
		return this;
	}

	public float GetAspectRatio()
	{
		return (GetHeight() == 0) ? MathUtils.NaN : GetWidth() / GetHeight();
	}

	public Vector2f GetCenter(Vector2f pos)
	{
		pos.x = GetX() + GetWidth() / 2f;
		pos.y = GetY() + GetHeight() / 2;
		return pos;
	}

	public AABB SetCenter(float x, float y)
	{
		SetPosition(x - GetWidth() / 2, y - GetHeight() / 2);
		return this;
	}

	public AABB SetCenter(XY pos)
	{
		SetPosition(pos.GetX() - GetWidth() / 2, pos.GetY() - GetHeight() / 2);
		return this;
	}

	public AABB FitOutside(AABB rect)
	{
		float ratio = GetAspectRatio();
		if (ratio > rect.GetAspectRatio())
		{
			SetSize(rect.GetHeight() * ratio, rect.GetHeight());
		}
		else
		{
			SetSize(rect.GetWidth(), rect.GetWidth() / ratio);
		}
		SetPosition((rect.GetX() + rect.GetWidth() / 2) - GetWidth() / 2,
				(rect.GetY() + rect.GetHeight() / 2) - GetHeight() / 2);
		return this;
	}

	public AABB FitInside(AABB rect)
	{
		float ratio = GetAspectRatio();
		if (ratio < rect.GetAspectRatio())
		{
			SetSize(rect.GetHeight() * ratio, rect.GetHeight());
		}
		else
		{
			SetSize(rect.GetWidth(), rect.GetWidth() / ratio);
		}
		SetPosition((rect.GetX() + rect.GetWidth() / 2) - GetWidth() / 2,
				(rect.GetY() + rect.GetHeight() / 2) - GetHeight() / 2);
		return this;
	}

	
	public void SetX(float x)
	{
		this.minX = x;
	}

	public void SetY(float y)
	{
		this.minY = y;
	}

	public void SetWidth(float w)
	{
		this.maxX = w;
	}

	public void SetHeight(float h)
	{
		this.maxY = h;
	}

	public float GetX()
	{
		return minX;
	}

	public float GetY()
	{
		return minY;
	}
		/*
	public bool contains(Circle circle)
	{
		float xmin = circle.x - circle.GetRadius();
		float xmax = xmin + 2f * circle.GetRadius();

		float ymin = circle.y - circle.GetRadius();
		float ymax = ymin + 2f * circle.GetRadius();

		return ((xmin > minX && xmin < minX + maxX) && (xmax > minX && xmax < minX + maxX))
				&& ((ymin > minY && ymin < minY + maxY) && (ymax > minY && ymax < minY + maxY));
	}
		*/
	public bool IsEmpty()
	{
		return this.maxX <= 0 && this.maxY <= 0;
	}

	public AABB Random()
	{
		this.minX = MathUtils.Random(0f, LSystem.viewSize.GetWidth());
		this.minY = MathUtils.Random(0f, LSystem.viewSize.GetHeight());
		this.maxX = MathUtils.Random(0f, LSystem.viewSize.GetWidth());
		this.maxY = MathUtils.Random(0f, LSystem.viewSize.GetHeight());
		return this;
	}

	public float GetCenterX()
	{
		return this.minX + this.maxX / 2f;
	}

	public float GetCenterY()
	{
		return this.minY + this.maxY / 2f;
	}
	public RectBox ToRectBox()
	{
		return new RectBox(this.minX, this.minY, this.maxX, this.maxY);
	}

	public override int GetHashCode()
	{
		 uint prime = 31;
		uint result = 1;
		result = prime * result + NumberUtils.FloatToIntBits(minX);
		result = prime * result + NumberUtils.FloatToIntBits(minY);
		result = prime * result + NumberUtils.FloatToIntBits(maxX);
		result = prime * result + NumberUtils.FloatToIntBits(maxY);
		return (int)result;
	}

	public override string ToString()
	{
		StringKeyValue builder = new StringKeyValue("AABB");
		builder.Kv("minX", minX).Comma().Kv("minY", minY).Comma().Kv("maxX", maxX).Comma().Kv("maxY", maxY);
		return builder.ToString();
	}

}

}

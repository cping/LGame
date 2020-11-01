namespace loon.geom
{
    public class Dimension
    {

		public float width = -1, height = -1;

		private Matrix4 matrix4;

		private RectBox rect;

		private bool dirty = false;

		public Dimension(): this(-1, -1)
		{
			
		}

		public Dimension(float w, float h)
		{
			width = w;
			height = h;
			dirty = true;
		}

		public Dimension(Dimension d)
		{
			width = d.GetWidth();
			height = d.GetHeight();
			dirty = true;
		}

		public RectBox GetRect()
		{
			if (rect == null)
			{
				rect = new RectBox(0, 0, width, height);
			}
			else
			{
				rect.SetBounds(0, 0, width, height);
			}
			return rect;
		}

		public Matrix4 GetMatrix()
		{
			if (dirty)
			{
				if (matrix4 == null)
				{
					matrix4 = new Matrix4();
				}
			//	matrix4.SetToOrtho2D(0, 0, width, height);
				dirty = false;
			}
			return matrix4;
		}

		public bool IsDirty()
		{
			return dirty;
		}

		public bool Contains(float x, float y)
		{
			return x >= 0 && x < width && y >= 0 && y < height;
		}

		public float Height()
		{
			return height;
		}

		public float Width()
		{
			return width;
		}

		public int GetHeight()
		{
			return (int)height;
		}

		public int GetWidth()
		{
			return (int)width;
		}

		public Dimension SetWidth(int width)
		{
			this.width = width;
			this.dirty = true;
			return this;
		}

		public Dimension SetSize(int width, int height)
		{
			this.width = width;
			this.height = height;
			this.dirty = true;
			return this;
		}

		public Dimension SetSize(Dimension d)
		{
			this.width = d.GetWidth();
			this.height = d.GetHeight();
			this.dirty = true;
			return this;
		}

		public Dimension SetHeight(int height)
		{
			this.height = height;
			this.dirty = true;
			return this;
		}

		public Dimension Cpy()
		{
			return new Dimension(width, height);
		}

		public bool IsLandscape()
		{
			return this.height < this.width;
		}

		public bool IsPortrait()
		{
			return this.height >= this.width;
		}

	public override string ToString()
		{
			return "(" + width + "," + height + ")";
		}
	}
}

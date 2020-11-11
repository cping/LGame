using loon.utils;

namespace loon.geom
{
    public class RectI : XY
    {

        public class Range : XY
        {

            public int left;

            public int top;

            public int right;

            public int bottom;

            public Range()
            {
            }

            public Range(RectI rect) : this(rect.Left(), rect.Top(), rect.Right(), rect.Bottom())
            {

            }

            public Range(int left, int top, int right, int bottom)
            {
                this.left = left;
                this.top = top;
                this.right = right;
                this.bottom = bottom;
            }

            public Range(Range r)
            {
                left = r.left;
                top = r.top;
                right = r.right;
                bottom = r.bottom;
            }


            public override int GetHashCode()
            {
                uint prime = 31;
                uint result = 1;
                result = prime * result + NumberUtils.FloatToIntBits(left);
                result = prime * result + NumberUtils.FloatToIntBits(top);
                result = prime * result + NumberUtils.FloatToIntBits(right);
                result = prime * result + NumberUtils.FloatToIntBits(bottom);
                return (int)result;
            }

            public override bool Equals(object obj)
            {
                Range r = (Range)obj;
                if (r != null)
                {
                    return left == r.left && top == r.top && right == r.right && bottom == r.bottom;
                }
                return false;
            }

            public bool IsEmpty()
            {
                return left >= right || top >= bottom;
            }

            public int X()
            {
                return left;
            }

            public int Y()
            {
                return top;
            }

            public int Width()
            {
                return right - left;
            }

            public int Height()
            {
                return bottom - top;
            }

            public int CenterX()
            {
                return (left + right) >> 1;
            }

            public int CenterY()
            {
                return (top + bottom) >> 1;
            }

            public float ExactCenterX()
            {
                return (left + right) * 0.5f;
            }

            public float ExactCenterY()
            {
                return (top + bottom) * 0.5f;
            }

            public void SetEmpty()
            {
                left = right = top = bottom = 0;
            }

            public void Set(int left, int top, int right, int bottom)
            {
                this.left = left;
                this.top = top;
                this.right = right;
                this.bottom = bottom;
            }

            public void Set(Range src)
            {
                this.left = src.left;
                this.top = src.top;
                this.right = src.right;
                this.bottom = src.bottom;
            }

            public void Offset(int dx, int dy)
            {
                left += dx;
                top += dy;
                right += dx;
                bottom += dy;
            }

            public void OffsetTo(int newLeft, int newTop)
            {
                right += newLeft - left;
                bottom += newTop - top;
                left = newLeft;
                top = newTop;
            }

            public void Inset(int dx, int dy)
            {
                left += dx;
                top += dy;
                right -= dx;
                bottom -= dy;
            }

            public bool Contains(int x, int y)
            {
                return left < right && top < bottom && x >= left && x < right && y >= top && y < bottom;
            }

            public bool Contains(Circle circle)
            {
                float xmin = circle.x - circle.boundingCircleRadius;
                float xmax = xmin + 2f * circle.boundingCircleRadius;

                float ymin = circle.y - circle.boundingCircleRadius;
                float ymax = ymin + 2f * circle.boundingCircleRadius;

                return ((xmin > GetX() && xmin < GetX() + Width()) && (xmax > GetX() && xmax < GetX() + Width()))
                        && ((ymin > GetY() && ymin < GetY() + Height()) && (ymax > GetY() && ymax < GetY() + Height()));
            }

            public bool Contains(int left, int top, int right, int bottom)
            {
                return this.left < this.right && this.top < this.bottom && this.left <= left && this.top <= top
                        && this.right >= right && this.bottom >= bottom;
            }

            public bool Contains(Range r)
            {
                return this.left < this.right && this.top < this.bottom && left <= r.left && top <= r.top
                        && right >= r.right && bottom >= r.bottom;
            }

            public bool Intersect(int left, int top, int right, int bottom)
            {
                if (this.left < right && left < this.right && this.top < bottom && top < this.bottom)
                {
                    if (this.left < left)
                    {
                        this.left = left;
                    }
                    if (this.top < top)
                    {
                        this.top = top;
                    }
                    if (this.right > right)
                    {
                        this.right = right;
                    }
                    if (this.bottom > bottom)
                    {
                        this.bottom = bottom;
                    }
                    return true;
                }
                return false;
            }

            public bool Intersect(Range r)
            {
                return Intersect(r.left, r.top, r.right, r.bottom);
            }

            public bool SetIntersect(Range a, Range b)
            {
                if (a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom)
                {
                    left = MathUtils.Max(a.left, b.left);
                    top = MathUtils.Max(a.top, b.top);
                    right = MathUtils.Min(a.right, b.right);
                    bottom = MathUtils.Min(a.bottom, b.bottom);
                    return true;
                }
                return false;
            }

            public bool Intersects(int left, int top, int right, int bottom)
            {
                return this.left < right && left < this.right && this.top < bottom && top < this.bottom;
            }

            public bool Intersects(Range a, Range b)
            {
                return a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom;
            }

            public void Union(int left, int top, int right, int bottom)
            {
                if ((left < right) && (top < bottom))
                {
                    if ((this.left < this.right) && (this.top < this.bottom))
                    {
                        if (this.left > left)
                            this.left = left;
                        if (this.top > top)
                            this.top = top;
                        if (this.right < right)
                            this.right = right;
                        if (this.bottom < bottom)
                            this.bottom = bottom;
                    }
                    else
                    {
                        this.left = left;
                        this.top = top;
                        this.right = right;
                        this.bottom = bottom;
                    }
                }
            }

            public void Union(Range r)
            {
                Union(r.left, r.top, r.right, r.bottom);
            }

            public void Union(int x, int y)
            {
                if (x < left)
                {
                    left = x;
                }
                else if (x > right)
                {
                    right = x;
                }
                if (y < top)
                {
                    top = y;
                }
                else if (y > bottom)
                {
                    bottom = y;
                }
            }

            public void Sort()
            {
                if (left > right)
                {
                    int temp = left;
                    left = right;
                    right = temp;
                }
                if (top > bottom)
                {
                    int temp = top;
                    top = bottom;
                    bottom = temp;
                }
            }

            public void Scale(float scale)
            {
                if (scale != 1.0f)
                {
                    left = (int)(left * scale + 0.5f);
                    top = (int)(top * scale + 0.5f);
                    right = (int)(right * scale + 0.5f);
                    bottom = (int)(bottom * scale + 0.5f);
                }
            }

            public RectI GetRect()
            {
                return new RectI(this);
            }


            public float GetX()
            {
                return X();
            }


            public float GetY()
            {
                return Y();
            }

        }

        public int width = 0;
        public int height = 0;
        public int x = 0;
        public int y = 0;

        public RectI() : this(0, 0, 0, 0)
        {

        }

        public RectI(RectI rect) : this(rect.x, rect.y, rect.width, rect.height)
        {

        }

        public RectI(Range range) : this(range.X(), range.Y(), range.Width(), range.Height())
        {

        }

        public RectI(int w, int h) : this(0, 0, w, h)
        {

        }

        public RectI(int x1, int y1, int w1, int h1)
        {
            this.x = x1;
            this.y = y1;
            this.width = w1;
            this.height = h1;
        }

        public RectI Set(RectI r)
        {
            this.x = r.x;
            this.y = r.y;
            this.width = r.width;
            this.height = r.height;
            return this;
        }

        public RectI Set(int x1, int y1, int w1, int h1)
        {
            this.x = x1;
            this.y = y1;
            this.width = w1;
            this.height = h1;
            return this;
        }

        public bool Inside(int x, int y)
        {
            return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y - this.y) < this.height);
        }

        public int GetRight()
        {
            return this.x + this.width;
        }

        public int GetBottom()
        {
            return this.y + this.height;
        }

        public RectI GetIntersection(RectI rect)
        {
            int x1 = MathUtils.Max(x, rect.x);
            int x2 = MathUtils.Min(x + width, rect.x + rect.width);
            int y1 = MathUtils.Max(y, rect.y);
            int y2 = MathUtils.Min(y + height, rect.y + rect.height);
            return new RectI(x1, y1, x2 - x1, y2 - y1);
        }

        public static RectI GetIntersection(RectI a, RectI b)
        {
            int a_x = a.x;
            int a_r = a.GetRight();
            int a_y = a.y;
            int a_t = a.GetBottom();
            int b_x = b.x;
            int b_r = b.GetRight();
            int b_y = b.y;
            int b_t = b.GetBottom();
            int i_x = MathUtils.Max(a_x, b_x);
            int i_r = MathUtils.Min(a_r, b_r);
            int i_y = MathUtils.Max(a_y, b_y);
            int i_t = MathUtils.Min(a_t, b_t);
            return i_x < i_r && i_y < i_t ? new RectI(i_x, i_y, i_r - i_x, i_t - i_y) : null;
        }

        public static RectI GetIntersection(RectI a, RectI b, RectI result)
        {
            int a_x = a.x;
            int a_r = a.GetRight();
            int a_y = a.y;
            int a_t = a.GetBottom();
            int b_x = b.x;
            int b_r = b.GetRight();
            int b_y = b.y;
            int b_t = b.GetBottom();
            int i_x = MathUtils.Max(a_x, b_x);
            int i_r = MathUtils.Min(a_r, b_r);
            int i_y = MathUtils.Max(a_y, b_y);
            int i_t = MathUtils.Min(a_t, b_t);
            if (i_x < i_r && i_y < i_t)
            {
                result.Set(i_x, i_y, i_r - i_x, i_t - i_y);
                return result;
            }
            return result;
        }

        public int Left()
        {
            return this.x;
        }

        public int Right()
        {
            return this.x + this.width;
        }

        public int Top()
        {
            return this.y;
        }

        public int Bottom()
        {
            return this.y + this.height;
        }

        public int MiddleX()
        {
            return this.x + this.width / 2;
        }

        public int MiddleY()
        {
            return this.y + this.height / 2;
        }

        public int CenterX()
        {
            return x + width / 2;
        }

        public int CenterY()
        {
            return y + height / 2;
        }

        public Range GetRange()
        {
            return new Range(this);
        }


        public float GetX()
        {
            return x;
        }

        public float GetY()
        {
            return y;
        }

        public int GetWidth()
        {
            return width;
        }

        public int GetHeight()
        {
            return height;
        }

        public bool IsEmpty()
        {
            return width <= 0 && height <= 0;
        }

        public static void GetNearestCorner(int x, int y, int w, int h, int px, int py, PointI result)
        {
            result.Set((int)MathUtils.Nearest(px, x, x + w), (int)MathUtils.Nearest(y, y, y + h));
        }

        public static bool GetSegmentIntersectionIndices(int x, int y, int w, int h, int x1, int y1, int x2, int y2,
                int ti1, int ti2, PointI ti, PointI n1, PointI n2)
        {
            int dx = x2 - x1;
            int dy = y2 - y1;

            int nx = 0, ny = 0;
            int nx1 = 0, ny1 = 0, nx2 = 0, ny2 = 0;
            int p, q, r;

            for (int side = 1; side <= 4; side++)
            {
                switch (side)
                {
                    case 1:
                        nx = -1;
                        ny = 0;
                        p = -dx;
                        q = x1 - x;
                        break;
                    case 2:
                        nx = 1;
                        ny = 0;
                        p = dx;
                        q = x + w - x1;
                        break;
                    case 3:
                        nx = 0;
                        ny = -1;
                        p = -dy;
                        q = y1 - y;
                        break;
                    default:
                        nx = 0;
                        ny = -1;
                        p = dy;
                        q = y + h - y1;
                        break;
                }

                if (p == 0)
                {
                    if (q <= 0)
                    {
                        return false;
                    }
                }
                else
                {
                    r = q / p;
                    if (p < 0)
                    {
                        if (r > ti2)
                        {
                            return false;
                        }
                        else if (r > ti1)
                        {
                            ti1 = r;
                            nx1 = nx;
                            ny1 = ny;
                        }
                    }
                    else
                    {
                        if (r < ti1)
                        {
                            return false;
                        }
                        else if (r < ti2)
                        {
                            ti2 = r;
                            nx2 = nx;
                            ny2 = ny;
                        }
                    }
                }
            }
            ti.Set(ti1, ti2);
            n1.Set(nx1, ny1);
            n2.Set(nx2, ny2);
            return true;
        }

        public static void GetDiff(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2, RectI result)
        {
            result.Set(x2 - x1 - w1, y2 - y1 - h1, w1 + w2, h1 + h2);
        }

        public static bool ContainsPoint(int x, int y, int w, int h, int px, int py, int delta)
        {
            return px - x > delta && py - y > delta && x + w - px > delta && y + h - py > delta;
        }

        public static bool IsIntersecting(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2)
        {
            return x1 < x2 + w2 && x2 < x1 + w1 && y1 < y2 + h2 && y2 < y1 + h1;
        }

        public static int GetSquareDistance(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2)
        {
            int dx = x1 - x2 + (w1 - w2) / 2;
            int dy = y1 - y2 + (h1 - h2) / 2;
            return dx * dx + dy * dy;
        }

        public RectI Random()
        {
            this.x = MathUtils.Random(0, LSystem.viewSize.GetWidth());
            this.y = MathUtils.Random(0, LSystem.viewSize.GetHeight());
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
            StringKeyValue builder = new StringKeyValue("RectI");
            builder.Kv("x", x).Comma().Kv("y", y).Comma().Kv("width", width).Comma().Kv("height", height);
            return builder.ToString();
        }
    }
}

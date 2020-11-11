using loon.utils;

namespace loon.geom
{
    public class RectF : XY
    {

        public class Range : XY
        {

            public float left;

            public float top;

            public float right;

            public float bottom;

            public Range()
            {
            }

            public Range(RectF rect) : this(rect.Left(), rect.Top(), rect.Right(), rect.Bottom())
            {

            }

            public Range(float left, float top, float right, float bottom)
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

            public float X()
            {
                return left;
            }

            public float Y()
            {
                return top;
            }

            public float Width()
            {
                return right - left;
            }

            public float Height()
            {
                return bottom - top;
            }

            public float CenterX()
            {
                return ((int)(left + right)) >> 1;
            }

            public float CenterY()
            {
                return ((int)(top + bottom)) >> 1;
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

            public void Set(float left, float top, float right, float bottom)
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

            public void OffSet(float dx, float dy)
            {
                left += dx;
                top += dy;
                right += dx;
                bottom += dy;
            }

            public void OffSetTo(float newLeft, float newTop)
            {
                right += newLeft - left;
                bottom += newTop - top;
                left = newLeft;
                top = newTop;
            }

            public void InSet(float dx, float dy)
            {
                left += dx;
                top += dy;
                right -= dx;
                bottom -= dy;
            }

            public bool Contains(float x, float y)
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

            public bool Contains(float left, float top, float right, float bottom)
            {
                return this.left < this.right && this.top < this.bottom && this.left <= left && this.top <= top
                        && this.right >= right && this.bottom >= bottom;
            }

            public bool Contains(Range r)
            {
                return this.left < this.right && this.top < this.bottom && left <= r.left && top <= r.top
                        && right >= r.right && bottom >= r.bottom;
            }

            public bool Intersect(float left, float top, float right, float bottom)
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

            public bool Intersects(float left, float top, float right, float bottom)
            {
                return this.left < right && left < this.right && this.top < bottom && top < this.bottom;
            }

            public bool Intersects(Range a, Range b)
            {
                return a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom;
            }

            public void Union(float left, float top, float right, float bottom)
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

            public void Union(float x, float y)
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
                    float temp = left;
                    left = right;
                    right = temp;
                }
                if (top > bottom)
                {
                    float temp = top;
                    top = bottom;
                    bottom = temp;
                }
            }

            public void Scale(float scale)
            {
                if (scale != 1.0f)
                {
                    left = (float)(left * scale + 0.5f);
                    top = (float)(top * scale + 0.5f);
                    right = (float)(right * scale + 0.5f);
                    bottom = (float)(bottom * scale + 0.5f);
                }
            }

            public RectF GetRect()
            {
                return new RectF(this);
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

        public float width = 0f;
        public float height = 0f;
        public float x = 0f;
        public float y = 0f;

        public RectF() : this(0, 0, 0, 0)
        {

        }

        public RectF(float w, float h) : this(0, 0, w, h)
        {

        }

        public RectF(RectF rect) : this(rect.x, rect.y, rect.width, rect.height)
        {

        }

        public RectF(Range range) : this(range.X(), range.Y(), range.Width(), range.Height())
        {

        }

        public RectF Set(RectF r)
        {
            this.x = r.x;
            this.y = r.y;
            this.width = r.width;
            this.height = r.height;
            return this;
        }

        public RectF Set(float x1, float y1, float w1, float h1)
        {
            this.x = x1;
            this.y = y1;
            this.width = w1;
            this.height = h1;
            return this;
        }

        public RectF(float x1, float y1, float w1, float h1)
        {
            this.x = x1;
            this.y = y1;
            this.width = w1;
            this.height = h1;
        }

        public bool Inside(float x, float y)
        {
            return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y - this.y) < this.height);
        }

        public float GetRight()
        {
            return this.x + this.width;
        }

        public float GetBottom()
        {
            return this.y + this.height;
        }

        public RectF GetIntersection(RectF rect)
        {
            float x1 = MathUtils.Max(x, rect.x);
            float x2 = MathUtils.Min(x + width, rect.x + rect.width);
            float y1 = MathUtils.Max(y, rect.y);
            float y2 = MathUtils.Min(y + height, rect.y + rect.height);
            return new RectF(x1, y1, x2 - x1, y2 - y1);
        }

        public static RectF GetIntersection(RectF a, RectF b)
        {
            float a_x = a.x;
            float a_r = a.GetRight();
            float a_y = a.y;
            float a_t = a.GetBottom();
            float b_x = b.x;
            float b_r = b.GetRight();
            float b_y = b.y;
            float b_t = b.GetBottom();
            float i_x = MathUtils.Max(a_x, b_x);
            float i_r = MathUtils.Min(a_r, b_r);
            float i_y = MathUtils.Max(a_y, b_y);
            float i_t = MathUtils.Min(a_t, b_t);
            return i_x < i_r && i_y < i_t ? new RectF(i_x, i_y, i_r - i_x, i_t - i_y) : new RectF();
        }

        public static RectF GetIntersection(RectF a, RectF b, RectF result)
        {
            float a_x = a.x;
            float a_r = a.GetRight();
            float a_y = a.y;
            float a_t = a.GetBottom();
            float b_x = b.x;
            float b_r = b.GetRight();
            float b_y = b.y;
            float b_t = b.GetBottom();
            float i_x = MathUtils.Max(a_x, b_x);
            float i_r = MathUtils.Min(a_r, b_r);
            float i_y = MathUtils.Max(a_y, b_y);
            float i_t = MathUtils.Min(a_t, b_t);
            if (i_x < i_r && i_y < i_t)
            {
                result.Set(i_x, i_y, i_r - i_x, i_t - i_y);
                return result;
            }
            return result;
        }

        public float Left()
        {
            return this.x;
        }

        public float Right()
        {
            return this.x + this.width;
        }

        public float Top()
        {
            return this.y;
        }

        public float Bottom()
        {
            return this.y + this.height;
        }

        public float MiddleX()
        {
            return this.x + this.width / 2f;
        }

        public float MiddleY()
        {
            return this.y + this.height / 2f;
        }

        public float CenterX()
        {
            return x + width / 2;
        }

        public float CenterY()
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

        public float GetWidth()
        {
            return width;
        }

        public float GetHeight()
        {
            return height;
        }

        public bool isEmpty()
        {
            return width <= 0 && height <= 0;
        }

        public static void GetNearestCorner(float x, float y, float w, float h, float px, float py, PointF result)
        {
            result.Set(MathUtils.Nearest(px, x, x + w), MathUtils.Nearest(y, y, y + h));
        }

        public static bool GetSegmentIntersectionIndices(float x, float y, float w, float h, float x1, float y1,
                float x2, float y2, float ti1, float ti2, PointF ti, PointF n1, PointF n2)
        {
            float dx = x2 - x1;
            float dy = y2 - y1;

            float nx = 0, ny = 0;
            float nx1 = 0, ny1 = 0, nx2 = 0, ny2 = 0;
            float p, q, r;

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

        public static void GetDiff(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2,
                RectF result)
        {
            result.Set(x2 - x1 - w1, y2 - y1 - h1, w1 + w2, h1 + h2);
        }

        public static bool ContainsPoint(float x, float y, float w, float h, float px, float py, float delta)
        {
            return px - x > delta && py - y > delta && x + w - px > delta && y + h - py > delta;
        }

        public static bool IsIntersecting(float x1, float y1, float w1, float h1, float x2, float y2, float w2,
                float h2)
        {
            return x1 < x2 + w2 && x2 < x1 + w1 && y1 < y2 + h2 && y2 < y1 + h1;
        }

        public static float GetSquareDistance(float x1, float y1, float w1, float h1, float x2, float y2, float w2,
                float h2)
        {
            float dx = x1 - x2 + (w1 - w2) / 2;
            float dy = y1 - y2 + (h1 - h2) / 2;
            return dx * dx + dy * dy;
        }

        public RectF Random()
        {
            this.x = MathUtils.Random(0f, LSystem.viewSize.GetWidth());
            this.y = MathUtils.Random(0f, LSystem.viewSize.GetHeight());
            this.width = MathUtils.Random(0f, LSystem.viewSize.GetWidth());
            this.height = MathUtils.Random(0f, LSystem.viewSize.GetHeight());
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
            StringKeyValue builder = new StringKeyValue("RectF");
            builder.Kv("x", x).Comma().Kv("y", y).Comma().Kv("width", width).Comma().Kv("height", height);
            return builder.ToString();
        }
    }
}

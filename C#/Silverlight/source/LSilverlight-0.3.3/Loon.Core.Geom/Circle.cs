namespace Loon.Core.Geom
{
    using Loon.Utils;
    using Loon.Action.Collision;

    public class Circle : Ellipse
    {
        private const long serialVersionUID = 1L;

        public float radius;

        /// <summary>
        /// 构建一个圆形
        /// </summary>
        ///
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="radius_0"></param>
        public Circle(float x, float y, float radius_0)
            : this(x, y, radius_0, Loon.Core.Geom.Ellipse.DEFAULT_SEGMENT_MAX_COUNT)
        {

        }

        /// <summary>
        /// 构建一个圆形
        /// </summary>
        ///
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <param name="radius_0"></param>
        /// <param name="segment"></param>
        public Circle(float x, float y, float radius_0, int segment)
            : base(x, y, radius_0, radius_0, segment)
        {

            this.x = x - radius_0;
            this.y = y - radius_0;
            this.radius = radius_0;
            this.boundingCircleRadius = radius_0;
            this.type = Loon.Core.Geom.ShapeType.CIRCLE_SHAPE;
        }

        /// <summary>
        /// 返回当前圆形的中心X点
        /// </summary>
        ///
        public override float GetCenterX()
        {
            return GetX() + radius;
        }

        /// <summary>
        /// 返回当前圆形的中心Y点
        /// </summary>
        ///
        public override float GetCenterY()
        {
            return GetY() + radius;
        }

        /// <summary>
        /// 设定当前圆形半径
        /// </summary>
        ///
        /// <param name="radius_0"></param>
        public void SetRadius(float radius_0)
        {
            if (radius_0 != this.radius)
            {
                pointsDirty = true;
                this.radius = radius_0;
                SetRadii(radius_0, radius_0);
            }
        }

        /// <summary>
        /// 返回当前圆形半径
        /// </summary>
        ///
        /// <returns></returns>
        public float GetRadius()
        {
            return radius;
        }

        /// <summary>
        /// 检查当前圆形与指定形状是否相交
        /// </summary>
        ///
        public override bool Intersects(Shape shape)
        {
            if (shape is Circle)
            {
                Circle other = (Circle)shape;
                float totalRad2 = GetRadius() + other.GetRadius();

                if (MathUtils.Abs(other.GetCenterX() - GetCenterX()) > totalRad2)
                {
                    return false;
                }
                if (MathUtils.Abs(other.GetCenterY() - GetCenterY()) > totalRad2)
                {
                    return false;
                }

                totalRad2 *= totalRad2;

                float dx = MathUtils.Abs(other.GetCenterX() - GetCenterX());
                float dy = MathUtils.Abs(other.GetCenterY() - GetCenterY());

                return totalRad2 >= ((dx * dx) + (dy * dy));
            }
            else if (shape is RectBox)
            {
                return Intersects((RectBox)shape);
            }
            else
            {
                return base.Intersects(shape);
            }
        }

        /// <summary>
        /// 检查当前圆形是否包含指定坐标
        /// </summary>
        ///
        public override bool Contains(float x0, float y0)
        {
            return CollisionHelper.IsCollision(x + radius, y + radius, radius, x0,
                y0, 0);
        }

        /// <summary>
        /// 检查当前圆形是否包含指定直线
        /// </summary>
        ///
        /// <param name="line"></param>
        /// <returns></returns>
        public bool Contains(Line line)
        {
            return Contains(line.GetX1(), line.GetY1())
                    && Contains(line.GetX2(), line.GetY2());
        }

        protected internal override void FindCenter()
        {
            center = new float[2];
            center[0] = x + radius;
            center[1] = y + radius;
        }

        protected internal override void CalculateRadius()
        {
            boundingCircleRadius = radius;
        }

        private bool Intersects(RectBox other)
        {
            RectBox box = other;
            Circle circle = this;

            if (box.Contains(x + radius, y + radius))
            {
                return true;
            }

            float x1 = box.GetX();
            float y1 = box.GetY();
            float x2 = box.GetX() + box.GetWidth();
            float y2 = box.GetY() + box.GetHeight();

            Line[] lines = new Line[4];
            lines[0] = new Line(x1, y1, x2, y1);
            lines[1] = new Line(x2, y1, x2, y2);
            lines[2] = new Line(x2, y2, x1, y2);
            lines[3] = new Line(x1, y2, x1, y1);

            float r2 = circle.GetRadius() * circle.GetRadius();

            Vector2f pos = new Vector2f(circle.GetCenterX(), circle.GetCenterY());

            for (int i = 0; i < 4; i++)
            {
                float dis = lines[i].DistanceSquared(pos);
                if (dis < r2)
                {
                    return true;
                }
            }

            return false;
        }

        public bool Intersects(Line other)
        {
            Vector2f lineSegmentStart = new Vector2f(other.GetX1(), other.GetY1());
            Vector2f lineSegmentEnd = new Vector2f(other.GetX2(), other.GetY2());
            Vector2f circleCenter = new Vector2f(GetCenterX(), GetCenterY());
            Vector2f closest;
            Vector2f segv = lineSegmentEnd.Cpy().Sub(lineSegmentStart);
            Vector2f ptv = circleCenter.Cpy().Sub(lineSegmentStart);
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
                Vector2f projv = segv.Cpy().Scale(projvl / segvLength);
                closest = lineSegmentStart.Cpy().Add(projv);
            }
            bool intersects = circleCenter.Cpy().Sub(closest).LengthSquared() <= GetRadius()
                    * GetRadius();
            return intersects;
        }
    }
}

namespace Loon.Action.Collision
{

    using System;
    using Loon.Core.Geom;
    using Loon.Core.Graphics;
    using Loon.Core;

    public sealed class CollisionHelper
    {

        /**
         * 检查两个坐标值是否在指定的碰撞半径内
         * 
         * @param x1
         * @param y1
         * @param r1
         * @param x2
         * @param y2
         * @param r2
         * @return
         */
        public static bool IsCollision(float x1, float y1, float r1, float x2,
                float y2, float r2)
        {
            float a = r1 + r2;
            float dx = x1 - x2;
            float dy = y1 - y2;
            return a * a > dx * dx + dy * dy;
        }

        /// <summary>
        /// 获得两个矩形间距离
        /// </summary>
        ///
        /// <param name="box1"></param>
        /// <param name="box2"></param>
        /// <returns></returns>
        public static float GetDistance(RectBox box1, RectBox box2)
        {
            float xdiff = box1.x - box2.x;
            float ydiff = box1.y - box2.y;
            return Loon.Utils.MathUtils.Sqrt(xdiff * xdiff + ydiff * ydiff);
        }

        /// <summary>
        /// 检查两个矩形是否发生了碰撞
        /// </summary>
        ///
        /// <param name="rect1"></param>
        /// <param name="rect2"></param>
        /// <returns></returns>
        public static bool IsRectToRect(RectBox rect1, RectBox rect2)
        {
            return rect1.Intersects(rect2);
        }

        /// <summary>
        /// 判断两个圆形是否发生了碰撞
        /// </summary>
        ///
        /// <param name="rect1"></param>
        /// <param name="rect2"></param>
        /// <returns></returns>
        public static bool IsCircToCirc(RectBox rect1, RectBox rect2)
        {
            Point middle1 = GetMiddlePoint(rect1);
            Point middle2 = GetMiddlePoint(rect2);
            float distance = middle1.DistanceTo(middle2);
            float radius1 = rect1.GetWidth() / 2;
            float radius2 = rect2.GetWidth() / 2;
            return (distance - radius2) < radius1;
        }

        /// <summary>
        /// 检查矩形与圆形是否发生了碰撞
        /// </summary>
        ///
        /// <param name="rect1"></param>
        /// <param name="rect2"></param>
        /// <returns></returns>
        public static bool IsRectToCirc(RectBox rect1, RectBox rect2)
        {
            float radius = rect2.GetWidth() / 2;
            Point middle = GetMiddlePoint(rect2);
            Point upperLeft = new Point(rect1.GetMinX(), rect1.GetMinY());
            Point upperRight = new Point(rect1.GetMaxX(), rect1.GetMinY());
            Point downLeft = new Point(rect1.GetMinX(), rect1.GetMaxY());
            Point downRight = new Point(rect1.GetMaxX(), rect1.GetMaxY());
            bool collided = true;
            if (!IsPointToLine(upperLeft, upperRight, middle, radius))
            {
                if (!IsPointToLine(upperRight, downRight, middle, radius))
                {
                    if (!IsPointToLine(upperLeft, downLeft, middle, radius))
                    {
                        if (!IsPointToLine(downLeft, downRight, middle, radius))
                        {
                            collided = false;
                        }
                    }
                }
            }
            return collided;
        }

        /// <summary>
        /// 换算点线距离
        /// </summary>
        ///
        /// <param name="point1"></param>
        /// <param name="point2"></param>
        /// <param name="middle"></param>
        /// <param name="radius"></param>
        /// <returns></returns>
        private static bool IsPointToLine(Point point1, Point point2,
                Point middle, float radius)
        {
            Line line = new Line(point1, point2);
            float distance = line.PtLineDist(middle);
            return distance < radius;
        }

        /// <summary>
        /// 返回中间距离的Point2D形式
        /// </summary>
        ///
        /// <param name="rectangle"></param>
        /// <returns></returns>
        private static Point GetMiddlePoint(RectBox rectangle)
        {
            return new Point(rectangle.GetCenterX(), rectangle.GetCenterY());
        }

        /// <summary>
        /// 判定指定的两张图片之间是否产生了碰撞
        /// </summary>
        ///
        /// <param name="src"></param>
        /// <param name="x1"></param>
        /// <param name="y1"></param>
        /// <param name="dest"></param>
        /// <param name="x2"></param>
        /// <param name="y2"></param>
        /// <returns></returns>
        public bool isPixelCollide(LImage src, float x1, float y1, LImage dest,
                float x2, float y2)
        {
            float width1 = x1 + src.GetWidth() - 1, height1 = y1 + src.GetHeight()
                    - 1, width2 = x2 + dest.GetWidth() - 1, height2 = y2
                    + dest.GetHeight() - 1;
            int xstart = (int)Loon.Utils.MathUtils.Max(x1, x2), ystart = (int)Loon.Utils.MathUtils.Max(y1, y2), xend = (int)Loon.Utils.MathUtils.Min(width1, width2), yend = (int)Loon.Utils.MathUtils.Min(height1, height2);
            int toty = Loon.Utils.MathUtils.Abs(yend - ystart);
            int totx = Loon.Utils.MathUtils.Abs(xend - xstart);
            for (int y = 1; y < toty - 1; y++)
            {
                int ny = Loon.Utils.MathUtils.Abs(ystart - (int)y1) + y;
                int ny1 = Loon.Utils.MathUtils.Abs(ystart - (int)y2) + y;
                for (int x = 1; x < totx - 1; x++)
                {
                    int nx = Loon.Utils.MathUtils.Abs(xstart - (int)x1) + x;
                    int nx1 = Loon.Utils.MathUtils.Abs(xstart - (int)x2) + x;
                    try
                    {
                        if (((src.GetPixel(nx, ny).PackedValue != LSystem.TRANSPARENT))
                                && ((dest.GetPixel(nx1, ny1).PackedValue != LSystem.TRANSPARENT)))
                        {
                            return true;
                        }
                    }
                    catch (Exception e)
                    {
                        Loon.Utils.Debugging.Log.Exception(e);
                    }
                }
            }
            return false;
        }


        /// <summary>
        /// 判断指定大小的两组像素是否相交
        /// </summary>
        ///
        /// <param name="rectA"></param>
        /// <param name="dataA"></param>
        /// <param name="rectB"></param>
        /// <param name="dataB"></param>
        /// <returns></returns>
        public static bool Intersect(RectBox rectA, int[] dataA, RectBox rectB,
                int[] dataB)
        {
            int top = (int)Loon.Utils.MathUtils.Max(rectA.GetY(), rectB.GetY());
            int bottom = (int)Loon.Utils.MathUtils.Min(rectA.GetBottom(), rectB.GetBottom());
            int left = (int)Loon.Utils.MathUtils.Max(rectA.GetX(), rectB.GetX());
            int right = (int)Loon.Utils.MathUtils.Min(rectA.GetRight(), rectB.GetRight());

            for (int y = top; y < bottom; y++)
            {
                for (int x = left; x < right; x++)
                {

                    int colorA = dataA[(int)((x - rectA.x) + (y - rectA.y)
                            * rectA.width)];
                    int colorB = dataB[(int)((x - rectB.x) + (y - rectB.y)
                            * rectB.width)];
                    if ((int)(((uint)colorA) >> 24) != 0 && (int)(((uint)colorB) >> 24) != 0)
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        /// <summary>
        /// 判断两个Shape是否相交
        /// </summary>
        ///
        /// <param name="s1"></param>
        /// <param name="s2"></param>
        /// <returns></returns>
        public static bool Intersects(Shape s1, Shape s2)
        {
            if (s1 == null || s2 == null)
            {
                return false;
            }
            return s1.Intersects(s2);
        }

        /// <summary>
        /// 判断两个Shape是否存在包含关系
        /// </summary>
        ///
        /// <param name="s1"></param>
        /// <param name="s2"></param>
        /// <returns></returns>
        public static bool Contains(Shape s1, Shape s2)
        {
            if (s1 == null || s2 == null)
            {
                return false;
            }
            return s1.Contains(s2);
        }

        public static Line GetLine(Shape shape, int s, int e)
        {
            float[] start = shape.GetPoint(s);
            float[] end = shape.GetPoint(e);
            Line line = new Line(start[0], start[1], end[0], end[1]);
            return line;
        }

        public static Line GetLine(Shape shape, float sx, float sy, int e)
        {
            float[] end = shape.GetPoint(e);
            Line line = new Line(sx, sy, end[0], end[1]);
            return line;
        }
    }
}

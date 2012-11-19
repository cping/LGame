using System;
using Loon.Utils;
using Loon.Core.Geom;

namespace Loon.Core.Graphics.Device
{
    public class LGraphicsMath
    {
        private static readonly int[] SHIFT = { 0, 1144, 2289, 3435, 4583, 5734, 6888,
				8047, 9210, 10380, 11556, 12739, 13930, 15130, 16340, 17560, 18792,
				20036, 21294, 22566, 23853, 25157, 26478, 27818, 29179, 30560,
				31964, 33392, 34846, 36327, 37837, 39378, 40951, 42560, 44205,
				45889, 47615, 49385, 51202, 53070, 54991, 56970, 59009, 61113,
				63287, 65536 };

        public static int Round(int div1, int div2)
        {
            int remainder = div1 % div2;
            if (MathUtils.Abs(remainder) * 2 <= MathUtils.Abs(div2))
            {
                return div1 / div2;
            }
            else if (div1 * div2 < 0)
            {
                return div1 / div2 - 1;
            }
            else
            {
                return div1 / div2 + 1;
            }
        }

        public static long Round(long div1, long div2)
        {
            long remainder = div1 % div2;
            if (MathUtils.Abs(remainder) * 2 <= MathUtils.Abs(div2))
            {
                return div1 / div2;
            }
            else if (div1 * div2 < 0)
            {
                return div1 / div2 - 1;
            }
            else
            {
                return div1 / div2 + 1;
            }
        }


        public static int ToShift(int angle)
        {
            if (angle <= 45)
            {
                return SHIFT[angle];
            }
            else if (angle >= 315)
            {
                return -SHIFT[360 - angle];
            }
            else if (angle >= 135 && angle <= 180)
            {
                return -SHIFT[180 - angle];
            }
            else if (angle >= 180 && angle <= 225)
            {
                return SHIFT[angle - 180];
            }
            else if (angle >= 45 && angle <= 90)
            {
                return SHIFT[90 - angle];
            }
            else if (angle >= 90 && angle <= 135)
            {
                return -SHIFT[angle - 90];
            }
            else if (angle >= 225 && angle <= 270)
            {
                return SHIFT[270 - angle];
            }
            else
            {
                return -SHIFT[angle - 270];
            }
        }


        public static Loon.Core.Geom.Point.Point2i GetBoundingPointAtAngle(int boundingX,
                int boundingY, int boundingWidth, int boundingHeight, int angle)
        {
            if (angle >= 315 || angle <= 45)
            {
                return new Loon.Core.Geom.Point.Point2i(boundingX + boundingWidth, boundingY
                        + ((int)(((uint)boundingHeight * (65536 - ToShift(angle))) >> 17)));
            }
            else if (angle > 45 && angle < 135)
            {
                return new Loon.Core.Geom.Point.Point2i(boundingX
                        + ((int)(((uint)boundingWidth * (65536 + ToShift(angle))) >> 17)),
                        boundingY);
            }
            else if (angle >= 135 && angle <= 225)
            {
                return new Loon.Core.Geom.Point.Point2i(boundingX, boundingY
                        + ((int)(((uint)boundingHeight * (65536 + ToShift(angle))) >> 17)));
            }
            else
            {
                return new Loon.Core.Geom.Point.Point2i(boundingX
                        + ((int)(((uint)boundingWidth * (65536 - ToShift(angle))) >> 17)),
                        boundingY + boundingHeight);
            }
        }

        public static RectBox GetBoundingBox(int[] xpoints, int[] ypoints,
                int npoints)
        {
            int boundsMinX = Int32.MaxValue;
            int boundsMinY = Int32.MaxValue;
            int boundsMaxX = Int32.MinValue;
            int boundsMaxY = Int32.MinValue;

            for (int i = 0; i < npoints; i++)
            {
                int x_0 = xpoints[i];
                boundsMinX = Math.Min(boundsMinX, x_0);
                boundsMaxX = Math.Max(boundsMaxX, x_0);
                int y_1 = ypoints[i];
                boundsMinY = Math.Min(boundsMinY, y_1);
                boundsMaxY = Math.Max(boundsMaxY, y_1);
            }

            return new RectBox(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
                    boundsMaxY - boundsMinY);
        }

        public static int GetBoundingShape(int[] xPoints, int[] yPoints,
                int startAngle, int arcAngle, int centerX, int centerY,
                int boundingX, int boundingY, int boundingWidth, int boundingHeight)
        {
            xPoints[0] = centerX;
            yPoints[0] = centerY;
            Loon.Core.Geom.Point.Point2i startPoint = GetBoundingPointAtAngle(boundingX, boundingY,
                    boundingWidth, boundingHeight, startAngle);
            xPoints[1] = startPoint.x;
            yPoints[1] = startPoint.y;
            int i = 2;
            for (int angle = 0; angle < arcAngle; i++, angle += 90)
            {
                if (angle + 90 > arcAngle
                        && ((startAngle + angle - 45) % 360) / 90 == ((startAngle
                                + arcAngle + 45) % 360) / 90)
                {
                    break;
                }
                int modAngle = (startAngle + angle) % 360;
                if (modAngle > 315 || modAngle <= 45)
                {
                    xPoints[i] = boundingX + boundingWidth;
                    yPoints[i] = boundingY;
                }
                else if (modAngle > 135 && modAngle <= 225)
                {
                    xPoints[i] = boundingX;
                    yPoints[i] = boundingY + boundingHeight;
                }
                else if (modAngle > 45 && modAngle <= 135)
                {
                    xPoints[i] = boundingX;
                    yPoints[i] = boundingY;
                }
                else
                {
                    xPoints[i] = boundingX + boundingWidth;
                    yPoints[i] = boundingY + boundingHeight;
                }
            }
            Loon.Core.Geom.Point.Point2i endPoint = GetBoundingPointAtAngle(boundingX, boundingY,
                    boundingWidth, boundingHeight, (startAngle + arcAngle) % 360);
            if (xPoints[i - 1] != endPoint.x || yPoints[i - 1] != endPoint.y)
            {
                xPoints[i] = endPoint.x;
                yPoints[i++] = endPoint.y;
            }
            return i;
        }

        public static bool Contains(int[] xPoints, int[] yPoints,
                int nPoints,RectBox bounds, int x1, int y1)
        {
            if ((bounds != null && bounds.Inside(x1, y1))
                    || (bounds == null && GetBoundingBox(xPoints, yPoints, nPoints)
                            .Inside(x1, y1)))
            {
                int hits = 0;
                int ySave = 0;
                int i = 0;

                while (i < nPoints && yPoints[i] == y1)
                {
                    i++;
                }
                for (int n = 0; n < nPoints; n++)
                {
                    int j = (i + 1) % nPoints;

                    int dx = xPoints[j] - xPoints[i];
                    int dy = yPoints[j] - yPoints[i];

                    if (dy != 0)
                    {

                        int rx = x1 - xPoints[i];
                        int ry = y1 - yPoints[i];

                        if (yPoints[j] == y1 && xPoints[j] >= x1)
                        {
                            ySave = yPoints[i];
                        }
                        if (yPoints[i] == y1 && xPoints[i] >= x1)
                        {
                            if ((ySave > y1) != (yPoints[j] > y1))
                            {
                                hits--;
                            }
                        }
                        if (ry * dy >= 0
                                && (ry <= dy && ry >= 0 || ry >= dy && ry <= 0)
                                && Round(dx * ry, dy) >= rx)
                        {
                            hits++;
                        }
                    }
                    i = j;
                }
                return (hits % 2) != 0;
            }

            return false;
        }
	
	
    }
}

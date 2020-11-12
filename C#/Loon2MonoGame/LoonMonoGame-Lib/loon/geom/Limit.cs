using java.lang;
using loon.utils;

namespace loon.geom
{

	public class Limit
	{

		private readonly static PointI tmp_point = new PointI(0, 0);

		private readonly static RectF tmp_rect_f = new RectF();

		private readonly static RectI tmp_rect_I = new RectI();

		public static void SetBoundingPointAtAngle(PointI p, int boundingX,
				int boundingY, int boundingWidth, int boundingHeight, int angle)
		{
			if (angle >= 315 || angle <= 45)
			{
				p.Set(boundingX + boundingWidth,
						boundingY
								+ (boundingHeight
										* (65536 - (int)((uint)MathUtils.ToShift(angle)) >> 17)));
			}
			else if (angle > 45 && angle < 135)
			{
				p.Set(boundingX
						+ (boundingWidth * (65536 + (int)((uint)MathUtils.ToShift(angle)) >> 17)),
						boundingY);
			}
			else if (angle >= 135 && angle <= 225)
			{
				p.Set(boundingX,
						boundingY
								+ (boundingHeight
										* (65536 + (int)((uint)MathUtils.ToShift(angle)) >> 17)));
			}
			else
			{
				p.Set(boundingX
						+ (boundingWidth * (65536 - (int)((uint)MathUtils.ToShift(angle)) >> 17)),
						boundingY + boundingHeight);
			}
		}

		public static PointI GetBoundingPointAtAngle(int boundingX,
				int boundingY, int boundingWidth, int boundingHeight, int angle)
		{
			if (angle >= 315 || angle <= 45)
			{
				return new PointI(
						boundingX + boundingWidth,
						boundingY
								+ (boundingHeight
										* (65536 - (int)((uint)MathUtils.ToShift(angle)) >> 17)));
			}
			else if (angle > 45 && angle < 135)
			{
				return new PointI(
						boundingX
								+ (boundingWidth
										* (65536 + (int)((uint)MathUtils.ToShift(angle)) >> 17)),
						boundingY);
			}
			else if (angle >= 135 && angle <= 225)
			{
				return new PointI(
						boundingX,
						boundingY
								+ (boundingHeight
										* (65536 + (int)((uint)MathUtils.ToShift(angle)) >> 17)));
			}
			else
			{
				return new PointI(
						boundingX
								+ (boundingWidth
										* (65536 - (int)((uint)MathUtils.ToShift(angle)) >> 17)),
						boundingY + boundingHeight);
			}
		}

		public static RectF GetBoundingBox(float[] points, int npoints)
		{
			float boundsMinX = Float.MAX_VALUE_JAVA;
			float boundsMinY = Float.MAX_VALUE_JAVA;
			float boundsMaxX = Float.MIN_VALUE_JAVA;
			float boundsMaxY = Float.MIN_VALUE_JAVA;

			for (int i = 0; i < npoints; i = +2)
			{
				float x = points[i];
				boundsMinX = MathUtils.Min(boundsMinX, x);
				boundsMaxX = MathUtils.Max(boundsMaxX, x);
				float y = points[i + 1];
				boundsMinY = MathUtils.Min(boundsMinY, y);
				boundsMaxY = MathUtils.Max(boundsMaxY, y);
			}

			return new RectF(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
					boundsMaxY - boundsMinY);
		}

		public static RectF SetBoundingBox(RectF rect, float[] xpoints,
				float[] ypoints, int npoints)
		{
			float boundsMinX = Float.MAX_VALUE_JAVA;
			float boundsMinY = Float.MAX_VALUE_JAVA;
			float boundsMaxX = Float.MIN_VALUE_JAVA;
			float boundsMaxY = Float.MIN_VALUE_JAVA;

			for (int i = 0; i < npoints; i++)
			{
				float x = xpoints[i];
				boundsMinX = MathUtils.Min(boundsMinX, x);
				boundsMaxX = MathUtils.Max(boundsMaxX, x);
				float y = ypoints[i];
				boundsMinY = MathUtils.Min(boundsMinY, y);
				boundsMaxY = MathUtils.Max(boundsMaxY, y);
			}

			return rect.Set(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
					boundsMaxY - boundsMinY);
		}

		public static RectF GetBoundingBox(float[] xpoints, float[] ypoints,
				int npoints)
		{
			float boundsMinX = Float.MAX_VALUE_JAVA;
			float boundsMinY = Float.MAX_VALUE_JAVA;
			float boundsMaxX = Float.MIN_VALUE_JAVA;
			float boundsMaxY = Float.MIN_VALUE_JAVA;

			for (int i = 0; i < npoints; i++)
			{
				float x = xpoints[i];
				boundsMinX = MathUtils.Min(boundsMinX, x);
				boundsMaxX = MathUtils.Max(boundsMaxX, x);
				float y = ypoints[i];
				boundsMinY = MathUtils.Min(boundsMinY, y);
				boundsMaxY = MathUtils.Max(boundsMaxY, y);
			}

			return new RectF(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
					boundsMaxY - boundsMinY);
		}

		public static RectI SetBoundingBox(RectI rect, int[] xpoints,
				int[] ypoints, int npoints)
		{
			int boundsMinX = Integer.MAX_VALUE_JAVA;
			int boundsMinY = Integer.MAX_VALUE_JAVA;
			int boundsMaxX = Integer.MIN_VALUE_JAVA;
			int boundsMaxY = Integer.MIN_VALUE_JAVA;

			for (int i = 0; i < npoints; i++)
			{
				int x = xpoints[i];
				boundsMinX = MathUtils.Min(boundsMinX, x);
				boundsMaxX = MathUtils.Max(boundsMaxX, x);
				int y = ypoints[i];
				boundsMinY = MathUtils.Min(boundsMinY, y);
				boundsMaxY = MathUtils.Max(boundsMaxY, y);
			}

			return rect.Set(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
					boundsMaxY - boundsMinY);
		}

		public static RectI GetBoundingBox(int[] xpoints, int[] ypoints,
				int npoints)
		{
			int boundsMinX = Integer.MAX_VALUE_JAVA;
			int boundsMinY = Integer.MAX_VALUE_JAVA;
			int boundsMaxX = Integer.MIN_VALUE_JAVA;
			int boundsMaxY = Integer.MIN_VALUE_JAVA;

			for (int i = 0; i < npoints; i++)
			{
				int x = xpoints[i];
				boundsMinX = MathUtils.Min(boundsMinX, x);
				boundsMaxX = MathUtils.Max(boundsMaxX, x);
				int y = ypoints[i];
				boundsMinY = MathUtils.Min(boundsMinY, y);
				boundsMaxY = MathUtils.Max(boundsMaxY, y);
			}

			return new RectI(boundsMinX, boundsMinY, boundsMaxX - boundsMinX,
					boundsMaxY - boundsMinY);
		}

		public static int GetBoundingShape(int[] xPoints, int[] yPoints,
				int startAngle, int arcAngle, int centerX, int centerY,
				int boundingX, int boundingY, int boundingWidth, int boundingHeight)
		{
			xPoints[0] = centerX;
			yPoints[0] = centerY;
			SetBoundingPointAtAngle(tmp_point, boundingX, boundingY, boundingWidth,
					boundingHeight, startAngle);
			xPoints[1] = tmp_point.x;
			yPoints[1] = tmp_point.y;
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
			SetBoundingPointAtAngle(tmp_point, boundingX, boundingY, boundingWidth,
					boundingHeight, (startAngle + arcAngle) % 360);
			if (xPoints[i - 1] != tmp_point.x || yPoints[i - 1] != tmp_point.y)
			{
				xPoints[i] = tmp_point.x;
				yPoints[i++] = tmp_point.y;
			}
			return i;
		}

		public static int GetBoundingShape(float[] xPoints, float[] yPoints,
				float startAngle, float arcAngle, float centerX, float centerY,
				float boundingX, float boundingY, float boundingWidth,
				float boundingHeight)
		{
			xPoints[0] = centerX;
			yPoints[0] = centerY;
			SetBoundingPointAtAngle(tmp_point, (int)boundingX, (int)boundingY,
					(int)boundingWidth, (int)boundingHeight, (int)startAngle);
			xPoints[1] = tmp_point.x;
			yPoints[1] = tmp_point.y;
			int i = 2;
			for (int angle = 0; angle < arcAngle; i++, angle += 90)
			{
				if (angle + 90 > arcAngle
						&& ((startAngle + angle - 45) % 360) / 90 == ((startAngle
								+ arcAngle + 45) % 360) / 90)
				{
					break;
				}
				float modAngle = (startAngle + angle) % 360;
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
			SetBoundingPointAtAngle(tmp_point, (int)boundingX, (int)boundingY,
					(int)boundingWidth, (int)boundingHeight,
					(int)(startAngle + arcAngle) % 360);
			if (xPoints[i - 1] != tmp_point.x || yPoints[i - 1] != tmp_point.y)
			{
				xPoints[i] = tmp_point.x;
				yPoints[i++] = tmp_point.y;
			}
			return i;
		}

		public static bool Contains(int[] xPoints, int[] yPoints,
				int nPoints, RectI bounds, int x, int y)
		{
			if ((bounds != null && bounds.Inside(x, y))
					|| (bounds == null && SetBoundingBox(tmp_rect_I, xPoints,
							yPoints, nPoints).Inside(x, y)))
			{
				int hits = 0;
				int ySave = 0;
				int i = 0;

				while (i < nPoints && yPoints[i] == y)
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

						int rx = x - xPoints[i];
						int ry = y - yPoints[i];

						if (yPoints[j] == y && xPoints[j] >= x)
						{
							ySave = yPoints[i];
						}
						if (yPoints[i] == y && xPoints[i] >= x)
						{
							if ((ySave > y) != (yPoints[j] > y))
							{
								hits--;
							}
						}
						if (ry * dy >= 0
								&& (ry <= dy && ry >= 0 || ry >= dy && ry <= 0)
								&& MathUtils.Round(dx * ry, dy) >= rx)
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

		public static bool Contains(float[] xPoints, float[] yPoints,
				int nPoints, RectF bounds, float x, float y)
		{
			if ((bounds != null && bounds.Inside(x, y))
					|| (bounds == null && SetBoundingBox(tmp_rect_f, xPoints,
							yPoints, nPoints).Inside(x, y)))
			{
				int hits = 0;
				float ySave = 0;
				int i = 0;

				while (i < nPoints && yPoints[i] == y)
				{
					i++;
				}
				for (int n = 0; n < nPoints; n++)
				{
					int j = (i + 1) % nPoints;

					float dx = xPoints[j] - xPoints[i];
					float dy = yPoints[j] - yPoints[i];

					if (dy != 0)
					{

						float rx = x - xPoints[i];
						float ry = y - yPoints[i];

						if (yPoints[j] == y && xPoints[j] >= x)
						{
							ySave = yPoints[i];
						}
						if (yPoints[i] == y && xPoints[i] >= x)
						{
							if ((ySave > y) != (yPoints[j] > y))
							{
								hits--;
							}
						}
						if (ry * dy >= 0
								&& (ry <= dy && ry >= 0 || ry >= dy && ry <= 0)
								&& MathUtils.Round(dx * ry, dy) >= rx)
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

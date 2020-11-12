using loon.utils;

namespace loon.geom
{

	public class Triangulation
	{

		private readonly TArray<Vector2f> points;
		private readonly TArray<Vector2f> nonconvexPoints;

		private bool isCw;

		public Triangulation(TArray<XY> points)
		{
			this.points = new TArray<Vector2f>();
			for (int i = 0; i < points.Size(); i++)
			{
				this.points.Add(new Vector2f(points.Get(i)));
			}
			this.nonconvexPoints = new TArray<Vector2f>();
			CalcPolyOrientation();
			CalcNonConvexPoints();
		}

		private void CalcNonConvexPoints()
		{

			if (points.Size() <= 3)
			{
				return;
			}

			Vector2f p;
			Vector2f v;
			Vector2f u;

			float res;
			for (int i = 0; i < points.Size() - 1; i++)
			{
				p = points.Get(i);
				Vector2f tmp = points.Get(i + 1);
				v = new Vector2f();
				v.x = tmp.x - p.x;
				v.y = tmp.y - p.y;

				if (i == points.Size() - 2)
				{
					u = points.Get(0);
				}
				else
				{
					u = points.Get(i + 2);
				}
				res = u.x * v.y - u.y * v.x + v.x * p.y - v.y * p.x;
				if ((res > 0 && isCw) || (res <= 0 && !isCw))
				{
					nonconvexPoints.Add(tmp);
				}

			}
		}

		private void CalcPolyOrientation()
		{
			if (points.Size() < 3)
			{
				return;
			}

			int index = 0;
			Vector2f pointOfIndex = points.Get(0);
			for (int i = 1; i < points.Size(); i++)
			{
				if (points.Get(i).x < pointOfIndex.x)
				{
					pointOfIndex = points.Get(i);
					index = i;
				}
				else if (points.Get(i).x == pointOfIndex.x && points.Get(i).y > pointOfIndex.y)
				{
					pointOfIndex = points.Get(i);
					index = i;
				}
			}

			Vector2f prevPointOfIndex;
			if (index == 0)
			{
				prevPointOfIndex = points.Get(points.Size() - 1);
			}
			else
			{
				prevPointOfIndex = points.Get(index - 1);
			}
			Vector2f v1 = new Vector2f(pointOfIndex.x - prevPointOfIndex.x, pointOfIndex.y - prevPointOfIndex.y);

			Vector2f succPointOfIndex;
			if (index == points.Size() - 1)
			{
				succPointOfIndex = points.Get(0);
			}
			else
			{
				succPointOfIndex = points.Get(index + 1);
			}

			float res = succPointOfIndex.x * v1.y - succPointOfIndex.y * v1.x + v1.x * prevPointOfIndex.y
					- v1.y * prevPointOfIndex.x;

			isCw = (res <= 0);

		}

		private bool IsEar(Vector2f p1, Vector2f p2, Vector2f p3)
		{
			if (!(IsConvex(p1, p2, p3)))
			{
				return false;
			}
			for (int i = 0; i < nonconvexPoints.Size(); i++)
			{
				if (Triangle2f.IsInside(p1, p2, p3, nonconvexPoints.Get(i)))
					return false;
			}
			return true;
		}

		private bool IsConvex(Vector2f p1, Vector2f p2, Vector2f p3)
		{
			Vector2f v = new Vector2f(p2.x - p1.x, p2.y - p1.y);
			float res = p3.x * v.y - p3.y * v.x + v.x * p1.y - v.y * p1.x;
			return !((res > 0 && isCw) || (res <= 0 && !isCw));
		}

		private int GetIndex(int index, int offSet)
		{
			int newindex;

			if (index + offSet >= points.Size())
			{
				newindex = points.Size() - (index + offSet);
			}
			else
			{
				if (index + offSet < 0)
				{
					newindex = points.Size() + (index + offSet);
				}
				else
				{
					newindex = index + offSet;
				}
			}
			return newindex;
		}

		public TArray<Triangle2f> CreateTriangulates()
		{
			TArray<Triangle2f> triangles = new TArray<Triangle2f>();

			if (points.Size() <= 3)
			{
				return triangles;
			}

			int index = 1;

			for (; points.Size() > 3;)
			{
				if (IsEar(points.Get(GetIndex(index, -1)), points.Get(index), points.Get(GetIndex(index, 1))))
				{
					triangles.Add(new Triangle2f(points.Get(GetIndex(index, -1)), points.Get(index),
							points.Get(GetIndex(index, 1))));
					points.Remove(points.Get(index));
					index = GetIndex(index, -1);
				}
				else
				{
					index = GetIndex(index, 1);
				}
			}

			triangles.Add(new Triangle2f(points.Get(0), points.Get(1), points.Get(2)));

			return triangles;
		}
	}

}

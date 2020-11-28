using loon.utils;

namespace loon.action.map.heuristics
{
	public class Closest : AStarFindHeuristic
	{

		public override float GetScore(float sx, float sy, float tx, float ty)
		{
			float dx = tx - sx;
			float dy = ty - sy;
			float result = MathUtils.Sqrt((dx * dx) + (dy * dy));
			return result;
		}

		public override int Type
		{
			get
			{
				return AStarFindHeuristic.CLOSEST;
			}
		}

		public override string ToString()
		{
			return "Closest";
		}

	}
}

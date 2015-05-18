namespace Loon.Action.Map.Heuristics {
	
	using Loon.Action.Map;
	
	public class ClosestSquared : AStarFindHeuristic {

        public override float GetScore(float sx, float sy, float tx, float ty)
        {
			float dx = tx - sx;
			float dy = ty - sy;
			return ((dx * dx) + (dy * dy));
		}

        public override int GetCodeType()
        {
			return AStarFindHeuristic.CLOSEST_SQUARED;
		}
	
	}
}

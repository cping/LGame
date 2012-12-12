namespace Loon.Action.Map.Heuristics {
	
    using Loon.Utils;
	
	public class Closest : AStarFindHeuristic {

        public override float GetScore(float sx, float sy, float tx, float ty)
        {
			float dx = tx - sx;
			float dy = ty - sy;
			float result = MathUtils.Sqrt((dx * dx) + (dy * dy));
			return result;
		}

        public override int GetCodeType()
        {
			return AStarFindHeuristic.CLOSEST;
		}
	
	}
}

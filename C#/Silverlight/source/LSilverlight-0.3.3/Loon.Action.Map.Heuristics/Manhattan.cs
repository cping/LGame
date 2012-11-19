namespace Loon.Action.Map.Heuristics {
	
	using Loon.Action.Map;
    using Loon.Utils;
	
	public class Manhattan : AStarFindHeuristic {

        public override float GetScore(float sx, float sy, float tx, float ty)
        {
			return (MathUtils.Abs(sx - tx) + MathUtils.Abs(sy - ty));
		}

        public override int GetCodeType()
        {
			return AStarFindHeuristic.MANHATTAN;
		}
	
	}
}

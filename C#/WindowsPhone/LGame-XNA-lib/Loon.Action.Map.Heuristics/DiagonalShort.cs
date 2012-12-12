namespace Loon.Action.Map.Heuristics {
	
	using Loon.Action.Map;
    using Loon.Utils;
	
	public class DiagonalShort : AStarFindHeuristic {

        public override float GetScore(float sx, float sy, float tx, float ty)
        {
            float diagonal = MathUtils.Min(MathUtils.Abs(sx - tx),
                    MathUtils.Abs(sy - ty));
            float straight = (MathUtils.Abs(sx - tx) + MathUtils.Abs(sy - ty));
			return 2 * diagonal + (straight - 2 * diagonal);
		}

        public override int GetCodeType()
        {
			return AStarFindHeuristic.DIAGONAL_SHORT;
		}
	
	}
}

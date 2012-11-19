namespace Loon.Action.Map.Heuristics {
	
	using Loon.Action.Map;
    using Loon.Utils;
	
	public class Diagonal : AStarFindHeuristic {

        public override float GetScore(float sx, float sy, float tx, float ty)
        {
			float dx = MathUtils.Abs(tx - sx);
            float dy = MathUtils.Abs(ty - sy);
            float dz = MathUtils.Max(dx, dy);
			return dz;
		}

        public override int GetCodeType()
        {
			return AStarFindHeuristic.DIAGONAL;
		}
	
	}
}

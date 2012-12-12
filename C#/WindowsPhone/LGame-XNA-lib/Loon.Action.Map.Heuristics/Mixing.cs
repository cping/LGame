namespace Loon.Action.Map.Heuristics {
	
	using Loon.Action.Map;
    using Loon.Utils;
	
	public class Mixing : AStarFindHeuristic {

        public override float GetScore(float sx, float sy, float tx, float ty)
        {
			float nx = MathUtils.Abs(tx - sx);
			float ny = MathUtils.Abs(ty - sy);
			float orthogonal = MathUtils.Abs(nx - ny);
			float diagonal = MathUtils.Abs(((nx + ny) - orthogonal) / 2);
			return diagonal + orthogonal + nx + ny;
		}

        public override int GetCodeType()
        {
			return AStarFindHeuristic.MIXING;
		}
	
	}
}

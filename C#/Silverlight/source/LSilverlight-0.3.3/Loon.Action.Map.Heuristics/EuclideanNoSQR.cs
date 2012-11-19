namespace Loon.Action.Map.Heuristics {
	
	using Loon.Action.Map;
    using Loon.Utils;
	
	public class EuclideanNoSQR : AStarFindHeuristic {

        public override float GetScore(float sx, float sy, float tx, float ty)
        {
			return MathUtils.Pow((sx - tx), 2) + MathUtils.Pow((sy - ty), 2);
		}

        public override int GetCodeType()
        {
			return AStarFindHeuristic.EUCLIDEAN_NOSQR;
		}
	
	}
}

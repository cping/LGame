namespace loon.action.map
{
   public abstract class AStarFindHeuristic
{
		public const int MANHATTAN = 0;
		public const int MIXING = 1;
		public const int DIAGONAL = 2;
		public const int DIAGONAL_SHORT = 3;
		public const int EUCLIDEAN = 4;
		public const int EUCLIDEAN_NOSQR = 5;
		public const int CLOSEST = 6;
		public const int CLOSEST_SQUARED = 7;
		public const int BESTFIRST = 8;
		public const int OCTILE = 9;
		public const int DIAGONAL_MIN = 10;

		public abstract float GetScore(float sx, float sy, float tx, float ty);

		public abstract int Type { get; }
	}
}

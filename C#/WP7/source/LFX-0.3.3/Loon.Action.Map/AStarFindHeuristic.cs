namespace Loon.Action.Map {
	
	using System;
	using System.Collections;
	using System.ComponentModel;
	using System.IO;
	using System.Runtime.CompilerServices;
	
	public abstract class AStarFindHeuristic {

        public const int MANHATTAN = 0;
        public const int MIXING = 1;
        public const int DIAGONAL = 2;
        public const int DIAGONAL_SHORT = 3;
        public const int EUCLIDEAN = 4;
        public const int EUCLIDEAN_NOSQR = 5;
        public const int CLOSEST = 6;
        public const int CLOSEST_SQUARED = 7;

        public abstract float GetScore(float sx, float sy, float tx, float ty);

        public abstract int GetCodeType();
	
	}
}

namespace Loon.Action.Map {
	
    using Loon.Core.Geom;
    using System.Collections.Generic;
	
	public interface AStarFinderListener {
	
		void PathFound(List<Vector2f> path);
	
	}
}

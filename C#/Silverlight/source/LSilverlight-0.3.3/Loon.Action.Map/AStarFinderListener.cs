namespace Loon.Action.Map {
	
	using System;
	using System.Collections;
	using System.ComponentModel;
	using System.IO;
	using System.Runtime.CompilerServices;
    using System.Collections.Generic;
    using Loon.Core.Geom;
	
	public interface AStarFinderListener {
	
		void PathFound(List<Vector2f> path);
	
	}
}

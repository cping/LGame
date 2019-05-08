package loon.action.map;

import loon.geom.Vector2f;
import loon.utils.TArray;

public interface AStarFinderListener {

	void pathFound(TArray<Vector2f> path);

}

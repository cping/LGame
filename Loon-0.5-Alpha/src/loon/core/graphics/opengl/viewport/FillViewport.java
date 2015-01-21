package loon.core.graphics.opengl.viewport;

import loon.core.graphics.Camera;
import loon.utils.collection.Scaling;

public class FillViewport extends ScalingViewport {

	public FillViewport(float worldWidth, float worldHeight) {
		super(Scaling.fill, worldWidth, worldHeight);
	}

	public FillViewport(float worldWidth, float worldHeight, Camera camera) {
		super(Scaling.fill, worldWidth, worldHeight, camera);
	}
}

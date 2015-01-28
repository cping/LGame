package loon.core.graphics.opengl;

import loon.core.graphics.Camera;
import loon.utils.collection.Scaling;

public class FitViewport extends ScalingViewport {

	public FitViewport(float worldWidth, float worldHeight) {
		super(Scaling.fit, worldWidth, worldHeight);
	}

	public FitViewport(float worldWidth, float worldHeight, Camera camera) {
		super(Scaling.fit, worldWidth, worldHeight, camera);
	}
}

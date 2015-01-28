package loon.core.graphics.opengl;

import loon.core.graphics.Camera;
import loon.utils.collection.Scaling;

public class StretchViewport extends ScalingViewport {

	public StretchViewport(float worldWidth, float worldHeight) {
		super(Scaling.stretch, worldWidth, worldHeight);
	}

	public StretchViewport(float worldWidth, float worldHeight, Camera camera) {
		super(Scaling.stretch, worldWidth, worldHeight, camera);
	}
}

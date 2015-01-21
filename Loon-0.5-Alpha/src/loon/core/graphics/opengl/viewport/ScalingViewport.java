package loon.core.graphics.opengl.viewport;

import loon.core.graphics.Camera;
import loon.core.graphics.CenterCamera;
import loon.core.graphics.opengl.math.Location2;
import loon.utils.collection.Scaling;


public class ScalingViewport extends Viewport {
	private Scaling scaling;

	public ScalingViewport(Scaling scaling, float worldWidth, float worldHeight) {
		this(scaling, worldWidth, worldHeight, new CenterCamera());
	}

	public ScalingViewport(Scaling scaling, float worldWidth,
			float worldHeight, Camera camera) {
		this.scaling = scaling;
		setWorldSize(worldWidth, worldHeight);
		setCamera(camera);
	}

	@Override
	public void update(int screenWidth, int screenHeight, boolean centerCamera) {
		Location2 scaled = scaling.apply(getWorldWidth(), getWorldHeight(),
				screenWidth, screenHeight);
		int viewportWidth = Math.round(scaled.x);
		int viewportHeight = Math.round(scaled.y);
		setScreenBounds((screenWidth - viewportWidth) / 2,
				(screenHeight - viewportHeight) / 2, viewportWidth,
				viewportHeight);

		apply(centerCamera);
	}

	public Scaling getScaling() {
		return scaling;
	}

	public void setScaling(Scaling scaling) {
		this.scaling = scaling;
	}
}

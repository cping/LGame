package loon.core.graphics.opengl;

import loon.core.geom.Vector2f;
import loon.core.graphics.Camera;
import loon.core.graphics.CenterCamera;
import loon.utils.collection.Scaling;

public class ExtendViewport extends Viewport {

	private float minWorldWidth, minWorldHeight;
	private float maxWorldWidth, maxWorldHeight;

	public ExtendViewport(float minWorldWidth, float minWorldHeight) {
		this(minWorldWidth, minWorldHeight, 0, 0, new CenterCamera());
	}

	public ExtendViewport(float minWorldWidth, float minWorldHeight,
			Camera camera) {
		this(minWorldWidth, minWorldHeight, 0, 0, camera);
	}

	public ExtendViewport(float minWorldWidth, float minWorldHeight,
			float maxWorldWidth, float maxWorldHeight) {
		this(minWorldWidth, minWorldHeight, maxWorldWidth, maxWorldHeight,
				new CenterCamera());
	}

	public ExtendViewport(float minWorldWidth, float minWorldHeight,
			float maxWorldWidth, float maxWorldHeight, Camera camera) {
		this.minWorldWidth = minWorldWidth;
		this.minWorldHeight = minWorldHeight;
		this.maxWorldWidth = maxWorldWidth;
		this.maxWorldHeight = maxWorldHeight;
		setCamera(camera);
	}

	@Override
	public void update(int screenWidth, int screenHeight, boolean centerCamera) {
		float worldWidth = minWorldWidth;
		float worldHeight = minWorldHeight;
		Vector2f scaled = Scaling.fit.apply(worldWidth, worldHeight,
				screenWidth, screenHeight);

		int viewportWidth = Math.round(scaled.x);
		int viewportHeight = Math.round(scaled.y);
		if (viewportWidth < screenWidth) {
			float toViewportSpace = viewportHeight / worldHeight;
			float toWorldSpace = worldHeight / viewportHeight;
			float lengthen = (screenWidth - viewportWidth) * toWorldSpace;
			if (maxWorldWidth > 0)
				lengthen = Math.min(lengthen, maxWorldWidth - minWorldWidth);
			worldWidth += lengthen;
			viewportWidth += Math.round(lengthen * toViewportSpace);
		} else if (viewportHeight < screenHeight) {
			float toViewportSpace = viewportWidth / worldWidth;
			float toWorldSpace = worldWidth / viewportWidth;
			float lengthen = (screenHeight - viewportHeight) * toWorldSpace;
			if (maxWorldHeight > 0)
				lengthen = Math.min(lengthen, maxWorldHeight - minWorldHeight);
			worldHeight += lengthen;
			viewportHeight += Math.round(lengthen * toViewportSpace);
		}

		setWorldSize(worldWidth, worldHeight);

		setScreenBounds((screenWidth - viewportWidth) / 2,
				(screenHeight - viewportHeight) / 2, viewportWidth,
				viewportHeight);

		apply(centerCamera);
	}

	public float getMinWorldWidth() {
		return minWorldWidth;
	}

	public void setMinWorldWidth(float minWorldWidth) {
		this.minWorldWidth = minWorldWidth;
	}

	public float getMinWorldHeight() {
		return minWorldHeight;
	}

	public void setMinWorldHeight(float minWorldHeight) {
		this.minWorldHeight = minWorldHeight;
	}

	public float getMaxWorldWidth() {
		return maxWorldWidth;
	}

	public void setMaxWorldWidth(float maxWorldWidth) {
		this.maxWorldWidth = maxWorldWidth;
	}

	public float getMaxWorldHeight() {
		return maxWorldHeight;
	}

	public void setMaxWorldHeight(float maxWorldHeight) {
		this.maxWorldHeight = maxWorldHeight;
	}
}

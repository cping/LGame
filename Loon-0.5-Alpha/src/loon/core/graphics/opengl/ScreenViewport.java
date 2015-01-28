package loon.core.graphics.opengl;

import loon.core.graphics.Camera;
import loon.core.graphics.CenterCamera;


public class ScreenViewport extends Viewport {

	private float unitsPerPixel = 1;

	public ScreenViewport() {
		this(new CenterCamera());
	}

	public ScreenViewport(Camera camera) {
		setCamera(camera);
	}

	@Override
	public void update(int screenWidth, int screenHeight, boolean centerCamera) {
		setScreenBounds(0, 0, screenWidth, screenHeight);
		setWorldSize(screenWidth * unitsPerPixel, screenHeight * unitsPerPixel);
		apply(centerCamera);
	}

	public float getUnitsPerPixel() {
		return unitsPerPixel;
	}

	public void setUnitsPerPixel(float unitsPerPixel) {
		this.unitsPerPixel = unitsPerPixel;
	}
}

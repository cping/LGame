package loon.core.graphics.opengl.viewport;

import loon.core.Ray;
import loon.core.geom.RectBox;
import loon.core.graphics.Camera;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.ScissorStack;
import loon.core.graphics.opengl.math.Transform4;
import loon.core.graphics.opengl.math.Location2;
import loon.core.graphics.opengl.math.Location3;

public abstract class Viewport {
	
	private Camera camera;
	private float worldWidth, worldHeight;
	private int screenX, screenY, screenWidth, screenHeight;

	private final Location3 tmp = new Location3();

	public void apply() {
		apply(false);
	}

	public void apply(boolean centerCamera) {
		GLEx.gl.glViewport(screenX, screenY, screenWidth, screenHeight);
		camera.viewportWidth = worldWidth;
		camera.viewportHeight = worldHeight;
		if (centerCamera){
			camera.position.set(worldWidth / 2, worldHeight / 2, 0);
		}
		camera.update();
	}

	public final void update(int screenWidth, int screenHeight) {
		update(screenWidth, screenHeight, false);
	}

	public void update(int screenWidth, int screenHeight, boolean centerCamera) {
		apply(centerCamera);
	}

	public Location2 unproject(Location2 screenCoords) {
		tmp.set(screenCoords.x, screenCoords.y, 1);
		camera.unproject(tmp, screenX, screenY, screenWidth, screenHeight);
		screenCoords.set(tmp.x, tmp.y);
		return screenCoords;
	}

	public Location2 project(Location2 worldCoords) {
		tmp.set(worldCoords.x, worldCoords.y, 1);
		camera.project(tmp, screenX, screenY, screenWidth, screenHeight);
		worldCoords.set(tmp.x, tmp.y);
		return worldCoords;
	}

	public Location3 unproject(Location3 screenCoords) {
		camera.unproject(screenCoords, screenX, screenY, screenWidth,
				screenHeight);
		return screenCoords;
	}

	public Location3 project(Location3 worldCoords) {
		camera.project(worldCoords, screenX, screenY, screenWidth, screenHeight);
		return worldCoords;
	}

	public Ray getPickRay(float screenX, float screenY) {
		return camera.getPickRay(screenX, screenY, this.screenX, this.screenY,
				screenWidth, screenHeight);
	}

	public void calculateScissors(Transform4 batchTransform, RectBox area,
			RectBox scissor) {
		ScissorStack.calculateScissors(camera, screenX, screenY, screenWidth,
				screenHeight, batchTransform, area, scissor);
	}

	public Location2 toScreenCoordinates(Location2 worldCoords,
			Transform4 transformMatrix) {
		tmp.set(worldCoords.x, worldCoords.y, 0);
		tmp.mul(transformMatrix);
		camera.project(tmp);
		tmp.y = GLEx.height() - tmp.y;
		worldCoords.x = tmp.x;
		worldCoords.y = tmp.y;
		return worldCoords;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public float getWorldWidth() {
		return worldWidth;
	}

	public void setWorldWidth(float worldWidth) {
		this.worldWidth = worldWidth;
	}

	public float getWorldHeight() {
		return worldHeight;
	}

	public void setWorldHeight(float worldHeight) {
		this.worldHeight = worldHeight;
	}

	public void setWorldSize(float worldWidth, float worldHeight) {
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}

	public int getScreenX() {
		return screenX;
	}

	public void setScreenX(int screenX) {
		this.screenX = screenX;
	}

	public int getScreenY() {
		return screenY;
	}

	public void setScreenY(int screenY) {
		this.screenY = screenY;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public void setScreenPosition(int screenX, int screenY) {
		this.screenX = screenX;
		this.screenY = screenY;
	}

	public void setScreenSize(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}
	
	public void setScreenBounds(int screenX, int screenY, int screenWidth,
			int screenHeight) {
		this.screenX = screenX;
		this.screenY = screenY;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	public int getLeftGutterWidth() {
		return screenX;
	}

	public int getRightGutterX() {
		return screenX + screenWidth;
	}

	public int getRightGutterWidth() {
		return GLEx.width() - (screenX + screenWidth);
	}

	public int getBottomGutterHeight() {
		return screenY;
	}

	public int getTopGutterY() {
		return screenY + screenHeight;
	}

	public int getTopGutterHeight() {
		return GLEx.height() - (screenY + screenHeight);
	}
}

/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.action.camera;

import loon.geom.Vector2f;
import loon.opengl.GLEx;

public abstract class Viewport {

	private int x, y, width, height;
	private float scaleX, invScaleX;
	private float scaleY, invScaleY;

	private int previousWindowWidth, previousWindowHeight;
	private float previousScaleX, previousScaleY, previousTranslateX, previousTranslateY;

	protected abstract void onResize(int windowWidth, int windowHeight);

	public Viewport apply(GLEx g) {
		if (previousWindowWidth != g.getWidth() || previousWindowHeight != g.getHeight()) {
			onResize(g.getWidth(), g.getHeight());
			previousWindowWidth = g.getWidth();
			previousWindowHeight = g.getHeight();
		}

		previousScaleX = g.getScaleX();
		previousScaleY = g.getScaleY();
		previousTranslateX = g.getTranslationX();
		previousTranslateY = g.getTranslationY();
		g.scale(scaleX, scaleY);
		g.translate(-x, -y);
		g.setClip(0, 0, width, height);
		return this;
	}

	public Viewport unapply(GLEx g) {
		g.clearClip();
		g.translate(previousTranslateX, previousTranslateY);
		g.scale(previousScaleX, previousScaleY);
		return this;
	}

	public Viewport toScreenCoordinates(Vector2f result, float worldX, float worldY) {
		result.x = (worldX * scaleX) + x;
		result.y = (worldY * scaleY) + y;
		return this;
	}

	public Viewport toWorldCoordinates(Vector2f result, float screenX, float screenY) {
		result.x = (screenX - x) * invScaleX;
		result.y = (screenY - y) * invScaleY;
		return this;
	}

	public Viewport toScreenCoordinates(Vector2f worldCoordinates) {
		return toScreenCoordinates(worldCoordinates, worldCoordinates.x, worldCoordinates.y);
	}

	public Viewport toWorldCoordinates(Vector2f screenCoordinates) {
		return toWorldCoordinates(screenCoordinates, screenCoordinates.x, screenCoordinates.y);
	}

	protected Viewport setBounds(int x, int y, int width, int height, float scaleX, float scaleY) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.scaleX = scaleX;
		this.invScaleX = 1f / scaleX;
		this.scaleY = scaleY;
		this.invScaleY = 1f / scaleY;
		return this;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getInvScaleX() {
		return invScaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public float getInvScaleY() {
		return invScaleY;
	}
}

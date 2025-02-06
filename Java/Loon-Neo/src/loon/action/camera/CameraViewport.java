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

import loon.LSystem;
import loon.events.ResizeListener;
import loon.geom.Dimension;
import loon.geom.Vector2f;
import loon.utils.Easing.EasingMode;

public class CameraViewport extends Viewport {

	private ResizeListener<CameraViewport> _resized;

	public CameraViewport() {
		this(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public CameraViewport(float worldWidth, float worldHeight) {
		final Dimension dim = LSystem.viewSize;
		float scaleX = worldWidth <= dim.getWidth() ? worldWidth / LSystem.viewSize.getWidth() : 1f;
		float scaleY = worldHeight <= dim.getHeight() ? worldHeight / LSystem.viewSize.getHeight() : 1f;
		setBounds(0f, 0f, worldWidth, worldHeight, scaleX, scaleY);
	}

	public CameraViewport(float x, float y, float worldWidth, float worldHeight, float sx, float sy) {
		setBounds(x, y, worldWidth, worldHeight, sx, sy);
	}

	@Override
	public void onResize(float width, float height) {
		float scaleX = this.getWidth() <= width ? (this.getScaleX() * (this.getWidth() / width)) : getScaleX();
		float scaleY = this.getHeight() <= height ? (this.getScaleY() * (this.getHeight() / height)) : getScaleY();
		setBounds(this.getWidth(), this.getHeight(), scaleX, scaleY);
		if (_resized != null) {
			_resized.onResize(this);
		}
	}

	/**
	 * 移动当前摄像机中心位置去指定中心点
	 * 
	 * @param ease
	 * @param dstX
	 * @param dsty
	 * @param delay
	 * @return
	 */
	public MoveEffect startMove(EasingMode ease, float dstX, float dsty, float delay) {
		MoveEffect moved = new MoveEffect(ease, dstX, dsty, delay, this);
		setEffect(moved);
		moved.start();
		return moved;
	}

	/**
	 * 移动当前摄像机中心位置去指定中心点
	 * 
	 * @param ease
	 * @param dstX
	 * @param dsty
	 * @return
	 */
	public MoveEffect startMove(EasingMode ease, float dstX, float dsty) {
		return startMove(ease, dstX, dsty, LSystem.DEFAULT_EASE_DELAY);
	}

	/**
	 * 移动当前摄像机中心位置去指定中心点
	 * 
	 * @param dstX
	 * @param dsty
	 * @return
	 */
	public MoveEffect startMove(float dstX, float dsty) {
		return startMove(EasingMode.Linear, dstX, dsty);
	}

	/**
	 * 缩放当前镜头为指定大小
	 * 
	 * @param ease
	 * @param zoom
	 * @param delay
	 * @return
	 */
	public ZoomEffect startZoom(EasingMode ease, Vector2f zoom, float delay) {
		ZoomEffect scaled = new ZoomEffect(ease, zoom, delay, this);
		setEffect(scaled);
		scaled.start();
		return scaled;
	}

	/**
	 * 缩放当前镜头为指定大小
	 * 
	 * @param ease
	 * @param zoom
	 * @return
	 */
	public ZoomEffect startZoom(EasingMode ease, Vector2f zoom) {
		return startZoom(ease, zoom, LSystem.DEFAULT_EASE_DELAY);
	}

	/**
	 * 缩放当前镜头为指定大小
	 * 
	 * @param zoom
	 * @return
	 */
	public ZoomEffect startZoom(Vector2f zoom) {
		return startZoom(EasingMode.Linear, zoom);
	}

	public ShakeEffect startShake(EasingMode ease, float offset) {
		ShakeEffect shake = new ShakeEffect(ease, offset, this);
		setEffect(shake);
		shake.start();
		return shake;
	}

	public ShakeEffect startShake(float offset) {
		return startShake(EasingMode.Linear, offset);
	}

	public CameraViewport setResize(ResizeListener<CameraViewport> l) {
		this._resized = l;
		return this;
	}
}

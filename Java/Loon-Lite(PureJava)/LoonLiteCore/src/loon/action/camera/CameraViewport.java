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

public class CameraViewport extends Viewport {

	private ResizeListener<CameraViewport> _resized;

	public CameraViewport() {
		this(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public CameraViewport(float worldWidth, float worldHeight) {
		setBounds(0f, 0f, worldWidth, worldHeight, worldWidth / LSystem.viewSize.getWidth(),
				worldHeight / LSystem.viewSize.getHeight());
	}

	public CameraViewport(float x, float y, float worldWidth, float worldHeight, float sx, float sy) {
		setBounds(x, y, worldWidth, worldHeight, sx, sy);
	}

	@Override
	public void onResize(int width, int height) {
		if (_resized != null) {
			_resized.onResize(this);
		}
	}

	public CameraViewport setResize(ResizeListener<CameraViewport> l) {
		this._resized = l;
		return this;
	}
}

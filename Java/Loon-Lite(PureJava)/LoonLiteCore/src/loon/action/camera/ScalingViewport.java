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
import loon.utils.MathUtils;
import loon.utils.Scale;
import loon.utils.Scale.Mode;

public class ScalingViewport extends Viewport {

	private final Scale scaling;
	private final float worldWidth, worldHeight;
	private final Vector2f size = new Vector2f();
	private final Vector2f scale = new Vector2f();

	private boolean powerOfTwo;

	private Mode mode;

	public ScalingViewport(Mode mode, boolean powerOfTwo, float worldWidth, float worldHeight) {
		this(mode, Scale.ONE, powerOfTwo, worldWidth, worldHeight);
	}

	public ScalingViewport(Mode mode, Scale scaling, boolean powerOfTwo, float worldWidth, float worldHeight) {
		this.mode = mode;
		this.scaling = scaling;
		this.powerOfTwo = powerOfTwo;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}

	@Override
	public void onResize(int width, int height) {
		scaling.scaledSize(mode, size, scale, powerOfTwo, worldWidth, worldHeight, width, height);

		float scaleX = 1f;
		float scaleY = 1f;
		if (mode != Scale.Mode.FIT) {
			scaleX = scale.x;
			scaleY = scale.y;
		}
		final int viewWidth = MathUtils.round(size.x);
		final int viewHeight = MathUtils.round(size.y);

		setBounds((width - viewWidth) / 2f, (height - viewHeight) / 2f, MathUtils.round(worldWidth),
				MathUtils.round(worldHeight), (worldWidth / width) * scaleX, (worldHeight / height) * scaleY);
	}

	public boolean isPowerOfTwo() {
		return powerOfTwo;
	}

	public void setPowerOfTwo(boolean powerOfTwo) {
		this.powerOfTwo = powerOfTwo;
	}
}
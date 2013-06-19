package loon.action;

import loon.utils.MathUtils;

/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
public class CircleTo extends ActionEvent {

	private int x;

	private int y;

	private int cx;

	private int cy;

	private int radius;

	private int velocity;

	private float dt;

	public CircleTo(int radius, int velocity) {
		this.radius = radius;
		this.velocity = velocity;
	}

	@Override
	public boolean isComplete() {
		return isComplete;
	}

	@Override
	public void onLoad() {
		this.cx = (int) original.getX();
		this.cy = (int) original.getY();
		this.x = (cx + radius);
		this.y = cy;
	}

	@Override
	public void update(long elapsedTime) {
		dt += MathUtils.max((elapsedTime / 1000), 0.05f);
		this.x = (int) (this.cx + this.radius
				* MathUtils.cos(MathUtils.toRadians(this.velocity * dt)));
		this.y = (int) (this.cy + this.radius
				* MathUtils.sin(MathUtils.toRadians(this.velocity * dt)));
		synchronized (original) {
			original.setLocation(x + offsetX, y + offsetY);
		}
	}

}

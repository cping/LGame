package org.loon.framework.android.game.action.sprite.j2me;

import org.loon.framework.android.game.core.graphics.device.LGraphics;


/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public abstract class Layer {
	
	private int width;

	private int height;

	private int x;

	private int y;

	private boolean visible;

	Layer(int x, int y, int width, int height, boolean visible) {
		setSize(width, height);
		setPosition(x, y);
		setVisible(visible);
	}

	void setSize(int width, int height) {
		if (width < 1 || height < 1)
			throw new IllegalArgumentException();

		this.width = width;
		this.height = height;
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	public final boolean isVisible() {
		return visible;
	}

	public void move(int dx, int dy) {
		synchronized (this) {
			x += dx;
			y += dy;
		}
	}

	public abstract void paint(LGraphics g);

	public void setPosition(int x, int y) {
		synchronized (this) {
			this.x = x;
			this.y = y;
		}
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}


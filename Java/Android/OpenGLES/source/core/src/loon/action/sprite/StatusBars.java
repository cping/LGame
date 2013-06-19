/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite;

import java.util.ArrayList;

import loon.core.LObject;
import loon.core.geom.RectBox;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.utils.CollectionUtils;


public final class StatusBars extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<StatusBar> barCaches;

	private boolean visible;

	public StatusBars() {
		this.barCaches = new ArrayList<StatusBar>(
				CollectionUtils.INITIAL_CAPACITY);
		this.visible = true;
	}

	public StatusBar addBar(int value, int maxValue, int x, int y, int w, int h) {
		synchronized (barCaches) {
			StatusBar bar = new StatusBar(value, maxValue, x, y, w, h);
			barCaches.add(bar);
			return bar;
		}
	}

	public StatusBar addBar(int x, int y, int width, int height) {
		return addBar(100, 100, x, y, width, height);
	}

	public StatusBar addBar(int width, int height) {
		return addBar(100, 100, 0, 0, width, height);
	}

	public void addBar(StatusBar bar) {
		synchronized (barCaches) {
			barCaches.add(bar);
		}
	}

	public boolean removeBar(StatusBar bar) {
		if (bar == null) {
			return false;
		}
		synchronized (barCaches) {
			return barCaches.remove(bar);
		}
	}
	
	public int size(){
		return barCaches.size();
	}

	public void clear() {
		synchronized (barCaches) {
			barCaches.clear();
		}
	}

	public void hide(StatusBar bar) {
		if (bar != null) {
			bar.setVisible(false);
		}
	}

	public void show(StatusBar bar) {
		if (bar != null) {
			bar.setVisible(true);
		}
	}

	@Override
	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		int size = barCaches.size();
		if (size > 0) {
			synchronized (barCaches) {
				StatusBar.glBegin();
				for (int i = 0; i < size; i++) {
					StatusBar bar = barCaches.get(i);
					if (bar != null && bar.visible) {
						bar.createUI(g);
					}
				}
				StatusBar.glEnd();
			}
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (!visible) {
			return;
		}
		int size = barCaches.size();
		if (size > 0) {
			synchronized (barCaches) {
				for (int i = 0; i < size; i++) {
					StatusBar bar = barCaches.get(i);
					if (bar != null && bar.visible) {
						bar.update(elapsedTime);
					}
				}
			}
		}
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public RectBox getCollisionBox() {
		return null;
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void dispose() {
		this.visible = false;
		int size = barCaches.size();
		for (int i = 0; i < size; i++) {
			StatusBar bar = barCaches.get(i);
			if (bar != null) {
				bar.dispose();
				bar = null;
			}
		}
		barCaches.clear();
		barCaches = null;
	}
}

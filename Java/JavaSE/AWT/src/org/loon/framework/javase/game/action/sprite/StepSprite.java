package org.loon.framework.javase.game.action.sprite;

import java.util.ArrayList;

import org.loon.framework.javase.game.action.map.Field2D;
import org.loon.framework.javase.game.core.LObject;
import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.geom.RectBox;
import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.Screen.LTouch;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
/**
 * 让精灵延指定路线自动行走
 */
public class StepSprite extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<int[]> steps;

	private TouchSprite touch;

	private boolean isRunning;

	public StepSprite() {
		this(true);
	}

	public StepSprite(boolean all) {
		this((LImage) null, TouchSprite.BLOCK_SIZE, TouchSprite.BLOCK_SIZE, all);
	}

	public StepSprite(String path) {
		this(path, true);
	}

	public StepSprite(String path, boolean all) {
		this(LImage.createImage(path), 0, 0, all);
	}

	public StepSprite(int tileWidth, int tileHeight) {
		this(tileWidth, tileHeight, true);
	}

	public StepSprite(int tileWidth, int tileHeight, boolean all) {
		this((LImage) null, tileWidth, tileHeight, all);
	}

	public StepSprite(String path, int tileWidth, int tileHeight, boolean all) {
		this(LImage.createImage(path), tileWidth, tileHeight, all);
	}

	public StepSprite(LImage tex2d, int tileWidth, int tileHeight, boolean all) {
		this(null, tex2d, tileWidth, tileHeight, LSystem.screenRect.width,
				LSystem.screenRect.height, all);
	}

	public StepSprite(Field2D field, LImage tex2d, int tileWidth,
			int tileHeight, int maxWidth, int maxHeight, boolean all) {
		this.touch = new TouchSprite(field, tex2d, tileWidth, tileHeight,
				maxWidth, maxHeight, all);
		this.steps = new ArrayList<int[]>(10);
		this.setDelay(20);
	}

	public TouchSprite getTouchSprite() {
		return touch;
	}

	public long getDelay() {
		return touch.getDelay();
	}

	public void setDelay(long d) {
		touch.setDelay(d);
	}

	public void go() {
		this.isRunning = true;
	}

	public void stop() {
		this.isRunning = false;
	}

	public boolean isComplete() {
		return this.isRunning;
	}

	public void addEnd(int x, int y) {
		synchronized (steps) {
			steps.add(steps.size(), new int[] { x, y });
		}
	}

	public void add(int x, int y) {
		synchronized (steps) {
			steps.add(new int[] { x, y });
		}
	}

	public void addBegin(int x, int y) {
		synchronized (steps) {
			steps.add(0, new int[] { x, y });
		}
	}

	public int size() {
		synchronized (steps) {
			return steps.size();
		}
	}

	public int getX(int i) {
		synchronized (steps) {
			if (steps.size() == 0) {
				return 0;
			}
			return ((int[]) steps.get(i))[0];
		}
	}

	public int getY(int i) {
		synchronized (steps) {
			if (steps.size() == 0) {
				return 0;
			}
			return ((int[]) steps.get(i))[1];
		}
	}

	public int[] remove(int i) {
		synchronized (steps) {
			return steps.remove(i);
		}
	}

	public void removeAll() {
		synchronized (steps) {
			steps.clear();
		}
	}

	public void onPosition(LTouch t) {
		touch.onPosition(t);
	}

	public void onPosition(float x, float y) {
		touch.onPosition(x, y);
	}

	public int getHeight() {
		return touch.getHeight();
	}

	public int getWidth() {
		return touch.getWidth();
	}

	public int hashCode() {
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, touch.hashCode());
		hashCode = LSystem.unite(hashCode, steps.size());
		return hashCode;
	}

	public void setTexture(String path) {
		touch.setTexture(LImage.createImage(path));
	}

	public void setTexture(LImage tex2d) {
		touch.setTexture(tex2d);
	}

	public Field2D getField2D() {
		return touch.getField2D();
	}

	private void update() {
		if (isRunning) {
			if (steps.size() > 0) {
				int[] pos = steps.get(0);
				if (pos[0] < 0 || pos[1] < 0 || pos[0] > touch.getReaderWidth()
						|| pos[1] > touch.getReaderHeight()) {
					touch.onPosition(pos[0], pos[1]);
				} else {
					touch.onTouch(pos[0], pos[1]);
				}
				steps.remove(0);
			}
		}
	}

	public ISprite getBindSprite() {
		return touch.getBindSprite();
	}

	public void bind(ISprite sprite) {
		touch.bind(sprite);
	}

	public void update(long elapsedTime) {
		if (!touch.isVisible()) {
			return;
		}
		if (touch.isComplete()) {
			update();
		}
		touch.update(elapsedTime);
	}

	public void createUI(LGraphics g) {
		touch.createUI(g);
	}

	public float getAlpha() {
		return touch.getAlpha();
	}

	public LImage getBitmap() {
		return touch.getBitmap();
	}

	public RectBox getCollisionBox() {
		return touch.getCollisionBox();
	}

	public boolean isVisible() {
		return touch.isVisible();
	}

	public void setVisible(boolean v) {
		touch.setVisible(v);
	}

	public void dispose() {
		this.isRunning = false;
		if (touch != null) {
			touch.dispose();
			touch = null;
		}
		if (steps != null) {
			steps.clear();
		}
	}
}

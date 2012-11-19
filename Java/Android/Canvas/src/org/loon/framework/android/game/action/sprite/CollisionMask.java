package org.loon.framework.android.game.action.sprite;

import org.loon.framework.android.game.core.geom.RectBox;


/**
 * Copyright 2008 - 2010
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
public class CollisionMask {

	private int top, left, right, bottom;

	private Mask data;

	private RectBox rect;

	public CollisionMask(Mask data) {
		set(data, 0, 0, data.getWidth(), data.getHeight());
	}

	public void set(Mask data, int x, int y, int w, int h) {
		this.data = data;
		if (rect == null) {
			this.rect = new RectBox(x, y, w, h);
		} else {
			this.rect.setBounds(x, y, w, h);
		}
	}

	public RectBox getBounds() {
		return rect;
	}

	private void calculateBoundingBox() {
		top = rect.y - rect.height / 2;
		left = rect.x - rect.width / 2;
		right = left + rect.width;
		bottom = top + rect.height;
	}

	public boolean checkBoundingBoxCollision(CollisionMask other) {
		return rect.intersects(other.getBounds())
				|| rect.contains(other.getBounds());
	}

	public boolean checkBoundingBoxCollision(int x, int y) {
		return rect.intersects(x, y) || rect.contains(x, y);
	}

	public boolean collidesWith(CollisionMask other) {
		if (checkBoundingBoxCollision(other)) {
			other.calculateBoundingBox();
			calculateBoundingBox();
			boolean a = false;
			boolean b = false;
			for (int y = top; y < bottom; y++) {
				for (int x = left; x < right; x++) {
					a = data.getPixel(x - left, y - top);
					b = other.data.getPixel(x - other.left, y - other.top);
					if (a && b) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean collidesWith(int x, int y) {
		if (checkBoundingBoxCollision(x, y)) {
			calculateBoundingBox();
			return data.getPixel(x - left, y - top);
		}
		return false;
	}

}

/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.map.Config;
import loon.action.sprite.Entity;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public class OutEffect extends Entity implements BaseEffect {

	private boolean completed;

	private int type, multiples;

	private RectBox limit;

	public OutEffect(String fileName, int code) {
		this(LTextures.loadTexture(fileName), code);
	}

	public OutEffect(LTexture t, int code) {
		this(t, LSystem.viewSize.getRect(), code);
	}

	public OutEffect(LTexture t, RectBox limit, int code) {
		this.setTexture(t);
		this.setSize(t.width(), t.height());
		this.setRepaint(true);
		this.type = code;
		this.multiples = 1;
		this.limit = limit;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (!completed) {
			switch (type) {
			case Config.DOWN:
				move_45D_down(multiples);
				break;
			case Config.UP:
				move_45D_up(multiples);
				break;
			case Config.LEFT:
				move_45D_left(multiples);
				break;
			case Config.RIGHT:
				move_45D_right(multiples);
				break;
			case Config.TDOWN:
				move_down(multiples);
				break;
			case Config.TUP:
				move_up(multiples);
				break;
			case Config.TLEFT:
				move_left(multiples);
				break;
			case Config.TRIGHT:
				move_right(multiples);
				break;
			}
			if (!limit.intersects(x(), y(), _width, _height)) {
				completed = true;
			}
		}
	}

	public boolean isComplete() {
		return completed;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (!completed) {
			g.draw(_image, drawX(offsetX), drawY(offsetY));
		}
	}

	public int getMultiples() {
		return multiples;
	}

	public void setMultiples(int multiples) {
		this.multiples = multiples;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void close() {
		super.close();
		completed = true;
	}

}

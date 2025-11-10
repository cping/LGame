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
import loon.action.map.Config;
import loon.geom.RectBox;
import loon.opengl.GLEx;

/**
 * 图片从指定方向离开画面的过渡效果
 */
public class OutEffect extends BaseAbstractEffect {

	private int _model, _multiples;

	private RectBox _limit;

	public OutEffect(String fileName, int code) {
		this(LSystem.loadTexture(fileName), code);
	}

	public OutEffect(LTexture t, int code) {
		this(t, LSystem.viewSize.getRect(), code);
	}

	public OutEffect(LTexture t, RectBox limit, int code) {
		this.setTexture(t);
		this.setSize(t.width(), t.height());
		this.setRepaint(true);
		this._model = code;
		this._multiples = 1;
		this._limit = limit;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (!_completed) {
			switch (_model) {
			case Config.DOWN:
				move_45D_down(_multiples);
				break;
			case Config.UP:
				move_45D_up(_multiples);
				break;
			case Config.LEFT:
				move_45D_left(_multiples);
				break;
			case Config.RIGHT:
				move_45D_right(_multiples);
				break;
			case Config.TDOWN:
				move_down(_multiples);
				break;
			case Config.TUP:
				move_up(_multiples);
				break;
			case Config.TLEFT:
				move_left(_multiples);
				break;
			case Config.TRIGHT:
				move_right(_multiples);
				break;
			}
			if (!_limit.intersects(x(), y(), _width, _height)) {
				_completed = true;
			}
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (!_completed) {
			g.draw(_image, drawX(offsetX), drawY(offsetY));
		}
	}

	public int getMultiples() {
		return _multiples;
	}

	public void setMultiples(int multiples) {
		this._multiples = multiples;
	}

	@Override
	public OutEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}
}

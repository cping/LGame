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
package loon.component.turtle;

import loon.LTexture;
import loon.LTextures;
import loon.action.map.Field2D;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class ImageCommand extends TurtleCommand {

	private float _startX, _startY;

	private float _endX, _endY;

	private float _lastX, _lastY;

	private LTexture _texture;

	private int _direction;

	private boolean _updateDir;

	public ImageCommand(String name, String path, float sx, float sy, float ex, float ey, float time) {
		super(name, time);
		this._startX = sx;
		this._startY = sy;
		this._endX = ex;
		this._endY = ey;
		this._lastX = this._startX;
		this._lastY = this._startY;
		this._texture = LTextures.loadTexture(path);
	}

	public float getLastX() {
		return this._lastX;
	}

	public float getLastY() {
		return this._lastY;
	}

	public boolean isUpdateDirection() {
		return this._updateDir;
	}

	@Override
	public void update(float dt, float progress) {

	}

	@Override
	public void render(GLEx g, float progress) {
		final int dirX = MathUtils.ifloor(_endX - _startX);
		final int dirY = MathUtils.ifloor(_endY - _startY);
		final int newX = MathUtils.ifloor(_startX + dirX * progress);
		final int newY = MathUtils.ifloor(_startY + dirY * progress);
		g.draw(_texture, newX, newY);
		final int oldDir = _direction;
		_direction = Field2D.getDirection(newX, newY, oldDir);
		_updateDir = (oldDir != _direction);
		_lastX = newX;
		_lastY = newY;
	}

}

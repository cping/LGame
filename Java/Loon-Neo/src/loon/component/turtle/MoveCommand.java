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

import loon.canvas.LColor;
import loon.opengl.GLEx;

public abstract class MoveCommand extends TurtleCommand {
	
	protected boolean _fixDrawline;

	protected float _currentX, _currentY;

	protected float _currentEndX, _currentEndY;

	protected float _currentAngle;

	protected float _currentLineWidth;

	protected LColor _currentColor;

	public MoveCommand(String name, float x, float y, float endX, float endY, float angle, LColor color, float width,
			float time) {
		super(name, time);
		this._currentX = x;
		this._currentY = y;
		this._currentEndX = endX;
		this._currentEndY = endY;
		this._currentAngle = angle;
		this._currentColor = color;
		this._currentLineWidth = width;
	}

	@Override
	public void update(float dt, float progress) {

	}

	@Override
	public void render(GLEx g, float progress) {
		float xStart = _currentX;
		float yStart = _currentY;
		float xSpeed = _currentEndX;
		float ySpeed = _currentEndY;
		drawLine(g, xStart, yStart, xSpeed, ySpeed, progress);
	}

	public abstract void drawLine(GLEx g, float sx, float sy, float ex, float ey, float progress);

}

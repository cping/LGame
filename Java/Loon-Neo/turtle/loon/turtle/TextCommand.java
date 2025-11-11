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
package loon.turtle;

import loon.canvas.LColor;
import loon.opengl.GLEx;

public class TextCommand extends TurtleCommand {

	private String _context;

	private float _currentX;

	private float _currentY;

	private float _currentAngle;

	private LColor _fontColor;

	public TextCommand(String name, String text, float x, float y, float angle, LColor fontColor, float time) {
		super(name, time);
		this._context = text;
		this._currentX = x;
		this._currentY = y;
		this._currentAngle = angle;
		this._fontColor = fontColor;
	}

	@Override
	public void update(float dt, float progress) {

	}

	@Override
	public void render(GLEx g, float progress) {
		g.drawString(_context, _currentX, _currentY, _currentAngle, _fontColor);
	}

}

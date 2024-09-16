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
package loon.component;

import loon.LSystem;
import loon.canvas.LColor;
import loon.component.turtle.GotoCommand;
import loon.component.turtle.ImageCommand;
import loon.component.turtle.TextCommand;
import loon.component.turtle.TurtleCommand;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.Duration;

/**
 * 模仿python的turtle实现
 */
public class LTurtle extends LComponent {

	private final TArray<TurtleCommand> _commands;

	private LColor _turtleColor;

	private LColor _turtleBgColor;

	private float _currentX;

	private float _currentY;

	private float _currentAngle;

	private float _currentSpeed;

	private float _currentLineWidth;

	public LTurtle(int x, int y, int width, int height) {
		super(x, y, width, height);
		this._commands = new TArray<TurtleCommand>();
		this._turtleColor = LColor.black;
		this._turtleBgColor = LColor.white;
		this._currentLineWidth = 2f;
		this._currentSpeed = 1f;
		this._currentAngle = 0f;
		this._currentX = getWidth() / 2f;
		this._currentY = getHeight() / 2f;
	}

	public LTurtle color(LColor c) {
		_turtleColor = c;
		return this;
	}

	public LTurtle color(String c) {
		_turtleColor = new LColor(c);
		return this;
	}

	public LTurtle bgcolor(LColor c) {
		_turtleBgColor = c;
		return this;
	}

	public LTurtle bgcolor(String c) {
		_turtleBgColor = new LColor(c);
		return this;
	}

	public LTurtle moveTo(float x, float y) {
		this._currentX = x;
		this._currentY = y;
		return this;
	}

	public LTurtle goTo(float x, float y) {
		float endX = _currentX + x;
		float endY = _currentY + y;
		pushCommand(new GotoCommand("goto", false, _currentX, _currentY, endX, endY, _currentAngle, _turtleColor,
				_currentLineWidth, _currentSpeed));
		this._currentX = endX;
		this._currentY = endY;
		return this;
	}

	public LTurtle posX(float amount) {
		float endX = _currentX + amount;
		float endY = _currentY + 0f;
		pushCommand(new GotoCommand("posx", true, _currentX, _currentY, endX, endY, _currentAngle, _turtleColor,
				_currentLineWidth, _currentSpeed));
		this._currentX = endX;
		this._currentY = endY;
		return this;
	}

	public LTurtle posY(float amount) {
		float endX = _currentX + 0f;
		float endY = _currentY + amount;
		pushCommand(new GotoCommand("posy", true, _currentX, _currentY, endX, endY, _currentAngle, _turtleColor,
				_currentLineWidth, _currentSpeed));
		this._currentX = endX;
		this._currentY = endY;
		return this;
	}

	public LTurtle image(String path, float x, float y) {
		float endX = _currentX + x;
		float endY = _currentY + y;
		pushCommand(new ImageCommand("image", path, _currentX, _currentY, endX, endY, _currentSpeed));
		this._currentX = endX;
		this._currentY = endY;
		return this;
	}

	public LTurtle text(String text) {
		return text(text, 0f, 0f);
	}

	public LTurtle text(String text, float x, float y) {
		float endX = _currentX + x;
		float endY = _currentY + y;
		pushCommand(new TextCommand("text", text, _currentX, _currentY, _currentAngle, _turtleColor, _currentSpeed));
		this._currentX = endX;
		this._currentY = endY;
		return this;
	}

	public LTurtle forward(float amount) {
		float endX = _currentX + (MathUtils.cos(MathUtils.toRadians(_currentAngle)) * amount);
		float endY = _currentY + (MathUtils.sin(MathUtils.toRadians(_currentAngle)) * amount);
		pushCommand(new GotoCommand("forward", true, _currentX, _currentY, endX, endY, _currentAngle, _turtleColor,
				_currentLineWidth, _currentSpeed));
		this._currentX = endX;
		this._currentY = endY;
		return this;
	}

	public LTurtle fd(float amount) {
		return forward(amount);
	}

	public LTurtle backward(float amount) {
		float endX = _currentX - (MathUtils.cos(MathUtils.toRadians(_currentAngle)) * amount);
		float endY = _currentY - (MathUtils.sin(MathUtils.toRadians(_currentAngle)) * amount);
		pushCommand(new GotoCommand("backward", true, _currentX, _currentY, endX, endY, _currentAngle, _turtleColor,
				_currentLineWidth, _currentSpeed));
		this._currentX = endX;
		this._currentY = endY;
		return this;
	}

	public LTurtle bk(float amount) {
		return backward(amount);
	}

	public LTurtle back(float amount) {
		return backward(amount);
	}

	public LTurtle setPos(float x, float y) {
		return goTo(x, y);
	}

	public LTurtle right(float angle) {
		return drawTurn(this._currentAngle - angle, false);
	}

	public LTurtle rt(float angle) {
		return right(angle);
	}

	public LTurtle left(float angle) {
		return drawTurn(this._currentAngle + angle, true);
	}

	public LTurtle lt(float angle) {
		return left(angle);
	}

	public LTurtle speed(float speed) {
		this._currentSpeed = speed;
		return this;
	}

	public float getAngle() {
		return _currentAngle;
	}

	public LTurtle setHeading(float angle) {
		this._currentAngle = angle;
		return this;
	}

	private LTurtle drawTurn(float finalAngle, boolean left) {
		while (_currentAngle != finalAngle) {
			_currentAngle = getNextNumberWithoutOverflow(_currentAngle, (left ? 1.5f : -1.5f) * (_currentSpeed / 2),
					finalAngle);
		}
		return this;
	}

	private float getNextNumberWithoutOverflow(float current, float incrementation, float capacity) {
		if (incrementation < 0) {
			if (incrementation + current < capacity) {
				return capacity;
			}
		} else {
			if (incrementation + current > capacity) {
				return capacity;
			}
		}
		return current + incrementation;
	}

	public LTurtle push(TurtleCommand tc) {
		if (tc == null) {
			return this;
		}
		push(tc);
		return this;
	}

	protected void pushCommand(TurtleCommand tc) {
		_commands.add(tc);
	}

	@Override
	public void process(long elapsedTime) {
		final float dt = MathUtils.max(Duration.toS(elapsedTime), LSystem.MIN_SECONE_SPEED_FIXED);
		for (int i = 0; i < _commands.size; i++) {
			boolean result = _commands.get(i).doUpdate(dt);
			if (result) {
				break;
			}
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		g.fillRect(x, y, getWidth(), getHeight(), this._turtleBgColor);
		final int size = this._commands.size;
		for (int i = size - 1; i > -1; i--) {
			_commands.get(i).doRender(g);
		}
	}

	public LTurtle clearImages() {
		final int size = this._commands.size;
		for (int i = size - 1; i > -1; i--) {
			TurtleCommand cmd = _commands.get(i);
			if ("image".equals(cmd.getTurleName())) {
				_commands.removeIndex(i);
			}
		}
		return this;
	}

	public LTurtle showImages() {
		final int size = this._commands.size;
		for (int i = size - 1; i > -1; i--) {
			TurtleCommand cmd = _commands.get(i);
			if ("image".equals(cmd.getTurleName())) {
				cmd.setVisible(true);
			}
		}
		return this;
	}

	public LTurtle hideImages() {
		final int size = this._commands.size;
		for (int i = size - 1; i > -1; i--) {
			TurtleCommand cmd = _commands.get(i);
			if ("image".equals(cmd.getTurleName())) {
				cmd.setVisible(false);
			}
		}
		return this;
	}

	public LTurtle clear() {
		_commands.clear();
		return this;
	}

	@Override
	public String getUIName() {
		return "Turtle";
	}

	@Override
	public void destory() {

	}

}

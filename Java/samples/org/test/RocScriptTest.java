package org.test;

import loon.LSystem;
import loon.Screen;
import loon.action.avg.drama.RocSSprite;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class RocScriptTest extends Screen {

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		StringBuilder command = new StringBuilder();
		command.append("print 'testing'");
		command.append(LSystem.LS);
		command.append("wait 1000");
		command.append(LSystem.LS);
		command.append("print 456");
		command.append(LSystem.LS);
		command.append("print 789");
		command.append(LSystem.LS);
		command.append("function xyz(x , y) begin");
		command.append(LSystem.LS);
		command.append("for i = x, i < y, i + 1 begin");
		command.append(LSystem.LS);
		command.append("println i");
		command.append(LSystem.LS);
		command.append("end");
		command.append(LSystem.LS);
		command.append("end");
		command.append(LSystem.LS);
		command.append("xyz(5 , 8)");
		command.append(LSystem.LS);
		command.append("print 'end'");
		String cmd = command.toString();
		System.out.println(cmd);
		RocSSprite sprite = new RocSSprite(cmd, false);
		add(sprite);
	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

}

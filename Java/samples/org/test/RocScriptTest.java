package org.test;

import loon.LSystem;
import loon.Screen;
import loon.action.avg.drama.IRocFunction;
import loon.action.avg.drama.RocFunctions;
import loon.action.avg.drama.RocSSprite;
import loon.action.avg.drama.RocScript;
import loon.action.sprite.SpriteLabel;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class RocScriptTest extends Screen {

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		//以字符串方式，注入一组脚本命令
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
		command.append(LSystem.LS);
		command.append("label(testing)");
		command.append(LSystem.LS);
		command.append("wait 3000");
		command.append(LSystem.LS);
		command.append("dellabel()");
		command.append(LSystem.LS);
		command.append("print 'end'");
		String cmd = command.toString();

		// 构建脚本执行器，非文件模式载入（若为true，则表示注入的是文件目录）
		RocSSprite sprite = new RocSSprite(cmd, false);
		// 无限循环脚本
		sprite.setLoopScript(true);
		// 获得脚本执行器
		RocScript script = sprite.getScript();
		// 获得脚本执行器的函数列表
		RocFunctions funs = script.getFunctions();
		final SpriteLabel label = new SpriteLabel("", 66, 66);
		// 添加自定义函数，显示label
		funs.add("label", new IRocFunction() {

			@Override
			public Object call(String value) {
				label.setLabel(value);
				add(label);
				return value;
			}
		});
		// 自定义函数，删除label
		funs.add("dellabel", new IRocFunction() {

			@Override
			public Object call(String value) {
				remove(label);
				return value;
			}
		});
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

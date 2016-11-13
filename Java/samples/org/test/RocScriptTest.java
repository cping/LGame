package org.test;

import loon.Screen;
import loon.action.avg.drama.CommandLink;
import loon.action.avg.drama.IRocFunction;
import loon.action.avg.drama.RocFunctions;
import loon.action.avg.drama.RocSSprite;
import loon.action.avg.drama.RocScript;
import loon.action.sprite.SpriteLabel;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class RocScriptTest extends Screen {

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		
		add(MultiScreenTest.getBackButton(this,1));

		// 以字符串方式，注入一组脚本命令
		CommandLink command = new CommandLink();
		command.line("print 'testing'");
		command.line("wait 1000");
		command.line("print 456");
		command.line("print 789");
		command.line("print testvar");
		command.line("if testvar == 'ABCDEFG' then");
		command.line("print 'abcdefg'");
		command.line("else");
		command.line("print 'gfedcba'");
		command.line("end");
		command.line("function xyz(x , y) begin");
		command.line("for i = x, i < y, i + 1 begin");
		command.line("println i");
		command.line("end");
		command.line("end");
		command.line("xyz(5 , 8)");
		command.line("print 'end'");
		command.line("label(testing)");
		command.line("wait 3000");
		command.line("dellabel()");
		command.line("function getNum(x) begin");
		command.line("return (x + 1)");
		command.line("end");
		command.line("t = getNum(9)");
		command.line("print t");
		command.line("function hello() begin");
		command.line("return \"Hello World!\"");
		command.line("end");
		command.line("println hello()");
		command.line("print 'end'");
		String cmd = command.toString();

		// 构建脚本执行器，非文件模式载入（若为true，则表示注入的是文件目录）
		RocSSprite sprite = new RocSSprite(cmd, false);

		// 无限循环脚本
		sprite.setLoopScript(true);
		// 获得脚本执行器
		RocScript script = sprite.getScript();
		script.setDebug(false);
		// 在脚本外部注入变量(循环模式下，每次循环会清空数据，所以此处注入仅有第一次运行脚本会生效)
		script.addVar("testvar", "ABCDEFG");
		// 获得脚本执行器的函数列表
		RocFunctions funs = script.getFunctions();
		final SpriteLabel label = new SpriteLabel("", 66, 66);
		// 添加自定义函数，显示label
		funs.add("label", new IRocFunction() {

			@Override
			public Object call(String[] value) {
				label.setLabel(value[0]);
				add(label);
				return value;
			}
		});
		// 自定义函数，删除label
		funs.add("dellabel", new IRocFunction() {

			@Override
			public Object call(String[] value) {
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

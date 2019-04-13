package org.test;

import loon.Stage;
import loon.action.avg.drama.CommandLink;
import loon.action.avg.drama.IRocFunction;
import loon.action.avg.drama.RocFunctions;
import loon.action.avg.drama.RocSSprite;
import loon.action.avg.drama.RocScript;
import loon.action.sprite.SpriteLabel;

public class RocScriptTest extends Stage {

	@Override
	public void create() {
		
		add(MultiScreenTest.getBackButton(this,1));

		// 以字符串方式，注入一组脚本命令
		CommandLink command = new CommandLink();
		command.line("print 'testing'")
		.line("wait 1000")
		.line("print 456")
		.line("print 789")
		.line("print testvar")
		.line("if testvar == 'ABCDEFG' then")
		.line("print 'abcdefg123'")
		.line("else")
		.line("print 'gfedcba'")
		.line("end")
		.line("function xyz(x , y) begin")
		.line("for i = x, i < y, i + 1 begin")
		.line("println i")
		.line("end")
		.line("end")
		.line("xyz(5 , 8)")
		.line("print 'end'")
		.line("label(testing)")
		.line("wait 3000")
		.line("dellabel()")
		.line("function getNum(x) begin")
		.line("return (x + 1)")
		.line("end")
		.line("t = getNum(9)")
		.line("print t")
		.line("function hello() begin")
		.line("return \"Hello World!\"")
		.line("end")
		.line("println hello()")
		.line("print 'end'");

		// 构建脚本执行器，非文件模式载入（若为true，则表示注入的是文件目录）
		RocSSprite sprite = new RocSSprite(command, false);

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
}

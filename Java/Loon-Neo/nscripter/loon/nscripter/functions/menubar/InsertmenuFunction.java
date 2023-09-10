package loon.nscripter.functions.menubar;

import loon.nscripter.Config;
import loon.nscripter.ValuesParser;
import loon.nscripter.functions.Function;
import loon.nscripter.variables.menubar.MenuItem;
import loon.utils.TArray;
import loon.utils.processes.GameProcess;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class InsertmenuFunction extends Function {

	public InsertmenuFunction(String name) {
		super("insertmenu");
	}

	@Override
	public String parse(String o) {
		TArray<String> mask = new TArray<String>();

		mask.add("$VAR");
		mask.add("NAME");
		mask.add("%VAR");
		mask.add("1");
		mask.add("2");

		_parameters = ValuesParser.getParams(_parameters, mask);

		if (_parameters != null) {
			return _parameters.items[_parameters.size - 1];
		} else {
			throw new RuntimeException("Incorrect function parameters.");
		}
	}

	@Override
	public GameProcess run() {
		GameProcess process = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				int level = Integer.parseInt(_parameters.get(2));

				Config.TopMenu.MenuList
						.add(new MenuItem(ValuesParser.getString(_parameters.items[0]), _parameters.items[1], level));

				_parameters = null;
			}
		};
		return process;
	}
}

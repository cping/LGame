package loon.nscripter.functions.menubar;

import loon.nscripter.Config;
import loon.nscripter.functions.Function;
import loon.nscripter.variables.menubar.MenuItem;
import loon.utils.TArray;
import loon.utils.processes.GameProcess;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class ResetmenuFunction extends Function {

	public ResetmenuFunction(String name) {
		super("resetmenu");
	}

	@Override
	public String parse(String o) {
		return o;
	}

	@Override
	public GameProcess run() {
		GameProcess process = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {

				Config.TopMenu.MenuList = new TArray<MenuItem>();

				_parameters = null;
			}
		};
		return process;
	}
}

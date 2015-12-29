package loon.nscripter.functions;

import loon.canvas.LColor;
import loon.nscripter.Config.Drawing;
import loon.nscripter.ValuesParser;
import loon.utils.TArray;
import loon.utils.processes.GameProcess;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class MonocroFunction extends Function {

	public MonocroFunction() {
		super("monocro");
	}

	@Override
	public String parse(String o) {
		TArray<String> mask = new TArray<String>();

		mask.add("COLOR|off");
		mask.add("1");

		_parameters = ValuesParser.getParams(_parameters, mask);

		if (_parameters != null) {
			return _parameters.items[1];
		} else {
			throw new RuntimeException("Incorrect function parameters.");
		}
	}

	@Override
	public GameProcess run() {
		GameProcess process = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				if (_parameters.items[0].equals("off")) {
					Drawing.TintColor = LColor.white;
				} else {
					Drawing.TintColor = ValuesParser
							.getColor(_parameters.items[0]);
				}

				_parameters = null;
			}
		};
		return process;
	}

}

package loon.nscripter.functions;

import loon.nscripter.Config.Delay;
import loon.nscripter.Config.Parsing;
import loon.nscripter.ValuesParser;
import loon.utils.TArray;
import loon.utils.processes.GameProcess;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class WaitFunction extends Function {

	public WaitFunction() {
		super("wait");
	}

	@Override
	public String parse(String o) {
		TArray<String> mask = new TArray<String>();

		mask.add("%VAR");
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
				Delay.TimeToSleep = (int)ValuesParser
						.getNumber(_parameters.items[0]);
				Delay.Sleeping = true;
				Delay.StartOnTouch = false;
				Parsing.Wait = true;
				_parameters = null;
			}
		};
		return process;
	}

}

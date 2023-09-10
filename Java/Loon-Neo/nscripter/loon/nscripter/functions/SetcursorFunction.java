package loon.nscripter.functions;

import loon.geom.Vector2f;
import loon.nscripter.Config.TextWindow;
import loon.nscripter.ValuesParser;
import loon.nscripter.variables.sprites.Sprite;
import loon.utils.TArray;
import loon.utils.processes.GameProcess;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class SetcursorFunction extends Function {

	public SetcursorFunction() {
		super("setcursor");
	}

	@Override
	public String parse(String o) {
		TArray<String> mask = new TArray<String>();
		mask.add("%VAR");
		mask.add("$VAR");
		mask.add("%VAR");
		mask.add("%VAR");
		mask.add("1");
		_parameters = ValuesParser.getParams(_parameters, mask);

		if (_parameters != null) {
			return _parameters.items[4];
		} else {
			throw new RuntimeException("Incorrect function parameters.");
		}
	}

	@Override
	public GameProcess run() {
		GameProcess process = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				int val = (int) ValuesParser.getNumber(_parameters.items[0]);

				Sprite spr = ValuesParser.getSprite(_parameters.items[1]);

				switch (val) {
				case 0:
					TextWindow.ClickWaitCursor = spr;
					TextWindow.ClickWaitCursorVector = new Vector2f(Float.parseFloat(_parameters.items[2]),
							Float.parseFloat(_parameters.items[3]));
					break;

				case 1:
					TextWindow.PageWaitCursor = spr;
					TextWindow.PageWaitCursorVector = new Vector2f(Float.parseFloat(_parameters.items[2]),
							Float.parseFloat(_parameters.items[3]));
					break;

				default:
					throw new RuntimeException("Incorrect function parameters.");
				}

				_parameters = null;
			}
		};
		return process;
	}

}

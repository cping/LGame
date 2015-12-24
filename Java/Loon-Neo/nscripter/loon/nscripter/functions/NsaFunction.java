package loon.nscripter.functions;

import loon.nscripter.Config;
import loon.nscripter.variables.NsaVariable;
import loon.nscripter.variables.Variable;
import loon.utils.processes.GameProcess;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class NsaFunction extends Function {

	public NsaFunction() {
		super("nsa");
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
				Variable nsaVar = Config.getEngineVarList("nsa");
				if (nsaVar != null) {
					nsaVar.set("1");
				} else {
					Config.EngineVarList.add(new NsaVariable(1));
				}
			}
		};
		return process;
	}

}

package loon.action.avg.drama;

public interface IMacros {

	public void call(IScriptLog log, int scriptLine, Command macros,
			String message);

	public boolean isSyncing();

}

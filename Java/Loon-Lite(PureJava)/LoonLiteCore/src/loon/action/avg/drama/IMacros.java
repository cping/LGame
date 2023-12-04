package loon.action.avg.drama;

public interface IMacros {

	void call(IScriptLog log, int scriptLine, Command macros, String message);

	boolean isSyncing();

}

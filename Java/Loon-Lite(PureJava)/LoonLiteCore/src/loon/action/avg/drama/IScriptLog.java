package loon.action.avg.drama;

public interface IScriptLog {

	void show(boolean flag);

	void err(Object mes);

	void info(Object mes);

	void line(Object mes);

	void err(String mes, Object... o);

	void info(String mes, Object... o);
}

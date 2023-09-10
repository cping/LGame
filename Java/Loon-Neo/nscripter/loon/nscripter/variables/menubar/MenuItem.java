package loon.nscripter.variables.menubar;

public class MenuItem {
	private String _name;
	private String _function;
	private int _level;

	public MenuItem(String name, String function, int level) {
		this._name = name;
		this._function = function;
		this._level = level;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getFunction() {
		return _function;
	}

	public void setFunction(String f) {
		this._function = f;
	}

	public int getLevel() {
		return _level;
	}

	public void setLevel(int l) {
		this._level = l;
	}
}
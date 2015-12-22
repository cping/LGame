package loon.nscripter.variables.alias;

public class NumVariable {
	
	public int _number;
	public String _name;

	public NumVariable(String name, int number) {
		this._name = name;
		this._number = number;
	}

	public int getNumber() {
		return _number;
	}

	public void setNumber(int number) {
		this._number = number;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}
}

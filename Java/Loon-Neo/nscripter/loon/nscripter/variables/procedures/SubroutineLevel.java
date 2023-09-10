package loon.nscripter.variables.procedures;

public class SubroutineLevel {

	public int _stoppedLine;
	public String _leftoverValue;

	public SubroutineLevel(int stoppedLine, String leftOverValue) {
		this._stoppedLine = stoppedLine;
		this._leftoverValue = leftOverValue;
	}

	public int getStoppedLine() {
		return _stoppedLine;
	}

	public void setStoppedLine(int s) {
		this._stoppedLine = s;
	}

	public String getLeftoverValue() {
		return _leftoverValue;
	}

	public void setLeftoverValue(String l) {
		this._leftoverValue = l;
	}
}

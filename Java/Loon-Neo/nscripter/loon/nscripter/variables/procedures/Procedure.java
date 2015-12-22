package loon.nscripter.variables.procedures;

public class Procedure {

	public final int _startPoint, _endPoint;
	public final String _name;

	public Procedure(String name, int startPoint, int endPoint) {
		this._name = name;
		this._startPoint = startPoint;
		this._endPoint = endPoint;
	}

	public int getStartPoint() {
		return _startPoint;
	}

	public int getEndPoint() {
		return _endPoint;
	}

	public String getName() {
		return _name;
	}
}

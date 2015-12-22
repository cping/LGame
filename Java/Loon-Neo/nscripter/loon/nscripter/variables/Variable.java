package loon.nscripter.variables;

import loon.utils.TArray;

public abstract class Variable {

	protected String _name;

	protected TArray<String> _parameters;
	
	public Variable(String name,String par){
	     _parameters = new TArray<String>();
         _parameters.add(par);
         _name = name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public String getName() {
		return _name;
	}

	public void setParameters(TArray<String> p) {
		this._parameters = p;
	}

	public TArray<String> getParameters() {
		return _parameters;
	}

	String get() {
		return _parameters.get(0);
	}

	boolean set(String param) {
		_parameters.set(0, param);
		return true;
	}
}
package loon.nscripter.functions;

import loon.utils.TArray;
import loon.utils.processes.GameProcess;

public abstract class Function {

	protected String _name;

	protected TArray<String> _parameters;
	
	public Function(String name){
	     _parameters = new TArray<String>();
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
	
    abstract GameProcess run();

    abstract String parse(String param);
}

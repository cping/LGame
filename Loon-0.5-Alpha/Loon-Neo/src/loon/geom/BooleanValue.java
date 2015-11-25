package loon.geom;

public class BooleanValue {

	private boolean result = false;

	public BooleanValue(boolean res) {
		this.set(res);
	}

	public void set(boolean res) {
		this.result = res;
	}

	public boolean result() {
		return this.result;
	}

}

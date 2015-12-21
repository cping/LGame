package loon.geom;

public class BooleanValue {

	private boolean value = false;

	public BooleanValue(boolean v) {
		this.set(v);
	}

	public void set(boolean res) {
		this.value = res;
	}

	public boolean result() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}

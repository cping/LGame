package loon;

public class EmptyBundle implements Bundle<Object> {

	@Override
	public void put(String key, Object value) {
	}

	@Override
	public Object get(String key) {
		return null;
	}

	@Override
	public Object get(String key, Object defaultValue) {
		return null;
	}

	@Override
	public Object remove(String key) {
		return null;
	}

	@Override
	public Object remove(String key, Object defaultValue) {
		return null;
	}

}

package loon;

public interface LazyLoading {

	public static interface Data {

		public Screen onScreen();

	}

	public void register(LSetting setting, LazyLoading.Data lazy);
}

package loon.preloader;

public interface LoaderCallback<T> {
	public void success (T result);

	public void error ();
}

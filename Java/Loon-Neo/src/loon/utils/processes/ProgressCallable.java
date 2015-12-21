package loon.utils.processes;

public interface ProgressCallable<T> {
	
	public T call(final ProgressListener p) throws Exception;
	
}

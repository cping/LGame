package loon.utils.processes;

public interface ProgressListener {

	public static final int PROGRESS_MIN = 0;
	public static final int PROGRESS_MAX = 100;

	public void onProgressChanged(final int progress);
}

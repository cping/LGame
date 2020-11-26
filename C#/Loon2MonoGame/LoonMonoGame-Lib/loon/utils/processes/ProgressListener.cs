namespace loon.utils.processes
{
	public abstract class ProgressListener
	{
		public const int PROGRESS_MIN = 0;

		public const int PROGRESS_MAX = 100;
		public abstract void OnProgressChanged(int progress);
	}
}

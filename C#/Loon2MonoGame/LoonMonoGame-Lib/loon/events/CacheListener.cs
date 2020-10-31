namespace loon.events
{
	public interface CacheListener
	{
		 void OnSpawn();

		 void OnUnspawn();

		 void Disposed(bool close);
	}
}

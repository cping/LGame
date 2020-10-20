namespace loon.events
{
	public interface QueryEvent<T>
	{
		void Hit(T t);
	}

}

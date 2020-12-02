namespace loon.events
{
	public abstract class Observe<T> : UpdateableT<T> , ActionUpdate {

		public Observe():base(default, null)
		{
			
		}

		public Observe(T context, string name):base(context, name)
		{
			
		}

		public abstract bool Completed();
    }

}

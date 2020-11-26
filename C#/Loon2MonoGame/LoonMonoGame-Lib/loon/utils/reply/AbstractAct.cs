
namespace loon.utils.reply
{
    public abstract class AbstractAct<T> : Bypass, ActView<T>
    {

        public class DefUnitPort : UnitPort
        {
            public override void OnEmit() { }
        }

        public static readonly DefUnitPort DEF = new DefUnitPort();

		public virtual ActView<M> Map<M>(Function<T, M> func) 
		{
			AbstractAct<T> outer = this;
			return new MappedActImpl<M>(func, outer);
		}

		private class MappedActImpl<M> : MappedAct<M>
		{
			private Function<T, M> func;
			private AbstractAct<T> outer;

			public MappedActImpl(Function<T, M> func, AbstractAct<T> outer)
			{
				this.func = func;
				this.outer = outer;
			}

			protected internal override Connection Connect()
			{
				return outer.Connect(new MapActViewListenerImpl<T,M>(this, func));
			}

			private class MapActViewListenerImpl<T2, M2> : ActViewListener<T2>
			{

				private readonly AbstractAct<M2> outerInstance;

				private readonly Function<T2, M2> func;

				public MapActViewListenerImpl(AbstractAct<M2> outer, Function<T2, M2> f)
				{
					this.outerInstance = outer;
					this.func = f;
				}

				public void OnEmit(T2 value)
				{
					outerInstance.NotifyEmit(func.Apply(value));
				}
			}
		}


		public virtual ActView<T> Filter(Function<T, bool> pred)
		{
			AbstractAct<T> outer = this;
			return new FilterImpl<T>(pred, outer);
		}

		private class FilterImpl<T1> : MappedAct<T1>
		{

			private Function<T1, bool> pred;
			private AbstractAct<T1> outer;

			public FilterImpl(Function<T1, bool> pred, loon.utils.reply.AbstractAct<T1> outer)
			{
				this.pred = pred;
				this.outer = outer;
			}

			protected internal override Connection Connect()
			{
				return outer.Connect(new MapActViewListenerImpl<T1>(this, pred));
			}

			private class MapActViewListenerImpl<T2> : ActViewListener<T2>
			{

				private readonly AbstractAct<T2> outerInstance;

				private readonly Function<T2, bool> pred;

				public MapActViewListenerImpl(AbstractAct<T2> outer, Function<T2, bool> p)
				{
					this.outerInstance = outer;
					this.pred = p;
				}

				public void OnEmit(T2 value)
				{
					if (pred.Apply(value))
					{
						outerInstance.NotifyEmit(value);
					}
				}
			}
		}

		public virtual Connection Connect(ActViewListener<T> port)
		{
			return AddConnection(port);
		}

		public virtual void Disconnect(ActViewListener<T> port)
		{
			RemoveConnection(port);
		}

		public override GoListener DefaultListener()
		{
			ActViewListener<T> p = (ActViewListener<T>)AbstractAct<T>.DEF;
			return p;
		}

		protected internal virtual void NotifyEmit(T e)
		{
			this.Notify(EMIT, e, null, null);
		}

        ActView<M> ActView<T>.Map<M>(Function<T, M> func)
        {
            throw new System.NotImplementedException();
        }


        protected internal static readonly Notifier EMIT = new NotifyEmitImpl();

		private class NotifyEmitImpl : Notifier
		{
			public override void Notify(object port, object e, object a, object b)
			{
				((ActViewListener<object>)port).OnEmit(e);
			}
		}
	}
}

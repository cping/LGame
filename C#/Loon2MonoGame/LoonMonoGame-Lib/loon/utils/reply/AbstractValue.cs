namespace loon.utils.reply
{
    public abstract class AbstractValue<T> : Bypass, VarView<T>
    {
		public abstract T Get();

		public virtual VarView<M> Map<M>(Function<T, M> func)
		{
			AbstractValue<T> outer = this;
			return new MappedValueImpl<M>(func, outer);
		}

		private class MappedValueImpl<M> : MappedValue<M>
		{

			private Function<T, M> func;
			private AbstractValue<T> outer;

			public MappedValueImpl(Function<T, M> func, AbstractValue<T> outer)
			{
				this.func = func;
				this.outer = outer;
			}

			public override M Get()
			{
				return func.Apply(outer.Get());
			}

			public override string ToString()
			{

				return outer + ".map(" + func + ")";
			}

			protected internal override Connection Connect()
			{
				return outer.Connect(new MapVarViewListenerImpl<T,M>(this, func));
			}

			private class MapVarViewListenerImpl<T2, M2> : VarViewListener<T2>
			{

				private readonly AbstractValue<M2> outerInstance;

				private readonly Function<T2, M2> func;

				public MapVarViewListenerImpl(AbstractValue<M2> outer, Function<T2, M2> f)
				{
					this.outerInstance = outer;
					this.func = f;
				}

				public void OnChange(T2 value, T2 ovalue)
				{
					outerInstance.NotifyChange(func.Apply(value), func.Apply(ovalue));
				}
			}
		}


		public virtual Connection Connect(VarViewListener<T> listener)
		{
			return AddConnection(listener);
		}

		
		public virtual Connection Connect(ActViewListener<T> listener)
		{
			return Connect(Wrap(listener));
		}

		private static VarViewListener<T> Wrap(ActViewListener<T> listener)
		{
			return new VarViewWrapImpl<T>(listener);
		}

		private class VarViewWrapImpl<T1> : VarViewListener<T1>
		{
			private readonly ActViewListener<T1> listener;

			public VarViewWrapImpl(ActViewListener<T1> listener)
			{
				this.listener = listener;
			}

			public void OnChange(T1 newValue, T1 oldValue)
			{
				listener.OnEmit(newValue);
			}
		}

		public virtual Connection Connect(Port<T> port)
		{
		
			return Connect((VarViewListener<T>)port);
		}

	
		public virtual void Disconnect(VarViewListener<T> listener)
		{
			RemoveConnection(listener);
		}

		public override int GetHashCode()
		{
			T value = Get();
			return ((object)value == default) ? 0 : value.GetHashCode();
		}

		public override bool Equals(object other)
		{
			if (other == null)
			{
				return false;
			}
			if (other.GetType() != this.GetType())
			{
				return false;
			}
			T value = Get();
			T ovalue = ((AbstractValue<T>)other).Get();
			return AreEqual(value, ovalue);
		}

		public override string ToString()
		{
			string cname = this.GetType().FullName;
			return cname.Substring(cname.LastIndexOf(".") + 1) + "(" + Get() + ")";
		}

		public override GoListener DefaultListener()
		{
			VarViewListener<T> p = (VarViewListener<T>)AbstractAct<T>.DEF;
			return p;
		}

		protected internal virtual T UpdateAndNotifyIf(T value)
		{
			return UpdateAndNotify(value, false);
		}

		protected internal virtual T UpdateAndNotify(T value)
		{
			return UpdateAndNotify(value, true);
		}

		protected internal virtual T UpdateAndNotify(T value, bool force)
		{
			CheckMutate();
			T ovalue = UpdateLocal(value);
			if (force || !AreEqual(value, ovalue))
			{
				EmitChange(value, ovalue);
			}
			return ovalue;
		}

		protected internal virtual void EmitChange(T value, T oldValue)
		{
			NotifyChange(value, oldValue);
		}

		protected internal virtual void NotifyChange(T value, T oldValue)
		{
			this.Notify(CHANGE, value, oldValue, default);
		}

		protected internal virtual T UpdateLocal(T value)
		{
			throw new System.NotSupportedException();
		}

        protected internal readonly Notifier<T> CHANGE = new NotifierImpl();

		private class NotifierImpl : Notifier<T>
		{
			public override void Notify(GoListener lner, T value, T oldValue, T ignored)
			{
				((VarViewListener<T>)lner).OnChange(value, oldValue);
			}
		}
	}
}

namespace loon.utils.reply
{

	public class GoPromise<T> : GoFuture<T> {


		protected internal readonly new Var<Try<T>> _result;

		public static GoPromise<T1> Create<T1>()
		{
			return new GoPromise<T1>();
		}

		public virtual void Succeed(T value)
		{
			_result.Update(Try<T>.CreateSuccess<T>(value));
		}

		public virtual void Fail(System.Exception cause)
		{
			_result.Update(Try<T>.CreateFailure<T>(cause));
		}

		public virtual Port<Try<T>> Completer()
		{
			return _result.Port();
		}

		public virtual Port<T> Succeeder()
		{
			return new SucceederImpl<T>(this);
		}

		private class SucceederImpl<T1> : Port<T1>
		{
			private readonly GoPromise<T1> outerInstance;

			public SucceederImpl(GoPromise<T1> outerInstance)
			{
				this.outerInstance = outerInstance;
			}

			public override void OnEmit(T1 result)
			{
				outerInstance.Succeed(result);
			}
		}

		public virtual Port<System.Exception> Failer()
		{
			return new FailerImpl(new GoPromise<System.Exception>());
		}

		private class FailerImpl : Port<System.Exception>
		{
			private readonly GoPromise<System.Exception> outerInstance;

			public FailerImpl(GoPromise<System.Exception> outerInstance)
			{
				this.outerInstance = outerInstance;
			}

			public override void OnEmit(System.Exception cause)
			{
				outerInstance.Fail(cause);
			}
		}

		public virtual bool HasConnections()
		{
			return _result.HasConnections();
		}

		protected internal GoPromise() : this(new GoPromiseImpl<T>())
		{
		}

		private class GoPromiseImpl<T1> : Var<Try<T1>>
		{

			public GoPromiseImpl() : base(null)
			{
			}

			protected internal override Try<T1> UpdateAndNotify(Try<T1> value, bool force)
			{
				lock (this)
				{
					if (_value != null)
					{
						throw new LSysException("already completed");
					}
					try
					{
						return base.UpdateAndNotify(value, force);
					}
					finally
					{
						_listeners = null;
					}
				}
			}
		}

		private GoPromise(Var<Try<T>> result) : base(result)
		{
			_result = result;
		}


	}
}

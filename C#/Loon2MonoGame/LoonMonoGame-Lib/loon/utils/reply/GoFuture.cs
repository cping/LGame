namespace loon.utils.reply
{
   public class GoFuture<T>
	{

		protected internal readonly VarView<Try<T>> _result;
		protected internal VarView<bool> _isComplete;

		protected internal GoFuture(VarView<Try<T>> result)
		{
			_result = result;
		}

		public static  GoFuture<T> Success(T value)
		{
			return Result(Try<T>.CreateSuccess(value));
		}

		public static GoFuture<T1> Result<T1>(Try<T1> result)
		{
			return new GoFuture<T1>(Var<T1>.Create(result));
		}

	}
}

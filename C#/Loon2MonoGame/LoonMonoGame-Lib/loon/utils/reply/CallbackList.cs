namespace loon.utils.reply
{

	public class CallbackList<T> : Callback<T> {

	public static  TArray<Callback<T1>> CreateAdd<T1>(TArray<Callback<T1>> list,
			Callback<T1> callback)
	{
		if (list == null)
		{
			list = new TArray<Callback<T1>>();
		}
		list.Add(callback);
		return list;
	}

	public static TArray<Callback<T1>> DispatchSuccessClear<T1>(
			TArray<Callback<T1>> list, T1 result)
	{
		if (list != null)
		{
			for (int ii = 0, ll = list.size; ii < ll; ii++)
			{
				list.Get(ii).OnSuccess(result);
			}
		}
		return null;
	}

	public static TArray<Callback<T1>> DispatchFailureClear<T1>(
			TArray<Callback<T1>> list, System.Exception cause)
	{
		if (list != null)
		{
			for (int ii = 0, ll = list.size; ii < ll; ii++)
			{
				list.Get(ii).OnFailure(cause);
			}
		}
		return null;
	}

	private TArray<Callback<T>> callbacks = new TArray<Callback<T>>();

	protected void CheckState()
	{
		if (callbacks == null)
		{
			throw new LSysException("callbackList has already fired !");
		}
	}

	public static  CallbackList<T1> Create<T1>(Callback<T1> callback)
	{
		CallbackList<T1> list = new CallbackList<T1>();
		list.Add(callback);
		return list;
	}

	public CallbackList<T> Add(Callback<T> callback)
	{
		CheckState();
		callbacks.Add(callback);
		return this;
	}

	public void Remove(Callback<T> callback)
	{
		CheckState();
		callbacks.Remove(callback);
	}

	public void OnSuccess(T result)
	{
		CheckState();
		foreach (Callback<T> cb in callbacks)
		{
			cb.OnSuccess(result);
		}
		callbacks = null;
	}


	public void OnFailure(System.Exception t)
	{
		CheckState();
		foreach (Callback<T> cb in callbacks)
		{
			cb.OnFailure(t);
		}
		callbacks = null;
	}

}

}

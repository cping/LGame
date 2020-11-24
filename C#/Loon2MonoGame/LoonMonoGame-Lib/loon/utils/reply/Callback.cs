using java.lang;

namespace loon.utils.reply
{
	public interface Callback<T>
	{
		void OnSuccess(T result);

		void OnFailure(System.Exception cause);

	}
}

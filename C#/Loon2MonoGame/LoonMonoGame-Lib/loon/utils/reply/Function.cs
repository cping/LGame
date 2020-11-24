namespace loon.utils.reply
{
	public interface Function<F, T>
	{
		T Apply(F input);
	}


}

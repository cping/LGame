using Loon.Java;
namespace Loon
{
	public class UnityHandler
	{
		public void Post(Runnable runnable)
		{
			runnable.Run();
		}
	}
}

using Loon.Java;
namespace Loon
{
    public class XNAHandler
    {
        public void Post(Runnable runnable)
        {
            runnable.Run();
        }
    }
}

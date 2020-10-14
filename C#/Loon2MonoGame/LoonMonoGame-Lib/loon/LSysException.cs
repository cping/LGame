using java.lang;

namespace loon
{
	public class LSysException : RuntimeException
	{

	public LSysException(string message):base(message)
	{
	}

	public LSysException(string message, Throwable cause) : base(message, cause)
	{

	}

	public LSysException(string message, int line) :base(message)
	{

	}
}

}

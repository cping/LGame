namespace java.lang
{
    public class Exception : Throwable
    {
        public Exception() : base()
        {
        }

        public Exception(string message) : base(message)
        {
        }
        public Exception(string message, Throwable cause) : base(message)
        {
        }
    }
}

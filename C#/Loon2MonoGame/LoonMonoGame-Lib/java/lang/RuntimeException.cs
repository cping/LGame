namespace java.lang
{
    public class RuntimeException : Exception
    {
        public RuntimeException() : base()
        {
        }

        public RuntimeException(string message) : base(message)
        {
        }
        public RuntimeException(string message, Throwable cause) : base(message, cause)
        {
        }
    }
}

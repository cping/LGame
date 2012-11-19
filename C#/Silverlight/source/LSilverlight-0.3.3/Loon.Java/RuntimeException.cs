namespace Loon.Java
{
    using System;

    public class RuntimeException : Exception
    {
        public RuntimeException()
        {
        }

        public RuntimeException(Exception cause)
            : base("RuntimeException", cause)
        {
        }

        public RuntimeException(string cause)
            : base(cause)
        {
        }

        public RuntimeException(string cause, Exception e)
            : base(cause, e)
        {
        }
    }
}

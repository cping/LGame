using java.lang;

namespace java.util 
{ 
    public class NoSuchElementException: RuntimeException
    {
        public NoSuchElementException() : base()
        {   
        }

        public NoSuchElementException(string message) : base(message)
        {   
        }
    }
}

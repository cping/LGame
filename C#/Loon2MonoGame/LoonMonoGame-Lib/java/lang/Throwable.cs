using System;
using System.Collections.Generic;
using System.Text;

namespace java.lang
{
    public class Throwable : System.Exception
    {
        private readonly Throwable cause;
        private readonly string message;
        private readonly string trace;

        public Throwable() : this((string)null)
        {
        }
        public Throwable(Throwable cause):base(cause == null ? null : cause.ToString(),cause)
        {
            this.cause = cause;
        }

        public Throwable(string message) : base(message)
        {
            this.cause = this;
            this.message = message;
            this.trace = System.Environment.StackTrace;
        }

        public Throwable(string message,Throwable cause) : base()
        {
            this.cause = cause;
            this.message = message;
            this.trace = System.Environment.StackTrace;
        }

        public Throwable GetCause()
        {
            lock (this) { 
               return (cause == this ? null : cause);
            }
        }

        public void PrintStackTrace()
        {
            JavaSystem.err_f.Println(this.trace);
        }

        virtual public string GetMessage()
        {
            return message;
        }

        override public string ToString()
        {
            if (message == null) { return this.GetType().FullName; }
            else { return this.GetType().FullName + ": " + message; }
        }
    }
}

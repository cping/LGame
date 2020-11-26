using java.lang;

namespace loon
{
    public abstract class LGame
    {
        public enum Type
        {
            JAVASE, ANDROID, IOS, WP, HTML5, UNITY, SWITCH, STUB
        }
        public enum Status
        {
            PAUSE, RESUME, EXIT
        }

        public class Error
        {

            public string message;
            public Throwable cause;

            public Error(string message, Throwable cause)
            {
                this.message = message;
                this.cause = cause;
            }
        }

        protected internal Display displayImpl;

        public virtual Display Register(Screen screen)
        {
            return null;
        }
    }
}

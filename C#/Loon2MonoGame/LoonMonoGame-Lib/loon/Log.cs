namespace loon
{
    public abstract class Log
    {

        public abstract void OnError(System.Exception e);

        public class Level
        {

            public static readonly Level ALL = new Level("All", 0);

            public static readonly Level DEBUG = new Level("Debug", 1);

            public static readonly Level INFO = new Level("Info", 2);

            public static readonly Level WARN = new Level("Warn", 3);

            public static readonly Level ERROR = new Level("Error", 4);

            public static readonly Level IGNORE = new Level("Ignore", 5);

            public readonly string levelString;

            public readonly int id;

            internal Level(string levelString, int levelInt)
            {
                this.levelString = levelString;
                this.id = levelInt;
            }

            public override string ToString()
            {
                return levelString;
            }

            public virtual int toType()
            {
                return id;
            }
        }

        private Collector collector;
        private Level minLevel = Level.DEBUG;

        public interface Collector
        {

            void Logged(Level level, string msg, System.Exception e);
        }

        public virtual void SetCollector(Collector collector)
        {
            this.collector = collector;
        }

        public virtual Level MinLevel
        {
            set
            {
                minLevel = value;
            }
        }

        public virtual void Debug(string msg)
        {
            Debug(msg, (System.Exception)null);
        }

        public virtual void Debug(string msg, params object[] args)
        {
            Debug(Format(msg, args), (System.Exception)null);
        }

        public virtual void Debug(string msg, System.Exception e)
        {
            Call(Level.DEBUG, msg, e);
        }

        public virtual void Info(string msg)
        {
            Info(msg, (System.Exception)null);
        }

        public virtual void Info(string msg, params object[] args)
        {
            Info(Format(msg, args), (System.Exception)null);
        }

        public virtual void Info(string msg, System.Exception e)
        {
            Call(Level.INFO, msg, e);
        }

        public virtual void Warn(string msg)
        {
            Warn(msg, (System.Exception)null);
        }

        public virtual void Warn(string msg, params object[] args)
        {
            Warn(Format(msg, args), (System.Exception)null);
        }

        public virtual void Warn(string msg, System.Exception e)
        {
            Call(Level.WARN, msg, e);
        }

        public virtual void Error(string msg)
        {
            Error(msg, (System.Exception)null);
        }

        public virtual void Error(string msg, params object[] args)
        {
            Error(Format(msg, args), (System.Exception)null);
        }

        public virtual void Error(string msg, System.Exception e)
        {
            Call(Level.ERROR, msg, e);
        }

        protected internal virtual string Format(string msg, object[] args)
        {
            return msg;
        }

        internal virtual void Call(Level level, string msg, System.Exception e)
        {
            //todo
        }

        protected internal abstract void CallNativeLog(Level level, string msg, System.Exception e);
    }

}

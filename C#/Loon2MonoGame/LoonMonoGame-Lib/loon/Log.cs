using loon.utils;

namespace loon
{
    public abstract class Log
    {
        protected internal abstract void CallNativeLog(Level level, string msg, System.Exception e);
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

        public virtual void SetMinLevel(Level v)
        {
               this.minLevel = v;
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
            if (args == null)
            {
                return msg;
            }
            return StringUtils.Format(msg, args);
        }

        internal virtual void Call(Level level, string msg, System.Exception e)
        {

            if (LSystem.IsConsoleLog())
            {
                if (collector != null)
                {
                    collector.Logged(level, msg, e);
                }
                if (level.id >= minLevel.id)
                {
                    CallNativeLog(level, msg, e);
                    LGame game = LSystem.Base;
                    if (game != null)
                    {
                        LSetting setting = game.setting;
                        // 待实现GLEx
                      /*  LProcess process = LSystem.GetProcess();
                        if (process != null && (setting.isDebug || setting.isDisplayLog))
                        {
                            LColor color = LColor.white;
                            if (level.id > Level.INFO.id)
                            {
                                color = LColor.red;
                            }
                            if (process != null)
                            {
                                if (e == null)
                                {
                                    process.addLog(msg, color);
                                }
                                else
                                {
                                    process.addLog(msg + " [ " + e.getMessage() + " ] ", color);
                                }
                            }
                      */
                        }
                    }
                }
                if (e != null)
                {
                  OnError(e);
                }
            }

        }

    

}

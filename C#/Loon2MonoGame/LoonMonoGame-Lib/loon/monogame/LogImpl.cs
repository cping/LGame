using java.lang;
using System;
using static loon.Log;

namespace loon.monogame
{
    public sealed class LogImpl
    {

        public void Exception(object o)
        {
            JavaSystem.Out.Println(o);
        }

        public void DebugWrite(string text)
        {
            JavaSystem.Out.Println(text);
        }

        private const int MAX_LOG_MESSAGES = 128;

        private static LogMessage[] store;

        private static int oldestMessageIndex;

        private static int newestMessageIndex;

        private Level level = Level.INFO;

        private LogFormat logFormat;

        private string app;

        static LogImpl()
        {
            Clear();
        }

        internal LogImpl(Type clazz) : this(LSystem.GetExtension(clazz.FullName), 0)
        {

        }

        internal LogImpl(string app, int type)
        {
            this.logFormat = new LogFormat(true, type);
            this.app = app;
            this.level = Level.ALL;
        }

        public Level GetLogLevel()
        {
            return level;
        }

        public void SetLevel(int level)
        {
            if (level == Level.DEBUG.id)
            {
                this.level = Level.DEBUG;
            }
            else if (level == Level.INFO.id)
            {
                this.level = Level.INFO;
            }
            else if (level == Level.WARN.id)
            {
                this.level = Level.WARN;
            }
            else if (level == Level.ERROR.id)
            {
                this.level = Level.ERROR;
            }
            else if (level == Level.IGNORE.id)
            {
                this.level = Level.IGNORE;
            }
            else if (level == Level.ALL.id)
            {
                this.level = Level.ALL;
            }
            else
            {
                throw new IllegalArgumentException("Levels of error messages !");
            }
        }

        public void SetLevel(Level level)
        {
            this.level = level;
        }

        public bool IsVisible()
        {
            return logFormat.IsShow();
        }

        public void SetVisible(bool show)
        {
            logFormat.SetShow(show);
        }

        public void Hide()
        {
            SetVisible(false);
        }

        public void Show()
        {
            SetVisible(true);
        }

        public void D(string message)
        {
            if (level.id == Level.ALL.id || level.id <= Level.DEBUG.id)
            {
                AddLogMessage(message, Level.DEBUG, null);
            }
        }

        public void D(string message, Throwable tw)
        {
            if (level.id == Level.ALL.id || level.id <= Level.DEBUG.id)
            {
                AddLogMessage(message, Level.DEBUG, tw);
            }
        }

        public void I(string message)
        {
            if (level.id == Level.ALL.id || level.id <= Level.INFO.id)
            {
                AddLogMessage(message, Level.INFO, null);
            }
        }

        public void I(string message, Throwable tw)
        {
            if (level.id <= Level.INFO.id)
            {
                AddLogMessage(message, Level.INFO, tw);
            }
        }

        public void W(string message)
        {
            if (level.id == Level.ALL.id || level.id <= Level.WARN.id)
            {
                AddLogMessage(message, Level.WARN, null);
            }
        }

        public void W(string message, Throwable tw)
        {
            if (level.id == Level.ALL.id || level.id <= Level.WARN.id)
            {
                AddLogMessage(message, Level.WARN, tw);
            }
        }

        public void E(string message)
        {
            if (level.id == Level.ALL.id || level.id <= Level.ERROR.id)
            {
                AddLogMessage(message, Level.ERROR, null);
            }
        }

        public void E(string message, Throwable tw)
        {
            if (level.id <= Level.ERROR.id)
            {
                AddLogMessage(message, Level.ERROR, tw);
            }
        }

        public LogFormat GetLogFormat()
        {
            return logFormat;
        }

        public bool IsDebugEnabled()
        {
            return level.id <= Level.DEBUG.id;
        }

        public bool IsInfoEnabled()
        {
            return level.id <= Level.INFO.id;
        }

        public void AddLogMessage(string message, Level level, System.Exception throwable)
        {
            if (message == null)
            {
                message = "";
            }
            string text = message;
            if (throwable != null)
            {
                text += " " + throwable.ToString();
            }
            newestMessageIndex = (newestMessageIndex + 1) % MAX_LOG_MESSAGES;
            if (newestMessageIndex == oldestMessageIndex)
            {
                store[newestMessageIndex].SetLogMessage(level, text);
                oldestMessageIndex = (oldestMessageIndex + 1) % MAX_LOG_MESSAGES;
            }
            else
            {
                store[newestMessageIndex] = new LogMessage(level, text);
                if (oldestMessageIndex < 0)
                {
                    oldestMessageIndex = 0;
                }
            }
            LogMessage log = store[newestMessageIndex];
            logFormat.Out(log.time, app, log.level.levelString, log.message);
            if (throwable != null)
            {
                JavaSystem.Err.Println(throwable.StackTrace);
            }
        }

        public LogMessage[] GetLogMessages()
        {
            lock (this)
            {
                int numberOfMessages;
                if (newestMessageIndex < 0)
                {
                    numberOfMessages = 0;
                }
                else if (newestMessageIndex >= oldestMessageIndex)
                {
                    numberOfMessages = newestMessageIndex - oldestMessageIndex + 1;
                }
                else
                {
                    numberOfMessages = MAX_LOG_MESSAGES;
                }
                LogMessage[] copy = new LogMessage[numberOfMessages];
                for (int i = 0; i < numberOfMessages; i++)
                {
                    int index = newestMessageIndex - i;
                    if (index < 0)
                    {
                        index = MAX_LOG_MESSAGES + index;
                    }
                    copy[numberOfMessages - i - 1] = store[index];
                }
                return copy;
            }
        }

        public static void Clear()
        {
            lock (typeof(LogImpl))
            {
                oldestMessageIndex = -1;
                newestMessageIndex = -1;
                store = new LogMessage[MAX_LOG_MESSAGES];
            }
        }

    }

}

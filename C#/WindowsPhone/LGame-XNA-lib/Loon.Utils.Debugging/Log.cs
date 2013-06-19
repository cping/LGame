using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;

namespace Loon.Utils.Debugging
{
    public sealed class Log
    {
        public static void Exception(object o)
        {
            Log.DebugWrite(((System.Exception)o).StackTrace);
        }

        public static void DebugWrite(string text)
        {
            System.Diagnostics.Debug.WriteLine("[" + System.DateTime.Now.ToLocalTime().ToString() + "] " + text);
        }

        private const int MAX_LOG_MESSAGES = 25;

        private static LogMessage[] store;

        private static int oldestMessageIndex;

        private static int newestMessageIndex;

        private Level level;

        private LogFormat logFormat;

        private string app;

        static Log()
        {
            Clear();
        }

        public Log(Type clazz)
            : this(clazz.Name,0)
        {
        }

        public Log(string a, int type)
        {
            this.level = Level.ALL;
            this.logFormat = new LogFormat(true, type);
            this.app = a;
        }

        public Level GetLogLevel()
        {
            return level;
        }

        /// <summary>
        /// 设定当前日志等级
        /// </summary>
        ///
        /// <param name="l"></param>
        public void SetLevel(int l)
        {
            if (l == Level.DEBUG.level)
            {
                this.level = Level.DEBUG;
            }
            else if (l == Level.INFO.level)
            {
                this.level = Level.INFO;
            }
            else if (l == Level.WARN.level)
            {
                this.level = Level.WARN;
            }
            else if (l == Level.ERROR.level)
            {
                this.level = Level.ERROR;
            }
            else if (l == Level.IGNORE.level)
            {
                this.level = Level.IGNORE;
            }
            else if (l == Level.ALL.level)
            {
                this.level = Level.ALL;
            }
            else
            {
                throw new ArgumentException("Levels of error messages !");
            }
        }

        public void SetLevel(Level l)
        {
            this.level = l;
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

        public void Out(string message)
        {
            AddLogMessage(message, level, null);
        }

        public void Out(string message, Exception ex)
        {
            AddLogMessage(message, level, ex);
        }

        public void D(string message)
        {
            if (level.level == Level.ALL.level || level.level <= Level.DEBUG.level)
            {
                AddLogMessage(message, Level.DEBUG, null);
           }
        }

        public void D(string message, Exception ex)
        {
            if (level.level == Level.ALL.level || level.level <= Level.DEBUG.level)
            {
                AddLogMessage(message, Level.DEBUG, ex);
            }
        }

        public void I(string message)
        {
            if (level.level == Level.ALL.level || level.level <= Level.INFO.level)
            {
                AddLogMessage(message, Level.INFO, null);
            }
        }

        public void I(string message, Exception ex)
        {
            if (level.level == Level.ALL.level || level.level <= Level.INFO.level)
            {
                AddLogMessage(message, Level.INFO, ex);
            }
        }

        public void W(string message)
        {
            if (level.level == Level.ALL.level || level.level <= Level.WARN.level)
            {
                AddLogMessage(message, Level.WARN, null);
            }
        }

        public void W(string message, Exception ex)
        {
            if (level.level == Level.ALL.level || level.level <= Level.WARN.level)
            {
                AddLogMessage(message, Level.WARN, ex);
            }
        }

        public void E(string message)
        {
            if (level.level == Level.ALL.level || level.level <= Level.ERROR.level)
            {
                AddLogMessage(message, Level.ERROR, null);
            }
        }

        public void E(string message, Exception ex)
        {
            if (level.level == Level.ALL.level || level.level <= Level.ERROR.level)
            {
                AddLogMessage(message, Level.ERROR, ex);
            }
        }

        public LogFormat GetLogFormat()
        {
            return logFormat;
        }

        public bool IsDebugEnabled()
        {
            return level.level <= Level.DEBUG.level;
        }

        public bool IsInfoEnabled()
        {
            return level.level <= Level.INFO.level;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void AddLogMessage(string message, Level l,
                Exception throwable)
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
                store[newestMessageIndex].SetLogMessage(l, text);
                oldestMessageIndex = (oldestMessageIndex + 1) % MAX_LOG_MESSAGES;
            }
            else
            {
                store[newestMessageIndex] = new LogMessage(l, text);
                if (oldestMessageIndex < 0)
                {
                    oldestMessageIndex = 0;
                }
            }
            LogMessage log = store[newestMessageIndex];
            logFormat.Out(log.time, app, log.level.levelString, log.message);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public LogMessage[] GetLogMessages()
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

        [MethodImpl(MethodImplOptions.Synchronized)]
        public static void Clear()
        {
            oldestMessageIndex = -1;
            newestMessageIndex = -1;
            store = new LogMessage[MAX_LOG_MESSAGES];
        }

    }
}

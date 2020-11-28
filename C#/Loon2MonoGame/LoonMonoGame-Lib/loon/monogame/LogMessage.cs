using System;
using static loon.Log;

namespace loon.monogame
{
  public  class LogMessage
{
        private static string LOG_DEFAULT_DATE = "yyyy-MM-dd HH:mm:ss,fff";

		public Level level;

		public string time;

		public string message;

		protected internal LogMessage(Level level, string message)
		{
			SetLogMessage(level, message);
		}


		protected internal virtual void SetLogMessage(Level level, string message)
		{
			this.level = level;
			this.message = message;
			this.time = DateTime.Now.ToString(LOG_DEFAULT_DATE) ;
		}

		public virtual Level GetLevel()
		{
				return level;
		}

		public virtual string GetMessage()
		{
				return message;
		}

		public virtual string GetTime()
		{
				return time;
		}

		public override string ToString()
		{
			return (time + " [" + level + "] " + message);
		}
	}
}

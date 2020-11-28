using java.lang;
using System.Diagnostics;

namespace loon.monogame
{

	public class LogFormat
	{

		private const int TIME_INDEX = 0;

		private const int APP_INDEX = 1;

		private const int MODULE_INDEX = 2;

		private const int MESSAGE_INDEX = 3;

		private static readonly string[] LOG_TITLE = new string[] { "time", "app", "module", "message" };

		private static readonly string[] LOG_TAG = new string[] { "-", "-", "-", "-" };

		private int limitTagSize;

		private int count;

		private string logMsg;

		private bool show;

		protected internal readonly int[] logTypeStyle;

		protected internal int logType;

		public LogFormat(bool s, int t) : this(s, t, 25, 15, 7, 256, 64)
		{
		}

		public LogFormat(bool s, int t, int timeSize, int appSize, int moduleSize, int messageSize, int maxTagSize)
		{
			this.show = s;
			this.logType = t;
			this.limitTagSize = maxTagSize;
			this.logTypeStyle = new int[MESSAGE_INDEX + 1];
			logTypeStyle[TIME_INDEX] = timeSize;
			logTypeStyle[APP_INDEX] = appSize;
			logTypeStyle[MODULE_INDEX] = moduleSize;
			logTypeStyle[MESSAGE_INDEX] = messageSize;
		}

		private string FormatString(string[] str, string pad, string sp)
		{
			return FormatString(str, pad, sp, true);
		}

		private string FormatString(string[] str, string pad, string sp, bool tag)
		{
			System.Text.StringBuilder sbr = new System.Text.StringBuilder();
			if (tag)
			{
				for (int i = 0; i < str.Length; i++)
				{
					int size = str[i].Length;
					if (size > logTypeStyle[i] || size > limitTagSize)
					{
						sbr.Append(str[i].Substring(0, logTypeStyle[i]) + sp);
						continue;
					}
					sbr.Append(str[i]);
					for (int j = size; j < logTypeStyle[i] && j < limitTagSize; j++)
					{
						sbr.Append(pad);
					}
					sbr.Append(sp);
				}
			}
			else
			{
				for (int i = 0; i < str.Length; i++)
				{
					if (str[i].Length > logTypeStyle[i])
					{
						sbr.Append(str[i].Substring(0, logTypeStyle[i]) + sp);
						continue;
					}
					sbr.Append(str[i]);
					for (int j = str[i].Length; j < logTypeStyle[i]; j++)
					{
						sbr.Append(pad);
					}
					sbr.Append(sp);
				}
			}
			return sbr.ToString();
		}

		public virtual void Title(int flag, string msg)
		{
			lock (this)
			{
				switch (flag)
				{
					case 0:
						JavaSystem.Out.Println(msg);
						break;
					case 1:
						JavaSystem.Err.Println(msg);
						break;
				}
			}
		}

		public virtual void Out(string msg)
		{
			lock (this)
			{
				if (!show)
				{
					return;
				}
				Title(logType, msg);
			}
		}

		public virtual bool IsShow()
		{
				return show;
		}

		public virtual void SetShow(bool s)
		{
			this.show = s;
		}


		public int GetLimitTagSize()
		{
			return limitTagSize;
		}

		public void SetLimitTagSize(int tagSize)
		{
			this.limitTagSize = tagSize;
		}


		public virtual void Out(string tm, string app, string level, string msg)
		{
			lock (this)
			{
				string[] value = new string[] { tm, app, level, msg };
				if (count++ % 9999 == 0)
				{
					logMsg = (new System.Text.StringBuilder(FormatString(LOG_TAG, "-", " "))).Append(LSystem.LS).Append(FormatString(LOG_TITLE, " ", " ")).Append(LSystem.LS).Append(FormatString(LOG_TAG, "-", " ")).Append(LSystem.LS).Append(FormatString(value, " ", " ")).ToString();
				}
				else
				{
					logMsg = FormatString(value, " ", " ", false);
				}
				Out(logMsg);
			}
		}

	}
}

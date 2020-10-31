using java.lang;

namespace loon.utils
{
    public sealed class TimeUtils
{

		private const long nanosPerMilli = 1000000L;

		public enum Unit
		{
			NANOS, MICROS, MILLIS, SECONDS
		}

		public class TimeFormat
		{
			public const int UNDEFINED = 0, HH_MM_SS = 1, MM_SS_000 = 2, MM_SS_0 = 3, HH_MM_SS_000 = 4, DD_HH_MM = 5;

			private readonly int number;

			public TimeFormat(int num)
            {
				this.number = num;
            }

			public int GetNumber()
            {
				return this.number;
            }

		public string GetFormat()
		{
			switch (number)
			{
				default:
				case HH_MM_SS:
					return "{0}:{1}:{2}";
				case HH_MM_SS_000:
					return "{0}:{1}:{2}.{3}";
				case MM_SS_000:
					return "{0}:{1}.{2}";
				case MM_SS_0:
					return "{0}:{1}.{2}";
			}
		}
	}

	private TimeUtils()
	{
	}

	public static float CurrentNanos()
	{
		return CurrentMicros() * 1000f;
	}

	public static float CurrentMicros()
	{
		return CurrentMillis() * 1000f;
	}

	public static float CurrentMillis()
	{
		return CurrentSeconds() * 1000f;
	}

	public static float CurrentSeconds()
	{
		return GetSeconds(Millis());
	}

	public static float CurrentTime(Unit unit)
	{
		switch (unit)
		{
			case Unit.NANOS:
				return CurrentNanos();
			case Unit.MICROS:
				return CurrentMicros();
			case Unit.MILLIS:
				return CurrentMillis();
			default:
				return CurrentSeconds();
		}
	}

	public static float CurrentTime()
	{
		return CurrentTime(GetDefaultTimeUnit());
	}

	public static float Convert(float time, Unit source, Unit target)
	{
		if (source == target)
			return time;

		float factor = 1;

		if (source == Unit.SECONDS)
		{
			if (target == Unit.MILLIS)
				factor = 1000f;
			else if (target == Unit.MICROS)
				factor = 1000000f;
			else
				factor = 1000000000f;
		}
		else if (source == Unit.MILLIS)
		{
			if (target == Unit.SECONDS)
				factor = 1f / 1000f;
			else if (target == Unit.MICROS)
				factor = 1000f;
			else
				factor = 1000000f;
		}
		else if (source == Unit.MICROS)
		{
			if (target == Unit.SECONDS)
				factor = 1f / 1000000f;
			else if (target == Unit.MILLIS)
				factor = 1f / 1000f;
			else
				factor = 1000f;
		}
		else
		{
			if (target == Unit.SECONDS)
				factor = 1f / 1000000000f;
			else if (target == Unit.MILLIS)
				factor = 1f / 1000000f;
			else if (target == Unit.MICROS)
				factor = 1f / 1000f;
		}

		return time * factor;
	}

	public static Unit GetDefaultTimeUnit()
	{
		return Unit.SECONDS;
	}

	public static long NanoTime()
	{
		return JavaSystem.CurrentTimeMillis() * nanosPerMilli;
	}

	public static long Millis()
	{
		return JavaSystem.CurrentTimeMillis();
	}

	public static long NanosToMillis(long nanos)
	{
		return nanos / nanosPerMilli;
	}

	public static long MillisToNanos(long Millis)
	{
		return Millis * nanosPerMilli;
	}

	public  static long TimeSinceNanos(long prevTime)
	{
		return NanoTime() - prevTime;
	}

	public  static long TimeSinceMillis(long prevTime)
	{
		return Millis() - prevTime;
	}

	public  static string FormatTime(long time)
	{
		int steps = 0;
		while (time >= 1000)
		{
			time /= 1000;
			steps++;
		}
		return time + GetTimeUnit(steps);
	}

	private static string GetTimeUnit(int steps)
	{
		switch (steps)
		{
			case 0:
				return "ns";
			case 1:
				return "us";
			case 2:
				return "ms";
			case 3:
				return "s";
			case 4:
				return "m";
			case 5:
				return "h";
			case 6:
				return "days";
			case 7:
				return "months";
			case 8:
				return "years";
			default:
				return "d (WTF dude check you calculation!)";
		}
	}

	public static long GetUTC8Days()
	{
		return GetDays(Millis(), 8);
	}

	public static long GetUTCDays()
	{
		return GetDays(Millis(), 0);
	}

	public static long GetDays( long ms,  long offsetHour)
	{
		return (ms + (offsetHour * LSystem.HOUR)) / 1000 / 60 / 60 / 24 % 365;
	}

	public static long GetHours()
	{
		return GetHours(Millis());
	}

	public static long GetHours( long ms)
	{
		return ms / 1000 / 60 / 60 % 24;
	}

	public static long GetMinutes()
	{
		return GetMinutes(Millis());
	}

	public static long GetMinutes( long ms)
	{
		return ms / 1000 / 60 % 60;
	}

	public static long GetSeconds()
	{
		return GetSeconds(Millis());
	}

	public static long GetSeconds( long ms)
	{
		return ms / 1000 % 60;
	}

	public static long GetMilliSeconds( long ms)
	{
		return ms % 1000;
	}

	public static string FormatMillis(long val)
	{
		StrBuilder sbr = new StrBuilder(20);
		string sgn = "";
		if (val < 0)
		{
			sgn = "-";
		}
		val = MathUtils.Abs(val);
		FormatTime(sbr, sgn, 0, (val / 3600000));
		val %= 3600000;
		FormatTime(sbr, ":", 2, (val / 60000));
		val %= 60000;
		FormatTime(sbr, ":", 2, (val / 1000));
		return sbr.ToString();
	}

	private static void FormatTime(StrBuilder tag, string pfx, int dgt, long val)
	{
		tag.Append(pfx);
		if (dgt > 1)
		{
			int pad = (dgt - 1);
			for (long i = val; i > 9 && pad > 0; i /= 10)
			{
				pad--;
			}
			for (int j = 0; j < pad; j++)
			{
				tag.Append('0');
			}
		}
		tag.Append(val);
	}

	public static string MillisTime()
	{
		return FormatMillis(Millis());
	}

	public static string GetUTC8Time()
	{
		return GetUTCTime(8);
	}

	public static string GetUTCTime()
	{
		return GetUTCTime(0);
	}

	public static string GetUTCTime(long offsetHour)
	{
		return GetUTCTime(Millis(), offsetHour);
	}

	public static string GetUTCTime(long duration, long offsetHour)
	{
		return GetUTCTime(duration + (offsetHour * LSystem.HOUR), TimeFormat.HH_MM_SS);
	}

	private static string Zero(long v)
	{
		return MathUtils.AddZeros(v, 2);
	}

	public static string GetUTCTime(long duration, TimeFormat format)
	{
		long h = GetHours(duration);
		long m = GetMinutes(duration);
		long s = GetSeconds(duration);
		long ms = GetMilliSeconds(duration);
		switch (format.GetNumber())
		{
			case TimeFormat.HH_MM_SS:
				return StringUtils.Format(format.GetFormat(), Zero(h), Zero(m), Zero(s));
			case TimeFormat.HH_MM_SS_000:
				return StringUtils.Format(format.GetFormat(), Zero(h), Zero(m), Zero(s), ms);
			case TimeFormat.MM_SS_000:
				return StringUtils.Format(format.GetFormat(), Zero(m), Zero(s), ms);
			case TimeFormat.MM_SS_0:
				return StringUtils.Format(format.GetFormat(), Zero(m), Zero(s), ms / 100);
			case TimeFormat.UNDEFINED:
			default:
				return JavaSystem.Str(ms);
		}
	}

	public static  string FormatSeconds( int secondsTotal)
	{
		return FormatSeconds(secondsTotal, new StrBuilder());
	}

	public static  string FormatSeconds( int secondsTotal,  StrBuilder output)
	{
		 int second = 60;
		 int minutes = secondsTotal / second;
		 int seconds = secondsTotal % second;

		output.Append(minutes);
		output.Append(':');

		if (seconds < 10)
		{
			output.Append('0');
		}
		output.Append(seconds);

		return output.ToString();
	}
}
}

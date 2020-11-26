using System;

namespace loon.utils.timer
{
    public class Duration : IComparable<Duration>
    {

		protected internal static Duration _instance = null;

		public static void FreeStatic()
		{
			_instance = null;
		}

		public static Duration Get()
		{
			return GetInstance();
		}

		public static Duration GetInstance()
		{
				if (_instance == null)
				{
					lock (typeof(Duration))
					{
						if (_instance == null)
						{
							_instance = new Duration();
						}
					}
				}
				return _instance;
			
		}

		public static readonly Duration ZERO = new Duration(0);

		public static readonly Duration HALF_ONE = new Duration(0.5f);

		public static readonly Duration ONE = new Duration(1);

		public static readonly Duration ONE_SECOND = new Duration(1000);

		public static readonly Duration ONE_MINUTE = new Duration(60000);

		public static readonly Duration ONE_HOUR = new Duration(3600000);

		public static readonly Duration ONE_DAY = new Duration(86400000);

		public static Duration At(float ms)
		{
			return new Duration(ms);
		}

		public static Duration AtSecond(float sec)
		{
			return new Duration(sec * LSystem.SECOND);
		}

		public static Duration AtMinute(float min)
		{
			return new Duration(min * LSystem.MINUTE);
		}

		public static Duration AtHour(float hour)
		{
			return new Duration(hour * LSystem.HOUR);
		}

		public static Duration AtDay(float day)
		{
			return new Duration(day * LSystem.DAY);
		}

		private float _millisTime;

		public Duration() : this(0f)
		{
		}

		public Duration(float ms)
		{
			Set(ms);
		}

		public virtual Duration Set(float ms)
		{
			long year = 100 * LSystem.YEAR;
			if (ms < -year)
			{
				this._millisTime = -year;
			}
			else if (float.IsNaN(ms))
			{
				this._millisTime = 0;
			}
			else if (ms == NumberUtils.IntBitsToFloat(0x7f800000))
			{
				this._millisTime = 0;
			}
			else if (ms > year)
			{
				this._millisTime = year;
			}
			else
			{
				this._millisTime = ms;
			}
			return this;
		}

		public virtual LTimer ToTime()
		{
			return LTimer.At(this);
		}

		public virtual Calculator Calc()
		{
			return new Calculator(_millisTime);
		}

		public virtual Duration Add(float millis)
		{
			return Set(_millisTime + millis);
		}

		public virtual Duration Add(Duration other)
		{
			if (other == null)
			{
				return this;
			}
			return Set(_millisTime + other._millisTime);
		}

		public virtual Duration Sub(float millis)
		{
			return Set(_millisTime - millis);
		}

		public virtual Duration Sub(Duration other)
		{
			if (other == null)
			{
				return this;
			}
			return Set(_millisTime - other._millisTime);
		}

		public virtual Duration Mul(float millis)
		{
			return Set(_millisTime * millis);
		}

		public virtual Duration Mul(Duration other)
		{
			if (other == null)
			{
				return this;
			}
			return Set(_millisTime * other._millisTime);
		}

		public virtual Duration Div(float millis)
		{
			return Set(_millisTime / millis);
		}

		public virtual Duration Div(Duration other)
		{
			if (other == null)
			{
				return this;
			}
			return Set(_millisTime / other._millisTime);
		}

		public virtual bool LessThan(Duration other)
		{
			if (other == null)
			{
				return false;
			}
			return _millisTime < other._millisTime;
		}

		public virtual bool LessThanOrEquals(Duration other)
		{
			if (other == null)
			{
				return false;
			}
			return _millisTime <= other._millisTime;
		}

		public virtual bool LessEquals(Duration other)
		{
			if (other == null)
			{
				return false;
			}
			return _millisTime == other._millisTime;
		}

		public virtual bool GreaterThan(Duration other)
		{
			if (other == null)
			{
				return true;
			}
			return _millisTime > other._millisTime;
		}

		public virtual bool GreaterThanOrEquals(Duration other)
		{
			if (other == null)
			{
				return true;
			}
			return _millisTime >= other._millisTime;
		}

		public virtual Duration Negate()
		{
			return Set(-_millisTime);
		}

		public virtual Duration Millis(float ms)
		{
			return Set(ms);
		}

		public virtual float ToMillis()
		{
			return _millisTime;
		}

		public virtual long GetMillis()
		{
			
				return ToMillisLong();
			
		}

		public virtual long ToMillisLong()
		{
			int bit = MathUtils.GetFloatDotBackSize(_millisTime);
			long numBits = 0L;
			if (bit < 1)
			{
				numBits = 1L;
			}
			else if (bit < 2)
			{
				numBits = 10L;
			}
			else if (bit < 3)
			{
				numBits = 100L;
			}
			else if (bit < 4)
			{
				numBits = 1000L;
			}
			else if (bit < 5)
			{
				numBits = 10000L;
			}
			else if (bit < 6)
			{
				numBits = 100000L;
			}
			else if (bit < 7)
			{
				numBits = 1000000L;
			}
			else if (bit < 8)
			{
				numBits = 10000000L;
			}
			else if (bit < 9)
			{
				numBits = 100000000L;
			}
			else if (bit < 10)
			{
				numBits = 1000000000L;
			}
			else if (bit < 11)
			{
				numBits = 10000000000L;
			}
			return (long)(numBits * _millisTime);
		}

		public virtual Duration Seconds(float s)
		{
			return Set(s / (float)LSystem.SECOND);
		}

		public virtual float ToSeconds()
		{
			return _millisTime / (float)LSystem.SECOND;
		}

		public virtual Duration Minute(float m)
		{
			return Set(m / (float)LSystem.MINUTE);
		}

		public virtual float ToMinute()
		{
			return _millisTime / (float)LSystem.MINUTE;
		}

		public virtual Duration Hours(float h)
		{
			return Set(h / (float)LSystem.HOUR);
		}

		public virtual float ToHours()
		{
			return _millisTime / (float)LSystem.HOUR;
		}

		public virtual Duration Day(float d)
		{
			return Set(d / (float)LSystem.DAY);
		}

		public virtual float ToDay()
		{
			return _millisTime / (float)LSystem.DAY;
		}

		public virtual Duration Week(float w)
		{
			return Set(w / (float)LSystem.WEEK);
		}

		public virtual float ToWeek()
		{
			return _millisTime / (float)LSystem.WEEK;
		}

		public virtual Duration Year(float y)
		{
			return Set(y / (float)LSystem.YEAR);
		}

		public virtual float ToYear()
		{
			return _millisTime / (float)LSystem.YEAR;
		}

		public virtual string FormatTime(string format)
		{
			return FormatTime(this, format);
		}

		public virtual string FormatTime()
		{
			return FormatTime(":");
		}

		protected internal static string FormatTime(Duration timer, string format)
		{
			string str = LSystem.EMPTY;
			int minute = MathUtils.Floor(timer.ToMinute());
			if (minute < 10)
			{
				str = "0" + minute;
			}
			else
			{
				str = LSystem.EMPTY + minute;
			}
			str += format;
			int second = MathUtils.Floor(timer.ToSeconds() % 60f);
			if (second < 10)
			{
				str += "0" + second;
			}
			else
			{
				str += LSystem.EMPTY + second;
			}
			return str;
		}

		public override bool Equals(object obj)
		{
			if (obj == null)
			{
				return false;
			}
			if (!(obj is Duration))
			{
				return false;
			}
			if (obj == this)
			{
				return true;
			}
			return ((Duration)obj)._millisTime == this._millisTime;
		}

		public virtual int CompareTo(Duration o)
		{
			if (o == null)
			{
				return 1;
			}
			return NumberUtils.Compare(_millisTime, o._millisTime);
		}

		public override int GetHashCode()
		{
			int hashCode = 1;
			hashCode = LSystem.Unite(hashCode, _millisTime);
			return hashCode;
		}

		public override string ToString()
		{
			return MathUtils.ToString(_millisTime, true) + " ms";
		}
	}
}

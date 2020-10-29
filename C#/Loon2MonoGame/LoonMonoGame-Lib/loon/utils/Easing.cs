namespace loon.utils
{
    public class Easing
    {
        public enum EasingMode
        {
            InQuad, OutQuad, InOutQuad, InCubic, OutCubic, InOutCubic, InQuart, OutQuart, InOutQuart, InQuint, OutQuint, InOutQuint, InSine, OutSine, InOutSine, InExp, OutExp, InOutExp, InCirc, OutCirc, InOutCirc, InBack, OutBack, InOutBack, OutBounce, InBounce, InOutBounce, Linear
        }


        public static readonly Easing NONE = new Easing("NONE", TYPE_IN,
                FUNCTION_LINEAR);

        public static readonly Easing ELASTIC_INOUT = new Easing("ELASTIC_INOUT",
                TYPE_IN_OUT, FUNCTION_ELASTIC);

        public static readonly Easing QUAD_INOUT = new Easing("QUAD_INOUT",
                TYPE_IN_OUT, FUNCTION_LINEAR);

        public static readonly Easing REGULAR_IN = new Easing("REGULAR_IN", TYPE_IN,
                FUNCTION_QUADRADIC);

        public static readonly Easing REGULAR_OUT = new Easing("REGULAR_OUT",
                TYPE_OUT, FUNCTION_QUADRADIC);

        public static readonly Easing REGULAR_IN_OUT = new Easing("REGULAR_IN_OUT",
                TYPE_IN_OUT, FUNCTION_QUADRADIC);

        public static readonly Easing STRONG_IN = new Easing("STRONG_IN", TYPE_IN,
                FUNCTION_QUINTIC);

        public static readonly Easing STRONG_OUT = new Easing("STRONG_OUT", TYPE_OUT,
                FUNCTION_QUINTIC);

        public static readonly Easing STRONG_IN_OUT = new Easing("STRONG_IN_OUT",
                TYPE_IN_OUT, FUNCTION_QUINTIC);

        public static readonly Easing BACK_IN = new Easing("BACK_IN", TYPE_IN,
                FUNCTION_BACK);

        public static readonly Easing BACK_OUT = new Easing("BACK_OUT", TYPE_OUT,
                FUNCTION_BACK);

        public static readonly Easing CUBIC_IN = new Easing("CUBIC_IN", TYPE_IN,
                FUNCTION_IN);

        public static readonly Easing CUBIC_OUT = new Easing("CUBIC_OUT", TYPE_OUT,
                FUNCTION_OUT);

        public static readonly Easing CUBIC_INOUT = new Easing("CUBIC_INOUT",
                TYPE_OUT, FUNCTION_INOUT);

        public static readonly Easing BOUNCE_IN = new Easing("BOUNCE_IN", TYPE_OUT,
                FUNCTION_BOUNCE_OUT);

        public static readonly Easing BOUNCE_INOUT = new Easing("BOUNCE_INOUT",
                TYPE_IN_OUT, FUNCTION_BOUNCE_OUT);

        public static readonly Easing BACK_IN_OUT = new Easing("BACK_IN_OUT",
                TYPE_IN_OUT, FUNCTION_BACK);

        public static readonly Easing ELASTIC_IN = new Easing("ELASTIC_IN", TYPE_IN,
                FUNCTION_ELASTIC);

        public static readonly Easing ELASTIC_OUT = new Easing("ELASTIC_OUT",
                TYPE_OUT, FUNCTION_ELASTIC);

        public static readonly Easing ELASTIC_IN_OUT = new Easing("ELASTIC_IN_OUT",
                TYPE_IN_OUT, FUNCTION_ELASTIC);

        public static readonly Easing TIME_NONE = new Easing("TIME_NONE", TYPE_TIME,
                FUNCTION_NONE);

        public static readonly Easing TIME_LINEAR = new Easing("TIME_LINEAR",
                TYPE_TIME, FUNCTION_LINEAR);

        public static readonly Easing TIME_EASE_IN = new Easing("TIME_EASE_IN",
                TYPE_TIME, FUNCTION_IN);

        public static readonly Easing TIME_EASE_OUT = new Easing("TIME_EASE_OUT",
                TYPE_TIME, FUNCTION_OUT);

        public static readonly Easing TIME_EASE_INOUT = new Easing("TIME_EASE_INOUT",
                TYPE_TIME, FUNCTION_INOUT);

        public static readonly Easing TIME_EASE_IN_BACK = new Easing(
			"TIME_EASE_IN_BACK", TYPE_TIME, FUNCTION_IN_BACK);

        public static readonly Easing TIME_EASE_OUT_BACK = new Easing(
			"TIME_EASE_OUT_BACK", TYPE_TIME, FUNCTION_OUT_BACK);

        public static readonly Easing TIME_BOUNCE_OUT = new Easing("TIME_BOUNCE_OUT",
                TYPE_TIME, FUNCTION_BOUNCE_OUT);

        public static readonly Easing TIME_EASE_OUT_ELASTIC = new Easing(
			"TIME_EASE_OUT_ELASTIC", TYPE_TIME, FUNCTION_OUT_ELASTIC);


        public static EasingMode ToEasingMode(string name)
        {
            string key = name == null ? "Linear" : name.Trim();
            if ("InQuad".Equals(key))
            {
                return EasingMode.InQuad;
            }
            else if ("OutQuad".Equals(key))
            {
                return EasingMode.OutQuad;
            }
            else if ("InOutQuad".Equals(key))
            {
                return EasingMode.InOutQuad;
            }
            else if ("InCubic".Equals(key))
            {
                return EasingMode.InCubic;
            }
            else if ("OutCubic".Equals(key))
            {
                return EasingMode.OutCubic;
            }
            else if ("InOutCubic".Equals(key))
            {
                return EasingMode.InOutCubic;
            }
            else if ("InQuart".Equals(key))
            {
                return EasingMode.InQuart;
            }
            else if ("OutQuart".Equals(key))
            {
                return EasingMode.OutQuart;
            }
            else if ("InOutQuart".Equals(key))
            {
                return EasingMode.InOutQuart;
            }
            else if ("InQuint".Equals(key))
            {
                return EasingMode.InQuint;
            }
            else if ("OutQuint".Equals(key))
            {
                return EasingMode.OutQuint;
            }
            else if ("InOutQuint".Equals(key))
            {
                return EasingMode.InOutQuint;
            }
            else if ("InSine".Equals(key))
            {
                return EasingMode.InSine;
            }
            else if ("OutSine".Equals(key))
            {
                return EasingMode.OutSine;
            }
            else if ("InOutSine".Equals(key))
            {
                return EasingMode.InOutSine;
            }
            else if ("InExp".Equals(key))
            {
                return EasingMode.InExp;
            }
            else if ("OutExp".Equals(key))
            {
                return EasingMode.OutExp;
            }
            else if ("InOutExp".Equals(key))
            {
                return EasingMode.InOutExp;
            }
            else if ("InCirc".Equals(key))
            {
                return EasingMode.InCirc;
            }
            else if ("OutCirc".Equals(key))
            {
                return EasingMode.OutCirc;
            }
            else if ("InOutCirc".Equals(key))
            {
                return EasingMode.InOutCirc;
            }
            else if ("InBack".Equals(key))
            {
                return EasingMode.InBack;
            }
            else if ("OutBack".Equals(key))
            {
                return EasingMode.OutBack;
            }
            else if ("InOutBack".Equals(key))
            {
                return EasingMode.InOutBack;
            }
            else if ("OutBounce".Equals(key))
            {
                return EasingMode.OutBounce;
            }
            else if ("InBounce".Equals(key))
            {
                return EasingMode.InBounce;
            }
            else if ("InOutBounce".Equals(key))
            {
                return EasingMode.InOutBounce;
            }
            else
            {
                return EasingMode.Linear;
            }
        }


        private const int TYPE_IN = 0;

        private const int TYPE_OUT = 1;

        private const int TYPE_IN_OUT = 2;

        private const int TYPE_TIME = 3;

        private const int FUNCTION_LINEAR = 0;

        private const int FUNCTION_QUADRADIC = 1;

        private const int FUNCTION_IN = 2;

        private const int FUNCTION_QUARTIC = 3;

        private const int FUNCTION_QUINTIC = 4;

        private const int FUNCTION_BACK = 5;

        private const int FUNCTION_ELASTIC = 6;

        private const int FUNCTION_NONE = 7;

        private const int FUNCTION_OUT = 8;

        private const int FUNCTION_INOUT = 9;

        private const int FUNCTION_IN_BACK = 10;

        private const int FUNCTION_OUT_BACK = 11;

        private const int FUNCTION_BOUNCE_OUT = 12;

        private const int FUNCTION_OUT_ELASTIC = 13;

        private readonly int type;

        private readonly int function;

        private readonly float strength;

        private readonly string name;

        protected Easing() : this("NONE")
        {

        }

        protected Easing(string name) : this(name, TYPE_IN, FUNCTION_LINEAR)
        {

        }

        protected Easing(string name, int type) : this(name, type, FUNCTION_LINEAR)
        {

        }

        private Easing(string name, int type, int function) : this(name, type, function, 1)
        {

        }

        private Easing(string name, int type, int function, float stength)
        {
            this.name = name;
            this.type = type;
            this.function = function;
            this.strength = stength;
        }

        public Easing(Easing easing, float strength) : this(easing.name, easing.type, easing.function, strength)
        {

        }

		public float Apply(float time, float duration)
		{
			return Apply(time, duration, true);
		}

		public float Apply(float time, float duration, bool mul)
		{
			if (TYPE_TIME == type)
			{
				return Call(function, duration / time);
			}
			if (time <= 0 || duration <= 0)
			{
				return 0;
			}
			else if (time >= duration)
			{
				return duration;
			}

			float t = time / duration;

			float easedT;

			switch (type)
			{
				default:
					easedT = t;
					break;

				case TYPE_IN:
					easedT = Call(function, t);
					break;

				case TYPE_OUT:
					easedT = 1 - Call(function, 1 - t);
					break;

				case TYPE_IN_OUT:
					if (t < 0.5)
					{
						easedT = Call(function, 2 * t) / 2;
					}
					else
					{
						easedT = 1 - Call(function, 2 - 2 * t) / 2;
					}
					break;
			}
			if (strength != 1)
			{
				easedT = strength * easedT + (1 - strength) * t;
			}
			if (mul)
			{
				return (easedT * duration);
			}
			return easedT;
		}

		public float ApplyClamp(float time, float duration)
		{
			return Apply((time < 0) ? 0 : (time > 1 ? 1 : time), duration);
		}

		public float Apply(float start, float range, float time, float duration)
		{
			float pos = (duration == 0) ? 1 : Apply(time, duration);
			return start + range * pos;
		}

		public float ApplyClamp(float start, float range, float time, float duration)
		{
			return Apply(start, range, duration, MathUtils.Clamp(time, 0, duration));
		}

		protected static float Call(int fun, float t)
		{

			float t2;
			float t3;

			switch (fun)
			{

				default:
				case FUNCTION_LINEAR:
					return t;

				case FUNCTION_QUADRADIC:
					return t * t;

				case FUNCTION_IN:
					return t * t * t;

				case FUNCTION_QUARTIC:
					t2 = t * t;
					return t2 * t2;

				case FUNCTION_QUINTIC:
					t2 = t * t;
					return t2 * t2 * t;

				case FUNCTION_BACK:
					t2 = t * t;
					t3 = t2 * t;
					return t3 + t2 - t;

				case FUNCTION_ELASTIC:
					t2 = t * t;
					t3 = t2 * t;

					float scale = t2 * (2 * t3 + t2 - 4 * t + 2);
					float wave = -MathUtils.Sin(t * 3.5f * MathUtils.PI);

					return scale * wave;
				case FUNCTION_NONE:
					return 0;
				case FUNCTION_OUT:
					t2 = t - 1;
					return (1 + t2 * t2 * t2);
				case FUNCTION_INOUT:
					t2 = 2 * t;
					if (t2 < 1)
					{
						return (t2 * t2 * t2) / 2;
					}
					t3 = t2 - 2;
					return (2 + t3 * t3 * t3) / 2;
				case FUNCTION_IN_BACK:
					t2 = 1.70158f;
					return t * t * ((t2 + 1) * t - t2);
				case FUNCTION_OUT_BACK:
					t2 = 1.70158f;
					t3 = t - 1;
					return (t3 * t3 * ((t2 + 1) * t3 + t2) + 1);
				case FUNCTION_BOUNCE_OUT:
					if (t < (1 / 2.75f))
					{
						return 7.5625f * t * t;
					}
					else if (t < (2 / 2.75f))
					{
						t2 = t - (1.5f / 2.75f);
						return 7.5625f * t2 * t2 + 0.75f;
					}
					else if (t < (2.5 / 2.75))
					{
						t2 = t - (2.25f / 2.75f);
						return 7.5625f * t2 * t2 + 0.9375f;
					}
					else
					{
						t2 = t - (2.625f / 2.75f);
						return 7.5625f * t2 * t2 + 0.984375f;
					}
				case FUNCTION_OUT_ELASTIC:
					t2 = 0.3f / 4;
					t3 = (float)(2 * MathUtils.PI / 0.3);
					return MathUtils.Pow(2, -10 * t) * MathUtils.Sin((t - t2) * t3) + 1;
			}
		}

		public static float InQuad(float t, float totaltime, float max, float min)
		{
			max -= min;
			t /= totaltime;
			return max * t * t + min;
		}

		public static float OutQuad(float t, float totaltime, float max, float min)
		{
			max -= min;
			t /= totaltime;
			return -max * t * (t - 2.0f) + min;
		}

		public static float InOutQuad(float t, float totaltime, float max, float min)
		{
			max -= min;
			t /= totaltime;
			if (t / 2.0f < 1.0f)
			{
				return max / 2.0f * t * t + min;
			}
			t -= 1.0f;
			return -max * (t * (t - 2.0f) - 1.0f) + min;
		}

		public static float InCubic(float t, float totaltime, float max, float min)
		{
			max -= min;
			t /= totaltime;
			return max * t * t * t + min;
		}

		public static float OutCubic(float t, float totaltime, float max, float min)
		{
			max -= min;
			t = t / totaltime - 1.0f;
			return max * (t * t * t + 1.0f) + min;
		}

		public static float InOutCubic(float t, float totaltime, float max,
				float min)
		{
			max -= min;
			t /= totaltime;
			if (t / 2.0f < 1.0f)
			{
				return max / 2.0f * t * t * t + min;
			}
			t -= 2.0f;
			return max / 2.0f * (t * t * t + 2.0f) + min;
		}

		public static float InQuart(float t, float totaltime, float max, float min)
		{
			max -= min;
			t /= totaltime;
			return max * t * t * t * t + min;
		}

		public static float OutQuart(float t, float totaltime, float max, float min)
		{
			max -= min;
			t = t / totaltime - 1.0f;
			return -max * (t * t * t * t - 1.0f) + min;
		}

		public static float InOutQuart(float t, float totaltime, float max,
				float min)
		{
			max -= min;
			t /= totaltime;
			if (t / 2.0f < 1.0f)
			{
				return max / 2.0f * t * t * t * t + min;
			}
			t -= 2.0f;
			return -max / 2.0f * (t * t * t * t - 2.0f) + min;
		}

		public static float InQuint(float t, float totaltime, float max, float min)
		{
			max -= min;
			t /= totaltime;
			return max * t * t * t * t * t + min;
		}

		public static float OutQuint(float t, float totaltime, float max, float min)
		{
			max -= min;
			t = t / totaltime - 1.0f;
			return max * (t * t * t * t * t + 1.0f) + min;
		}

		public static float InOutQuint(float t, float totaltime, float max,
				float min)
		{
			max -= min;
			t /= totaltime;
			if (t / 2.0f < 1.0f)
			{
				return max / 2.0f * t * t * t * t * t + min;
			}
			t -= 2.0f;
			return max / 2.0f * (t * t * t * t * t + 2.0f) + min;
		}

		public static float InSine(float t, float totaltime, float max, float min)
		{
			max -= min;
			return -max * MathUtils.Cos(t * 1.570796326794897f / totaltime) + max
					+ min;
		}

		public static float OutSine(float t, float totaltime, float max, float min)
		{
			max -= min;
			return max * MathUtils.Sin(t * 1.570796326794897f / totaltime) + min;
		}

		public static float InOutSine(float t, float totaltime, float max, float min)
		{
			max -= min;
			return -max / 2.0f
					* (MathUtils.Cos(t * 3.141592653589793f / totaltime) - 1.0f)
					+ min;
		}

		public static float InExp(float t, float totaltime, float max, float min)
		{
			max -= min;
			return t == 0.0f ? min : max
					* MathUtils.Pow(2.0f, 10.0f * (t / totaltime - 1.0f)) + min;
		}

		public static float OutExp(float t, float totaltime, float max, float min)
		{
			max -= min;
			return t == totaltime ? max + min : max
					* (-MathUtils.Pow(2.0f, -10.0f * t / totaltime) + 1.0f) + min;
		}

		public static float InOutExp(float t, float totaltime, float max, float min)
		{
			if (t == 0f)
			{
				return min;
			}
			if (t == totaltime)
			{
				return max;
			}
			max -= min;
			t /= totaltime;
			if (t / 2.0f < 1.0f)
			{
				return max / 2.0f * MathUtils.Pow(2.0f, 10.0f * (t - 1.0f)) + min;
			}
			t -= 1.0f;
			return max / 2.0f * (-MathUtils.Pow(2.0f, -10.0f * t) + 2.0f) + min;
		}

		public static float InCirc(float t, float totaltime, float max, float min)
		{
			max -= min;
			t /= totaltime;
			return -max * (MathUtils.Sqrt(1.0f - t * t) - 1.0f) + min;
		}

		public static float OutCirc(float t, float totaltime, float max, float min)
		{
			max -= min;
			t = t / totaltime - 1.0f;
			return max * MathUtils.Sqrt(1.0f - t * t) + min;
		}

		public static float InOutCirc(float t, float totaltime, float max, float min)
		{
			max -= min;
			t /= totaltime;
			if (t / 2.0f < 1.0f)
			{
				return -max / 2.0f * (MathUtils.Sqrt(1.0f - t * t) - 1.0f) + min;
			}
			t -= 2.0f;
			return max / 2.0f * (MathUtils.Sqrt(1.0f - t * t) + 1.0f) + min;
		}

		public static float InBack(float t, float totaltime, float max, float min,
				float s)
		{
			max -= min;
			t /= totaltime;
			return max * t * t * ((s + 1.0f) * t - s) + min;
		}

		public static float OutBack(float t, float totaltime, float max, float min,
				float s)
		{
			max -= min;
			t = t / totaltime - 1.0f;
			return max * (t * t * ((s + 1.0f) * t * s) + 1.0f) + min;
		}

		public static float InOutBack(float t, float totaltime, float max,
				float min, float s)
		{
			max -= min;
			s *= 1.525f;
			if (t / 2.0f < 1.0f)
			{
				return max * (t * t * ((s + 1.0f) * t - s)) + min;
			}
			t -= 2.0f;
			return max / 2.0f * (t * t * ((s + 1.0f) * t + s) + 2.0f) + min;
		}

		public static float OutBounce(float t, float totaltime, float max, float min)
		{
			max -= min;
			t /= totaltime;
			if (t < 0.3636363636363637f)
			{
				return max * (7.5625f * t * t) + min;
			}
			if (t < 0.7272727272727273f)
			{
				t -= 0.5454545454545454f;
				return max * (7.5625f * t * t + 0.75f) + min;
			}
			if (t < 0.9090909090909091f)
			{
				t -= 0.8181818181818182f;
				return max * (7.5625f * t * t + 0.9375f) + min;
			}
			t -= 0.9545454545454546f;
			return max * (7.5625f * t * t + 0.984375f) + min;
		}

		public static float InBounce(float t, float totaltime, float max, float min)
		{
			return max - OutBounce(totaltime - t, totaltime, max - min, 0.0f) + min;
		}

		public static float InOutBounce(float t, float totaltime, float max,
				float min)
		{
			if (t < totaltime / 2.0f)
			{
				return InBounce(t * 2.0f, totaltime, max - min, max) * 0.5f + min;
			}
			return OutBounce(t * 2.0f - totaltime, totaltime, max - min, 0.0f)
					* 0.5f + min + (max - min) * 0.5f;
		}

		public static float Linear(float t, float totaltime, float max, float min)
		{
			return (max - min) * t / totaltime + min;
		}
	}
}

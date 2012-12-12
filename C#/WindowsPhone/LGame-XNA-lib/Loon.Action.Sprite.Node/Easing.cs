using Loon.Utils;
namespace Loon.Action.Sprite.Node {
	
	public class Easing {
	
		private const int TYPE_IN = 0;
	
		private const int TYPE_OUT = 1;
	
		private const int TYPE_IN_OUT = 2;
	
		private const int FUNCTION_LINEAR = 0;
	
		private const int FUNCTION_QUADRADIC = 1;
	
		private const int FUNCTION_CUBIC = 2;
	
		private const int FUNCTION_QUARTIC = 3;
	
		private const int FUNCTION_QUINTIC = 4;
	
		private const int FUNCTION_BACK = 5;
	
		private const int FUNCTION_ELASTIC = 6;
	
		public static readonly Easing NONE = new Easing(TYPE_IN, FUNCTION_LINEAR);
	
		public static readonly Easing REGULAR_IN = new Easing(TYPE_IN,
				FUNCTION_QUADRADIC);
	
		public static readonly Easing REGULAR_OUT = new Easing(TYPE_OUT,
				FUNCTION_QUADRADIC);
	
		public static readonly Easing REGULAR_IN_OUT = new Easing(TYPE_IN_OUT,
				FUNCTION_QUADRADIC);
	
		public static readonly Easing STRONG_IN = new Easing(TYPE_IN, FUNCTION_QUINTIC);
	
		public static readonly Easing STRONG_OUT = new Easing(TYPE_OUT,
				FUNCTION_QUINTIC);
	
		public static readonly Easing STRONG_IN_OUT = new Easing(TYPE_IN_OUT,
				FUNCTION_QUINTIC);
	
		public static readonly Easing BACK_IN = new Easing(TYPE_IN, FUNCTION_BACK);
	
		public static readonly Easing BACK_OUT = new Easing(TYPE_OUT, FUNCTION_BACK);
	
		public static readonly Easing BACK_IN_OUT = new Easing(TYPE_IN_OUT,
				FUNCTION_BACK);
	
		public static readonly Easing ELASTIC_IN = new Easing(TYPE_IN,
				FUNCTION_ELASTIC);
	
		public static readonly Easing ELASTIC_OUT = new Easing(TYPE_OUT,
				FUNCTION_ELASTIC);
	
		public static readonly Easing ELASTIC_IN_OUT = new Easing(TYPE_IN_OUT,
				FUNCTION_ELASTIC);
	
		private readonly int type;
	
		private readonly int function;
	
		private readonly float strength;
	
		protected internal Easing():this(TYPE_IN, FUNCTION_LINEAR) {
			
		}
	
		protected internal Easing(int type_0):this(type_0, FUNCTION_LINEAR) {
			
		}
	
		private Easing(int type_0, int function_1):this(type_0, function_1, 1) {
			
		}
	
		private Easing(int type_0, int function_1, float stength) {
			this.type = type_0;
			this.function = function_1;
			this.strength = (float) stength;
		}

        public Easing(Easing easing, float strength_0)
            : this(easing.type, easing.function, strength_0)
        {

        }
	
		public float Ease(float time, float duration) {
			if (time <= 0 || duration <= 0) {
				return 0;
			} else if (time >= duration) {
				return duration;
			}
	
			float t = time / duration;
	
			float easedT;
	
			switch (type) {
	
			default:
				easedT = t;
				break;
	
			case TYPE_IN:
				easedT = Ease(t);
				break;
	
			case TYPE_OUT:
				easedT = 1 - Ease(1 - t);
				break;
	
			case TYPE_IN_OUT:
				if (t < 0.5d) {
					easedT = Ease(2 * t) / 2;
				} else {
					easedT = 1 - Ease(2 - 2 * t) / 2;
				}
				break;
			}
			if (strength != 1) {
				easedT = strength * easedT + (1 - strength) * t;
			}
			return (easedT * duration);
		}
	
		protected internal float Ease(float t) {
	
			float t2;
			float t3;
	
			switch (function) {
	
			default:
			case FUNCTION_LINEAR:
				return t;
	
			case FUNCTION_QUADRADIC:
				return t * t;
	
			case FUNCTION_CUBIC:
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
			}
		}
	}
}

using java.lang;
using loon.canvas;
using loon.events;
using loon.geom;
using loon.utils.reply;
using System;

namespace loon.utils
{
    public class HelperUtils
    {
		public static bool Contains<T>(T key, TArray<T> list)
		{
			foreach (T o in list)
			{
				if (key == null && o == null)
				{
					return true;
				}
				if ((object)o == (object)key || (o != null && o.Equals(key)))
				{
					return true;
				}
			}
			return false;
		}

		public static bool Contains(object key, params object[] objs)
		{
			foreach (object o in objs)
			{
				if (key == null && o == null)
				{
					return true;
				}
				if (o == key || (o != null && o.Equals(key)))
				{
					return true;
				}
			}
			return false;
		}

		private static readonly Affine2f _trans = new Affine2f();

		public static Vector2f Local2Global(float centerX, float centerY, float posX, float posY, Vector2f resultPoint)
		{
			return Local2Global(0, 1f, 1f, 0, 0, false, false, centerX, centerY, posX, posY, resultPoint);
		}

		public static Vector2f Local2Global(bool flipX, bool flipY, float centerX, float centerY, float posX, float posY, Vector2f resultPoint)
		{
			return Local2Global(0, 1f, 1f, 0, 0, flipX, flipY, centerX, centerY, posX, posY, resultPoint);
		}

		public static Vector2f Local2Global(float rotation, float centerX, float centerY, float posX, float posY, Vector2f resultPoint)
		{
			return Local2Global(rotation, 1f, 1f, 0, 0, false, false, centerX, centerY, posX, posY, resultPoint);
		}

		public static Vector2f Local2Global(float rotation, bool flipX, bool flipY, float centerX, float centerY, float posX, float posY, Vector2f resultPoint)
		{
			return Local2Global(rotation, 1f, 1f, 0, 0, flipX, flipY, centerX, centerY, posX, posY, resultPoint);
		}

		public static Vector2f Local2Global(float rotation, float scaleX, float scaleY, bool flipX, bool flipY, float centerX, float centerY, float posX, float posY, Vector2f resultPoint)
		{
			return Local2Global(rotation, scaleX, scaleY, 0, 0, flipX, flipY, centerX, centerY, posX, posY, resultPoint);
		}

		public static Vector2f Local2Global(float rotation, float scaleX, float scaleY, float skewX, float skewY, bool flipX, bool flipY, float centerX, float centerY, float posX, float posY, Vector2f resultPoint)
		{
			_trans.Idt();
			if (rotation != 0)
			{
				_trans.Translate(centerX, centerY);
				_trans.PreRotate(rotation);
				_trans.Translate(-centerX, -centerY);
			}
			if (flipX || flipY)
			{
				if (flipX && flipY)
				{
					Affine2f.Transform(_trans, centerX, centerY, Affine2f.TRANS_ROT180);
				}
				else if (flipX)
				{
					Affine2f.Transform(_trans, centerX, centerY, Affine2f.TRANS_MIRROR);
				}
				else if (flipY)
				{
					Affine2f.Transform(_trans, centerX, centerY, Affine2f.TRANS_MIRROR_ROT180);
				}
			}
			if ((scaleX != 1) || (scaleY != 1))
			{
				_trans.Translate(centerX, centerY);
				_trans.PreScale(scaleX, scaleY);
				_trans.Translate(-centerX, -centerY);
			}
			if ((skewX != 0) || (skewY != 0))
			{
				_trans.Translate(centerX, centerY);
				_trans.PreShear(skewX, skewY);
				_trans.Translate(-centerX, -centerY);
			}
			if (resultPoint != null)
			{
				_trans.TransformPoint(posX, posY, resultPoint);
				return resultPoint;
			}
			return resultPoint;
		}

		public static float Random()
		{
			return MathUtils.Random();
		}

		public static float Random(float start, float end)
		{
			return MathUtils.Random(start, end);
		}

		public static int Random(int start, int end)
		{
			return MathUtils.Random(start, end);
		}

		public static Calculator NewCalculator()
		{
			return new Calculator();
		}

		public static Counter NewCounter()
		{
			return new Counter();
		}

		public static LimitedCounter NewLimitedCounter(int limit)
		{
			return new LimitedCounter(limit);
		}

		public static ActionCounter NewActionCounter(int limit, Updateable update)
		{
			return new ActionCounter(limit, update);
		}

		public static double ToDouble(object o)
		{
			if (o == null)
			{
				return -1d;
			}
			if (o is short?)
			{
				return ((short?)o).Value;
			}
			if (o is int?)
			{
				return ((int?)o).Value;
			}
			if (o is long?)
			{
				return ((long?)o).Value;
			}
			if (o is float?)
			{
				return ((float?)o).Value;
			}
			if (o is double?)
			{
				return ((double?)o).Value;
			}
			if (o is Number)
			{
				return ((Number)o).DoubleValue();
			}
			if (o is bool?)
			{
				return ((bool?)o).Value ? 1 : 0;
			}
			if (o is char?)
			{
				char? v = (char?)o;
				char vc = v.Value;
				string ns = vc.ToString();
				if (MathUtils.IsNan(ns))
				{
					return Convert.ToDouble(ns);
				}
			}
			if (o is string)
			{
				string v = (string)o;
				if (MathUtils.IsNan(v))
				{
					return Convert.ToDouble(v);
				}
			}
			return -1d;
		}

		public static float ToFloat(object o)
		{
			if (o == null)
			{
				return -1f;
			}
			if (o is short?)
			{
				return ((short?)o).Value;
			}
			if (o is int?)
			{
				return ((int?)o).Value;
			}
			if (o is long?)
			{
				return ((long?)o).Value;
			}
			if (o is float?)
			{
				return ((float?)o).Value;
			}
			if (o is double?)
			{
				return (float)((double?)o).Value;
			}
			if (o is Number)
			{
				return ((Number)o).FloatValue();
			}
			if (o is bool?)
			{
				return ((bool?)o).Value ? 1 : 0;
			}
			if (o is char?)
			{
				char? v = (char?)o;
				char vc = v.Value;
				string ns = vc.ToString();
				if (MathUtils.IsNan(ns))
				{
					return Convert.ToSingle(ns);
				}
			}
			if (o is string)
			{
				string v = (string)o;
				if (MathUtils.IsNan(v))
				{
					return Convert.ToSingle(v);
				}
			}
			return -1f;
		}

		public static int ToInt(object o)
		{
			if (o == null)
			{
				return -1;
			}
			if (o is short?)
			{
				return ((short?)o).Value;
			}
			if (o is int?)
			{
				return ((int?)o).Value;
			}
			if (o is long?)
			{
				return (int)((long?)o).Value;
			}
			if (o is float?)
			{
				return (int)((float?)o).Value;
			}
			if (o is double?)
			{
				return (int)((double?)o).Value;
			}
			if (o is Number)
			{
				return ((Number)o).IntValue();
			}
			if (o is bool?)
			{
				return ((bool?)o).Value ? 1 : 0;
			}
			if (o is char?)
			{
				char? v = (char?)o;
				char vc = v.Value;
				string ns = vc.ToString();
				if (MathUtils.IsNan(ns))
				{
					return (int)Convert.ToSingle(ns);
				}
			}
			if (o is string)
			{
				string v = (string)o;
				if (MathUtils.IsNan(v))
				{
					return (int)Convert.ToSingle(v);
				}
			}
			return -1;
		}

		public static long ToLong(object o)
		{
			if (o == null)
			{
				return -1L;
			}
			if (o is short?)
			{
				return ((short?)o).Value;
			}
			if (o is int?)
			{
				return ((int?)o).Value;
			}
			if (o is long?)
			{
				return ((long?)o).Value;
			}
			if (o is float?)
			{
				return (long)((float?)o).Value;
			}
			if (o is double?)
			{
				return (long)((double?)o).Value;
			}
			if (o is Number)
			{
				return ((Number)o).LongValue();
			}
			if (o is bool?)
			{
				return ((bool?)o).Value ? 1 : 0;
			}
			if (o is char?)
			{
				char? v = (char?)o;
				char vc = v.Value;
				string ns = vc.ToString();
				if (MathUtils.IsNan(ns))
				{
					return (long)Convert.ToDouble(ns);
				}
			}
			if (o is string)
			{
				string v = (string)o;
				if (MathUtils.IsNan(v))
				{
					return (long)Convert.ToDouble(v);
				}
			}
			return -1;
		}

		public static string ToStr(object o)
		{
			if (o == null)
			{
				return LSystem.NULL;
			}
			if (o is short?)
			{
				return (((short?)o).Value).ToString();
			}
			if (o is int?)
			{
				return (((int?)o).Value).ToString();
			}
			if (o is long?)
			{
				return (((long?)o).Value).ToString();
			}
			if (o is float?)
			{
				return (((float?)o).Value).ToString();
			}
			if (o is double?)
			{
				return (((double?)o).Value).ToString();
			}
			if (o is Number)
			{
				return (((Number)o).FloatValue()).ToString();
			}
			if (o is bool?)
			{
				return (((bool?)o).Value).ToString();
			}
			if (o is char?)
			{
				char? v = (char?)o;
				char vc = v.Value;
				return vc.ToString();
			}
			if (o is string)
			{
				string v = (string)o;
				if (MathUtils.IsNan(v))
				{
					if (v.IndexOf('.') != -1)
					{
						return (Convert.ToSingle(v)).ToString();
					}
					else
					{
						return ((int)Convert.ToSingle(v)).ToString();
					}
				}
				else
				{
					return v;
				}
			}
			return o.ToString();
		}

		public static void Debug(string msg)
		{
			LSystem.Debug(msg);
		}

		public static void Debug(string msg, params object[] args)
		{
			LSystem.Debug(msg, args);
		}

		public static void Debug(string msg, System.Exception throwable)
		{
			LSystem.Debug(msg, throwable);
		}

		public static void Info(string msg)
		{
			LSystem.Info(msg);
		}

		public static void Info(string msg, params object[] args)
		{
			LSystem.Info(msg, args);
		}

		public static void Info(string msg, System.Exception throwable)
		{
			LSystem.Info(msg, throwable);
		}

		public static void Error(string msg)
		{
			LSystem.Error(msg);
		}

		public static void Error(string msg, params object[] args)
		{
			LSystem.Error(msg, args);
		}

		public static void Error(string msg, System.Exception throwable)
		{
			LSystem.Error(msg, throwable);
		}

		public static void ReportError(string msg, System.Exception throwable)
		{
			LSystem.ReportError(msg, throwable);
		}

		public static void D(string msg)
		{
			LSystem.Debug(msg);
		}

		public static void D(string msg, params object[] args)
		{
			LSystem.Debug(msg, args);
		}

		public static void D(string msg, System.Exception throwable)
		{
			LSystem.Debug(msg, throwable);
		}

		public static void I(string msg)
		{
			LSystem.Info(msg);
		}

		public static void I(string msg, params object[] args)
		{
			LSystem.Info(msg, args);
		}

		public static void I(string msg, System.Exception throwable)
		{
			LSystem.Info(msg, throwable);
		}

		public static void W(string msg)
		{
			LSystem.Warn(msg);
		}

		public static void W(string msg, params object[] args)
		{
			LSystem.Warn(msg, args);
		}

		public static void W(string msg, System.Exception throwable)
		{
			LSystem.Warn(msg, throwable);
		}

		public static void E(string msg)
		{
			LSystem.Error(msg);
		}

		public static void E(string msg, params object[] args)
		{
			LSystem.Error(msg, args);
		}

		public static void E(string msg, System.Exception throwable)
		{
			LSystem.Error(msg, throwable);
		}

		public static Pair<T1, T2> ToPair<T1, T2>(T1 a, T2 b)
		{
			return Pair<T1, T2>.Get(a, b);
		}

		public static Triple<T1, T2, T3> ToTriple<T1, T2, T3>(T1 a, T2 b, T3 c)
		{
			return Triple<T1, T2, T3>.Get(a, b, c);
		}

		public static Vector2f Point()
		{
			return Point(0f, 0f);
		}

		public static Vector2f Point(float x, float y)
		{
			return new Vector2f(x, y);
		}

		public static PointF Pointf()
		{
			return Pointf(0f, 0f);
		}

		public static PointF Pointf(float x, float y)
		{
			return new PointF(x, y);
		}

		public static PointI Pointi()
		{
			return Pointi(0, 0);
		}

		public static PointI Pointi(int x, int y)
		{
			return new PointI(x, y);
		}

		public static RectBox Rect(float x, float y, float w, float h)
		{
			return new RectBox(x, y, w, h);
		}

		public static RectF Rectf(float x, float y, float w, float h)
		{
			return new RectF(x, y, w, h);
		}

		public static RectI Recti(int x, int y, int w, int h)
		{
			return new RectI(x, y, w, h);
		}

		public static BooleanValue BoolValue(bool v)
		{
			return RefBool(v);
		}

		public static FloatValue FloatValue(float v)
		{
			return RefFloat(v);
		}

		public static IntValue IntValue(int v)
		{
			return RefInt(v);
		}

		public static BooleanValue RefBool()
		{
			return new BooleanValue();
		}

		public static FloatValue RefFloat()
		{
			return new FloatValue();
		}

		public static IntValue RefInt()
		{
			return new IntValue();
		}

		public static BooleanValue RefBool(bool v)
		{
			return new BooleanValue(v);
		}

		public static FloatValue RefFloat(float v)
		{
			return new FloatValue(v);
		}

		public static IntValue RefInt(int v)
		{
			return new IntValue(v);
		}

		public static T GetValue<T>(T val, T defval)
		{
			return val == null ? defval : val;
		}

		public static float ToPercent(float value, float min, float max)
		{
			return MathUtils.Percent(value, min, max);
		}

		public static float ToPercent(float value, float min, float max, float upperMax)
		{
			return MathUtils.Percent(value, min, max, upperMax);
		}

		public static float ToPercent(float value, float percent)
		{
			return MathUtils.Percent(value, percent);
		}

		public static int ToPercent(int value, int percent)
		{
			return MathUtils.Percent(value, percent);
		}

		public static BoolArray ToBoolArrayOf(params bool[] arrays)
		{
			return new BoolArray(arrays);
		}

		public static FloatArray ToFloatArrayOf(params float[] arrays)
		{
			return new FloatArray(arrays);
		}

		public static IntArray ToIntArrayOf(params int[] arrays)
		{
			return new IntArray(arrays);
		}

		public static bool ToOrder(ZIndex[] array)
		{
			if (array == null || array.Length < 2)
			{
				return false;
			}
			int len = array.Length;
			int key;
			ZIndex cur;
			for (int i = 1, j ; i < len; i++)
			{
				j = i;
				cur = array[j];
				key = array[j].GetLayer();
				for (; --j > -1;)
				{
					if (array[j].GetLayer() > key)
					{
						array[j + 1] = array[j];
					}
					else
					{
						break;
					}
				}
				array[j + 1] = cur;
			}
			return true;
		}

		public static LColor ToColor(int r, int g, int b, int a)
		{
			return new LColor(r, g, b, a);
		}

		public static LColor ToColor(int r, int g, int b)
		{
			return new LColor(r, g, b);
		}

		public static LColor ToColor(string c)
		{
			if (StringUtils.IsEmpty(c))
			{
				return new LColor();
			}
			return new LColor(c);
		}
	}
}

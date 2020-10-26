using java.lang;
using System;

namespace loon.utils
{
  public  class HelperUtils
{
		public static string ToStr(object o)
		{
			if (o == null)
			{
				return LSystem.NULL;
			}
            if (o is short s)
            {
                return JavaSystem.Str(s);
            }
            if (o is uint ui)
            {
                return JavaSystem.Str(ui);
            }
            if (o is int i) {
                return JavaSystem.Str(i);
            }
            if (o is long l)
            {
                return JavaSystem.Str(l);
            }
            if (o is ulong ul)
            {
                return JavaSystem.Str(ul);
            }
            if (o is float f)
            {
                return JavaSystem.Str(f);
            }
            if (o is double d)
            {
                return JavaSystem.Str(d);
            }
            if (o is bool b)
            {
                return JavaSystem.Str(b);
            }
            if (o is CharSequence cs)
            {
                return cs.ToString();
            }
            if (o is string v)
            {
                if (MathUtils.IsNan(v))
                {
                    if (v.IndexOf('.') != -1)
                    {

                        return JavaSystem.Str(Convert.ToDouble(v));
                    }
                    else
                    {
                        return JavaSystem.Str(Convert.ToInt32(v));
                    }
                }
                else
                {
                    return v;
                }
            }
            return Convert.ToString(o);
		}
	}
}

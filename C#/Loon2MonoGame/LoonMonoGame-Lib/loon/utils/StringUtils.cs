using java.lang;

namespace loon.utils
{
   public class StringUtils
{
		public static bool IsEmpty(string v)
		{
			return v == null || v.Length == 0 || "".Equals(v.Trim());
		}
		public static bool IsEmpty(params CharSequence[] v)
		{
			return v == null || v.Length == 0 || "".Equals(v.ToString().Trim());
		}
		public static bool IsEmpty(params string[] v)
		{
			return v == null || v.Length == 0 || "".Equals(v.ToString().Trim());
		}

		public static bool IsEmpty(params char[] v)
		{
			return v == null || v.Length == 0 || "".Equals(v.ToString().Trim());
		}
	}
}

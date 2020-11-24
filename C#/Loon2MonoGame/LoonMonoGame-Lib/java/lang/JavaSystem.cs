using loon;
using System;
using System.Text;

namespace java.lang
{
    /// <summary>
    /// 为防止和C#的System类搞混加上Java前缀......
    /// </summary>
    public class JavaSystem
    {
        public static readonly java.io.PrintStream Out = new java.io.PrintStream(false);
        public static readonly java.io.PrintStream Err = new java.io.PrintStream(true);
        public static readonly long startTicks_f = System.DateTimeOffset.Now.Ticks;

        public static Encoding GetEncoding()
        {
            return GetEncoding(LSystem.ENCODING);
        }

        public static Encoding GetEncoding(string e)
        {
            Encoding encode;

            if ("ASCII".Equals(e))
            {
                encode = Encoding.ASCII;
            }
            else if ("Unicode".Equals(e))
            {
                encode = Encoding.Unicode;
            }
            else if ("Default".Equals(e))
            {
                encode = Encoding.Default;
            }
            else if ("UTF-8".Equals(e))
            {
                encode = Encoding.UTF8;
            }
            else
            {
                encode = System.Text.Encoding.GetEncoding(e);
            }
            return encode;
        }

        public static byte[] GetBytes(String e, string v)
        {
            return GetEncoding(e).GetBytes(v);
        }

        public static byte[] GetBytes(string v)
        {
            return GetEncoding().GetBytes(v);
        }

        public static void Exception(object o)
        {
            DebugWrite(((System.Exception)o).StackTrace);
        }

        public static void DebugWrite(string text)
        {
            System.Diagnostics.Debug.WriteLine(text);
        }

        public static void Arraycopy(object src, int srcPos, object dest, int destPos, int length)
        {
            System.Array.Copy((System.Array)src, srcPos, (System.Array)dest, destPos, length);
        }

        public static void Exit(int code)
        {
            System.Environment.Exit(code);
        }

        public static String GetProperty(string key)
        {
            return GetProperty("");
        }

        public static String GetProperty(string key, string def)
        {
            string result = Environment.GetEnvironmentVariable(key);
            if (result != null && result.Length > 0)
            {
                return result;
            }
            return def;
        }

        public static long CurrentTimeMillis()
        {
            return System.DateTimeOffset.Now.ToUnixTimeMilliseconds();
        }

        public static long NanoTime()
        {
            return (System.DateTimeOffset.Now.Ticks - startTicks_f) * 100L;
        }

        public static long GetTotalMemory()
        {
            return System.Diagnostics.Process.GetCurrentProcess().VirtualMemorySize64;
        }

        public static long GetAvailableMemory()
        {
            return GetTotalMemory() - GetUsedMemory();
        }

        public static long GetUsedMemory()
        {
            return System.Diagnostics.Process.GetCurrentProcess().WorkingSet64;
        }

        public static bool IsMainThread()
        {
            return System.Threading.Thread.CurrentThread.ManagedThreadId == 1 && System.Threading.Thread.CurrentThread.Name == null;
        }

        public static T[][] Dim<T>(int n0, int n1)
        {
            if (n0 < 0) { throw new NegativeArraySizeException(); }
            T[][] a = new T[n0][];
            for (int i0 = 0; n1 >= 0 && i0 < n0; i0++) { a[i0] = new T[n1]; }
            return a;
        }

        public static T[][][] Dim<T>(int n0, int n1, int n2)
        {
            if (n0 < 0) throw new NegativeArraySizeException();
            T[][][] a = new T[n0][][];
            for (int i0 = 0; n1 >= 0 && i0 < n0; i0++) { a[i0] = Dim<T>(n1, n2); }
            return a;
        }

        public static T[][][][] Dim<T>(int n0, int n1, int n2, int n3)
        {
            if (n0 < 0) throw new NegativeArraySizeException();
            T[][][][] a = new T[n0][][][];
            for (int i0 = 0; n1 >= 0 && i0 < n0; i0++) { a[i0] = Dim<T>(n1, n2, n3); }
            return a;
        }

        public static int Div(int a, int b)
        {
            if (a == -2147483648 && b == -1) { return a; }
            if (b == 0) { throw new ArithmeticException(); }
            return a / b;
        }
        public static long Div(long a, long b)
        {
            if (a == -9223372036854775808 && b == -1) { return a; }
            if (b == 0) { throw new ArithmeticException(); }
            return a / b;
        }

        public static sbyte CastToByte(double x)
        {
            return (sbyte)CastToInt(x);
        }

        public static char CastToChar(double x)
        {
            return (char)CastToInt(x);
        }

        public static short CastToShort(double x)
        {
            return (short)CastToInt(x);
        }

        public static int CastToInt(double a)
        {
            if (System.Double.IsNaN(a))
            {
                return 0;
            }
            else if (a >= 0)
            {
                if (a > 2147483647) { return 2147483647; }
                return (int)System.Math.Floor(a);
            }
            else
            {
                if (a < -2147483648) { return -2147483648; }
                return (int)System.Math.Ceiling(a);
            }
        }
        public static long CastToLong(double a)
        {
            if (System.Double.IsNaN(a))
            {
                return 0;
            }
            else if (a >= 0)
            {
                if (a > 9223372036854775807) { return 9223372036854775807; }
                return (long)System.Math.Floor(a);
            }
            else
            {
                if (a < -9223372036854775808) { return -9223372036854775808; }
                return (long)System.Math.Ceiling(a);
            }
        }

        public static string Str(bool v)
        {
            return v ? "true" : "false";
        }

        public static string Str(sbyte v)
        {
            return v.ToString();
        }

        public static string Str(byte v)
        {
            return v.ToString();
        }

        public static string Str(char v)
        {
            return v.ToString();
        }
        public static string Str(short v)
        {
            return v.ToString("d");
        }

        public static string Str(long v)
        {
            return v.ToString("d");
        }
        public static string Str(ulong v)
        {
            return v.ToString("d");
        }

        public static string Str(uint v)
        {
            return v.ToString("d");
        }

        public static string Str(double v)
        {
            if (System.Double.IsNaN(v)) { return "NaN"; }
            if (System.Double.IsNegativeInfinity(v)) { return "-Infinity"; }
            if (System.Double.IsInfinity(v)) { return "Infinity"; }

            string s = v.ToString("R");
            string s2 = v.ToString("G16");
            if (s2.Length < s.Length && System.Double.TryParse(s2, out double parseback))
            {
                if (parseback == v) { s = s2; }
            }

            s = s.Replace(',', '.');
            if (s.IndexOf('.') < 0) { s += ".0"; }
            int idx = s.IndexOf("+");
            if (idx > 0) { s = s.Remove(idx, 1); }

            return s;
        }

        public static string Str(int v)
        {
            return v.ToString("d");
        }

        public static string Str(object o)
        {
            return (o == null) ? "null" : o.ToString();
        }

        public static string ASSIGNPLUS(ref string dest, string s)
        {
            return dest = Str(dest) + s;
        }
        public static short ASSIGNPLUS(ref short dest, int s)
        {
            return dest = (short)(dest + s);
        }
        public static char ASSIGNPLUS(ref char dest, int s)
        {
            return dest = (char)(dest + s);
        }
        public static sbyte ASSIGNPLUS(ref sbyte dest, int s)
        {
            return dest = (sbyte)(dest + s);
        }
        public static long ASSIGNPLUS(ref long dest, int s)
        {
            return dest = dest + s;
        }
        public static double ASSIGNPLUS(ref double dest, int s)
        {
            return dest = dest + s;
        }
        public static int ASSIGNPLUS(ref int dest, long s)
        {
            return dest = CastToInt(dest + s);
        }
        public static short ASSIGNPLUS(ref short dest, long s)
        {
            return dest = CastToShort(dest + s);
        }
        public static char ASSIGNPLUS(ref char dest, long s)
        {
            return dest = CastToChar(dest + s);
        }
        public static sbyte ASSIGNPLUS(ref sbyte dest, long s)
        {
            return dest = CastToByte(dest + s);
        }
        public static int ASSIGNPLUS(ref int dest, double s)
        {
            return dest = CastToInt(dest + s);
        }
        public static short ASSIGNPLUS(ref short dest, double s)
        {
            return dest = CastToShort(dest + s);
        }
        public static char ASSIGNPLUS(ref char dest, double s)
        {
            return dest = CastToChar(dest + s);
        }
        public static sbyte ASSIGNPLUS(ref sbyte dest, double s)
        {
            return dest = CastToByte(dest + s);
        }
        public static long ASSIGNPLUS(ref long dest, double s)
        {
            return dest = CastToLong(dest + s);
        }

        public static short ASSIGNMINUS(ref short dest, int s)
        {
            return dest = (short)(dest - s);
        }
        public static char ASSIGNMINUS(ref char dest, int s)
        {
            return dest = (char)(dest - s);
        }
        public static sbyte ASSIGNMINUS(ref sbyte dest, int s)
        {
            return dest = (sbyte)(dest - s);
        }
        public static long ASSIGNMINUS(ref long dest, int s)
        {
            return dest = (dest - s);
        }
        public static double ASSIGNMINUS(ref double dest, int s)
        {
            return dest = dest - s;
        }
        public static int ASSIGNMINUS(ref int dest, long s)
        {
            return dest = (int)(dest - s);
        }
        public static short ASSIGNMINUS(ref short dest, long s)
        {
            return dest = (short)(dest - s);
        }
        public static char ASSIGNMINUS(ref char dest, long s)
        {
            return dest = (char)(dest - s);
        }
        public static sbyte ASSIGNMINUS(ref sbyte dest, long s)
        {
            return dest = (sbyte)(dest - s);
        }
        public static long ASSIGNMINUS(ref long dest, long s)
        {
            return dest = (dest - s);
        }
        public static double ASSIGNMINUS(ref double dest, long s)
        {
            return dest = dest - s;
        }
        public static int ASSIGNMINUS(ref int dest, double s)
        {
            return dest = CastToInt(dest - s);
        }
        public static short ASSIGNMINUS(ref short dest, double s)
        {
            return dest = CastToShort(dest - s);
        }
        public static char ASSIGNMINUS(ref char dest, double s)
        {
            return dest = CastToChar(dest - s);
        }
        public static sbyte ASSIGNMINUS(ref sbyte dest, double s)
        {
            return dest = CastToByte(dest - s);
        }
        public static long ASSIGNMINUS(ref long dest, double s)
        {
            return dest = CastToLong(dest - s);
        }

        public static int ASSIGNDIV(ref int dest, int s)
        {
            return dest = Div(dest, s);
        }
        public static short ASSIGNDIV(ref short dest, int s)
        {
            return dest = (short)Div(dest, s);
        }
        public static char ASSIGNDIV(ref char dest, int s)
        {
            return dest = (char)Div(dest, s);
        }
        public static sbyte ASSIGNDIV(ref sbyte dest, int s)
        {
            return dest = (sbyte)Div(dest, s);
        }
        public static long ASSIGNDIV(ref long dest, int s)
        {
            return dest = Div(dest, s);
        }
        public static double ASSIGNDIV(ref double dest, int s)
        {
            return dest = dest / s;
        }
        public static int ASSIGNDIV(ref int dest, long s)
        {
            return dest = (int)Div(dest, s);
        }
        public static short ASSIGNDIV(ref short dest, long s)
        {
            return dest = (short)Div(dest, s);
        }
        public static char ASSIGNDIV(ref char dest, long s)
        {
            return dest = (char)Div(dest, s);
        }
        public static sbyte ASSIGNDIV(ref sbyte dest, long s)
        {
            return dest = (sbyte)Div(dest, s);
        }
        public static long ASSIGNDIV(ref long dest, long s)
        {
            return dest = Div(dest, s);
        }
        public static double ASSIGNDIV(ref double dest, long s)
        {
            return dest = dest / s;
        }
        public static int ASSIGNDIV(ref int dest, double s)
        {
            return dest = CastToInt(dest / s);
        }
        public static short ASSIGNDIV(ref short dest, double s)
        {
            return dest = CastToShort(dest / s);
        }
        public static char ASSIGNDIV(ref char dest, double s)
        {
            return dest = CastToChar(dest / s);
        }
        public static sbyte ASSIGNDIV(ref sbyte dest, double s)
        {
            return dest = CastToByte(dest / s);
        }
        public static long ASSIGNDIV(ref long dest, double s)
        {
            return dest = CastToLong(dest / s);
        }
        public static double ASSIGNDIV(ref double dest, double s)
        {
            return dest = dest / s;
        }
        public static short ASSIGNMUL(ref short dest, int s)
        {
            return dest = (short)(dest * s);
        }
        public static char ASSIGNMUL(ref char dest, int s)
        {
            return dest = (char)(dest * s);
        }
        public static sbyte ASSIGNMUL(ref sbyte dest, int s)
        {
            return dest = (sbyte)(dest * s);
        }
        public static long ASSIGNMUL(ref long dest, int s)
        {
            return dest = (dest * s);
        }
        public static double ASSIGNMUL(ref double dest, int s)
        {
            return dest = dest * s;
        }
        public static int ASSIGNMUL(ref int dest, long s)
        {
            return dest = (int)(dest * s);
        }
        public static short ASSIGNMUL(ref short dest, long s)
        {
            return dest = (short)(dest * s);
        }
        public static char ASSIGNMUL(ref char dest, long s)
        {
            return dest = (char)(dest * s);
        }
        public static sbyte ASSIGNMUL(ref sbyte dest, long s)
        {
            return dest = (sbyte)(dest * s);
        }
        public static double ASSIGNMUL(ref double dest, long s)
        {
            return dest = dest * s;
        }
        public static int ASSIGNMUL(ref int dest, double s)
        {
            return dest = CastToInt(dest * s);
        }
        public static short ASSIGNMUL(ref short dest, double s)
        {
            return dest = CastToShort(dest * s);
        }
        public static char ASSIGNMUL(ref char dest, double s)
        {
            return dest = CastToChar(dest * s);
        }
        public static sbyte ASSIGNMUL(ref sbyte dest, double s)
        {
            return dest = CastToByte(dest * s);
        }
        public static long ASSIGNMUL(ref long dest, double s)
        {
            return dest = CastToLong(dest * s);
        }

        public static short ASSIGNMOD(ref short dest, int s)
        {
            return dest = (short)(dest % s);
        }
        public static char ASSIGNMOD(ref char dest, int s)
        {
            return dest = (char)(dest % s);
        }
        public static sbyte ASSIGNMOD(ref sbyte dest, int s)
        {
            return dest = (sbyte)(dest % s);
        }
        public static long ASSIGNMOD(ref long dest, int s)
        {
            return dest = (dest % s);
        }
        public static double ASSIGNMOD(ref double dest, int s)
        {
            return dest = dest % s;
        }
        public static int ASSIGNMOD(ref int dest, long s)
        {
            return dest = (int)(dest % s);
        }
        public static short ASSIGNMOD(ref short dest, long s)
        {
            return dest = (short)(dest % s);
        }
        public static char ASSIGNMOD(ref char dest, long s)
        {
            return dest = (char)(dest % s);
        }
        public static sbyte ASSIGNMOD(ref sbyte dest, long s)
        {
            return dest = (sbyte)(dest % s);
        }
        public static double ASSIGNMOD(ref double dest, long s)
        {
            return dest = dest % s;
        }
        public static int ASSIGNMOD(ref int dest, double s)
        {
            return dest = CastToInt(dest % s);
        }
        public static short ASSIGNMOD(ref short dest, double s)
        {
            return dest = CastToShort(dest % s);
        }
        public static char ASSIGNMOD(ref char dest, double s)
        {
            return dest = CastToChar(dest % s);
        }
        public static sbyte ASSIGNMOD(ref sbyte dest, double s)
        {
            return dest = CastToByte(dest % s);
        }
        public static long ASSIGNMOD(ref long dest, double s)
        {
            return dest = CastToLong(dest % s);
        }

        public static short ASSIGNAND(ref short dest, int s)
        {
            return dest = (short)(((int)dest) & s);
        }
        public static char ASSIGNAND(ref char dest, int s)
        {
            return dest = (char)(((int)dest) & s);
        }
        public static sbyte ASSIGNAND(ref sbyte dest, int s)
        {
            return dest = (sbyte)(((int)dest) & s);
        }
        public static long ASSIGNAND(ref long dest, int s)
        {
            return dest = (long)(((long)dest) & s);
        }
        public static short ASSIGNAND(ref short dest, long s)
        {
            return dest = (short)(((int)dest) & ((int)s));
        }
        public static char ASSIGNAND(ref char dest, long s)
        {
            return dest = (char)(((int)dest) & ((int)s));
        }
        public static sbyte ASSIGNAND(ref sbyte dest, long s)
        {
            return dest = (sbyte)(((int)dest) & ((int)s));
        }
        public static long ASSIGNAND(ref long dest, long s)
        {
            return dest = (long)(((long)dest) & (s));
        }

        public static short ASSIGNOR(ref short dest, int s)
        {
            return dest = (short)(((int)dest) | ((int)s));
        }
        public static char ASSIGNOR(ref char dest, int s)
        {
            return dest = (char)(((int)dest) | ((int)s));
        }
        public static sbyte ASSIGNOR(ref sbyte dest, int s)
        {
            return dest = (sbyte)(((int)dest) | ((int)s));
        }
        public static long ASSIGNOR(ref long dest, int s)
        {
            return dest = (long)(dest | ((long)s));
        }
        public static short ASSIGNOR(ref short dest, long s)
        {
            return dest = (short)(((int)dest) | ((int)s));
        }
        public static char ASSIGNOR(ref char dest, long s)
        {
            return dest = (char)(((int)dest) | ((int)s));
        }
        public static sbyte ASSIGNOR(ref sbyte dest, long s)
        {
            return dest = (sbyte)(((int)dest) | ((int)s));
        }
        public static long ASSIGNOR(ref long dest, long s)
        {
            return dest = (long)(((long)dest) | ((long)s));
        }

        public static short ASSIGNXOR(ref short dest, int s)
        {
            return dest = (short)(((int)dest) ^ ((int)s));
        }
        public static char ASSIGNXOR(ref char dest, int s)
        {
            return dest = (char)(((int)dest) ^ ((int)s));
        }
        public static sbyte ASSIGNXOR(ref sbyte dest, int s)
        {
            return dest = (sbyte)(((int)dest) ^ ((int)s));
        }
        public static long ASSIGNXOR(ref long dest, int s)
        {
            return dest = (long)(((long)dest) ^ ((int)s));
        }

        public static short ASSIGNXOR(ref short dest, long s)
        {
            return dest = (short)(((int)dest) ^ ((int)s));
        }
        public static char ASSIGNXOR(ref char dest, long s)
        {
            return dest = (char)(((int)dest) ^ ((int)s));
        }
        public static sbyte ASSIGNXOR(ref sbyte dest, long s)
        {
            return dest = (sbyte)(((int)dest) ^ ((int)s));
        }
        public static long ASSIGNXOR(ref long dest, long s)
        {
            return dest = (long)(((long)dest) ^ ((long)s));
        }

        public static short ASSIGNLSHIFT(ref short dest, int s)
        {
            return dest = (short)(((int)dest) << ((int)s));
        }
        public static char ASSIGNLSHIFT(ref char dest, int s)
        {
            return dest = (char)(((int)dest) << ((int)s));
        }
        public static sbyte ASSIGNLSHIFT(ref sbyte dest, int s)
        {
            return dest = (sbyte)(((int)dest) << ((int)s));
        }
        public static int ASSIGNLSHIFT(ref int dest, long s)
        {
            return dest = ((dest) << ((int)s));
        }
        public static short ASSIGNLSHIFT(ref short dest, long s)
        {
            return dest = (short)(((int)dest) << ((int)s));
        }
        public static char ASSIGNLSHIFT(ref char dest, long s)
        {
            return dest = (char)(((int)dest) << ((int)s));
        }
        public static sbyte ASSIGNLSHIFT(ref sbyte dest, long s)
        {
            return dest = (sbyte)(((int)dest) << ((int)s));
        }
        public static long ASSIGNLSHIFT(ref long dest, long s)
        {
            return dest = (long)(((long)dest) << ((int)s));
        }

        public static short ASSIGNRSHIFT(ref short dest, int s)
        {
            return dest = (short)(((int)dest) >> ((int)s));
        }
        public static char ASSIGNRSHIFT(ref char dest, int s)
        {
            return dest = (char)(((int)dest) >> ((int)s));
        }
        public static sbyte ASSIGNRSHIFT(ref sbyte dest, int s)
        {
            return dest = (sbyte)(((int)dest) >> ((int)s));
        }
        public static long ASSIGNRSHIFT(ref long dest, int s)
        {
            return dest = ((dest) >> ((int)s));
        }
        public static int ASSIGNRSHIFT(ref int dest, long s)
        {
            return dest = ((dest) >> ((int)s));
        }
        public static short ASSIGNRSHIFT(ref short dest, long s)
        {
            return dest = (short)(((int)dest) >> ((int)s));
        }
        public static char ASSIGNRSHIFT(ref char dest, long s)
        {
            return dest = (char)(((int)dest) >> ((int)s));
        }
        public static sbyte ASSIGNRSHIFT(ref sbyte dest, long s)
        {
            return dest = (sbyte)(((int)dest) >> ((int)s));
        }


        public static int ASSIGNURSHIFT(ref int dest, int s)
        {
            return dest = (int)(((uint)dest) >> s);
        }
        public static short ASSIGNURSHIFT(ref short dest, int s)
        {
            return dest = (short)(((uint)dest) >> s);
        }
        public static char ASSIGNURSHIFT(ref char dest, int s)
        {
            return dest = (char)(((uint)dest) >> s);
        }
        public static sbyte ASSIGNURSHIFT(ref sbyte dest, int s)
        {
            return dest = (sbyte)(((uint)dest) >> s);
        }
        public static long ASSIGNURSHIFT(ref long dest, int s)
        {
            return dest = (long)(((ulong)dest) >> s);
        }
        public static int ASSIGNURSHIFT(ref int dest, long s)
        {
            return dest = (int)(((uint)dest) >> ((int)s));
        }
        public static short ASSIGNURSHIFT(ref short dest, long s)
        {
            return dest = (short)(((uint)dest) >> ((int)s));
        }
        public static char ASSIGNURSHIFT(ref char dest, long s)
        {
            return dest = (char)(((uint)dest) >> ((int)s));
        }
        public static sbyte ASSIGNURSHIFT(ref sbyte dest, long s)
        {
            return dest = (sbyte)(((uint)dest) >> ((int)s));
        }
        public static long ASSIGNURSHIFT(ref long dest, long s)
        {
            return dest = (long)(((ulong)dest) >> ((int)s));
        }
    }
}

using java.lang;
using loon.geom;
using System;
using static loon.Log;

namespace loon
{
    public class LSystem
    {
        private readonly static string _version = "0.5-beta";

        // 默认编码格式
        public readonly static string ENCODING = "UTF-8";

        // 默认的字符串打印完毕flag
        public const string FLAG_TAG = "▼";

        public const string FLAG_SELECT_TAG = "◆";

        /** 表示空值和无效的占位用字符串 **/
        public const string EMPTY = "";

        public const string NULL = "null";

        public readonly static string UNKNOWN = "unknown";

        /** 常见字符串操作用符号 **/
        public readonly static char DOT = '.';

        public readonly static char SLASH = '/';

        public readonly static char BACKSLASH = '\\';

        public readonly static char CR = '\r';

        public readonly static char LF = '\n';

        public readonly static char UNDERLINE = '_';

        public readonly static char DASHED = '-';

        public readonly static char COMMA = ',';

        public readonly static char DELIM_START = '{';

        public readonly static char DELIM_END = '}';

        public readonly static char BRACKET_START = '[';

        public readonly static char BRACKET_END = ']';

        public readonly static char COLON = ':';

        public readonly static char DOUBLE_QUOTES = '"';

        public readonly static char SINGLE_QUOTE = '\'';

        public readonly static char AMP = '&';

        public readonly static char SPACE = ' ';

        public readonly static char TAB = '	';

        // 默认缓存数量
        public readonly static int DEFAULT_MAX_CACHE_SIZE = 32;

        // 默认缓动函数延迟
        public readonly static float DEFAULT_EASE_DELAY = 1f / 60f;

        // 行分隔符
        public readonly static string LS = JavaSystem.GetProperty("line.separator", "\n");

        // 文件分割符
        public readonly static string FS = JavaSystem.GetProperty("file.separator", "/");

        // 换行标记
        public readonly static string NL = "\r\n";

        public readonly static float MIN_SECONE_SPEED_FIXED = 0.008f;

        // 兆秒
        public const long MSEC = 1;

        // 秒
        public const long SECOND = 1000;

        // 分
        public const long MINUTE = SECOND * 60;

        // 小时
        public const long HOUR = MINUTE * 60;

        // 天
        public const long DAY = HOUR * 24;

        // 周
        public const long WEEK = DAY * 7;

        // 理论上一年
        public const long YEAR = DAY * 365;

        public static readonly Dimension viewSize = new Dimension(480, 320);

        // 是否允许屏幕画面刷新
        protected internal static bool _auto_repaint = true;

        public static Platform Platform
        {
            get
            {
                return LGame._platform;
            }
        }

        public static LGame Base
        {
            get
            {
                LGame game = LGame._base;
                if (game != null)
                {
                    return game;
                }
                else if (Platform != null)
                {
                    game = Platform.GetGame();
                }
                return game;
            }
        }

        public static string GetSystemAppName()
        {
            if (Base != null)
            {
                return Base.setting.appName;
            }
            return LGame.APP_NAME;
        }

        public static bool IsLockAllTouchEvent()
        {
            if (Base != null)
            {
                return Base.setting.lockAllTouchEvent;
            }
            return false;

        }

        public static bool IsNotAllowDragAndMove()
        {
            if (Base != null)
            {
                return Base.setting.notAllowDragAndMove;
            }
            return false;

        }

        public static float GetEmulatorScale()
        {
            if (Base != null)
            {
                return Base.setting.emulatorScale;
            }
            return 1f;
        }

        public static bool IsTrueFontClip()
        {
            if (Base != null)
            {
                return Base.setting.useTrueFontClip;
            }
            return true;
        }

        public static bool IsConsoleLog()
        {
            if (Base != null)
            {
                return Base.setting.isConsoleLog;
            }
            return true;
        }

        public static string GetSystemGameFontName()
        {
            if (Base != null)
            {
                return Base.setting.fontName;
            }
            return LGame.FONT_NAME;
        }

        public static string GetVersion()
        {
            return _version;
        }

        public static void StopRepaint()
        {
            LSystem._auto_repaint = false;
        }

        public static void StartRepaint()
        {
            LSystem._auto_repaint = true;
        }

        public static string GetAllFileName(string name)
        {
            if (string.ReferenceEquals(name, null))
            {
                return "";
            }
            int idx = name.LastIndexOf('.');
            return idx == -1 ? name : name.Substring(0, idx);
        }

        public static string GetFileName(string name)
        {
            if (string.ReferenceEquals(name, null))
            {
                return "";
            }
            int length = name.Length;
            int idx = name.LastIndexOf('/');
            if (idx == -1)
            {
                idx = name.LastIndexOf('\\');
            }
            int size = idx + 1;
            if (size < length)
            {
                return name.Substring(size, length);
            }
            else
            {
                return "";
            }
        }

        public static string GetExtension(string name)
        {
            if (string.ReferenceEquals(name, null))
            {
                return "";
            }

            int index = name.LastIndexOf(".", StringComparison.Ordinal);
            if (index == -1)
            {
                return "";
            }
            else
            {
                return name.Substring(index + 1);
            }
        }

        public static int Unite(int hashCode, bool value)
        {
            int v = value ? 1231 : 1237;
            return Unite(hashCode, v);
        }

        public static int Unite(int hashCode, long value)
        {
            int v = (int)(value ^ ((long)((ulong)value >> 32)));
            return Unite(hashCode, v);
        }

        public static int Unite(int hashCode, float value)
        {
            int v = (int)(value);
            return Unite(hashCode, v);
        }

        public static int Unite(int hashCode, double value)
        {
            long v = (long)(value);
            return Unite(hashCode, v);
        }

        public static int Unite(int hashCode, object value)
        {
            return Unite(hashCode, value.GetHashCode());
        }

        public static int Unite(int hashCode, int value)
        {
            return 31 * hashCode + value;
        }

        public static uint Unite(uint hashCode, uint value)
        {
            return 31 * hashCode + value;
        }

        public static new bool Equals(object o1, object o2)
        {
            return (o1 == null) ? (o2 == null) : o1.Equals(o2);
        }

        public static bool IsMobile()
        {
            return false;
        }


        public static void Debug(string msg)
        {
            if (Base != null)
            {
                Base.Log().Debug(msg);
            }
        }

        public static void Debug(string msg, params object[] args)
        {
            if (Base != null)
            {
                Base.Log().Debug(msg, args);
            }
        }

        public static void Debug(string msg, System.Exception throwable)
        {
            if (Base != null)
            {
                Base.Log().Debug(msg, throwable);
            }
        }

        public static void Info(string msg)
        {
            if (Base != null)
            {
                Base.Log().Info(msg);
            }
        }

        public static void Info(string msg, params object[] args)
        {
            if (Base != null)
            {
                Base.Log().Info(msg, args);
            }
        }

        public static void Info(string msg, System.Exception throwable)
        {
            if (Base != null)
            {
                Base.Log().Info(msg, throwable);
            }
        }

        public static void Warn(string msg)
        {
            if (Base != null)
            {
                Base.Log().Warn(msg);
            }
        }

        public static void Warn(string msg, params object[] args)
        {
            if (Base != null)
            {
                Base.Log().Warn(msg, args);
            }
        }

        public static void Warn(string msg, System.Exception throwable)
        {
            if (Base != null)
            {
                Base.Log().Warn(msg, throwable);
            }
        }

        public static void Error(string msg)
        {
            if (Base != null)
            {
                Base.Log().Error(msg);
            }
        }

        public static void Error(string msg, params object[] args)
        {
            if (Base != null)
            {
                Base.Log().Error(msg, args);
            }
        }

        public static void Error(string msg, System.Exception throwable)
        {
            if (Base != null)
            {
                Base.Log().Error(msg, throwable);
            }
        }

        public static void ReportError(string msg, System.Exception throwable)
        {
            if (Base != null)
            {
                Base.ReportError(msg, throwable);
            }
        }

        public static void D(string msg)
        {
            Debug(msg);
        }

        public static void D(string msg, params object[] args)
        {
            Debug(msg, args);
        }

        public static void D(string msg, System.Exception throwable)
        {
            Debug(msg, throwable);
        }

        public static void I(string msg)
        {
            Info(msg);
        }

        public static void I(string msg, params object[] args)
        {
            Info(msg, args);
        }

        public static void I(string msg, System.Exception throwable)
        {
            Info(msg, throwable);
        }

        public static void W(string msg)
        {
            Warn(msg);
        }

        public static void W(string msg, params object[] args)
        {
            Warn(msg, args);
        }

        public static void W(string msg, System.Exception throwable)
        {
            Warn(msg, throwable);
        }

        public static void E(string msg)
        {
            Error(msg);
        }

        public static void E(string msg, params object[] args)
        {
            Error(msg, args);
        }

        public static void E(string msg, System.Exception throwable)
        {
            Error(msg, throwable);
        }

        public static void SetLogMinLevel(Level level)
        {
            if (Base != null)
            {
                Base.Log().SetMinLevel(level);
            }
        }

    }
}

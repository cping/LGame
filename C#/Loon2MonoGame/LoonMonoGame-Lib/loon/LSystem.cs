using java.lang;
using loon.geom;

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
    }
}

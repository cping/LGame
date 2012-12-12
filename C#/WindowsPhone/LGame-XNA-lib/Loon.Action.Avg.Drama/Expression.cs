namespace Loon.Action.Avg.Drama
{
    using System;
    using Loon.Core;

    public abstract class Expression
    {

        // 默认变量1,用于记录当前选择项
        public const string V_SELECT_KEY = "SELECT";

        // 左括号
        public const string BRACKET_LEFT_TAG = "(";

        // 右括号
        public const string BRACKET_RIGHT_TAG = ")";

        // 代码段开始标记
        public const string BEGIN_TAG = "begin";

        // 代码段结束标记
        public const string END_TAG = "end";

        // 代码段调用标记
        public const string CALL_TAG = "call";

        // 缓存刷新标记
        public const string RESET_CACHE_TAG = "Reset";

        // 累计输入数据标记
        public const string IN_TAG = "in";

        // 累计输入数据停止（输出）标记
        public const string OUT_TAG = "out";

        // 多选标记
        public const string SELECTS_TAG = "selects";

        // 打印标记
        public const string PRINT_TAG = "print";

        // 随机数标记
        public const string RAND_TAG = "rand";

        // 设定环境变量标记
        public const string SET_TAG = "set";

        // 载入内部脚本标记
        public const string INCLUDE_TAG = "include";

        // 条件判定标记
        public const string IF_TAG = "if";

        // 条件判定结束标记
        public const string IF_END_TAG = "endif";

        // 转折标记
        public const string ELSE_TAG = "else";

        // 以下为注视符号
        public const string FLAG_L_TAG = "//";

        public const string FLAG_C_TAG = "#";

        public const string FLAG_I_TAG = "'";

        public const string FLAG_LS_B_TAG = "/*";

        public const string FLAG_LS_E_TAG = "*/";

        public const string FLAG = "@";

        public const string FLAG_SAVE_TAG = "save";

        public const string FLAG_LOAD_TAG = "load";

        public const char FLAG_CHAR = '@';

    }
}

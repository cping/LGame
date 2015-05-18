namespace Loon.Action.Avg.Drama
{
    using System;
    using Loon.Core;

    public abstract class Expression
    {

        public const string V_SELECT_KEY = "SELECT";

        public const string BRACKET_LEFT_TAG = "(";

        public const string BRACKET_RIGHT_TAG = ")";

        public const string BEGIN_TAG = "begin";

        public const string END_TAG = "end";

        public const string CALL_TAG = "call";

        public const string RESET_CACHE_TAG = "Reset";

        public const string IN_TAG = "in";

        public const string OUT_TAG = "out";

        public const string SELECTS_TAG = "selects";

        public const string PRINT_TAG = "print";

        public const string RAND_TAG = "rand";

        public const string SET_TAG = "set";

        public const string INCLUDE_TAG = "include";

        public const string IF_TAG = "if";

        public const string IF_END_TAG = "endif";

        public const string ELSE_TAG = "else";

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

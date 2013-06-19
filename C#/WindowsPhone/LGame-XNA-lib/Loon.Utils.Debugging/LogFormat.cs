namespace Loon.Utils.Debugging
{
    using Loon.Core;

    public class LogFormat
    {

        static private readonly int[] LOG_LEN = { 24, 15, 7, 100 };

        static private readonly string[] LOG_TITLE = { "time", "app", "module",
			"message" };

        static private readonly string[] LOG_TAG = { "-", "-", "-", "-" };

        private int count;

        private string logMsg;

        private bool show;

        private int type;

        public LogFormat(bool v, int t)
        {
            this.show = v;
            this.type = t;
        }

        private static string FormatString(string[] str, string pad, string sp)
        {
            System.Text.StringBuilder sbr = new System.Text.StringBuilder();
            for (int i = 0; i < str.Length; i++)
            {
                if (str[i].Length > LOG_LEN[i])
                {
                    sbr.Append(str[i].Substring(0, (LOG_LEN[i]) - (0)) + sp);
                    continue;
                }
                sbr.Append(str[i]);
                for (int j = str[i].Length; j < LOG_LEN[i]; j++)
                {
                    sbr.Append(pad);
                }
                sbr.Append(sp);
            }
            return sbr.ToString();
        }

        [System.Runtime.CompilerServices.MethodImpl(System.Runtime.CompilerServices.MethodImplOptions.Synchronized)]
        public void Title(int f, string msg)
        {
            switch (f)
            {
                case 0:
                    System.Diagnostics.Debug.WriteLine(msg);
                    break;
                case 1:
                    System.Console.Out.WriteLine(msg);
                    break;
                case 2:
                    System.Console.Error.WriteLine(msg);
                    break;
            }
        }

        [System.Runtime.CompilerServices.MethodImpl(System.Runtime.CompilerServices.MethodImplOptions.Synchronized)]
        public void Out(string msg)
        {
            if (!show)
            {
                return;
            }
            Title(type, msg);
        }

        public int Type
        {
            get
            {
                return type;
            }
            set
            {
                type = value;
            }
        }

        public bool IsShow()
        {
            return show;
        }

        public void SetShow(bool s)
        {
            this.show = s;
        }

        [System.Runtime.CompilerServices.MethodImpl(System.Runtime.CompilerServices.MethodImplOptions.Synchronized)]
        public void Out(string tm, string app, string level, string msg)
        {
            string[] value_ren = { tm, app, level, msg };
            if (count++ % 9999 == 0)
            {
                logMsg = new System.Text.StringBuilder(FormatString(LOG_TAG, "-", " "))
                        .Append(LSystem.LS)
                        .Append(FormatString(LOG_TITLE, " ", " "))
                        .Append(LSystem.LS).Append(FormatString(LOG_TAG, "-", " "))
                        .Append(LSystem.LS).Append(FormatString(value_ren, " ", " ")).ToString();
            }
            else
            {
                logMsg = FormatString(value_ren, " ", " ");
            }
            Out(logMsg);
        }

    }
}

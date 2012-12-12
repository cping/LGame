using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;
using Loon.Core;

namespace Loon.Net
{
    public static class UserAgents
    {
        // windows
        public static readonly string Opera_9_20_Win = "Opera/9.20 (Windows NT 6.0; U; en)";
        public static readonly string FireFox_2_0_0_11_Win = "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11";
        public static readonly string IE_6_0_Win = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)";
        public static readonly string IE_7_0_Win = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)";
        public static readonly string Netscape_4_8_Win = "Mozilla/4.8 [en] (Windows NT 6.0; U)";
        public static readonly string IE_7_0_Win_Net = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705;..NET CLR 1.1.4322; InfoPath.1; Media Center PC 4.0; .NET CLR 2.0.50727)";

        // mac
        public static readonly string FireFox_2_0_0_9_Mac = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X; de; rv:1.8.1.9) Gecko/20071025 Firefox/2.0.0.9";
        public static readonly string Safari_3_Mac = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X; de-de) AppleWebKit/523.10.3 (KHTML, like Gecko) Version/3.0.4 Safari/523.10";

        public static readonly ReadOnlyCollection<string> All = Array.AsReadOnly(new string[]
            {
                Opera_9_20_Win,
                FireFox_2_0_0_11_Win,
                IE_6_0_Win,
                IE_7_0_Win,
                IE_7_0_Win_Net,
                Netscape_4_8_Win,
                FireFox_2_0_0_9_Mac,
                Safari_3_Mac
            });

        public static string RandomHumanAgent()
        {
            return AllHumans[LSystem.random.Next(AllHumans.Count)];
        }

        public static readonly ReadOnlyCollection<string> AllHumans = Array.AsReadOnly(new string[]
            {
                Opera_9_20_Win,
                FireFox_2_0_0_11_Win,
                IE_6_0_Win,
                IE_7_0_Win,
                Netscape_4_8_Win,
                FireFox_2_0_0_9_Mac,
                Safari_3_Mac,
            });

    }
}

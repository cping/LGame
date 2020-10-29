using loon.utils;

namespace loon.canvas
{

    public class LColorList
    {

        private static LColorList instance;

        public static void FreeStatic()
        {
            instance = null;
        }

        public static LColorList Get()
        {
            if (instance == null || instance.dirty)
            {
                lock (typeof(LColorList))
                {
                    if (instance == null || instance.dirty)
                    {
                        instance = new LColorList();
                    }
                }
            }
            return instance;
        }

        private readonly ListMap<string, LColor> colorList;

        private bool dirty;

        LColorList()
        {
            this.colorList = new ListMap<string, LColor>();
            dirty = true;
        }

        protected void PushColor(string name, LColor color)
        {
            colorList.Put(name, color);
        }

        public bool PutColor(string name, LColor color)
        {
            if (StringUtils.IsEmpty(name))
            {
                return false;
            }
            if (color == null)
            {
                return false;
            }
            if (dirty)
            {
                Init();
            }
            PushColor(name, color);
            return true;
        }

        public LColor Find(string name)
        {
            if (StringUtils.IsEmpty(name))
            {
                return LColor.white.Cpy();
            }
            if (dirty)
            {
                Init();
            }
            LColor color = colorList.Get(name.Trim().ToLower());
            if (color != null)
            {
                return color.Cpy();
            }
            return LColor.white.Cpy();
        }

        public string Find(LColor color)
        {
            if (color == null)
            {
                return LSystem.UNKNOWN;
            }
            return Find(color.GetRGB());
        }

        public string Find(uint pixel)
        {
            if (dirty)
            {
                Init();
            }
            for (int i = 0; i < colorList.size; i++)
            {
                LColor c = colorList.GetValueAt(i);
                if (c != null)
                {
                    if (c.GetRGB() == pixel)
                    {
                        return colorList.GetKeyAt(i);
                    }
                    if (c.GetARGB() == pixel)
                    {
                        return colorList.GetKeyAt(i);
                    }
                }
            }
            return LSystem.UNKNOWN;
        }

        public void Init()
        {

            if (dirty)
            {
                LColor transparent = new LColor(0, 0, 0, 0);
                PushColor("transparent", transparent);

                LColor aliceblue = new LColor(240, 248, 255);
                PushColor("aliceblue", aliceblue);

                LColor antiquewhite = new LColor(250, 235, 215);
                PushColor("antiquewhite", antiquewhite);

                LColor aqua = new LColor(0, 255, 255);
                PushColor("aqua", aqua);

                LColor aquamarine = new LColor(127, 255, 212);
                PushColor("aquamarine", aquamarine);

                LColor azure = new LColor(240, 255, 255);
                PushColor("azure", azure);

                LColor beige = new LColor(245, 245, 220);
                PushColor("beige", beige);

                LColor bisque = new LColor(255, 228, 196);
                PushColor("bisque", bisque);

                LColor black = new LColor(0, 0, 0);
                PushColor("black", black);

                LColor blanchedalmond = new LColor(255, 235, 205);
                PushColor("blanchedalmond", blanchedalmond);

                LColor blue = new LColor(0, 0, 255);
                PushColor("blue", blue);

                LColor blueviolet = new LColor(138, 43, 226);
                PushColor("blueviolet", blueviolet);

                LColor brown = new LColor(165, 42, 42);
                PushColor("brown", brown);

                LColor burlywood = new LColor(222, 184, 135);
                PushColor("burlywood", burlywood);

                LColor cadetblue = new LColor(95, 158, 160);
                PushColor("cadetblue", cadetblue);

                LColor chartreuse = new LColor(127, 255, 0);
                PushColor("chartreuse", chartreuse);

                LColor chocolate = new LColor(210, 105, 30);
                PushColor("chocolate", chocolate);

                LColor coral = new LColor(255, 127, 80);
                PushColor("coral", coral);

                LColor cornflowerblue = new LColor(100, 149, 237);
                PushColor("cornflowerblue", cornflowerblue);

                LColor cornsilk = new LColor(255, 248, 220);
                PushColor("cornsilk", cornsilk);

                LColor crimson = new LColor(220, 20, 60);
                PushColor("crimson", crimson);

                LColor cyan = new LColor(0, 255, 255);
                PushColor("cyan", cyan);

                LColor darkblue = new LColor(0, 0, 139);
                PushColor("darkblue", darkblue);

                LColor darkcyan = new LColor(0, 139, 139);
                PushColor("darkcyan", darkcyan);

                LColor darkgoldenrod = new LColor(184, 134, 11);
                PushColor("darkgoldenrod", darkgoldenrod);

                LColor darkgray = new LColor(169, 169, 169);
                PushColor("darkgray", darkgray);

                LColor darkgreen = new LColor(0, 100, 0);
                PushColor("darkgreen", darkgreen);

                LColor darkgrey = new LColor(169, 169, 169);
                PushColor("darkgrey", darkgrey);

                LColor darkkhaki = new LColor(189, 183, 107);
                PushColor("darkkhaki", darkkhaki);

                LColor darkmagenta = new LColor(139, 0, 139);
                PushColor("darkmagenta", darkmagenta);

                LColor darkolivegreen = new LColor(85, 107, 47);
                PushColor("darkolivegreen", darkolivegreen);

                LColor darkorange = new LColor(255, 140, 0);
                PushColor("darkorange", darkorange);

                LColor darkorchid = new LColor(153, 50, 204);
                PushColor("darkorchid", darkorchid);

                LColor darkred = new LColor(139, 0, 0);
                PushColor("darkred", darkred);

                LColor darksalmon = new LColor(233, 150, 122);
                PushColor("darksalmon", darksalmon);

                LColor darkseagreen = new LColor(143, 188, 143);
                PushColor("darkseagreen", darkseagreen);

                LColor darkslateblue = new LColor(72, 61, 139);
                PushColor("darkslateblue", darkslateblue);

                LColor darkslategray = new LColor(47, 79, 79);
                PushColor("darkslategray", darkslategray);

                LColor darkslategrey = new LColor(47, 79, 79);
                PushColor("darkslategrey", darkslategrey);

                LColor darkturquoise = new LColor(0, 206, 209);
                PushColor("darkturquoise", darkturquoise);

                LColor darkviolet = new LColor(148, 0, 211);
                PushColor("darkviolet", darkviolet);

                LColor deeppink = new LColor(255, 20, 147);
                PushColor("deeppink", deeppink);

                LColor deepskyblue = new LColor(0, 191, 255);
                PushColor("deepskyblue", deepskyblue);

                LColor dimgray = new LColor(105, 105, 105);
                PushColor("dimgray", dimgray);
                PushColor("dimgrey", dimgray);

                LColor dodgerblue = new LColor(30, 144, 255);
                PushColor("dodgerblue", dodgerblue);

                LColor firebrick = new LColor(178, 34, 34);
                PushColor("firebrick", firebrick);

                LColor floralwhite = new LColor(255, 250, 240);
                PushColor("floralwhite", floralwhite);

                LColor forestgreen = new LColor(34, 139, 34);
                PushColor("forestgreen", forestgreen);

                LColor fuchsia = new LColor(255, 0, 255);
                PushColor("fuchsia", fuchsia);

                LColor gainsboro = new LColor(220, 220, 220);
                PushColor("gainsboro", gainsboro);

                LColor ghostwhite = new LColor(248, 248, 255);
                PushColor("ghostwhite", ghostwhite);

                LColor gold = new LColor(255, 215, 0);
                PushColor("gold", gold);

                LColor goldenrod = new LColor(218, 165, 32);
                PushColor("goldenrod", goldenrod);

                LColor gray = new LColor(128, 128, 128);
                PushColor("gray", gray);

                LColor green = new LColor(0, 128, 0);
                PushColor("green", green);

                LColor greenyellow = new LColor(173, 255, 47);
                PushColor("greenyellow", greenyellow);

                LColor grey = new LColor(128, 128, 128);
                PushColor("grey", grey);

                LColor honeydew = new LColor(240, 255, 240);
                PushColor("honeydew", honeydew);

                LColor hotpink = new LColor(255, 105, 180);
                PushColor("hotpink", hotpink);

                LColor indianred = new LColor(205, 92, 92);
                PushColor("indianred", indianred);

                LColor indigo = new LColor(75, 0, 130);
                PushColor("indigo", indigo);

                LColor ivory = new LColor(255, 255, 240);
                PushColor("ivory", ivory);

                LColor khaki = new LColor(240, 230, 140);
                PushColor("khaki", khaki);

                LColor lavender = new LColor(230, 230, 250);
                PushColor("lavender", lavender);

                LColor lavenderblush = new LColor(255, 240, 245);
                PushColor("lavenderblush", lavenderblush);

                LColor lawngreen = new LColor(124, 252, 0);
                PushColor("lawngreen", lawngreen);

                LColor lemonchiffon = new LColor(255, 250, 205);
                PushColor("lemonchiffon", lemonchiffon);

                LColor lightblue = new LColor(173, 216, 230);
                PushColor("lightblue", lightblue);

                LColor lightcoral = new LColor(240, 128, 128);
                PushColor("lightcoral", lightcoral);

                LColor lightcyan = new LColor(224, 255, 255);
                PushColor("lightcyan", lightcyan);

                LColor lightgoldenrodyellow = new LColor(250, 250, 210);
                PushColor("lightgoldenrodyellow", lightgoldenrodyellow);

                LColor lightgray = new LColor(211, 211, 211);
                PushColor("lightgray", lightgray);

                LColor lightgreen = new LColor(144, 238, 144);
                PushColor("lightgreen", lightgreen);

                LColor lightgrey = new LColor(211, 211, 211);
                PushColor("lightgrey", lightgrey);

                LColor lightpink = new LColor(255, 182, 193);
                PushColor("lightpink", lightpink);

                LColor lightsalmon = new LColor(255, 160, 122);
                PushColor("lightsalmon", lightsalmon);

                LColor lightseagreen = new LColor(32, 178, 170);
                PushColor("lightseagreen", lightseagreen);

                LColor lightskyblue = new LColor(135, 206, 250);
                PushColor("lightskyblue", lightskyblue);

                LColor lightslategray = new LColor(119, 136, 153);
                PushColor("lightslategray", lightslategray);

                LColor lightslategrey = new LColor(119, 136, 153);
                PushColor("lightslategrey", lightslategrey);

                LColor lightsteelblue = new LColor(176, 196, 222);
                PushColor("lightsteelblue", lightsteelblue);

                LColor lightyellow = new LColor(255, 255, 224);
                PushColor("lightyellow", lightyellow);

                LColor lime = new LColor(0, 255, 0);
                PushColor("lime", lime);

                LColor limegreen = new LColor(50, 205, 50);
                PushColor("limegreen", limegreen);

                LColor linen = new LColor(250, 240, 230);
                PushColor("linen", linen);

                LColor magenta = new LColor(255, 0, 255);
                PushColor("magenta", magenta);

                LColor maroon = new LColor(128, 0, 0);
                PushColor("maroon", maroon);

                LColor mediumaquamarine = new LColor(102, 205, 170);
                PushColor("mediumaquamarine", mediumaquamarine);

                LColor mediumblue = new LColor(0, 0, 205);
                PushColor("mediumblue", mediumblue);

                LColor mediumorchid = new LColor(186, 85, 211);
                PushColor("mediumorchid", mediumorchid);

                LColor mediumpurple = new LColor(147, 112, 219);
                PushColor("mediumpurple", mediumpurple);

                LColor mediumseagreen = new LColor(60, 179, 113);
                PushColor("mediumseagreen", mediumseagreen);

                LColor mediumslateblue = new LColor(123, 104, 238);
                PushColor("mediumslateblue", mediumslateblue);

                LColor mediumspringgreen = new LColor(0, 250, 154);
                PushColor("mediumspringgreen", mediumspringgreen);

                LColor mediumturquoise = new LColor(72, 209, 204);
                PushColor("mediumturquoise", mediumturquoise);

                LColor mediumvioletred = new LColor(199, 21, 133);
                PushColor("mediumvioletred", mediumvioletred);

                LColor midnightblue = new LColor(25, 25, 112);
                PushColor("midnightblue", midnightblue);

                LColor mintcream = new LColor(245, 255, 250);
                PushColor("mintcream", mintcream);

                LColor mistyrose = new LColor(255, 228, 225);
                PushColor("mistyrose", mistyrose);

                LColor moccasin = new LColor(255, 228, 181);
                PushColor("moccasin", moccasin);

                LColor navajowhite = new LColor(255, 222, 173);
                PushColor("navajowhite", navajowhite);

                LColor navy = new LColor(0, 0, 128);
                PushColor("navy", navy);

                LColor oldlace = new LColor(253, 245, 230);
                PushColor("oldlace", oldlace);

                LColor olive = new LColor(128, 128, 0);
                PushColor("olive", olive);

                LColor olivedrab = new LColor(107, 142, 35);
                PushColor("olivedrab", olivedrab);

                LColor orange = new LColor(255, 165, 0);
                PushColor("orange", orange);

                LColor orangered = new LColor(255, 69, 0);
                PushColor("orangered", orangered);

                LColor orchid = new LColor(218, 112, 214);
                PushColor("orchid", orchid);

                LColor palegoldenrod = new LColor(238, 232, 170);
                PushColor("palegoldenrod", palegoldenrod);

                LColor palegreen = new LColor(152, 251, 152);
                PushColor("palegreen", palegreen);

                LColor paleturquoise = new LColor(175, 238, 238);
                PushColor("paleturquoise", paleturquoise);

                LColor palevioletred = new LColor(219, 112, 147);
                PushColor("palevioletred", palevioletred);

                LColor papayawhip = new LColor(255, 239, 213);
                PushColor("papayawhip", papayawhip);

                LColor peachpuff = new LColor(255, 218, 185);
                PushColor("peachpuff", peachpuff);

                LColor peru = new LColor(205, 133, 63);
                PushColor("peru", peru);

                LColor pink = new LColor(255, 192, 203);
                PushColor("pink", pink);

                LColor plum = new LColor(221, 160, 221);
                PushColor("plum", plum);

                LColor powderblue = new LColor(176, 224, 230);
                PushColor("powderblue", powderblue);

                LColor purple = new LColor(128, 0, 128);
                PushColor("purple", purple);

                LColor rebeccapurple = new LColor(102, 51, 153);
                PushColor("rebeccapurple", rebeccapurple);

                LColor red = new LColor(255, 0, 0);
                PushColor("red", red);

                LColor rosybrown = new LColor(188, 143, 143);
                PushColor("rosybrown", rosybrown);

                LColor royalblue = new LColor(65, 105, 225);
                PushColor("royalblue", royalblue);

                LColor saddlebrown = new LColor(139, 69, 19);
                PushColor("saddlebrown", saddlebrown);

                LColor salmon = new LColor(250, 128, 114);
                PushColor("salmon", salmon);

                LColor sandybrown = new LColor(244, 164, 96);
                PushColor("sandybrown", sandybrown);

                LColor seagreen = new LColor(46, 139, 87);
                PushColor("seagreen", seagreen);

                LColor seashell = new LColor(255, 245, 238);
                PushColor("seashell", seashell);

                LColor sienna = new LColor(160, 82, 45);
                PushColor("sienna", sienna);

                LColor silver = new LColor(192, 192, 192);
                PushColor("silver", silver);

                LColor skyblue = new LColor(135, 206, 235);
                PushColor("skyblue", skyblue);

                LColor slateblue = new LColor(106, 90, 205);
                PushColor("slateblue", slateblue);

                LColor slategray = new LColor(112, 128, 144);
                PushColor("slategray", slategray);
                PushColor("slategrey", slategray);

                LColor snow = new LColor(255, 250, 250);
                PushColor("snow", snow);

                LColor springgreen = new LColor(0, 255, 127);
                PushColor("springgreen", springgreen);

                LColor steelblue = new LColor(70, 130, 180);
                PushColor("steelblue", steelblue);

                LColor tan = new LColor(210, 180, 140);
                PushColor("tan", tan);

                LColor teal = new LColor(0, 128, 128);
                PushColor("teal", teal);

                LColor thistle = new LColor(216, 191, 216);
                PushColor("thistle", thistle);

                LColor tomato = new LColor(255, 99, 71);
                PushColor("tomato", tomato);

                LColor turquoise = new LColor(64, 224, 208);
                PushColor("turquoise", turquoise);

                LColor violet = new LColor(238, 130, 238);
                PushColor("violet", violet);

                LColor wheat = new LColor(245, 222, 179);
                PushColor("wheat", wheat);

                LColor white = new LColor(255, 255, 255);
                PushColor("white", white);

                LColor whitesmoke = new LColor(245, 245, 245);
                PushColor("whitesmoke", whitesmoke);

                LColor yellow = new LColor(255, 255, 0);
                PushColor("yellow", yellow);

                LColor yellowgreen = new LColor(154, 205, 50);
                PushColor("yellowgreen", yellowgreen);

                dirty = false;
            }

        }

        public bool isDirty()
        {
            return dirty;
        }

        public void SetDirty(bool dirty)
        {
            if (dirty)
            {
                if (colorList != null)
                {
                    colorList.Clear();
                }
            }
            this.dirty = dirty;
        }

    }
}

using loon.canvas;

namespace loon.utils
{
    public class HtmlCmd : StringKeyValue
    {


        public class Tag
        {

            protected readonly HtmlCmd builder;
            protected readonly string element;
            protected string separator = "";

            public Tag(HtmlCmd builder, string element)
            {
                this.builder = builder;
                this.element = element;
                Begin();
            }

            protected HtmlCmd Begin()
            {
                builder.AddValue("<").AddValue(element).AddValue(" ");
                return builder;
            }

            public virtual HtmlCmd End()
            {
                builder.AddValue("</").AddValue(element).AddValue(">");
                return builder;
            }

            public override string ToString()
            {
                return builder.ToString();
            }

        }

        public class Font : Tag
        {

            public Font() : this(new HtmlCmd())
            {

            }

            public Font(HtmlCmd builder) : base(builder, "font")
            {

            }

            public Font Size(int size)
            {
                builder.AddValue(separator).AddValue("size=\"").AddValue(size).AddValue("\"");
                separator = " ";
                return this;
            }

            public Font Size(string size)
            {
                builder.AddValue(separator).AddValue("size=\"").AddValue(size).AddValue("\"");
                separator = " ";
                return this;
            }

            public Font Color(int color)
            {
                return Color("#" + new LColor(color).ToString());
            }

            public Font Color(string color)
            {
                builder.AddValue(separator).AddValue("color=\"").AddValue(color).AddValue("\"");
                separator = " ";
                return this;
            }

            public Font Face(string face)
            {
                builder.AddValue(separator).AddValue("face=\"").AddValue(face).AddValue("\"");
                separator = " ";
                return this;
            }

            public Font Text(string text)
            {
                builder.AddValue(">").AddValue(text);
                return this;
            }

        }

        public class Img : Tag
        {

            public Img() : this(new HtmlCmd())
            {

            }

            public Img(HtmlCmd builder) : base(builder, "img")
            {

            }

            public Img Src(string src)
            {
                builder.AddValue(separator).AddValue("src=\"").AddValue(src).AddValue("\"");
                separator = " ";
                return this;
            }

            public Img Alt(string alt)
            {
                builder.AddValue(separator).AddValue("alt=\"").AddValue(alt).AddValue("\"");
                separator = " ";
                return this;
            }

            public Img Height(string height)
            {
                builder.AddValue(separator).AddValue("height=\"").AddValue(height).AddValue("\"");
                separator = " ";
                return this;
            }

            public Img Height(int height)
            {
                builder.AddValue(separator).AddValue("height=\"").AddValue(height).AddValue("\"");
                separator = " ";
                return this;
            }

            public Img Width(string width)
            {
                builder.AddValue(separator).AddValue("width=\"").AddValue(width).AddValue("\"");
                separator = " ";
                return this;
            }

            public Img Width(int width)
            {
                builder.AddValue(separator).AddValue("width=\"").AddValue(width).AddValue("\"");
                separator = " ";
                return this;
            }


            public override HtmlCmd End()
            {
                builder.AddValue(">");
                return builder;
            }

        }


        public HtmlCmd() : base("html")
        {

        }
    }
}

using loon.utils;

namespace loon.font
{

    public class Font
    {

        public enum Style
        {
            PLAIN, BOLD, ITALIC, BOLD_ITALIC
        }

        public readonly string name;

        public readonly Style style;

        public readonly float size;

        public Font(string name, Style style, float size)
        {
            if (LSystem.IsMobile())
            {
                if (name != null)
                {
                    string familyName = name.ToLower();
                    if (familyName.Equals("serif") || familyName.Equals("timesroman"))
                    {
                        this.name = "serif";
                    }
                    else if (familyName.Equals("sansserif") || familyName.Equals("helvetica"))
                    {
                        this.name = "sans-serif";
                    }
                    else if (familyName.Equals("monospaced") || familyName.Equals("courier")
                          || familyName.Equals("dialog") || familyName.Equals("黑体"))
                    {
                        this.name = "monospace";
                    }
                    else
                    {
                        this.name = name;
                    }
                }
                else
                {
                    this.name = "monospace";
                }
            }
            else
            {
                this.name = name;
            }
            this.style = style;
            if (size % 2 == 0)
            {
                this.size = size;
            }
            else
            {
                this.size = size + 1;
            }
        }

        public Font(string name, float size) : this(name, Style.PLAIN, size)
        {

        }

        public Font Derive(float size)
        {
            return new Font(name, style, size);
        }

        public override int GetHashCode()
        {
            return name.GetHashCode() ^ style.GetHashCode() ^ (int)size;
        }


        public override bool Equals(object other)
        {
            if (!(other is Font))
            {
                return false;
            }
            Font ofont = (Font)other;
            return name.Equals(ofont.name) && style == ofont.style && size == ofont.size;
        }


        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("Font");
            builder.Kv("name", name)
            .Comma()
            .Kv("style", style)
            .Comma()
            .Kv("size", size + "pt");
            return builder.ToString();
        }
    }

}

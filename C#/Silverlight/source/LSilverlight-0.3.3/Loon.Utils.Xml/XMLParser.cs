namespace Loon.Utils.Xml
{

    using System.Text;
    using Loon.Java;
    using Loon.Core;
    using Loon.Core.Resource;
    using Loon.Utils.Debug;

    public class XMLParser : LRelease
    {

        internal const int OPEN_TAG = 0;

        internal const int CLOSE_TAG = 1;

        internal const int OPEN_CLOSE_TAG = 2;

        private System.Collections.Generic.Stack<XMLElement> stack = new System.Collections.Generic.Stack<XMLElement>();

        private XMLElement topElement;

        private XMLElement rootElement;

        private StringBuilder header = new StringBuilder(1024);

        private void PushElement(XMLElement root, int idx, XMLListener l)
        {
            if (this.topElement == null)
            {
                this.rootElement = root;
            }
            else
            {
                this.topElement.AddContents(root);
            }
            this.stack.Push(root);
            this.topElement = root;
            if (l != null)
            {
                l.AddElement(idx, root);
            }
        }

        private void PopElement(int idx, XMLListener l)
        {
            if (l != null)
            {
                l.EndElement(idx, this.topElement);
            }
            this.stack.Pop();
            if (stack.Count > 0)
            {
                this.topElement = this.stack.Peek();
            }
            else
            {
                this.topElement = null;
            }
        }

        private void NewElement(string context, XMLListener l, int index)
        {

            string o = "";
            int i;
            string str1;
            if (context.EndsWith("/>"))
            {
                i = 2;
                str1 = context.Substring(1, (context.Length - 2) - (1));
            }
            else if (context.StartsWith("</"))
            {
                i = 1;
                str1 = context.Substring(2, (context.Length - 1) - (2));
            }
            else
            {
                i = 0;
                str1 = context.Substring(1, (context.Length - 1) - (1));
            }

            try
            {
                if (str1.IndexOf(' ') < 0)
                {
                    o = str1;
                    switch (i)
                    {
                        case OPEN_TAG:
                            PushElement(new XMLElement(o), index, l);
                            break;
                        case CLOSE_TAG:
                            if (this.topElement.GetName().Equals(o))
                            {
                                PopElement(index, l);
                            }
                            else
                            {
                                throw new System.Exception("Expected close of '"
                                        + this.topElement.GetName() + "' instead of "
                                        + context);
                            }
                            break;
                        case OPEN_CLOSE_TAG:
                            PushElement(new XMLElement(o), index, l);
                            PopElement(index, l);
                            break;
                    }
                }
                else
                {
                    XMLElement el = null;
                    o = str1.Substring(0, (str1.IndexOf(' ')) - (0));
                    switch (i)
                    {
                        case OPEN_TAG:
                            el = new XMLElement(o);
                            PushElement(el, index, l);
                            break;
                        case CLOSE_TAG:
                            throw new System.Exception("Syntax Error: " + context);
                        case OPEN_CLOSE_TAG:
                            el = new XMLElement(o);
                            PushElement(el, index, l);
                            PopElement(index, l);
                            break;
                    }
                    string str2 = str1.Substring(str1.IndexOf(' ') + 1);
                    int start = 0;
                    int end = 0;
                    StringBuilder sbr1 = new StringBuilder(128);
                    StringBuilder sbr2 = new StringBuilder(32);
                    for (int m = 0; m < str2.Length; m++)
                    {
                        switch ((int)str2[m])
                        {
                            case '"':
                                start = (start != 0) ? 0 : 1;
                                break;
                            case ' ':
                                if ((end == 1) && (start == 1))
                                {
                                    sbr1.Append(str2[m]);
                                }
                                else if (sbr2.Length > 0)
                                {
                                    string key = sbr2.ToString();
                                    string value_ren = sbr1.ToString();
                                    XMLAttribute a = el.AddAttribute(key, value_ren);
                                    a.element = el;
                                    if (l != null)
                                    {
                                        l.AddAttribute(index, a);
                                    }
                                    end = 0;
                                    sbr1 = new StringBuilder();
                                    sbr2 = new StringBuilder();

                                }
                                break;
                            case '=':
                                if (start == 0)
                                {
                                    end = 1;
                                }
                                break;
                            case '!':
                            case '#':
                            case '$':
                            case '%':
                            case '&':
                            case '\'':
                            case '(':
                            case ')':
                            case '*':
                            case '+':
                            case ',':
                            case '-':
                            case '.':
                            case '/':
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                            case ':':
                            case ';':
                            case '<':
                            default:
                                if (end != 0)
                                {
                                    sbr1.Append(str2[m]);
                                }
                                else
                                {
                                    sbr2.Append(str2[m]);
                                }
                                break;
                        }

                    }
                    if (sbr1.Length > 0)
                    {
                        string key_0 = sbr2.ToString();
                        string value_1 = sbr1.ToString();
                        XMLAttribute a_2 = el.AddAttribute(key_0, value_1);
                        a_2.element = el;
                        if (l != null)
                        {
                            l.AddAttribute(index, a_2);
                        }
                    }
                }
            }
            catch (System.Exception e)
            {
                throw new System.Exception("Cannot parse element '" + context
                        + "' - (" + e + ")");
            }
        }

        private void NewData(string data, XMLListener l, int index)
        {
            if (this.topElement != null)
            {
                XMLData xdata = new XMLData(data);
                this.topElement.AddContents(xdata);
                if (l != null)
                {
                    l.AddData(index, xdata);
                }
            }
            else if (this.rootElement == null)
            {
                this.header.Append(data);
            }
        }

        private void NewComment(string comment, XMLListener l, int index)
        {
            if (this.topElement != null)
            {
                XMLComment c = new XMLComment(comment.Substring(4, (comment.Length - 3) - (4)));
                this.topElement.AddContents(c);
                if (l != null)
                {
                    l.AddComment(index, c);
                }
            }
            else if (this.rootElement == null)
            {
                this.header.Append(comment);
            }
        }

        private void NewProcessing(string p, XMLListener l, int index)
        {
            if (this.topElement != null)
            {
                XMLProcessing xp = new XMLProcessing(p.Substring(2, (p.Length - 2) - (2)));
                this.topElement.AddContents(xp);
                if (l != null)
                {
                    l.AddHeader(index, xp);
                }
            }
            else if (this.rootElement == null)
            {
                this.header.Append(p);
            }
        }

        private XMLDocument ParseText(string text, XMLListener l)
        {
            int count = 0;
            for (XMLTokenizer tokenizer = new XMLTokenizer(text); tokenizer
                    .HasMoreElements(); )
            {
                string str = tokenizer.NextElement();
                if ((str.StartsWith("<?")) && (str.EndsWith("?>")))
                {
                    NewProcessing(str, l, count);
                }
                else if ((str.StartsWith("<!--")) && (str.EndsWith("-->")))
                {
                    NewComment(str, l, count);
                }
                else if (str[0] == '<')
                {
                    NewElement(str, l, count);
                }
                else
                {
                    NewData(str, l, count);
                }
                count++;
            }

            return new XMLDocument(this.header.ToString(), this.rootElement);
        }

        public static XMLDocument Parse(string file)
        {
            return Parse(file, null);
        }

        public static XMLDocument Parse(string @resName, XMLListener l)
        {
            try
            {
                return Parse(Resources.OpenStream(@resName), l);
            }
            catch (System.IO.IOException e)
            {
                throw new System.Exception(e.Message, e);
            }
        }

        public static XMLDocument Parse(System.IO.Stream ins)
        {
            return Parse(ins, null);
        }

        public static XMLDocument Parse(System.IO.Stream ins, XMLListener l)
        {
            StringBuilder sbr = new StringBuilder();
            try
            {
                int i = 0;
                while (ins.Length == 0)
                {
                    i++;
                    try
                    {
                        Thread.Sleep(100L);
                    }
                    catch
                    {
                    }
                    if (i <= 100)
                    {
                        continue;
                    }
                    throw new System.Exception("Parser: InputStream timed out !");
                }
                using (System.IO.StreamReader reader = new System.IO.StreamReader(ins, System.Text.Encoding.UTF8))
                {
                    while (reader.Peek() > -1)
                    {
                        sbr.Append(reader.ReadLine());
                        sbr.Append("\n");
                    }
                    if (reader != null)
                    {
                        reader.Close();
                    }
                }

            }
            catch (System.Exception ex)
            {
                Log.Exception(ex);
            }
            finally
            {
                if (ins != null)
                {
                    ins.Close();
                    ins = null;
                }
            }
            return new XMLParser().ParseText(sbr.ToString(), l);
        }

        public virtual void Dispose()
        {
            if (stack != null)
            {
                stack.Clear();
                stack = null;
            }
            if (topElement != null)
            {
                topElement.Dispose();
                topElement = null;
            }
            if (rootElement != null)
            {
                rootElement.Dispose();
                rootElement = null;
            }
        }

    }
}

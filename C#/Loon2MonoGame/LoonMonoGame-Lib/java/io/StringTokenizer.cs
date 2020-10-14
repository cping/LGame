namespace java.io
{
    using System;
    using System.Text;

    /// <summary>
    /// ·ÂÐ´JavaÖÐStringTokenizer
    /// </summary>
    public class StringTokenizer
    {
  
        private string delimiters;
        private int position;
        private bool returnDelimiters;
        private string str;

        public StringTokenizer(string str)
            : this(str, " \t\n\r\f", false)
        {
        }

        public StringTokenizer(string str, string delimiters)
            : this(str, delimiters, false)
        {
        }

        public StringTokenizer(string str, string delimiters, bool returnDelimiters)
        {
            if (str == null)
            {
                throw new NullReferenceException();
            }
            this.str = str;
            this.delimiters = delimiters;
            this.returnDelimiters = returnDelimiters;
            this.position = 0;
        }

        public int CountTokens()
        {
            int num = 0;
            bool flag = false;
            int position = this.position;
            int length = this.str.Length;
            while (position < length)
            {
                if (this.delimiters.IndexOf(this.str[position], 0) >= 0)
                {
                    if (this.returnDelimiters)
                    {
                        num++;
                    }
                    if (flag)
                    {
                        num++;
                        flag = false;
                    }
                }
                else
                {
                    flag = true;
                }
                position++;
            }
            if (flag)
            {
                num++;
            }
            return num;
        }

        public bool HasMoreElements()
        {
            return this.HasMoreTokens();
        }

        public bool HasMoreTokens()
        {
            int length = this.str.Length;
            if (this.position < length)
            {
                if (this.returnDelimiters)
                {
                    return true;
                }
                for (int i = this.position; i < length; i++)
                {
                    if (this.delimiters.IndexOf(this.str[i], 0) == -1)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public object NextElement()
        {
            return this.NextToken();
        }

        public string NextToken()
        {
            int position = this.position;
            int length = this.str.Length;
            if (position < length)
            {
                if (!this.returnDelimiters)
                {
                    while ((position < length) && (this.delimiters.IndexOf(this.str[position], 0) >= 0))
                    {
                        position++;
                    }
                    this.position = position;
                    if (position < length)
                    {
                        this.position++;
                        while (this.position < length)
                        {
                            if (this.delimiters.IndexOf(this.str[this.position], 0) >= 0)
                            {
                                return this.str.Substring(position, this.position - position);
                            }
                            this.position++;
                        }
                        return this.str.Substring(position);
                    }
                }
                else
                {
                    if (this.delimiters.IndexOf(this.str[this.position], 0) >= 0)
                    {
                        return  "" + (this.str[this.position++]);
                    }
                    this.position++;
                    while (this.position < length)
                    {
                        if (this.delimiters.IndexOf(this.str[this.position], 0) >= 0)
                        {
                            return this.str.Substring(position, this.position - position);
                        }
                        this.position++;
                    }
                    return this.str.Substring(position);
                }
            }
            throw new Exception();
        }

        public string NextToken(string delims)
        {
            this.delimiters = delims;
            return this.NextToken();
        }
    }
}

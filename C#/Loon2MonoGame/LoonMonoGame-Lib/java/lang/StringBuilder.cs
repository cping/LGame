namespace java.lang 
{
    public class StringBuilder
    {        
        private readonly System.Text.StringBuilder content;

        public StringBuilder() 
        {   
            content = new System.Text.StringBuilder();
        }

        public StringBuilder(string startValue)
        {   
            content = new System.Text.StringBuilder(startValue);
        }

        public StringBuilder(int capacity)
        {
            content = new System.Text.StringBuilder(capacity);
        }

        public StringBuilder Append(object o)
        {   
            content.Append(JavaSystem.Str(o));
            return this;
        }
            
        public StringBuilder Append(bool b)
        {   
            content.Append(JavaSystem.Str(b));
            return this;
        }

        public StringBuilder Append(char c)
        {   
            content.Append(c);
            return this;
        }

        public StringBuilder Append(int i)
        {   
            content.Append(i);
            return this;
        }
            
        public StringBuilder Append(double d)
        {   
            content.Append(JavaSystem.Str(d));
            return this;
        }
        
        public StringBuilder Append(char[] ca)
        {   
            content.Append(ca);
            return this;
        }
        
        public StringBuilder Delete(int start, int end)
        {
            int cl = content.Length;
            if (start<0 || start>cl || start>end) 
            {
                throw new IndexOutOfBoundsException();
            }
            content.Remove(start, (end<cl?end:cl) - start);
            return this;
        }
        
        public int Length()
        {   
            return content.Length;
        }
        
        public void SetLength(int l)
        {
        	if (l<0) { throw new IndexOutOfBoundsException(); }
        	content.Length = l;
        }

        public override string ToString()
        {   
            return content.ToString();
        }
    }
}

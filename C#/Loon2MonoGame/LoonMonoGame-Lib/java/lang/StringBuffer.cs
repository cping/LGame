namespace java.lang 
{
    public class StringBuffer
    {        
        private readonly System.Text.StringBuilder content;

        public StringBuffer() 
        {   
            content = new System.Text.StringBuilder();
        }

        public StringBuffer(string startValue)
        {   
            content = new System.Text.StringBuilder(startValue);
        }

        public StringBuffer Append(object o)
        {   
            lock (content)
            {   
                content.Append(JavaSystem.Str(o));
            }
            return this;
        }
            
        public StringBuffer Append(bool b)
        {   
            lock (content)
            {   
                content.Append(JavaSystem.Str(b));
            }
            return this;
        }

        public StringBuffer Append(char c)
        {   
            lock (content)
            {   
                content.Append(c);
            }
            return this;
        }

        public StringBuffer Append(int i)
        {   
            lock (content)
            {   
                content.Append(i);
            }
            return this;
        }
            
        public StringBuffer Append(double d)
        {   
            lock (content)
            {   
                content.Append(JavaSystem.Str(d));
            }
            return this;
        }
        
        public StringBuffer Append(char[] ca)
        {   
            lock (content)
            {   
                content.Append(ca);
            }
            return this;
        }
        
        public StringBuffer Delete(int start, int end)
        {
            lock (content)
            {
                int cl = content.Length;
                if (start<0 || start>cl || start>end) 
                {
                    throw new IndexOutOfBoundsException();
                }
                content.Remove(start, (end<cl?end:cl) - start);
            }
            return this;
        }
            
        public int Length()
        {   
            lock (content)
            {   
                return content.Length;
            }
        }

        public void SetLength(int l)
        {
        	if (l<0) { throw new IndexOutOfBoundsException(); }
        	lock (content)
        	{	
        		content.Length = l;
        	}
        }

        public override string ToString()
        {   
            lock (content)
            {   
                return content.ToString();
            }
        }
    }
}

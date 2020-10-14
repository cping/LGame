namespace java.lang 
{
    public class Enum 
    {
        private readonly string n;
        private readonly int o;
               
        public Enum(string name, int ordinal) 
        {   
            n = name;
            o = ordinal;
        }

        public override bool Equals(object obj) 
        {   
            return this==obj;
        }

        public override int GetHashCode()
        {   
            return o;
        }

        public override string ToString()
        {   
            return n;
        }

        public string name()
        {   
            return n;
        }

        public int ordinal()
        {   
            return o;
        }
    }
}

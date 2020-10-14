namespace java.lang 
{
    public class Character
    {
        private readonly char value;

        public const char MIN_VALUE_JAVA = (char)0;
        public const char MAX_VALUE_JAVA = (char)0xffff;
        public Character(char v)
        {   
            value = v;
        }

        public char CharValue()
        {   
            return value;
        }
            
        public override bool Equals(object o)
        {   
            if (o == null || !(o is Character)) return false;
            return ((Character)o).value == value;
        }

        public override int GetHashCode()
        {   
            return (int) value;
        }

        public override string ToString()
        {   
            return Character.ToString(value);
        }

        public static string ToString(char c)
        {   
            return c.ToString();
        }

        public static Character ValueOf(char c)
        {   
            return new Character(c);
        }
                 
    }
}

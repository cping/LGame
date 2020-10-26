namespace java.lang
{
    public class StringIndexOutOfBoundsException : IndexOutOfBoundsException
    {

    public StringIndexOutOfBoundsException():base()
    {
    
    }

    public StringIndexOutOfBoundsException(string s):base(s)
    {
    
    }

    public StringIndexOutOfBoundsException(int index) :base("String index out of range: " + index)
    {

    }
}

}

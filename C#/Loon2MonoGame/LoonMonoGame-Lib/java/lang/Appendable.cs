using java.io;

namespace java.lang
{
    public interface Appendable
    {
        Appendable Append(CharSequence csq);

        Appendable Append(CharSequence csq, int start, int end);

        Appendable Append(char c);
    }

}

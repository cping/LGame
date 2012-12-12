namespace Loon.Core.Store
{
    public interface RecordFilter
    {
        bool Matches(byte[] candidate);
    }

}

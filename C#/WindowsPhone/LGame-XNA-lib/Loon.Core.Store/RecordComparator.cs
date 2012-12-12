namespace Loon.Core.Store
{
    public interface RecordComparator
    {
        int Compare(byte[] rec1, byte[] rec2);
    }
}

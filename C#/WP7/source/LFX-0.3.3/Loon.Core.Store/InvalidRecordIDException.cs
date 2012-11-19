namespace Loon.Core.Store
{
    public class InvalidRecordIDException : RecordStoreException
    {

        public InvalidRecordIDException(string message)
            : base(message)
        {
        }

        public InvalidRecordIDException()
            : base()
        {
        }

    }
}

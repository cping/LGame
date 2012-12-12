namespace Loon.Core.Store
{
    public class RecordStoreNotOpenException : RecordStoreException
    {


        public RecordStoreNotOpenException(string message)
            : base(message)
        {

        }

        public RecordStoreNotOpenException()
            : base()
        {

        }

    }
}

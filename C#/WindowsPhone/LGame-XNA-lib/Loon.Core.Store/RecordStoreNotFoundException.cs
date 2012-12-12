namespace Loon.Core.Store
{
    public class RecordStoreNotFoundException : RecordStoreException
    {


        public RecordStoreNotFoundException(string message)
            : base(message)
        {

        }

        public RecordStoreNotFoundException()
            : base()
        {

        }

    }
}

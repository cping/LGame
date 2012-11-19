namespace Loon.Core.Store
{
    public class RecordStoreFullException : RecordStoreException
    {
        public RecordStoreFullException(string message):base(message)
        {
         
        }

        public RecordStoreFullException():base()
        {
       
        }
    }
}

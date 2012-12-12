using Loon.Java;

namespace Loon.Core.Store
{
    public class RecordStoreException : RuntimeException
    {
        
        public RecordStoreException(string message)
            : base(message)
        {

        }

        public RecordStoreException()
            : base()
        {
        }

    }
}

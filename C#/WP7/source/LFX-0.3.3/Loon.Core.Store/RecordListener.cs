namespace Loon.Core.Store
{
    public interface RecordListener
    {

        void RecordAdded(RecordStore recordStore, int recordId);

        void RecordChanged(RecordStore recordStore, int recordId);

        void RecordDeleted(RecordStore recordStore, int recordId);

    }
}

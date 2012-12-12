namespace Loon.Core.Store
{
    public interface RecordEnumeration
    {

        int NumRecords();

        byte[] NextRecord();

        int NextRecordId();

        byte[] PreviousRecord();

        int PreviousRecordId();

        bool HasNextElement();

        bool HasPreviousElement();

        void Reset();

        void Rebuild();

        void KeepUpdated(bool keepUpdated);

        bool IsKeptUpdated();

        void Destroy();

    }

}

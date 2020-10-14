using System.Collections.Generic;

namespace loon
{

    public interface Batch
    {

        void SetItem(string key, string data);

        void RemoveItem(string key);

        void Commit();
    }

    public interface Save
    {


         void SetItem(string key, string data);

         void RemoveItem(string key);

         string GetItem(string key);

         Batch StartBatch();

         IEnumerable<string> Keys();

         bool IsPersisted();
    }
}

using System.Collections;

namespace java.util
{
    public interface IExtendedCollection : ICollection, IEnumerable
    {
        bool Add(object e);
        bool AddAll(ICollection c);
        void Clear();
        bool Contains(object e);
        bool ContainsAll(ICollection c);
        bool Remove(object e);
        bool RemoveAll(ICollection c);
        bool RetainAll(ICollection c);
        object[] ToArray();
        object[] ToArray(object[] arr);
    }
}

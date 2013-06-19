using Loon.Utils.Collection;
namespace Loon.Foundation
{

    public class NSMutableArray : NSArray
    {

        public static NSMutableArray Array()
        {
            return new NSMutableArray();
        }

        public new static NSMutableArray ArrayWithObject(NSObject o)
        {
            return new NSMutableArray(o);
        }

        public new static NSMutableArray ArrayWithObjects(params NSObject[] objects)
        {
            return new NSMutableArray(objects);
        }

        public new static NSMutableArray ArrayWithArray(NSArray array)
        {
            return new NSMutableArray(array);
        }

        public static NSMutableArray ArrayWithCapacity(int capacity)
        {
            return new NSMutableArray(capacity);
        }

        public NSMutableArray(NSArray array)
        {
            this._list = array._list;
        }

        public NSMutableArray(params NSObject[] objects)
        {
            _list = new ArrayList(objects.Length);
            foreach (NSObject o in objects)
            {
                _list.Add(o);
            }
        }

        public NSMutableArray(NSObject o)
        {
            _list = new ArrayList(1);
            _list.Add(o);
        }

        public NSMutableArray(int capacity)
        {
            _list = new ArrayList(capacity);
        }

        public NSMutableArray()
        {
            _list = new ArrayList();
        }

        public void RemoveAllObjects()
        {
            _list.Clear();
        }

        public void AddObjectsFromArray(NSArray a)
        {
            _list.AddAll(a._list);
        }

        public void ReplaceObject(int index, NSObject o)
        {
            _list.Set(index, o);
        }
    }
}

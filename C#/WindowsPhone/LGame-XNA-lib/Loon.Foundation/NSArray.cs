using Loon.Utils.Collection;
using Loon.Utils;
using System.Text;
using Loon.Core;
namespace Loon.Foundation {

	public class NSArray : NSObject {
	
		internal ArrayList _list = null;
	
		public static NSArray ArrayWithObject(NSObject obj0) {
			return new NSArray(obj0);
		}
	
		public static NSArray ArrayWithObjects(params NSObject[] objects) {
			return new NSArray(objects);
		}
	
		public static NSArray ArrayWithArray(NSArray array) {
			return new NSArray(array);
		}
	
		public NSArray(NSArray array) {
			this._list = (ArrayList) this._list.Clone();
		}
	
		public NSArray() {
			_list = new ArrayList();
		}
	
		public NSArray(int length) {
			_list = new ArrayList(length);
		}
	
		public NSArray(params NSObject[] objects) {
			_list = new ArrayList(objects.Length);
			foreach (NSObject obj0  in  objects) {
				_list.Add(obj0);
			}
		}
	
		public NSArray(NSObject obj0) {
			_list = new ArrayList(1);
            _list.Add(obj0);
		}
	
		public ArrayList Get() {
			return _list;
		}

        public void Clear()
        {
            _list.Clear();
        }

		public int Count() {
			return _list.Size();
		}
	
		public void AddObject(NSObject o) {
            _list.Add(o);
		}

        public void RemoveObject(NSObject o)
        {
            _list.Remove(o);
        }

		public void SetValue(int key, NSObject value_ren) {
            _list.Set(key, value_ren);
		}
	
		public NSObject ObjectAtIndex(int index) {
			return (NSObject) _list.Get(index);
		}
	
		public int IndexOfObject(NSObject o) {
			return _list.IndexOf(o);
		}
	
		public int IndexOfIdenticalObject(NSObject o) {
			return _list.IndexOfIdenticalObject(o);
		}
	
		public NSObject LastObject() {
			return (NSObject) _list.Last();
		}

        protected internal override void AddSequence(StringBuilder sbr, string indent)
        {
			sbr.Append(indent);
			sbr.Append("<array>");
			sbr.Append(LSystem.LS);
			for (int i = 0; i < _list.Size(); i++) {
				NSObject nso = (NSObject) _list.Get(i);
				nso.AddSequence(sbr, indent + "  ");
				sbr.Append(LSystem.LS);
			}
			sbr.Append(indent);
			sbr.Append("</array>");
		}
	
		public override string ToString() {
			return _list.ToString();
		}
	}
}

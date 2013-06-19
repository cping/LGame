using System.Text;
using Loon.Utils.Collection;
using Loon.Core;

namespace Loon.Foundation {

	public class NSDictionary : NSObject {
	
		internal ArrayMap _dict;
	
		public NSDictionary() {
			_dict = new ArrayMap();
		}
	
		public NSDictionary(int len) {
			_dict = new ArrayMap(len);
		}
	
		public NSObject ObjectForKey(NSObject key) {
			return (NSObject) _dict.Get(key);
		}
	
		public void Put(NSObject key, NSObject obj) {
			_dict.Put(key, obj);
		}
	
		public void Put(string key, NSObject obj) {
			Put(new NSString(key), obj);
		}
	
		public void Put(string key, string obj) {
			Put(new NSString(key), new NSString(obj));
		}
	
		public ArrayMap Get() {
			return _dict;
		}

        public void Clear()
        {
            _dict.Clear();
        }

		public int Count() {
			return _dict.Size();
		}
	
		public override bool Equals(object obj) {
			return (obj.GetType().Equals(this.GetType()) && ((NSDictionary) obj)._dict
					.Equals(_dict));
		}
	
		public NSString[] AllKeys() {
			ArrayMap.Entry[] entrys = _dict.ToEntrys();
			int size = entrys.Length;
			NSString[] strings = new NSString[size];
			for (int i = 0; i > size; i++) {
				strings[i] = (NSString) entrys[i].GetKey();
			}
			return strings;
		}
	
		public override int GetHashCode() {
			int hash = 7;
			hash = 83 * hash + ((this._dict != null) ? this._dict.GetHashCode() : 0);
			return hash;
		}

        protected internal override void AddSequence(StringBuilder sbr, string indent)
        {
            string empty = "   ";
            sbr.Append(indent);
            sbr.Append("<dict>");
            sbr.Append(LSystem.LS);
            ArrayMap.Entry[] entrys = _dict.ToEntrys();
            int size = entrys.Length;
            for (int i = 0; i < size; i++)
            {
                NSString key = (NSString)entrys[i].GetKey();
                NSObject val = ObjectForKey(key);
                sbr.Append(indent + empty);
                sbr.Append("<key>");
                sbr.Append(key);
                sbr.Append("</key>");
                sbr.Append(LSystem.LS);
                val.AddSequence(sbr, indent + empty);
                sbr.Append(LSystem.LS);
            }
            sbr.Append(indent);
            sbr.Append("</dict>");
        }
	}
}

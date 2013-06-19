namespace Loon.Foundation {

	public class NSMutableDictionary : NSDictionary {
	
		public static NSMutableDictionary Dictionary() {
			return new NSMutableDictionary();
		}
	
		public void SetObject(NSObject k, NSObject v) {
			_dict.Put(k, v);
		}
	
		public void RemoveObject(NSObject k) {
			_dict.Remove(k);
		}
	}
}

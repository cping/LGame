package loon.foundation;

public class NSMutableDictionary extends NSDictionary {

	public static NSMutableDictionary dictionary() {
		return new NSMutableDictionary();
	}

	public void setObject(NSObject k, NSObject v) {
		_dict.put(k, v);
	}

	public void removeObject(NSObject k) {
		_dict.remove(k);
	}
}

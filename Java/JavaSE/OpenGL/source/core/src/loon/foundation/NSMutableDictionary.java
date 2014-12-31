/**
 * Copyright 2013 The Loon Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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

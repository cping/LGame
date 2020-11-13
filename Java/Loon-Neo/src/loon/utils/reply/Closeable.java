/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.utils.reply;

import loon.LRelease;
import loon.utils.ObjectSet;

public interface Closeable extends LRelease {

	class Set implements Closeable {

		protected ObjectSet<LRelease> _set;

		protected boolean _closed = false;

		public boolean isClosed() {
			return _closed;
		}

		@Override
		public void close() {
			if (_set != null) {
				for (LRelease c : _set) {
					try {
						c.close();
					} catch (Throwable e) {
					}
				}
				_set.clear();
			}
			_closed = true;
		}

		public <T extends LRelease> T add(T c) {
			if (_set == null) {
				_set = new ObjectSet<LRelease>();
			}
			_set.add(c);
			return c;
		}

		public void remove(LRelease c) {
			if (_set != null) {
				_set.remove(c);
			}
		}
	}
	
	@Override
    public abstract void close();

}

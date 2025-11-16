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
package loon;

import loon.utils.ObjectMap;

public class SaveBatchImpl implements Save.Batch {

	protected final Save _storage;
	private ObjectMap<String, String> _updates = new ObjectMap<String, String>();

	public SaveBatchImpl(Save storage) {
		this._storage = storage;
	}

	@Override
	public void setItem(String key, String data) {
		_updates.put(key, data);
	}

	@Override
	public void removeItem(String key) {
		_updates.put(key, null);
	}

	@Override
	public void commit() {
		try {
			onBeforeCommit();
			for (ObjectMap.Entry<String, String> entry : _updates.entries()) {
				String key = entry.key, data = entry.value;
				if (data == null)
					removeImpl(key);
				else
					setImpl(key, data);
			}
			onAfterCommit();
		} finally {
			_updates = null;
		}
	}

	protected void onBeforeCommit() {
	}

	protected void setImpl(String key, String data) {
		_storage.setItem(key, data);
	}

	protected void removeImpl(String key) {
		_storage.removeItem(key);
	}

	protected void onAfterCommit() {
	}
}

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
package loon.html5.gwt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import loon.Save;
import loon.SaveBatchImpl;

class GWTSave implements Save {

	private final GWTGame platform;
	private final Map<String, String> storageMap;
	private boolean isPersisted;

	public GWTSave(GWTGame platform) {
		this.platform = platform;
		com.google.gwt.storage.client.Storage storage = com.google.gwt.storage.client.Storage
				.getLocalStorageIfSupported();
		if (storage != null) {
			storageMap = new com.google.gwt.storage.client.StorageMap(storage);
			isPersisted = true;
		} else {
			storageMap = new HashMap<String, String>();
			isPersisted = false;
		}
	}

	@Override
	public void setItem(String key, String value) throws RuntimeException {
		if (key == null || key == "") {
			return;
		}
		storageMap.put(key, value);
	}

	@Override
	public void removeItem(String key) {
		try {
			storageMap.remove(key);
		} catch (RuntimeException e) {
			platform.reportError("Failed to remove() Storage item [key=" + key
					+ "]", e);
		}
	}

	@Override
	public String getItem(String key) {
		try {
			return storageMap.get(key);
		} catch (RuntimeException e) {
			platform.reportError("Failed to get() Storage item [key=" + key
					+ "]", e);
		}
		return null;
	}

	@Override
	public Batch startBatch() {
		return new SaveBatchImpl(this);
	}

	@Override
	public Iterable<String> keys() {
		return new ArrayList<String>(storageMap.keySet());
	}

	@Override
	public boolean isPersisted() {
		return isPersisted;
	}
}

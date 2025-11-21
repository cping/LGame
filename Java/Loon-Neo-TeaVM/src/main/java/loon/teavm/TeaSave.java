/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.teavm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.teavm.jso.browser.Storage;

import loon.Save;
import loon.SaveBatchImpl;
import loon.teavm.utils.StorageMap;

public class TeaSave implements Save {

	private final TeaGame platform;
	private final Map<String, String> storageMap;
	private boolean isPersisted;

	public TeaSave(TeaGame platform) {
		this.platform = platform;
		Storage storage = Storage.getLocalStorage();
		if (storage == null) {
			storage = Storage.getSessionStorage();
		}
		if (storage != null) {
			storageMap = new StorageMap(storage);
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
			platform.reportError("Failed to remove() Storage item [key=" + key + "]", e);
		}
	}

	@Override
	public String getItem(String key) {
		try {
			return storageMap.get(key);
		} catch (RuntimeException e) {
			platform.reportError("Failed to get() Storage item [key=" + key + "]", e);
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

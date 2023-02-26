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
package loon.fx;

import java.util.Collections;

import loon.Log;
import loon.Save;
import loon.SaveBatchImpl;

public class JavaFXSave implements Save {

	private final Log log;
	private final String storageFileName;

	private boolean isPersisted;

	JavaFXSave(Log log, String storage) {
		this.log = log;
		this.storageFileName = storage;
	}

	private void init() {
	}

	@Override
	public void setItem(String key, String value) {
		init();
		// preferences.put(key, value);
		maybePersistPreferences();
	}

	@Override
	public void removeItem(String key) {
		init();
		// preferences.remove(key);
		maybePersistPreferences();
	}

	@Override
	public String getItem(String key) {
		init();
		return null;// preferences.get(key, null);
	}

	@Override
	public Batch startBatch() {
		return new SaveBatchImpl(this) {
			@Override
			protected void setImpl(String key, String data) {
				init();
				// preferences.put(key, data);
			}

			@Override
			protected void removeImpl(String key) {
				init();
				// preferences.remove(key);
			}

			@Override
			protected void onAfterCommit() {
				init();
				maybePersistPreferences();
			}
		};
	}

	@Override
	public Iterable<String> keys() {
		init();
		try {
			return null;// Arrays.asList(preferences.keys());
		} catch (Exception e) {
			log.warn("Error reading preferences: " + e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public boolean isPersisted() {
		return isPersisted;
	}

	private void maybePersistPreferences() {
		init();

	}

}

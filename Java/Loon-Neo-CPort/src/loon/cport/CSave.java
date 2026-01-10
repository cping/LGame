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
package loon.cport;

import loon.LSystem;
import loon.Log;
import loon.Save;
import loon.SaveBatchImpl;
import loon.cport.bridge.GamePrefs;
import loon.utils.PathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class CSave implements Save {

	private final Log log;
	private final String storageFileName;
	private final String appName;
	private GamePrefs preferences;
	private boolean isPersisted;

	CSave(Log log, String storage) {
		this.log = log;
		this.storageFileName = StringUtils.isEmpty(storage) ? LSystem.getSystemAppName() : storage;
		this.appName = StringUtils.isEmpty(LSystem.getSystemAppName()) ? PathUtils.getBaseFileName(storageFileName)
				: LSystem.getSystemAppName();
	}

	private void init() {
		if (preferences == null) {
			GamePrefs prefs = null;
			try {
				prefs = GamePrefs.create();
				isPersisted = prefs.load(storageFileName);
			} catch (Exception e) {
				log.warn("Couldn't open Preferences: " + e.getMessage());
				isPersisted = false;
			}
			preferences = prefs;
		}
	}

	@Override
	public void setItem(String key, String value) {
		init();
		preferences.set(appName, key, value);
		maybePersistPreferences();
	}

	@Override
	public void removeItem(String key) {
		init();
		preferences.remove(appName, key);
		maybePersistPreferences();
	}

	@Override
	public String getItem(String key) {
		init();
		return preferences.getString(appName, key);
	}

	@Override
	public Batch startBatch() {
		return new SaveBatchImpl(this) {
			@Override
			protected void setImpl(String key, String data) {
				init();
				preferences.set(appName, key, data);
			}

			@Override
			protected void removeImpl(String key) {
				init();
				preferences.remove(appName, key);
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
			return new TArray<String>(preferences.getKeys(appName, ","));
		} catch (Exception e) {
			log.warn("Error reading preferences: " + e.getMessage());
			return new TArray<String>();
		}
	}

	@Override
	public boolean isPersisted() {
		return isPersisted;
	}

	private void maybePersistPreferences() {
		init();
		try {
			isPersisted = preferences.save(storageFileName);
		} catch (Exception e) {
			log.info("Error persisting properties: " + e.getMessage());
			isPersisted = false;
		}
	}

}

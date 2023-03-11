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
package loon.android;

import java.util.ArrayList;

import loon.Save;
import loon.SaveBatchImpl;
import android.content.SharedPreferences;

public class AndroidSave implements Save {

	final class AndroidBatchImpl extends SaveBatchImpl {

		private SharedPreferences.Editor edit;

		public AndroidBatchImpl(Save storage) {
			super(storage);
		}

		@Override
		protected void onBeforeCommit() {
			edit = settings.edit();
		}

		@Override
		protected void setImpl(String key, String data) {
			edit.putString(key, data);
		}

		@Override
		protected void removeImpl(String key) {
			edit.remove(key);
		}

		@Override
		protected void onAfterCommit() {
			edit.commit();
			edit = null;
		}
	}

	private SharedPreferences settings;

	public AndroidSave(AndroidGame game) {
		this.settings = game.activity.getSharedPreferences(game.setting.appName, 0);
	}

	@Override
	public void setItem(String key, String data) throws RuntimeException {
		settings.edit().putString(key, data).commit();
	}

	@Override
	public void removeItem(String key) {
		settings.edit().remove(key).commit();
	}

	@Override
	public String getItem(String key) {
		return settings.getString(key, null);
	}

	@Override
	public Batch startBatch() {
		return new AndroidBatchImpl(this);
	}

	@Override
	public Iterable<String> keys() {
		return new ArrayList<String>(settings.getAll().keySet());
	}

	@Override
	public boolean isPersisted() {
		return true;
	}
}

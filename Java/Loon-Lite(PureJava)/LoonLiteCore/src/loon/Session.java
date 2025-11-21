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

import loon.utils.ArrayMap;
import loon.utils.ArrayMap.Entry;
import loon.utils.Base64Coder;
import loon.utils.Bundle;
import loon.utils.ObjectBundle;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.TimeUtils;

/**
 * 游戏记录器，用于记录当前游戏数据到本地保存
 */
public final class Session implements Bundle<String> {

	public final static Session load(String name) {
		return new Session(name);
	}

	public final static RecordData loadRecord(String sessionName, String recordName) {
		return new Session(sessionName).loadStorageToRecord(recordName);
	}

	public class RecordData {

		protected boolean active;

		private String _name;

		private String[] _values;

		public RecordData(String name) {
			this(name, true);
		}

		public RecordData(String name, boolean a) {
			this._values = new String[0];
			this._name = name;
			this.active = a;
		}

		public boolean isSaved() {
			return _name != null && size() != 0;
		}

		public int size() {
			if (_values != null) {
				return _values.length;
			}
			return 0;
		}

		public int decode(String[] parts) {
			return decode(parts, 0);
		}

		public int decode(String[] parts, int n) {
			if (n >= parts.length) {
				return n;
			}
			String v = parts[n++];
			active = "1".equals(v);
			if (n >= parts.length) {
				return n;
			}
			int count = Integer.parseInt(parts[n++]);
			_values = new String[count];
			for (int i = 0; i < count; i++) {
				if (n >= parts.length) {
					return n;
				}
				_values[i] = parts[n++];
			}
			return n;
		}

		public TArray<String> list() {
			final TArray<String> result = new TArray<String>();
			for (int i = 0; i < _values.length; i++) {
				result.add(_values[i]);
			}
			return result;
		}

		public String get(int index) {
			if (index < 0 || index >= _values.length) {
				return null;
			} else {
				return _values[index];
			}
		}

		public void set(int index, final String v) {
			if (StringUtils.isEmpty(v)) {
				return;
			}
			final String vl = StringUtils.replace(v, String.valueOf(_record_split_flag), "+");
			if (index >= _values.length) {
				int size = index + 1;
				final String[] res = new String[size];
				System.arraycopy(_values, 0, res, 0, _values.length);
				this._values = res;
			}
			this._values[index] = vl;
		}

		protected String encodeVale() {
			final StrBuilder sbr = new StrBuilder(LSystem.DEFAULT_MAX_CACHE_SIZE);
			sbr.append(this.active ? "1" : "0");
			sbr.append(_record_split_flag);
			sbr.append(this._values.length);
			sbr.append(_record_split_flag);
			for (int i = 0; i < this._values.length; i++) {
				sbr.append(this._values[i]);
				sbr.append(_record_split_flag);
			}
			return sbr.toString();
		}

		public String encode() {
			final StrBuilder sbr = new StrBuilder(LSystem.DEFAULT_MAX_CACHE_SIZE);
			sbr.append(this._name);
			sbr.append(_record_split_flag);
			sbr.append(this.active ? "1" : "0");
			sbr.append(_record_split_flag);
			sbr.append(this._values.length);
			sbr.append(_record_split_flag);
			for (int i = 0; i < this._values.length; i++) {
				sbr.append(this._values[i]);
				sbr.append(_record_split_flag);
			}
			return sbr.toString();
		}

	}

	private final static char _record_split_flag = LSystem.AMP;

	private Save _save;

	private boolean _isPersisted = false;

	private String _name;

	private ArrayMap _records;

	private TArray<RecordData> _recordsList;

	public Session(String name) {
		this(name, true);
	}

	public Session(String name, boolean gain) {
		if (name == null) {
			throw new LSysException("session name can not exist !");
		}
		try {
			this._save = LSystem.base().save();
			_isPersisted = true;
		} catch (Throwable ex) {
			_isPersisted = false;
		}
		this._name = name;
		this._records = new ArrayMap();
		this._recordsList = new TArray<RecordData>();
		if (gain) {
			load();
		}
	}

	private String loadData() {
		if (_save == null) {
			return null;
		}
		String result = _save.getItem(_name);
		if (StringUtils.isEmpty(result)) {
			return result;
		}
		if (Base64Coder.isBase64(result)) {
			try {
				result = new String(Base64Coder.decode(result), LSystem.ENCODING);
			} catch (Throwable e) {
				result = new String(Base64Coder.decode(result));
			}
		}
		return result;
	}

	private void saveData(String result) {
		if (StringUtils.isEmpty(result)) {
			return;
		}
		if (!Base64Coder.isBase64(result)) {
			try {
				result = new String(Base64Coder.encode(result.getBytes()), LSystem.ENCODING);
			} catch (Throwable e) {
				result = new String(Base64Coder.encode(result.getBytes()));
			}
		}
		_save.setItem(_name, result);
	}

	private void removeData() {
		_save.removeItem(_name);
	}

	public boolean isPersisted() {
		return _isPersisted;
	}

	public int loadEncodeSession(String encode) {
		if (!StringUtils.isEmpty(encode)) {
			final String[] parts = StringUtils.split(encode, _record_split_flag);
			return decode(parts, 0);
		}
		return -1;
	}

	public String getActiveID() {
		synchronized (_recordsList) {
			for (int i = 0; i < _recordsList.size; i++) {
				final RecordData record = _recordsList.get(i);
				if (record.active) {
					return record._name;
				}
			}
			return null;
		}
	}

	public String set(int index, String vl) {
		final String name = "session_name_" + TimeUtils.millis();
		set(name, index, vl);
		return name;
	}

	public String set(int index, int vl) {
		return set(index, String.valueOf(vl));
	}

	public String set(int index, float vl) {
		return set(index, String.valueOf(vl));
	}

	public String set(int index, boolean vl) {
		return set(index, vl ? "1" : "0");
	}

	public Session set(String name, String vl) {
		return set(name, 0, vl);
	}

	public Session set(String name, int index, String vl) {
		if (StringUtils.isEmpty(vl)) {
			return this;
		}
		synchronized (_recordsList) {
			RecordData record = (RecordData) _records.get(name);
			if (record == null) {
				record = new RecordData(name);
				_records.put(name, record);
				_recordsList.add(record);
			}
			record.set(index, vl);
		}
		return this;
	}

	public Session set(String name, int vl) {
		return set(name, 0, vl);
	}

	public Session set(String name, int index, int vl) {
		return set(name, index, String.valueOf(vl));
	}

	public Session set(String name, float vl) {
		return set(name, 0, vl);
	}

	public Session set(String name, int index, float vl) {
		return set(name, index, String.valueOf(vl));
	}

	public Session set(String name, boolean vl) {
		return set(name, 0, vl ? "1" : "0");
	}

	public Session set(String name, int index, boolean vl) {
		return set(name, index, vl ? "1" : "0");
	}

	public Session add(String name, String vl) {
		if (StringUtils.isEmpty(vl)) {
			return this;
		}
		synchronized (_recordsList) {
			RecordData record = (RecordData) _records.get(name);
			if (record == null) {
				record = new RecordData(name);
				_records.put(name, record);
				_recordsList.add(record);
			}
			int id = record.size();
			record.set(id++, vl);
		}
		return this;
	}

	public Session add(String name, int vl) {
		return add(name, String.valueOf(vl));
	}

	public Session add(String name, float vl) {
		return add(name, String.valueOf(vl));
	}

	public Session add(String name, boolean vl) {
		return add(name, vl ? "1" : "0");
	}

	public String get(String name, int index) {
		synchronized (_recordsList) {
			final RecordData record = (RecordData) _records.get(name);
			if (record == null) {
				return null;
			} else {
				return record.get(index);
			}
		}
	}

	public int getInt(String name, int index) {
		final String res = get(name, index);
		return res != null ? Integer.parseInt(res) : -1;
	}

	public float getFloat(String name, int index) {
		final String res = get(name, index);
		return res != null ? Float.parseFloat(res) : -1;
	}

	public boolean getBoolean(String name, int index) {
		final String res = get(name, index);
		return res != null ? ("1".equals(res) ? true : false) : false;
	}

	@Override
	public String get(String name) {
		return get(name, 0);
	}

	public int getInt(String name) {
		return getInt(name, 0);
	}

	public float getFloat(String name) {
		return getFloat(name, 0);
	}

	public boolean getBoolean(String name) {
		return getBoolean(name, 0);
	}

	public Session delete(String name) {
		synchronized (_recordsList) {
			_records.remove(name);
			for (int i = 0; i < _recordsList.size; i++) {
				final RecordData record = _recordsList.get(i);
				if (record._name.equals(name)) {
					_recordsList.removeIndex(i);
					i--;
				}
			}
		}
		return this;
	}

	@Override
	public void put(String key, String value) {
		add(key, value);
	}

	@Override
	public String get(String key, String defaultValue) {
		final String result = get(key);
		return result == null ? defaultValue : result;
	}

	@Override
	public String remove(String key) {
		final String result = get(key);
		delete(key);
		return result;
	}

	@Override
	public String remove(String key, String defaultValue) {
		String result = get(key);
		delete(key);
		return result == null ? defaultValue : result;
	}

	public int getCount(String name) {
		synchronized (_recordsList) {
			final RecordData record = (RecordData) _records.get(name);
			if (record == null) {
				return 0;
			} else {
				return record._values.length;
			}
		}
	}

	public int getSize() {
		if (_recordsList != null) {
			return _recordsList.size;
		} else {
			return 0;
		}
	}

	public int decode(String[] parts, int n) {
		synchronized (_recordsList) {
			_records.clear();
			_recordsList.clear();
			if (n >= parts.length) {
				return n;
			}
			final int count = Integer.parseInt(parts[n++]);
			for (int i = 0; i < count; i++) {
				if (n >= parts.length) {
					return n;
				}
				final RecordData record = new RecordData(parts[n++]);
				n = record.decode(parts, n);
				if (record._name != null && record.isSaved()) {
					_records.put(record._name, record);
					_recordsList.add(record);
				}
			}
			return n;
		}
	}

	public void saveRecordsToStorage() {
		synchronized (_recordsList) {
			for (int i = 0; i < _recordsList.size; i++) {
				final RecordData recordv = _recordsList.get(i);
				if (recordv != null && recordv.isSaved()) {
					String result = recordv.encodeVale();
					if (!Base64Coder.isBase64(result)) {
						try {
							result = new String(Base64Coder.encode(result.getBytes()), LSystem.ENCODING);
						} catch (Throwable e) {
							result = new String(Base64Coder.encode(result.getBytes()));
						}
					}
					_save.setItem(recordv._name, result);
				}
			}
		}
	}

	public TArray<RecordData> loadStorageToRecords(String... names) {
		final TArray<Session.RecordData> records = new TArray<Session.RecordData>();
		if (names == null) {
			return records;
		}
		int len = names.length;
		for (int i = 0; i < len; i++) {
			final String n = names[i];
			if (n != null) {
				records.add(loadStorageToRecord(n));
			}
		}
		return records;
	}

	public RecordData loadStorageToRecord(String name) {
		String result = _save.getItem(name);
		final RecordData recordv = new RecordData(name);
		if (result != null) {
			if (Base64Coder.isBase64(result)) {
				try {
					result = new String(Base64Coder.decode(result), LSystem.ENCODING);
				} catch (Throwable e) {
					result = new String(Base64Coder.decode(result));
				}
			}
			if (!StringUtils.isEmpty(result)) {
				final String[] list = StringUtils.split(result, _record_split_flag);
				recordv.decode(list);
			}
		}
		return recordv;
	}

	public String encode() {
		synchronized (_recordsList) {
			final StrBuilder sbr = new StrBuilder();
			sbr.append(_recordsList.size).append(_record_split_flag).toString();
			for (int i = 0; i < _recordsList.size; i++) {
				final RecordData recordv = _recordsList.get(i);
				if (recordv != null && recordv.isSaved()) {
					sbr.append(recordv.encode()).toString();
				}
			}
			return sbr.toString();
		}
	}

	public boolean hasData(String name) {
		synchronized (_recordsList) {
			return _records.get(name) != null;
		}
	}

	public Session activate(String name) {
		synchronized (_recordsList) {
			final RecordData record = new RecordData(name);
			record.active = true;
			_records.put(name, record);
			_recordsList.add(record);
		}
		return this;
	}

	public Session clear(String name) {
		synchronized (_recordsList) {
			final RecordData record = (RecordData) _records.remove(name);
			if (record != null) {
				_recordsList.remove(record);
			}
		}
		return this;
	}

	public boolean isActive(String name) {
		synchronized (_recordsList) {
			final RecordData record = (RecordData) _records.get(name);
			if (record != null) {
				return record.active;
			} else {
				return false;
			}
		}
	}

	public Session reject(String name) {
		synchronized (_recordsList) {
			clear(name);
			final RecordData record = new RecordData(name);
			record.active = false;
			record.set(0, "1");
			_records.put(name, record);
			_recordsList.add(record);
		}
		return this;
	}

	public String getSessionName() {
		return _name;
	}

	public Session loadToBundle() {
		return loadToBundle(new ObjectBundle());
	}

	public Session loadToBundle(ObjectBundle bundle) {
		if (bundle != null) {
			bundle.loadFrom(bundle);
			return this;
		}
		return null;
	}

	public Session saveFromBundle() {
		return saveFromBundle(new ObjectBundle());
	}

	public Session saveFromBundle(ObjectBundle bundle) {
		if (bundle != null) {
			bundle.savaTo(bundle);
			return this;
		}
		return null;
	}

	public Session save() {
		final String result = encode();
		if (!StringUtils.isEmpty(result)) {
			saveData(result);
		}
		return this;
	}

	public ArrayMap getRecords(int index) {
		final ArrayMap result = new ArrayMap(_records.size());
		for (int i = 0; i < _records.size(); i++) {
			Entry entry = _records.getEntry(i);
			result.put(entry.getKey(), ((RecordData) entry.getValue()).get(index));
		}
		return result;
	}

	public int load() {
		final String result = loadData();
		if (result == null) {
			return 0;
		}
		return loadEncodeSession(result);
	}

	public boolean isSaved() {
		return _isPersisted && !_records.isEmpty();
	}

	public Session cpy() {
		return new Session(_name);
	}

	@Override
	public int size() {
		return _recordsList.size;
	}

	@Override
	public void clear() {
		_recordsList.clear();
	}

	@Override
	public boolean isEmpty() {
		return _recordsList.isEmpty();
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	@Override
	public boolean hasKey(String key) {
		return hasData(key);
	}

	public Session dispose(String name) {
		synchronized (_recordsList) {
			clear(name);
			final RecordData record = new RecordData(name);
			record.active = false;
			_records.put(name, record);
			_recordsList.add(record);
		}
		return this;
	}

	public void dispose() {
		try {
			if (_records != null) {
				_records.clear();
			}
			if (_recordsList != null) {
				_recordsList.clear();
			}
			removeData();
		} catch (Throwable e) {
		}
	}

}

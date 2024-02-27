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
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.TimeUtils;

/**
 * 游戏记录器，用于记录当前游戏数据
 */
public class Session implements Bundle<String> {

	private Save _save;

	private boolean isPersisted = false;

	private String loadData() {
		if (_save == null) {
			return null;
		}
		String result = _save.getItem(name);
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
		_save.setItem(name, result);
	}

	private void removeData() {
		_save.removeItem(name);
	}

	public static Session load(String name) {
		return new Session(name);
	}

	private final char flag = '&';

	private final class Record {

		private String name;

		private String[] values;

		private boolean active;

		public Record(String name) {
			this.values = new String[0];
			this.name = name;
		}

		public boolean isSaved() {
			return name != null && size() != 0;
		}

		public int size() {
			if (values != null) {
				return values.length;
			}
			return 0;
		}

		public int decode(String[] parts, int n) {
			if (n >= parts.length) {
				return n;
			}
			active = "1".equals(parts[n++]);
			if (n >= parts.length) {
				return n;
			}
			int count = Integer.parseInt(parts[n++]);
			values = new String[count];
			for (int i = 0; i < count; i++) {
				if (n >= parts.length) {
					return n;
				}
				values[i] = parts[n++];
			}
			return n;
		}

		public String get(int index) {
			if (index < 0 || index >= values.length) {
				return null;
			} else {
				return values[index];
			}
		}

		public void set(int index, final String v) {
			if (StringUtils.isEmpty(v)) {
				return;
			}
			final String value = StringUtils.replace(v, String.valueOf(flag), "+");
			if (index >= values.length) {
				int size = index + 1;
				String[] res = new String[size];
				System.arraycopy(values, 0, res, 0, values.length);
				this.values = res;
			}
			this.values[index] = value;
		}

		public String encode() {
			StrBuilder sbr = new StrBuilder(32);
			sbr.append(this.name);
			sbr.append(flag);
			sbr.append(this.active ? "1" : "0");
			sbr.append(flag);
			sbr.append(this.values.length);
			sbr.append(flag);
			for (int i = 0; i < this.values.length; i++) {
				sbr.append(this.values[i]);
				sbr.append(flag);
			}
			return sbr.toString();
		}

	}

	private String name;

	private ArrayMap records;

	private TArray<Record> recordsList;

	public Session(String name) {
		this(name, true);
	}

	public Session(String name, boolean gain) {
		if (name == null) {
			throw new LSysException("session name can not exist !");
		}
		try {
			this._save = LSystem.base().save();
			isPersisted = true;
		} catch (Throwable ex) {
			isPersisted = false;
		}
		this.name = name;
		this.records = new ArrayMap();
		this.recordsList = new TArray<Record>();
		if (gain) {
			load();
		}
	}

	public boolean isPersisted() {
		return isPersisted;
	}

	public int loadEncodeSession(String encode) {
		if (!StringUtils.isEmpty(encode)) {
			String[] parts = StringUtils.split(encode, flag);
			return decode(parts, 0);
		}
		return -1;
	}

	public String getActiveID() {
		synchronized (recordsList) {
			for (int i = 0; i < recordsList.size; i++) {
				Record record = recordsList.get(i);
				if (record.active) {
					return record.name;
				}
			}
			return null;
		}
	}

	public String set(int index, String value) {
		String name = "session_name_" + TimeUtils.millis();
		set(name, index, value);
		return name;
	}

	public String set(int index, int value) {
		return set(index, String.valueOf(value));
	}

	public String set(int index, float value) {
		return set(index, String.valueOf(value));
	}

	public String set(int index, boolean value) {
		return set(index, value ? "1" : "0");
	}

	public Session set(String name, String value) {
		return set(name, 0, value);
	}

	public Session set(String name, int index, String value) {
		if (StringUtils.isEmpty(value)) {
			return this;
		}
		synchronized (recordsList) {
			Record record = (Record) records.get(name);
			if (record == null) {
				record = new Record(name);
				records.put(name, record);
				recordsList.add(record);
			}
			record.set(index, value);
		}
		return this;
	}

	public Session set(String name, int value) {
		return set(name, 0, value);
	}

	public Session set(String name, int index, int value) {
		return set(name, index, String.valueOf(value));
	}

	public Session set(String name, float value) {
		return set(name, 0, value);
	}

	public Session set(String name, int index, float value) {
		return set(name, index, String.valueOf(value));
	}

	public Session set(String name, boolean value) {
		return set(name, 0, value ? "1" : "0");
	}

	public Session set(String name, int index, boolean value) {
		return set(name, index, value ? "1" : "0");
	}

	public Session add(String name, String value) {
		if (StringUtils.isEmpty(value)) {
			return this;
		}
		synchronized (recordsList) {
			Record record = (Record) records.get(name);
			if (record == null) {
				record = new Record(name);
				records.put(name, record);
				recordsList.add(record);
			}
			int id = record.size();
			record.set(id++, value);
		}
		return this;
	}

	public Session add(String name, int value) {
		return add(name, String.valueOf(value));
	}

	public Session add(String name, float value) {
		return add(name, String.valueOf(value));
	}

	public Session add(String name, boolean value) {
		return add(name, value ? "1" : "0");
	}

	public String get(String name, int index) {
		synchronized (recordsList) {
			Record record = (Record) records.get(name);
			if (record == null) {
				return null;
			} else {
				return record.get(index);
			}
		}
	}

	public int getInt(String name, int index) {
		String res = get(name, index);
		return res != null ? Integer.parseInt(res) : -1;
	}

	public float getFloat(String name, int index) {
		String res = get(name, index);
		return res != null ? Float.parseFloat(res) : -1;
	}

	public boolean getBoolean(String name, int index) {
		String res = get(name, index);
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
		synchronized (recordsList) {
			records.remove(name);
			for (int i = 0; i < recordsList.size; i++) {
				Record record = recordsList.get(i);
				if (record.name.equals(name)) {
					recordsList.removeIndex(i);
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
		String result = get(key);
		return result == null ? defaultValue : result;
	}

	@Override
	public String remove(String key) {
		String result = get(key);
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
		synchronized (recordsList) {
			Record record = (Record) records.get(name);
			if (record == null) {
				return 0;
			} else {
				return record.values.length;
			}
		}
	}

	public int getSize() {
		if (recordsList != null) {
			return recordsList.size;
		} else {
			return 0;
		}
	}

	public int decode(String[] parts, int n) {
		synchronized (recordsList) {
			records.clear();
			recordsList.clear();
			if (n >= parts.length) {
				return n;
			}
			int count = Integer.parseInt(parts[n++]);
			for (int i = 0; i < count; i++) {
				if (n >= parts.length) {
					return n;
				}
				Record record = new Record(parts[n++]);
				n = record.decode(parts, n);
				if (record.name != null && record.isSaved()) {
					records.put(record.name, record);
					recordsList.add(record);
				}
			}
			return n;
		}
	}

	public String encode() {
		synchronized (recordsList) {
			final StrBuilder sbr = new StrBuilder();
			sbr.append(recordsList.size).append(flag).toString();
			for (int i = 0; i < recordsList.size; i++) {
				final Record record = recordsList.get(i);
				if (record != null && record.isSaved()) {
					sbr.append(record.encode()).toString();
				}
			}
			return sbr.toString();
		}
	}

	public boolean hasData(String name) {
		synchronized (recordsList) {
			return records.get(name) != null;
		}
	}

	public Session activate(String name) {
		synchronized (recordsList) {
			Record record = new Record(name);
			record.active = true;
			records.put(name, record);
			recordsList.add(record);
		}
		return this;
	}

	public Session clear(String name) {
		synchronized (recordsList) {
			Record record = (Record) records.remove(name);
			if (record != null) {
				recordsList.remove(record);
			}
		}
		return this;
	}

	public boolean isActive(String name) {
		synchronized (recordsList) {
			Record record = (Record) records.get(name);
			if (record != null) {
				return record.active;
			} else {
				return false;
			}
		}
	}

	public Session reject(String name) {
		synchronized (recordsList) {
			clear(name);
			Record record = new Record(name);
			record.active = false;
			record.set(0, "1");
			records.put(name, record);
			recordsList.add(record);
		}
		return this;
	}

	public String getSessionName() {
		return name;
	}

	public Session save() {
		String result = encode();
		if (!StringUtils.isEmpty(result)) {
			saveData(result);
		}
		return this;
	}

	public ArrayMap getRecords(int index) {
		ArrayMap result = new ArrayMap(records.size());
		for (int i = 0; i < records.size(); i++) {
			Entry entry = records.getEntry(i);
			result.put(entry.getKey(), ((Record) entry.getValue()).get(index));
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
		return isPersisted && !records.isEmpty();
	}

	public Session cpy() {
		return new Session(name);
	}

	@Override
	public int size() {
		return recordsList.size;
	}

	@Override
	public void clear() {
		recordsList.clear();
	}

	@Override
	public boolean isEmpty() {
		return recordsList.isEmpty();
	}

	public Session dispose(String name) {
		synchronized (recordsList) {
			clear(name);
			Record record = new Record(name);
			record.active = false;
			records.put(name, record);
			recordsList.add(record);
		}
		return this;
	}

	public void dispose() {
		try {
			if (records != null) {
				records.clear();
			}
			if (recordsList != null) {
				recordsList.clear();
			}
			removeData();
		} catch (Throwable e) {
		}
	}

}

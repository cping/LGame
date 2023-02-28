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
package loon.utils.parse;

import loon.BaseIO;
import loon.LSystem;
import loon.utils.LIterator;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.ObjectMap.Entry;
import loon.utils.StrBuilder;

/**
 * 工具类，用于解析csv的配置数据
 */
public class ParserCSVData {

	public static class CSVRow {

		// 对应的行号id
		private final int lineId;

		private final ObjectMap<String, Integer> headerMap;
		private final TArray<String> fields;

		public CSVRow(final int id, final ObjectMap<String, Integer> headerMap, final TArray<String> fields) {
			this.lineId = id;
			this.headerMap = headerMap;
			this.fields = fields;
		}

		public int getLine() {
			return this.lineId;
		}

		public String getByName(final String name) {
			if (headerMap == null) {
				return null;
			}
			final Integer col = headerMap.get(name);
			if (col != null) {
				return get(col);
			}
			return null;
		}

		public TArray<String> getRawList() {
			return fields;
		}

		public ObjectMap<String, String> getFieldMap() {
			if (headerMap == null) {
				return null;
			}
			final ObjectMap<String, String> fieldMap = new ObjectMap<String, String>(headerMap.size());
			for (final Entry<String, Integer> header : headerMap.entries()) {
				String key = header.getKey();
				Integer col = headerMap.get(key);
				String val = null == col ? null : get(col);
				fieldMap.put(key, val);
			}
			return fieldMap;
		}

		public int getFieldCount() {
			return fields.size();
		}

		public int size() {
			return this.fields.size();
		}

		public boolean isEmpty() {
			return this.fields.isEmpty();
		}

		public boolean contains(String key) {
			return this.fields.contains(key);
		}

		public Object[] toArray() {
			return this.fields.toArray();
		}

		public boolean add(String e) {
			return this.fields.add(e);
		}

		public boolean remove(String key) {
			return this.fields.remove(key);
		}

		public CSVRow clear() {
			this.fields.clear();
			return this;
		}

		public String get(int index) {
			return index >= fields.size() ? null : fields.get(index);
		}

		public void set(int index, String str) {
			this.fields.set(index, str);
		}

		public void remove(int index) {
			this.fields.removeIndex(index);
		}

		public int indexOf(String key) {
			return this.fields.indexOf(key);
		}

		@Override
		public String toString() {
			if (headerMap == null || headerMap.size == 0) {
				return "[]";
			}
			final StrBuilder buffer = new StrBuilder("CSVRow{");
			buffer.append("line=");
			buffer.append(this.lineId);
			buffer.append(", ");
			buffer.append("table=");
			if (headerMap != null) {
				buffer.append('{');
				ObjectMap<String, String> map = getFieldMap();
				for (final LIterator<Entry<String, String>> it = map.iterator(); it.hasNext();) {
					final Entry<String, String> entry = it.next();
					buffer.append(entry.getKey());
					buffer.append('=');
					if (entry.getValue() != null) {
						buffer.append(entry.getValue());
					}
					if (it.hasNext()) {
						buffer.append(", ");
					}
				}
				buffer.append('}');
			} else {
				buffer.append(fields.toString());
			}
			buffer.append('}');
			return buffer.toString();
		}
	}

	public static ParserCSVData parseString(String str) {
		ParserCSVData data = new ParserCSVData(str);
		data.readCSVList();
		return data;
	}

	public static ParserCSVData parseFile(String path) {
		ParserCSVData data = new ParserCSVData(BaseIO.loadText(path));
		data.readCSVList();
		return data;
	}

	private final StrTokenizer _reader;

	private CSVRow _header;

	private TArray<CSVRow> _rows;

	private boolean _finished;

	private char _csvFlag;

	private int maxFieldCount = 0;
	private int fieldCount = 0;
	private int startingLineNo = 0;
	private int lineNo = 0;

	protected ParserCSVData(String context) {
		this._reader = new StrTokenizer(context);
		this.reset();
	}

	public ParserCSVData reset() {
		this._rows = null;
		this._finished = false;
		this._csvFlag = LSystem.COMMA;

		this.maxFieldCount = 0;
		this.fieldCount = 0;
		this.startingLineNo = 0;
		this.lineNo = 0;

		return this;
	}

	public TArray<CSVRow> readCSVList() {
		if (this._rows == null) {
			this._rows = new TArray<CSVRow>();
		} else {
			this._rows.clear();
		}
		this._reader.reset();
		this._finished = false;
		for (; _reader.hasMoreTokens();) {
			CSVRow row = readCSV();
			if (row != null) {
				_rows.add(row);
			}
		}
		return _rows;
	}

	public CSVRow readCSV() {
		for (; !_finished;) {
			String result = this._reader.nextToken();
			if (result == null) {
				_finished = true;
				break;
			}
			startingLineNo = lineNo++;
			if (StringUtils.isEmpty(result)) {
				continue;
			}
			fieldCount = result.length();
			if (fieldCount > maxFieldCount) {
				maxFieldCount = fieldCount;
			}
			if (lineNo == 1) {
				initHeader(lineNo, result);
			}
			if (result.indexOf(_csvFlag) == -1) {
				continue;
			}
			final String[] split = StringUtils.split(result, _csvFlag);
			return new CSVRow(startingLineNo, this._header == null ? null : this._header.headerMap,
					StringUtils.getStringsToList(split));
		}
		return null;
	}

	private void initHeader(final int id, final String str) {
		final String[] split = StringUtils.split(str, _csvFlag);
		final int len = split.length;
		final ObjectMap<String, Integer> localHeaderMap = new ObjectMap<String, Integer>(len);
		for (int i = 0; i < len; i++) {
			final String field = split[i];
			if (StringUtils.isNotEmpty(field)) {
				localHeaderMap.put(field, i);
			}
		}
		_header = new CSVRow(id, localHeaderMap, StringUtils.getStringsToList(split));
	}

	public CSVRow getRow(int idx) {
		return this._rows.get(idx);
	}

	public CSVRow getHeader() {
		return this._header;
	}

	public ParserCSVData setCsvFlag(char flag) {
		this._csvFlag = flag;
		return this;
	}

	public char getCsvFlag() {
		return this._csvFlag;
	}

	public int getMaxFieldCount() {
		return this.maxFieldCount;
	}

	public int getStartingLineNo() {
		return this.startingLineNo;
	}

	public int getLineNo() {
		return this.lineNo;
	}

	@Override
	public String toString() {
		return this._rows == null ? "[]" : this._rows.toString();
	}

}

package loon.core.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import loon.core.LSystem;


/**
 * Copyright 2008 - 2011
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
 * @email javachenpeng@yahoo.com
 * @version 0.1.1
 */
public class CSVReader extends BufferedReader {

	private String delimiter = ",";

	private char escape = '\"';

	private String nowLine = null;

	public CSVReader(String fileName) {
		super(CSVReader.reader(fileName, LSystem.encoding));
	}

	public CSVReader(String fileName, String charsetName) {
		super(CSVReader.reader(fileName, charsetName));
	}

	public static BufferedReader reader(String fileName, String charsetName) {
		try {
			InputStreamReader reader = new InputStreamReader(Resources.openResource(fileName), charsetName);
			return new BufferedReader(reader);
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public CSVReader(final Reader in) {
		super(in);
	}

	public CSVReader(final Reader in, final String d) {
		this(in);
		delimiter = d;
	}

	public CSVReader(final Reader in, final char d) {
		this(in, String.valueOf(d));
	}

	public CSVReader(final Reader in, final int sz) {
		super(in, sz);
	}

	public CSVReader(final Reader in, final int sz, final String d) {
		this(in, sz);
		delimiter = d;
	}

	public CSVReader(final Reader in, final int sz, final char d) {
		this(in, sz, String.valueOf(d));
	}

	public String getDelimiter() {
		return delimiter;
	}

	@Override
	public String readLine() throws IOException {
		return super.readLine();
	}

	/**
	 * 返回CSV数值的字符串数组
	 * 
	 * @return
	 * @throws IOException
	 */
	public String[] readLineAsArray() throws IOException {
		ArrayList<String> v = readLineAsList();
		if (v == null){
			return null;
		}
		String items[] = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			items[i] = v.get(i);
		}
		return items;
	}

	public ArrayList<String> readLineAsList() throws IOException {
		String line = readLine();
		if (line == null) {
			return null;
		}
		return getCSVItems(line);
	}

	public String getNowLine() {
		return nowLine;
	}

	/**
	 * 分割CSV文件数值,并返回List
	 * 
	 * @param line
	 * @return
	 */
	private ArrayList<String> getCSVItems(String line) {
		ArrayList<String> v = new ArrayList<String>();
		int startIdx = 0;
		int searchIdx = -1;
		StringBuffer sbLine = new StringBuffer(line);
		while ((searchIdx = sbLine.toString().indexOf(delimiter, startIdx)) != -1) {
			String buf = null;

			if (sbLine.charAt(startIdx) != escape) {
				buf = sbLine.substring(startIdx, searchIdx);
				startIdx = searchIdx + 1;
			} else {

				int escapeIdx = -1;
				searchIdx = startIdx;
				boolean findDelimiter = false;

				while ((escapeIdx = sbLine.toString().indexOf(escape,
						searchIdx + 1)) != -1
						&& sbLine.length() > escapeIdx + 1) {
					char nextChar = sbLine.charAt(escapeIdx + 1);
					if (delimiter.indexOf(nextChar) != -1) {
						buf = sbLine.substring(startIdx + 1, escapeIdx);
						startIdx = escapeIdx + 2;
						findDelimiter = true;
						break;
					}
					if (nextChar == escape) {
						sbLine.deleteCharAt(escapeIdx);
						escapeIdx--;
					}
					searchIdx = escapeIdx + 1;
				}

				if (!findDelimiter) {
					break;
				}
			}

			v.add(buf.trim());
		}

		if (startIdx < sbLine.length()) {
			int lastIdx = sbLine.length() - 1;
			if (sbLine.charAt(startIdx) == escape
					&& sbLine.charAt(lastIdx) == escape) {
				sbLine.deleteCharAt(lastIdx);
				sbLine.deleteCharAt(startIdx);
			}
			v.add(sbLine.substring(startIdx, sbLine.length()).trim());
		} else if (startIdx == sbLine.length()) {
			v.add("");
		}

		return v;
	}

}

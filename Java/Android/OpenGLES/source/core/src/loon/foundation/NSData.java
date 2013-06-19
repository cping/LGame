/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.foundation;

import java.nio.ByteBuffer;
import java.util.Arrays;

import loon.core.LSystem;
import loon.net.Base64Coder;
import loon.utils.MathUtils;

public class NSData extends NSObject {

	private byte[] bytes;

	public NSData(byte[] b) {
		if (Base64Coder.isArrayByteBase64(b)) {
			bytes = Base64Coder.decode(b);
		} else {
			this.bytes = b;
		}
	}

	public NSData(String base64) {
		String data = "";
		if (Base64Coder.isBase64(base64)) {
			for (String line : base64.split("\n")) {
				data += line.trim();
			}
			char[] enc = data.toCharArray();
			bytes = Base64Coder.decodeBase64(enc);
		} else {
			this.bytes = base64.getBytes();
		}
	}

	public byte[] bytes() {
		return bytes;
	}

	public int length() {
		return bytes.length;
	}

	public void getBytes(ByteBuffer buf, int length) {
		buf.put(bytes, 0, MathUtils.min(bytes.length, length));
	}

	public void getBytes(ByteBuffer buf, int rangeStart, int rangeEnd) {
		buf.put(bytes, rangeStart, MathUtils.min(bytes.length, rangeEnd));
	}

	public String getBase64() {
		byte[] buffer = Base64Coder.encode(bytes);
		try {
			return new String(buffer, LSystem.encoding);
		} catch (java.io.UnsupportedEncodingException uue) {
			return new String(buffer);
		}
	}

	public String getString() {
		return getString(LSystem.encoding);
	}

	public String getString(String format) {
		byte[] buffer = this.bytes;
		try {
			return new String(buffer, format);
		} catch (java.io.UnsupportedEncodingException uue) {
			return new String(buffer);
		}
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass().equals(getClass())
				&& Arrays.equals(((NSData) obj).bytes, bytes);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 67 * hash + Arrays.hashCode(this.bytes);
		return hash;
	}

	@Override
	protected void addSequence(StringBuilder sbr,String indent) {
		sbr.append(indent);
		sbr.append("<data>");
		sbr.append(LSystem.LS);
		String base64 = getBase64();
		for (String line : base64.split(LSystem.LS)) {
			sbr.append(indent);
			sbr.append("  ");
			sbr.append(line);
			sbr.append(LSystem.LS);
		}
		sbr.append(indent);
		sbr.append("</data>");
	}

}

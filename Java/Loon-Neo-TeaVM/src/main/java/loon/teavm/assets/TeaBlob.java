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
package loon.teavm.assets;

import java.io.IOException;
import java.io.InputStream;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

public class TeaBlob {

	private ArrayBuffer response;
	
	private final Int8Array data;

	public TeaBlob(ArrayBuffer response, Int8Array data) {
		this.data = data;
		this.response = response;
	}

	public Int8Array getData() {
		return data;
	}

	public ArrayBuffer getResponse() {
		return response;
	}

	public int length() {
		return data.getLength();
	}

	public byte get(int i) {
		return data.get(i);
	}

	public InputStream read() {
		return new InputStream() {

			@Override
			public int read() throws IOException {
				if (pos == length())
					return -1;
				return get(pos++) & 0xff;
			}

			@Override
			public int available() {
				return length() - pos;
			}

			int pos;
		};
	}
}

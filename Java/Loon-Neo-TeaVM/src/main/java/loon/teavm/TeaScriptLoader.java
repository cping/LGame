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

import org.teavm.jso.ajax.ReadyStateChangeHandler;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.typedarrays.ArrayBuffer;

import loon.teavm.dom.ConvertUtils;

public class TeaScriptLoader {

	public interface LoadBinaryListener {

		public void onLoadBinaryFile(ArrayBuffer buffer);

		public void onFaild(int states, String statesText);
	}

	public interface LoadTextListener {

		public void onLoadTextFile(String text);

		public void onFaild(int states, String statesText);
	}

	public static void loadBinaryFile(String url, final LoadBinaryListener listener) {

		final XMLHttpRequest request = new XMLHttpRequest();
		request.setResponseType("arraybuffer");

		request.setOnReadyStateChange(new ReadyStateChangeHandler() {

			@Override
			public void stateChanged() {
				if (request.getStatus() == 200) {
					ArrayBuffer arrayBufer = (ArrayBuffer) request.getResponse();
					listener.onLoadBinaryFile(arrayBufer);
				} else {
					listener.onFaild(request.getStatus(), request.getStatusText());
				}

			}

		});
		request.open("GET", url);
		request.send();
	}

	public static void loadTextFile(String url, final LoadTextListener listener) {

		final XMLHttpRequest request = new XMLHttpRequest();
		request.setResponseType("arraybuffer");

		request.setOnReadyStateChange(new ReadyStateChangeHandler() {

			@Override
			public void stateChanged() {
				if (request.getStatus() == 200) {
					ArrayBuffer arrayBufer = (ArrayBuffer) request.getResponse();
					listener.onLoadTextFile(ConvertUtils.decodeUtf8(arrayBufer));
				} else {
					listener.onFaild(request.getStatus(), request.getStatusText());
				}

			}

		});
		request.open("GET", url);
		request.send();
	}

	public static boolean isURL(String src) {
		if (src == null || src.trim().length() == 0 || src.startsWith("/")) {
			return false;
		}
		return src.startsWith("file") || src.startsWith("http");
	}
}

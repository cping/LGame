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

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSString;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLScriptElement;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

import loon.teavm.TeaBlob;
import loon.teavm.dom.ConvertUtils;

public class AssetDownloadImpl implements AssetDownloader {

	private static final int MAX_DOWNLOAD_ATTEMPT = 3;

	private final boolean showLogs;
	private boolean showDownloadProgress;

	public AssetDownloadImpl(boolean showDownloadLogs) {
		showLogs = showDownloadLogs;
	}

	@Override
	public void load(boolean async, String url, AssetType type, AssetLoaderListener<TeaBlob> listener) {
		AssetLoaderListener<TeaBlob> internalListener = new AssetLoaderListener<>() {
			@Override
			public void onSuccess(String url, TeaBlob result) {
				if (showLogs) {
					System.out.println("Asset download success: " + url);
				}
				listener.onSuccess(url, result);
			}

			@Override
			public void onFailure(String url) {
				if (showLogs) {
					System.err.println("Asset download failed: " + url);
				}
			}

			@Override
			public void onProgress(int total, int loaded) {
				if (listener != null) {
					listener.onProgress(total, loaded);
				}
			}
		};

		if (showLogs) {
			System.out.println("Loading asset: " + url);
		}

		switch (type) {
		case Binary:
			loadBinary(async, url, internalListener, 0);
			break;
		case Directory:
			internalListener.onSuccess(url, null);
			break;
		default:
			throw new RuntimeException("Unsupported asset type " + type);
		}
	}

	@Override
	public void loadScript(boolean async, String url, AssetLoaderListener<String> listener) {
		if (showLogs) {
			System.out.println("Loading script: " + url);
		}
		Window current = Window.current();
		HTMLDocument document = current.getDocument();
		HTMLScriptElement scriptElement = (HTMLScriptElement) document.createElement("script");

		scriptElement.addEventListener("load", new EventListener<Event>() {
			@Override
			public void handleEvent(Event event) {
				if (showLogs) {
					System.out.println("Script download success: " + url);
				}
				if (listener != null) {
					listener.onSuccess(url, "");
				}
			}
		});
		scriptElement.addEventListener("error", (error) -> {
			if (showLogs) {
				System.err.println("Script download failed: " + url);
			}
			if (listener != null) {
				listener.onFailure(url);
			}
		});
		scriptElement.setSrc(url);
		document.getBody().appendChild(scriptElement);
	}

	private void loadBinary(boolean async, final String url, final AssetLoaderListener<TeaBlob> listener, int count) {
		if (count == MAX_DOWNLOAD_ATTEMPT) {
			if (listener != null) {
				listener.onFailure(url);
			}
			return;
		}

		if (async) {
			Window.setTimeout(() -> loadBinaryInternally(true, url, listener, count), 0);
		} else {
			loadBinaryInternally(false, url, listener, count);
		}
	}

	private void loadBinaryInternally(boolean async, final String url, final AssetLoaderListener<TeaBlob> listener,
			int count) {
		XMLHttpRequest request = new XMLHttpRequest();
		request.setOnReadyStateChange(evt -> {
			if (request.getReadyState() == XMLHttpRequest.DONE) {
				int status = request.getStatus();
				if (status == 0) {
					if (listener != null) {
						listener.onFailure(url);
					}
				} else if (status != 200) {
					if ((status != 404) && (status != 403)) {
						int newCount = count + 1;
						Window.setTimeout(() -> loadBinary(async, url, listener, newCount), 100);
					} else {
						if (listener != null) {
							listener.onFailure(url);
						}
					}
				} else {
					JSObject jsResponse = request.getResponse();
					Int8Array data = null;
					ArrayBuffer arrayBuffer = null;
					if (JSString.isInstance(jsResponse)) {
						String responseStr = toString(jsResponse);
						data = ConvertUtils.getInt8Array(responseStr.getBytes());
						arrayBuffer = data.getBuffer();
					} else {
						ArrayBuffer response = (ArrayBuffer) jsResponse;
						data = new Int8Array(response);
						arrayBuffer = response;
					}
					if (listener != null) {
						listener.onSuccess(url, new TeaBlob(arrayBuffer, data));
					}
				}
			}
		});

		setOnProgress(request, url, listener);
		request.open("GET", url, async);
		if (async) {
			request.setResponseType("arraybuffer");
		}
		request.send();
	}

	private void setOnProgress(XMLHttpRequest req, String url, final AssetLoaderListener<?> listener) {
		req.onProgress(evt -> {
			int loaded = evt.getLoaded();
			int total = evt.getTotal();
			if (listener != null) {
				listener.onProgress(total, loaded);
			}
		});
	}

	@JSBody(params = "jsObject", script = "return jsObject;")
	private static native String toString(JSObject jsObject);

	@JSBody(params = "object", script = "return URL.createObjectURL(object);")
	private static native String createObjectURL(JSObject object);
}
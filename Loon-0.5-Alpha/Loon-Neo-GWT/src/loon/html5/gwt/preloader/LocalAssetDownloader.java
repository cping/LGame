package loon.html5.gwt.preloader;

import loon.LSystem;
import loon.html5.gwt.preloader.AssetFilter.AssetType;
import loon.utils.ObjectMap;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.typedarrays.shared.TypedArrays;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;

/** 流氓写法，做一个内部接口，将除了图片和音效外的资源，一律写在class里加载，这样就没有跨域问题了，就可以不必经过服务器，而在任意浏览器运行了…… **/
public class LocalAssetDownloader extends IDownloader {

	private final LocalAssetResources localRes;

	private boolean tryInline = false;

	public LocalAssetDownloader(LocalAssetResources res) {
		super();
		this.localRes = res;
	}

	@SuppressWarnings("unchecked")
	public void load(String url, AssetType type, String mimeType,
			AssetLoaderListener<?> listener) {
		switch (type) {
		case Text:
			loadText(url, (AssetLoaderListener<String>) listener);
			break;
		case Image:
			loadImage(url, mimeType,
					(AssetLoaderListener<ImageElement>) listener);
			break;
		case Binary:
			loadBinary(url, (AssetLoaderListener<Blob>) listener);
			break;
		case Audio:
			loadAudio(url, (AssetLoaderListener<Void>) listener);
			break;
		case Directory:
			listener.onSuccess(null);
			break;
		default:
			throw new RuntimeException("Unsupported asset type " + type);
		}
	}

	public void loadText(String url, final AssetLoaderListener<String> listener) {
		if (localRes == null) {
			XMLHttpRequest request = XMLHttpRequest.create();
			request.setOnReadyStateChange(new ReadyStateChangeHandler() {
				@Override
				public void onReadyStateChange(XMLHttpRequest xhr) {
					if (xhr.getReadyState() == XMLHttpRequest.DONE) {
						if (xhr.getStatus() != 200) {
							listener.onFailure();
						} else {
							listener.onSuccess(xhr.getResponseText());
						}
					}
				}
			});
			setOnProgress(request, listener);
			request.open("GET", url);
			request.setRequestHeader("Content-Type",
					"text/plain; charset=utf-8");
			request.send();
			return;
		}
		String path = url;
		int pathLen;
		do {
			pathLen = path.length();
			path = path.replaceAll("[^/]+/\\.\\./", "");
		} while (path.length() != pathLen);
		path = path.replace("\\", "/");
		ObjectMap<String, String> res = localRes.texts;

		String text = res.get(path);
		if (text == null
				&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			text = res
					.get(path.substring(path.indexOf('/') + 1, path.length()));
		}
		if (text == null
				&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			text = res.get(LSystem.getFileName(path));
		}
		if (text == null) {
			text = res.get(LSystem.getFileName(path = ("assets/" + path)));
		}
		if (text == null) {
			if (tryInline) {
				XMLHttpRequest request = XMLHttpRequest.create();
				request.setOnReadyStateChange(new ReadyStateChangeHandler() {
					@Override
					public void onReadyStateChange(XMLHttpRequest xhr) {
						if (xhr.getReadyState() == XMLHttpRequest.DONE) {
							if (xhr.getStatus() != 200) {
								listener.onFailure();
							} else {
								listener.onSuccess(xhr.getResponseText());
							}
						}
					}
				});
				setOnProgress(request, listener);
				request.open("GET", url);
				request.setRequestHeader("Content-Type",
						"text/plain; charset=utf-8");
				request.send();
				return;
			} else {
				listener.onFailure();
			}
		} else {
			listener.onSuccess(text);
		}
		setOnProgress(listener);
	}

	public void loadBinary(final String url,
			final AssetLoaderListener<Blob> listener) {
		if (localRes == null) {
			XMLHttpRequest request = XMLHttpRequest.create();
			request.setOnReadyStateChange(new ReadyStateChangeHandler() {
				@Override
				public void onReadyStateChange(XMLHttpRequest xhr) {
					if (xhr.getReadyState() == XMLHttpRequest.DONE) {
						if (xhr.getStatus() != 200) {
							listener.onFailure();
						} else {
							Int8Array data = TypedArrays.createInt8Array(xhr
									.getResponseArrayBuffer());
							listener.onSuccess(new Blob(data));
						}
					}
				}
			});
			setOnProgress(request, listener);
			request.open("GET", url);
			request.setResponseType(ResponseType.ArrayBuffer);
			request.send();
			return;
		}
		String path = url;
		int pathLen;
		do {
			pathLen = path.length();
			path = path.replaceAll("[^/]+/\\.\\./", "");
		} while (path.length() != pathLen);
		path = path.replace("\\", "/");
		ObjectMap<String, Blob> res = localRes.binaries;
		Blob blob = res.get(path);
		if (blob == null
				&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			blob = res
					.get(path.substring(path.indexOf('/') + 1, path.length()));
		}
		if (blob == null
				&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			blob = res.get(LSystem.getFileName(path));
		}
		if (blob == null) {
			blob = res.get(LSystem.getFileName(path = ("assets/" + path)));
		}
		if (blob == null) {
			if (tryInline) {
				XMLHttpRequest request = XMLHttpRequest.create();
				request.setOnReadyStateChange(new ReadyStateChangeHandler() {
					@Override
					public void onReadyStateChange(XMLHttpRequest xhr) {
						if (xhr.getReadyState() == XMLHttpRequest.DONE) {
							if (xhr.getStatus() != 200) {
								listener.onFailure();
							} else {
								Int8Array data = TypedArrays
										.createInt8Array(xhr
												.getResponseArrayBuffer());
								listener.onSuccess(new Blob(data));
							}
						}
					}
				});
				setOnProgress(request, listener);
				request.open("GET", url);
				request.setResponseType(ResponseType.ArrayBuffer);
				request.send();
				return;
			} else {
				listener.onFailure();
			}
		} else {
			listener.onSuccess(blob);
		}
		setOnProgress(listener);
	}

	public void loadAudio(String url, final AssetLoaderListener<Void> listener) {
		if (useBrowserCache) {
			loadBinary(url, new AssetLoaderListener<Blob>() {
				@Override
				public void onProgress(double amount) {
					listener.onProgress(amount);
				}

				@Override
				public void onFailure() {
					listener.onFailure();
				}

				@Override
				public void onSuccess(Blob result) {
					listener.onSuccess(null);
				}

			});
		} else {
			listener.onSuccess(null);
		}
	}

	public void loadImage(final String url, final String mimeType,
			final AssetLoaderListener<ImageElement> listener) {
		String path = url;
		int pathLen;
		do {
			pathLen = path.length();
			path = path.replaceAll("[^/]+/\\.\\./", "");
		} while (path.length() != pathLen);
		path = path.replace("\\", "/");
		if (tryInline) {
			final ImageElement image = createImage();
			hookImgListener(image, new ImgEventListener() {
				@Override
				public void onEvent(NativeEvent event) {
					if (event.getType().equals("error")) {
						listener.onFailure();
					} else {
						listener.onSuccess(image);
					}
				}
			});
			image.setSrc(url);
		} else {
			ObjectMap<String, String> res = localRes.images;
			String base64 = res.get(path);
			if (base64 == null
					&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
				base64 = res.get(path.substring(path.indexOf('/') + 1,
						path.length()));
			}
			if (base64 == null
					&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
				base64 = res.get(LSystem.getFileName(path));
			}
			if (base64 == null) {
				base64 = res
						.get(LSystem.getFileName(path = ("assets/" + path)));
			}
			if (base64 == null) {
				final ImageElement image = createImage();
				hookImgListener(image, new ImgEventListener() {
					@Override
					public void onEvent(NativeEvent event) {
						if (event.getType().equals("error")) {
							listener.onFailure();
						} else {
							listener.onSuccess(image);
						}
					}
				});
				setOnProgress(listener);
				image.setSrc(path);
				return;
			}
			final ImageElement image = createImage();
			hookImgListener(image, new ImgEventListener() {
				@Override
				public void onEvent(NativeEvent event) {
					if (event.getType().equals("error")) {
						listener.onFailure();
					} else {
						listener.onSuccess(image);
					}
				}
			});
			setCrossOrigin(image, "anonymous");
			setOnProgress(listener);
			image.setSrc("data:" + mimeType + ";base64," + base64);
		}
	}

	private native void setCrossOrigin(Element elem, String state) /*-{
		if ('crossOrigin' in elem)
			elem.setAttribute('crossOrigin', state);
	}-*/;

	public boolean isTryInline() {
		return tryInline;
	}

	public void setTryInline(boolean tryInline) {
		this.tryInline = tryInline;
	}

	@Override
	public void clear() {
		if (localRes != null) {
			localRes.clear();
		}
	}

}

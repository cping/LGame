package loon.preloader;

import loon.preloader.AssetFilter.AssetType;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.typedarrays.shared.TypedArrays;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;

public class AssetDownloader {

	public AssetDownloader () {
		useBrowserCache = true;
		useInlineBase64 = false;
	}

	public void setUseBrowserCache (boolean useBrowserCache) {
		this.useBrowserCache = useBrowserCache;
	}

	public boolean isUseBrowserCache () {
		return useBrowserCache;
	}

	public void setUseInlineBase64 (boolean useInlineBase64) {
		this.useInlineBase64 = useInlineBase64;
	}

	public boolean isUseInlineBase64 () {
		return useInlineBase64;
	}

	public interface AssetLoaderListener<T> {

		public void onProgress (double amount);

		public void onFailure ();

		public void onSuccess (T result);

	}

	@SuppressWarnings("unchecked")
	public void load (String url, AssetType type, String mimeType, AssetLoaderListener<?> listener) {
		switch (type) {
		case Text:
			loadText(url, (AssetLoaderListener<String>)listener);
			break;
		case Image:
			loadImage(url, mimeType, (AssetLoaderListener<ImageElement>)listener);
			break;
		case Binary:
			loadBinary(url, (AssetLoaderListener<Blob>)listener);
			break;
		case Audio:
			loadAudio(url, (AssetLoaderListener<Void>)listener);
			break;
		case Directory:
			listener.onSuccess(null);
			break;
		default:
			throw new RuntimeException("Unsupported asset type " + type);
		}
	}

	public void loadText (String url, final AssetLoaderListener<String> listener) {
		XMLHttpRequest request = XMLHttpRequest.create();
		request.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void onReadyStateChange (XMLHttpRequest xhr) {
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
		request.setRequestHeader("Content-Type", "text/plain; charset=utf-8");
		request.send();
	}

	public void loadBinary (final String url, final AssetLoaderListener<Blob> listener) {
		XMLHttpRequest request = XMLHttpRequest.create();		
		request.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void onReadyStateChange (XMLHttpRequest xhr) {
				if (xhr.getReadyState() == XMLHttpRequest.DONE) {
					if (xhr.getStatus() != 200) {
						listener.onFailure();
					} else {
						Int8Array data = TypedArrays.createInt8Array(xhr.getResponseArrayBuffer());
						listener.onSuccess(new Blob(data));
					}
				}
			}
		});
		setOnProgress(request, listener);
		request.open("GET", url);
		request.setResponseType(ResponseType.ArrayBuffer);
		request.send();
	}

	public void loadAudio (String url, final AssetLoaderListener<Void> listener) {
		if (useBrowserCache) {
			loadBinary(url, new AssetLoaderListener<Blob>() {
				@Override
				public void onProgress (double amount) {
					listener.onProgress(amount);
				}

				@Override
				public void onFailure () {
					listener.onFailure();
				}

				@Override
				public void onSuccess (Blob result) {
					listener.onSuccess(null);
				}

			});
		} else {
			listener.onSuccess(null);
		}
	}

	public void loadImage (final String url, final String mimeType, final AssetLoaderListener<ImageElement> listener) {
		if (useBrowserCache || useInlineBase64) {
			loadBinary(url, new AssetLoaderListener<Blob>() {
				@Override
				public void onProgress (double amount) {
					listener.onProgress(amount);
				}

				@Override
				public void onFailure () {
					listener.onFailure();
				}

				@Override
				public void onSuccess (Blob result) {
					final ImageElement image = createImage();
					hookImgListener(image, new ImgEventListener() {
						@Override
						public void onEvent (NativeEvent event) {
							if (event.getType().equals("error"))
								listener.onFailure();
							else
								listener.onSuccess(image);
						}
					});
					if (isUseInlineBase64()) {
						image.setSrc("data:" + mimeType + ";base64," + result.toBase64());
					} else {
						image.setSrc(url);
					}
				}

			});
		} else {
			final ImageElement image = createImage();
			hookImgListener(image, new ImgEventListener() {
				@Override
				public void onEvent (NativeEvent event) {
					if (event.getType().equals("error"))
						listener.onFailure();
					else
						listener.onSuccess(image);
				}
			});
			image.setSrc(url);
		}
	}

	private static interface ImgEventListener {
		public void onEvent (NativeEvent event);
	}

	static native void hookImgListener (ImageElement img, ImgEventListener h) /*-{
		img
				.addEventListener(
						'load',
						function(e) {
							h.@loon.preloader.AssetDownloader.ImgEventListener::onEvent(Lcom/google/gwt/dom/client/NativeEvent;)(e);
						}, false);
		img
				.addEventListener(
						'error',
						function(e) {
							h.@loon.preloader.AssetDownloader.ImgEventListener::onEvent(Lcom/google/gwt/dom/client/NativeEvent;)(e);
						}, false);
	}-*/;

	static native ImageElement createImage () /*-{
		return new Image();
	}-*/;

	private native static void setOnProgress (XMLHttpRequest req, AssetLoaderListener<?> listener) /*-{
		var _this = this;
		this.onprogress = $entry(function(evt) {
			listener.@loon.preloader.AssetDownloader.AssetLoaderListener::onProgress(D)(evt.loaded);
		});
	}-*/;

	private boolean useBrowserCache;

	private boolean useInlineBase64;

}

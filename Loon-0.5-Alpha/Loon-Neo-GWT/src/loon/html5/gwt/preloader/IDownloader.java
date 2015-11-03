package loon.html5.gwt.preloader;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.xhr.client.XMLHttpRequest;

import loon.html5.gwt.preloader.AssetFilter.AssetType;

public abstract class IDownloader {

	protected boolean useBrowserCache, useInlineBase64;

	public IDownloader() {
		useBrowserCache = true;
		useInlineBase64 = false;
	}

	public void setUseBrowserCache(boolean useBrowserCache) {
		this.useBrowserCache = useBrowserCache;
	}

	public boolean isUseBrowserCache() {
		return useBrowserCache;
	}

	public void setUseInlineBase64(boolean useInlineBase64) {
		this.useInlineBase64 = useInlineBase64;
	}

	public boolean isUseInlineBase64() {
		return useInlineBase64;
	}
	
	public interface AssetLoaderListener<T> {

		public void onProgress(double amount);

		public void onFailure();

		public void onSuccess(T result);

	}

	public abstract void load(String url, AssetType type, String mimeType,
			AssetLoaderListener<?> listener);

	public abstract void loadText(String url,
			final AssetLoaderListener<String> listener);

	public abstract void loadBinary(final String url,
			final AssetLoaderListener<Blob> listener);

	public abstract void loadAudio(String url,
			final AssetLoaderListener<Void> listener);

	public abstract void loadImage(final String url, final String mimeType,
			final AssetLoaderListener<ImageElement> listener);

	static interface ImgEventListener {
		public void onEvent(NativeEvent event);
	}

	static native void hookImgListener(ImageElement img, ImgEventListener h) /*-{
		img
				.addEventListener(
						'load',
						function(e) {
							h.@loon.html5.gwt.preloader.IDownloader.ImgEventListener::onEvent(Lcom/google/gwt/dom/client/NativeEvent;)(e);
						}, false);
		img
				.addEventListener(
						'error',
						function(e) {
							h.@loon.html5.gwt.preloader.IDownloader.ImgEventListener::onEvent(Lcom/google/gwt/dom/client/NativeEvent;)(e);
						}, false);
	}-*/;

	static native ImageElement createImage() /*-{
		return new Image();
	}-*/;

	@SuppressWarnings("rawtypes")
	static native void setOnProgress(XMLHttpRequest req,
			AssetLoaderListener listener) /*-{
		var _this = this;
		this.onprogress = $entry(function(evt) {
			listener.@loon.html5.gwt.preloader.IDownloader.AssetLoaderListener::onProgress(D)(evt.loaded);
		});
	}-*/;

	@SuppressWarnings("rawtypes")
	static native void setOnProgress(
			AssetLoaderListener listener) /*-{
		var _this = this;
		this.onprogress = $entry(function(evt) {
			listener.@loon.html5.gwt.preloader.IDownloader.AssetLoaderListener::onProgress(D)(evt.loaded);
		});
	}-*/;
	
	public abstract void clear();
}

package loon.html5.gwt.preloader;

import loon.LSystem;
import loon.utils.Base64Coder;
import loon.utils.ObjectMap;

import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.typedarrays.shared.TypedArrays;

public class LocalAssetResources {

	static class AssetItem {

		public String url;

		public Object obj;

		public AssetItem(String u, Object o) {
			this.url = u;
			this.obj = o;
		}

		public String toString() {
			String ext = LSystem.getExtension(url).toLowerCase();
			StringBuilder sbr = new StringBuilder();
			if (DefaultAssetFilter.isImage(ext)) {
				sbr.append('i');
			} else if (DefaultAssetFilter.isText(ext)) {
				sbr.append('t');
			} else if (DefaultAssetFilter.isAudio(ext)) {
				sbr.append('a');
			} else {
				sbr.append('b');
			}
			sbr.append(':');
			sbr.append(url);
			sbr.append(':');
			if (obj == null) {
				sbr.append(0);
			} else if (obj instanceof String) {
				sbr.append(((String) obj).length());
			} else if (obj instanceof Integer) {
				sbr.append(((Integer) obj).intValue());
			} else if (obj instanceof Long) {
				sbr.append(((Long) obj).longValue());
			} else if (obj instanceof Blob) {
				sbr.append(((Blob) obj).length());
			} else {
				sbr.append(Long.MAX_VALUE);
			}
			sbr.append(':');
			if (DefaultAssetFilter.isImage(ext)) {
				sbr.append("image");
				sbr.append('/');
				if (ext.equals("jpg")) {
					sbr.append("jpeg");
				} else {
					sbr.append(ext);
				}
			} else if (DefaultAssetFilter.isText(ext)) {
				sbr.append("text/plan");
			} else if (DefaultAssetFilter.isAudio(ext)) {
				sbr.append("video");
				sbr.append('/');
				sbr.append(ext);
			} else {
				sbr.append("application/unknown");
			}
			return sbr.toString();
		}

	}

	public StringBuffer _context = new StringBuffer();

	public ObjectMap<String, String> texts = new ObjectMap<String, String>();
	public ObjectMap<String, Blob> binaries = new ObjectMap<String, Blob>();
	public ObjectMap<String, String> images = new ObjectMap<String, String>();

	public LocalAssetResources() {
		 def();
	}

	private void def() {}

	public void putAssetItem(AssetItem assets) {
		String result = assets.toString() + ";";
		if (_context.indexOf(result) == -1) {
			_context.append(result);
		}
	}

	public void putImage(String url) {
		putAssetItem(new AssetItem(url, Integer.MAX_VALUE));
	}

	public void putImage(String url, int size) {
		putAssetItem(new AssetItem(url, size));
	}

	public void putImage(String url, String base64) {
		images.put(url, base64);
		putAssetItem(new AssetItem(url, Integer.MAX_VALUE));
	}

	public void putText(String url, String context) {
		texts.put(url, context);
		putAssetItem(new AssetItem(url, Integer.MAX_VALUE));
	}

	public void putBlobString(String url, String base64data) {
		if (!Base64Coder.isBase64(base64data)) {
			putText(url, base64data);
			return;
		}
		byte[] bytes = Base64Coder.decodeBase64(base64data.toCharArray());
		Int8Array arrays = TypedArrays.createInt8Array(bytes.length);
		arrays.set(bytes);
		Blob blob = new Blob(arrays);
		binaries.put(url, blob);
		putAssetItem(new AssetItem(url, blob));
	}

	public void commit() {
		texts.put("assets.txt", "list:" + _context.toString());
	}

	public void clear() {
		_context.delete(0, _context.length());
		texts.clear();
		binaries.clear();
	}
}

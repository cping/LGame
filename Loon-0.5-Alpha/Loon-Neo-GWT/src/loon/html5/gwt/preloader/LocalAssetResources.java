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
			} else if (obj instanceof Blob) {
				sbr.append(((Blob) obj).length());
			} else {
				sbr.append(0);
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

	public LocalAssetResources() {
		def();
	}

	private void def() {
		StringBuilder sbr = new StringBuilder();
		sbr.append("<?xml version=\"1.0\" standalone=\"yes\"?>");
		sbr.append("\n");
		sbr.append("<pack file=\"assets/loon_pad_ui.png\">");
		sbr.append("\n");
		sbr.append("<block id=\"0\" name=\"back\" left=\"0\" top=\"0\" right=\"116\" bottom=\"116\"/>");
		sbr.append("\n");
		sbr.append("<block id=\"1\" name=\"fore\" left=\"116\" top=\"0\" right=\"222\" bottom=\"106\"/>");
		sbr.append("\n");
		sbr.append("<block id=\"2\" name=\"dot\" left=\"0\" top=\"116\" right=\"48\" bottom=\"164\"/>");
		sbr.append("\n");
		sbr.append("</pack>");

		putText("assets/loon_pad_ui.txt", sbr.toString());
		putImage("assets/loon_bar.png");
		putImage("assets/loon_control_base.png");
		putImage("assets/loon_control_dot.png");
		putImage("assets/loon_creese.png");
		putImage("assets/loon_e1.png");
		putImage("assets/loon_e2.png");
		putImage("assets/loon_icon.png");
		putImage("assets/loon_logo.png");
		putImage("assets/loon_pad_ui.png");
		putImage("assets/loon_rain_0.png");
		putImage("assets/loon_rain_1.png");
		putImage("assets/loon_rain_2.png");
		putImage("assets/loon_rain_3.png");
		putImage("assets/loon_sakura_0.png");
		putImage("assets/loon_sakura_1.png");
		putImage("assets/loon_snow_0.png");
		putImage("assets/loon_snow_1.png");
		putImage("assets/loon_snow_2.png");
		putImage("assets/loon_snow_3.png");
		putImage("assets/loon_snow_4.png");
		putImage("assets/loon_ui.png");
		putImage("assets/loon_wbar.png");
	}

	public void putAssetItem(AssetItem assets) {
		String result = assets.toString() + ";";
		if (_context.indexOf(result) == -1) {
			_context.append(result);
		}
	}

	public void putImage(String url) {
		putAssetItem(new AssetItem(url, 0));
	}

	public void putImage(String url, int size) {
		putAssetItem(new AssetItem(url, size));
	}

	public void putText(String url, String context) {
		texts.put(url, context);
		putAssetItem(new AssetItem(url, context));
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

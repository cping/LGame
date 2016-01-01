package loon.html5.gwt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;

public class GWTScriptLoader {

	public interface LoadBinaryListener {

		public void onLoadBinaryFile(ArrayBuffer buffer);

		public void onFaild(int states, String statesText);
	}

	public interface LoadTextListener {

		public void onLoadTextFile(String text);

		public void onFaild(int states, String statesText);
	}

	/**
	 * 跨域标记，亲测部分浏览器有反作用，慎用
	 * 
	 * @param elem
	 * @param state
	 */
	public static native void setCrossOrigin(Element elem, String state) /*-{
		if ('crossOrigin' in elem)
			elem.setAttribute('crossOrigin', state);
	}-*/;

	public static void loadBinaryFile(String url,
			final LoadBinaryListener listener) {

		XMLHttpRequest request = XMLHttpRequest.create();
		request.setResponseType(ResponseType.ArrayBuffer);
		request.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				if (xhr.getResponseArrayBuffer() == null) {
					return;
				}
				if (xhr.getStatus() == 200) {
					ArrayBuffer arrayBufer = xhr.getResponseArrayBuffer();

					listener.onLoadBinaryFile(arrayBufer);
				} else {
					listener.onFaild(xhr.getStatus(), xhr.getStatusText());
				}

			}
		});
		request.open("GET", url);
		request.send();
	}

	public static void loadTextFile(String url, final LoadTextListener listener) {
		try {
			new RequestBuilder(RequestBuilder.GET, url).sendRequest(null,
					new RequestCallback() {

						@Override
						public void onResponseReceived(Request request,
								Response response) {
							listener.onLoadTextFile(response.getText());
						}

						@Override
						public void onError(Request request, Throwable exception) {
							listener.onFaild(0, exception.getMessage());
						}
					});
		} catch (RequestException e) {
			listener.onFaild(0, e.getMessage());
		}
	}

	public static interface JsArrayMixedCallback {
		Object call(JsArrayMixed args);
	}

	public static native <T> T get(JavaScriptObject o, Object p)/*-{
		return o[p];
	}-*/;

	public static native String getString(JavaScriptObject o, String s)/*-{
		return o[s];
	}-*/;

	public static native int getInt(JavaScriptObject o, String s)/*-{
		return o[s];
	}-*/;

	public static native JavaScriptObject put(JavaScriptObject o, Object pname,
			Object val)/*-{
		o[pname] = val;
		return o;
	}-*/;

	public static native JavaScriptObject put(JavaScriptObject o, Object pname,
			int val)/*-{
		o[pname] = val;
		return o;
	}-*/;

	public static native JavaScriptObject put(JavaScriptObject o, Object pname,
			double val)/*-{
		o[pname] = val;
		return o;
	}-*/;

	public static native JavaScriptObject putObject(JavaScriptObject o,
			String pname, JavaScriptObject val)/*-{
		o[pname] = val;
		return o;
	}-*/;

	public static native JsArrayString props(JavaScriptObject o)/*-{
		var props = [];
		for ( var i in o) {
			props.push(i + "");
			;
		}
		return props;
	}-*/;

	public static native JavaScriptObject empty()/*-{
		return {};
	}-*/;

	public static JavaScriptObject obj(Map<String, Object> props) {
		JavaScriptObject o = empty();
		for (String k : props.keySet()) {
			Object val = props.get(k);
			put(o, k, val);
		}
		return o;
	}

	public static JavaScriptObject obj(Object... m) {
		return obj(toMap2(m));
	}

	private static Map<Object, Object> toMap2(Object... a) {
		Map<Object, Object> m = new HashMap<Object, Object>();
		for (int i = 0; i < a.length - 1; i = i + 2)
			m.put(a[i], a[i + 1]);
		return m;
	}

	public static native JavaScriptObject arrayEmpty()/*-{
		return [];
	}-*/;

	public static native JavaScriptObject arrayPush(JavaScriptObject arr,
			JavaScriptObject o)/*-{
		arr.push(o);
		return arr;
	}-*/;

	public static List<String> toList(JsArrayString array) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++) {
			list.add(array.get(i));
		}
		return list;
	}

	public static <T extends JavaScriptObject> List<T> toList(JsArray<T> array) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < array.length(); i++) {
			list.add(array.get(i));
		}
		return list;
	}

	public static <E extends JavaScriptObject> JsArray<E> toArray(List<E> list) {
		JsArray<E> array = JsArray.createArray().cast();
		for (E data : list) {
			array.push(data);
		}
		return array;
	}

	public static JsArrayNumber toArray(int[] ints) {
		JsArrayNumber array = JsArrayNumber.createArray().cast();
		for (int i = 0; i < ints.length; i++) {
			array.push(ints[i]);
		}
		return array;
	}
	
	public static boolean arrayContains(JsArray<JavaScriptObject> a,
			JavaScriptObject val) {
		for (int i = 0; i < a.length(); i++) {
			JavaScriptObject o = a.get(i);
			if (o != null && o.equals(val))
				return true;
		}
		return false;
	}

	public static boolean arrayContains(JsArrayMixed a, String val) {
		for (int i = 0; i < a.length(); i++) {
			String o = a.getString(i);
			if (o != null && o.equals(val))
				return true;
		}
		return false;
	}

	public static boolean arrayContains(JsArrayString a, String val) {
		for (int i = 0; i < a.length(); i++) {
			String o = a.get(i);
			if (o != null && o.equals(val))
				return true;
		}
		return false;
	}

	public static boolean arrayContains(JsArrayNumber a, String val) {
		for (int i = 0; i < a.length(); i++) {
			Number o = a.get(i);
			if (o != null && o.equals(val))
				return true;
		}
		return false;
	}

	public static JsArrayNumber toJsArray(int[] a) {
		JsArrayNumber jsa = (JsArrayNumber) JsArrayNumber.createArray();
		for (int i = 0; i < a.length; i++) {
			jsa.push(a[i]);
		}
		return jsa;
	}

	public static JsArrayNumber toJsArray(double[] a) {
		if (a == null) {
			return null;
		}
		JsArrayNumber jsa = (JsArrayNumber) JsArrayNumber.createArray();
		for (int i = 0; i < a.length; i++) {
			jsa.push(a[i]);
		}
		return jsa;
	}

	public static JsArray<JsArrayNumber> toJsArray(double[][] a) {
		if (a == null) {
			return null;
		}
		JsArray<JsArrayNumber> jsa = JsArrayNumber.createArray().cast();
		for (int i = 0; i < a.length; i++) {
			JsArrayNumber row = JsArrayNumber.createArray().cast();
			jsa.push(row);
			for (int j = 0; j < a[i].length; j++) {
				row.push(a[i][j]);
			}
		}
		return jsa;
	}

	public static JsArray<JsArrayString> toJsArray(String[][] a) {
		if (a == null) {
			return null;
		}
		JsArray<JsArrayString> jsa = JsArrayString.createArray().cast();
		for (int i = 0; i < a.length; i++) {
			JsArrayString row = JsArrayString.createArray().cast();
			jsa.push(row);
			for (int j = 0; j < a[i].length; j++) {
				row.push(a[i][j]);
			}
		}
		return jsa;
	}

	public static JsArrayString toJsArray(String[] a) {
		if (a == null) {
			return null;
		}
		JsArrayString jsa = JsArrayNumber.createArray().<JsArrayString> cast();
		for (int i = 0; i < a.length; i++) {
			jsa.push(a[i]);
		}
		return jsa;
	}

	public static JsArray<JavaScriptObject> toJsArray(JavaScriptObject[] ja) {
		if (ja == null) {
			return null;
		}
		JsArray<JavaScriptObject> jsa = JsArray.createArray().cast();
		for (JavaScriptObject t : ja) {
			jsa.push(t);
		}
		return jsa;
	}

	public static JavaScriptObject toJsObject(Object... ja) {
		if (ja == null)
			return null;
		JavaScriptObject jso = JsArray.createObject();
		for (int i = 0; i < ja.length; i += 2) {
			put(jso, ja[i], ja[i + 1]);
		}
		return jso;
	}

	public static JsArray<JavaScriptObject> to2DJsArray(double[][] ja) {
		JsArray<JavaScriptObject> jsa = JsArray.createArray().cast();
		if (ja == null) {
			return jsa;
		}
		for (int i = 0; i < ja.length; i++) {
			jsa.push(toJsArray(ja[i]));
		}
		return jsa;
	}

	public static JsArray<JavaScriptObject> to2DJsArray(String[][] ja) {
		JsArray<JavaScriptObject> jsa = JsArray.createArray().cast();
		if (ja == null) {
			return jsa;
		}
		for (int i = 0; i < ja.length; i++) {
			jsa.push(toJsArray(ja[i]));
		}
		return jsa;
	}

	public final native static void putBoolean(JavaScriptObject o, String prop,
			boolean b)/*-{
		if (b) {
			o[prop] = true;
		} else {
			o[prop] = false;
		}

	}-*/;

	public final native static void putNumber(JavaScriptObject o, String prop,
			double v) /*-{
		o[prop] = v;
	}-*/;

	public static double[] toJavaDoubleArray(JsArrayNumber a) {
		if (a == null)
			return null;
		double[] dd = new double[a.length()];
		for (int i = 0; i < dd.length; i++) {
			dd[i] = a.get(i);
		}
		return dd;
	}

	public static String[] toJavaStringArray(JsArrayString a) {
		if (a == null)
			return null;
		String[] dd = new String[a.length()];
		for (int i = 0; i < dd.length; i++) {
			dd[i] = a.get(i);
		}
		return dd;
	}

	public static native JavaScriptObject evalObject(String s)/*-{
		return $wnd.eval("(" + s + ")");
	}-*/;

	public static String print(JsArrayMixed a) {
		String s = "JSArray[";
		for (int i = 0; i < a.length(); i++) {
			s += a.getString(i) + ", ";
		}
		return s + "]";
	}

	public static native final void arrayRemoveItem(JsArray<?> a, int i)/*-{
		a.splice(i, 1)
	}-*/;

	public static native double getDouble(JavaScriptObject o, String p)/*-{
		return o[p];
	}-*/;

	public static native final String dump(JavaScriptObject obj,
			boolean printValues)/*-{
		var s = "{";
		for ( var i in obj) {
			s += i + (printValues ? ": " + obj[i] : "") + ", ";
		}
		return s + "}";
	}-*/;

	public static native JavaScriptObject window()/*-{
		return $wnd;
	}-*/;

	public static final native JavaScriptObject toJsFunction(
			JsArrayMixedCallback c)/*-{
		return $entry(function() {
			return c.@loon.html5.gwt.GWTScriptLoader.JsArrayMixedCallback::call(Lcom/google/gwt/core/client/JsArrayMixed;)(arguments);
		});
	}-*/;

	public static void loadFont(String fontJsUrl, Callback<Void, Exception> c) {
		ScriptInjector.fromUrl(fontJsUrl).setWindow(window()).setCallback(c)
				.inject();
	}

	public static void loadFont(TextResource fontJs) {
		String text = fontJs.getText();
		ScriptInjector.fromString(text).setWindow(window()).inject();
	}

	public static void loadFont(ExternalTextResource fontJs,
			final ResourceCallback<TextResource> callback)
			throws ResourceException {
		fontJs.getText(new ResourceCallback<TextResource>() {

			@Override
			public void onSuccess(TextResource resource) {
				loadFont(resource);
				if (callback != null)
					callback.onSuccess(resource);
			}

			@Override
			public void onError(ResourceException e) {
				if (callback != null)
					callback.onError(e);
			}
		});
	}

	public static void injectScript(com.google.gwt.safehtml.shared.SafeUri js,
			final com.google.gwt.user.client.rpc.AsyncCallback<Void> callback) {
		final com.google.gwt.dom.client.ScriptElement[] script = new com.google.gwt.dom.client.ScriptElement[1];
		script[0] = com.google.gwt.core.client.ScriptInjector
				.fromUrl(js.asString())
				.setWindow(com.google.gwt.core.client.ScriptInjector.TOP_WINDOW)
				.setCallback(
						new com.google.gwt.core.client.Callback<Void, Exception>() {
							@Override
							public void onSuccess(Void result) {
								script[0].removeFromParent();
								callback.onSuccess(result);
							}

							@Override
							public void onFailure(Exception reason) {
								callback.onFailure(reason);
							}
						}).inject().cast();
	}

	public static void injectJavascript(
			com.google.gwt.resources.client.TextResource... textResources) {
		for (com.google.gwt.resources.client.TextResource textResource : textResources) {
			com.google.gwt.core.client.ScriptInjector
					.fromString(textResource.getText())
					.setWindow(
							com.google.gwt.core.client.ScriptInjector.TOP_WINDOW)
					.inject();
		}
	}

	public static void injectCss(
			com.google.gwt.resources.client.TextResource... textResources) {
		for (com.google.gwt.resources.client.TextResource textResource : textResources) {
			com.google.gwt.dom.client.StyleInjector.inject(textResource
					.getText());
		}
	}

	public static boolean isURL(String src) {
		if (src == null || src.trim().length() == 0 || src.startsWith("/")) {
			return false;
		}
		return src.startsWith("file") || src.startsWith("http");
	}
}

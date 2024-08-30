package loon.tea.dom;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public abstract class URLWrapper implements JSObject {

    @JSBody(params = { "object" }, script = "return URL.createObjectURL(object);")
    public static native String createObjectURL(JSObject object);
}
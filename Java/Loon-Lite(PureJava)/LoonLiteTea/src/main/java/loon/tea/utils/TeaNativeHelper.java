package loon.tea.utils;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class TeaNativeHelper {
    @JSBody(params = {"o1", "o2"}, script = "return o1 === o2;")
    public static native boolean compareObject(JSObject o1, JSObject o2);
}
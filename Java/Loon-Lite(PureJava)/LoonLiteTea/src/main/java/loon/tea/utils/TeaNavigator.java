package loon.tea.utils;

import org.teavm.jso.JSBody;

public class TeaNavigator {

    @JSBody(script = "return navigator.platform;")
    public static native String getPlatform();

    @JSBody(script = "return navigator.userAgent;")
    public static native String getUserAgent();
}

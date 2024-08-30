package loon.tea.gl;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public abstract class WebGLContextAttributesWrapper implements JSObject {
    @JSProperty
    public abstract boolean isAlpha();

    @JSProperty
    public abstract void setAlpha(boolean alpha);

    @JSProperty
    public abstract boolean isDepth();

    @JSProperty
    public abstract void setDepth(boolean depth);

    @JSProperty
    public abstract boolean isScencil();

    @JSProperty
    public abstract void setStencil(boolean stencil);

    @JSProperty
    public abstract boolean isAntialias();

    @JSProperty
    public abstract void setAntialias(boolean antialias);

    @JSProperty
    public abstract boolean isPremultipliedAlpha();

    @JSProperty
    public abstract void setPremultipliedAlpha(boolean premultipliedAlpha);

    @JSProperty
    public abstract boolean isPreserveDrawingBuffer();

    @JSProperty
    public abstract void setPreserveDrawingBuffer(boolean preserveDrawingBuffer);

    @JSProperty
    public abstract void setPowerPreference(String powerPreference);

    @JSBody(script = "return {};")
    public static native WebGLContextAttributesWrapper create();
}
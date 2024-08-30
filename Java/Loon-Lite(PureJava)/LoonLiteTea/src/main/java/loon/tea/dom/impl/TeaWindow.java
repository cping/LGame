package loon.tea.dom.impl;

import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Location;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.EventListener;

import loon.tea.dom.DocumentWrapper;
import loon.tea.dom.EventListenerWrapper;
import loon.tea.dom.LocationWrapper;
import loon.tea.dom.WindowWrapper;

public class TeaWindow implements WindowWrapper, AnimationFrameCallback {

    private static final TeaWindow TEA_WINDOW = new TeaWindow();;

    public static TeaWindow get() {
        return TEA_WINDOW;
    }

    private Window window;
    private Runnable runnable;

    public TeaWindow() {
        this.window = Window.current();
    }

    @Override
    public DocumentWrapper getDocument() {
        DocumentWrapper document = (DocumentWrapper)window.getDocument();
        return document;
    }

    @Override
    public void requestAnimationFrame(Runnable runnable) {
        this.runnable = runnable;
        Window.requestAnimationFrame(this);
    }

    @Override
    public void onAnimationFrame(double arg0) {
        Runnable toRun = runnable;
        runnable = null;
        toRun.run();
    }

    @Override
    public LocationWrapper getLocation() {
        Location location = window.getLocation();
        return (LocationWrapper)location;
    }

    @Override
    public int getClientWidth() {
        return window.getDocument().getDocumentElement().getClientWidth();
    }

    @Override
    public int getClientHeight() {
        return window.getDocument().getDocumentElement().getClientHeight();
    }

    @Override
    public void addEventListener(String type, EventListenerWrapper listener) {
        EventListener<?> eListener = (EventListener<?>)listener;
        window.addEventListener(type, eListener);
    }
}

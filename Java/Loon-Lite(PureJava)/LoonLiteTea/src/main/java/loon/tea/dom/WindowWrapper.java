package loon.tea.dom;

public interface WindowWrapper {

    public DocumentWrapper getDocument();

    public void requestAnimationFrame(Runnable runnable);

    public LocationWrapper getLocation();

    public int getClientWidth();

    public int getClientHeight();

    public void addEventListener(String type, EventListenerWrapper listener);
}

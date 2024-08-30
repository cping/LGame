package loon.tea.dom;

public interface EventWrapper {

    String getType();

    EventTargetWrapper getTarget();

    public void preventDefault();

    public void stopPropagation();

    public float getDetail();
}

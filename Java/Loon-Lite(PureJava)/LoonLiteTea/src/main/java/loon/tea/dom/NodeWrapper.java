package loon.tea.dom;

public interface NodeWrapper extends EventTargetWrapper {
    public NodeWrapper getParentNode();

    public void appendChild(NodeWrapper node);
}

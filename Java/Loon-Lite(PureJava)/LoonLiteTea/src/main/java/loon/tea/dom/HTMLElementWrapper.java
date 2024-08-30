package loon.tea.dom;

public interface HTMLElementWrapper extends ElementWrapper {

    HTMLElementWrapper getOffsetParent();

    int getOffsetTop();

    int getOffsetLeft();
}

package loon.tea.dom;

public interface MouseEventWrapper extends EventWrapper {
    int getClientX();

    int getClientY();

    float getMovementX();

    float getMovementY();

    short getButton();
}

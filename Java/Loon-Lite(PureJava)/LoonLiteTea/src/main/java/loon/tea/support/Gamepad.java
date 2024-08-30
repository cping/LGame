package loon.tea.support;

import org.teavm.jso.JSObject;

public interface Gamepad extends JSObject {
    String getId();

    int getIndex();

    double getTimestamp();

    double[] getAxes();

    int[] getButtons();

    double getPreviousTimestamp();

    void setPreviousTimestamp(double previousTimestamp);
}

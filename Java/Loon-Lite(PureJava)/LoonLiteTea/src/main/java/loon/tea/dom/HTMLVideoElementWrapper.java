package loon.tea.dom;

import org.teavm.jso.JSObject;

public interface HTMLVideoElementWrapper extends JSObject {

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    int getVideoWidth();

    int getVideoHeight();

    String getPoster();

    void setPoster(String poster);
}

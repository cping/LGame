package loon.tea.dom;

public interface XMLHttpRequestWrapper extends XMLHttpRequestEventTargetWrapper {

    public static final short UNSENT = 0;
    public static final short OPENED = 1;
    public static final short HEADERS_RECEIVED = 2;
    public static final short LOADING = 3;
    public static final short DONE = 4;

    public void setOnreadystatechange(EventHandlerWrapper onreadystatechange);

    public void open(String method, String url);

    public void open(String method, String url, boolean async);

    public void setRequestHeader(String header, String value);

    public void setResponseType(String type);

    public void send();

    public short getReadyState();

    public short getStatus();

    public NodeWrapper getResponse();

    public String getResponseText();
}

package loon.tea;

import org.teavm.jso.dom.html.HTMLDocument;

public class Client {
    public static void main(String[] args) {
        var document = HTMLDocument.current();
        var div = document.createElement("div");
        div.appendChild(document.createTextNode("Loon-Lite-TeaVM"));
        document.getBody().appendChild(div);
    }
}

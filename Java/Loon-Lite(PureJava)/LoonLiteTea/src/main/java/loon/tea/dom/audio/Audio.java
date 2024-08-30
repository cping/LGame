package loon.tea.dom.audio;

import org.teavm.jso.JSBody;

public class Audio {

    public static HTMLAudioElementWrapper createIfSupported() {
        return createAudio();
    }

    @JSBody(script = "return new Audio();")
    private static native HTMLAudioElementWrapper createAudio();
}
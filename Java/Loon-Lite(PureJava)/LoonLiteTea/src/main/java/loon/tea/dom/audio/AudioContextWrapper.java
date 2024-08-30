package loon.tea.dom.audio;

public interface AudioContextWrapper extends BaseAudioContextWrapper {
    void resume();

    void suspend();
}
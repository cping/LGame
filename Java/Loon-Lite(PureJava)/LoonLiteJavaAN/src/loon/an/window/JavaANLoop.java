package loon.an.window;

public interface JavaANLoop {

    public boolean get();

    public void process(final boolean active);

    public JavaANLoop set(final boolean r);
}
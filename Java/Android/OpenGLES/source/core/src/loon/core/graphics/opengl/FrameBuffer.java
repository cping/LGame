package loon.core.graphics.opengl;


public interface FrameBuffer {

	public boolean isSupported();
	
	public int getWidth() ;

	public int getHeight();

	public int getID();

	public LTexture getTexture();

	public boolean isLoaded();

	public void bind() ;

	public void unbind() ;

	public void destroy() ;

	public void build();
}

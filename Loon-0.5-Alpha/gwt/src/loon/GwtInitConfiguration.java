package loon;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;

public class GwtInitConfiguration {

	public int width;

	public int height;

	public boolean stencil = false;

	public boolean antialiasing = false;

	public Panel rootPanel;

	public String canvasId;

	public TextArea log;
	
	public boolean useDebugGL = false;
	
	public boolean preferFlash = true;
	
	public boolean preserveDrawingBuffer = false;

	public GwtInitConfiguration (int width, int height) {
		this.width = width;
		this.height = height;
	}
}

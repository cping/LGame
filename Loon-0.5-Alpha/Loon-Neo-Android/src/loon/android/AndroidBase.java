package loon.android;

import android.os.Handler;
import android.view.Window;

public interface AndroidBase {
	
	void setImmersiveMode (boolean b);
	
	Handler getHandler ();
	
	Window getApplicationWindow ();
	
}

/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.html5.gwt;

import loon.Log;

public class GWTLog extends Log {

	private GWTLog() {
	}

	@Override
	protected void callNativeLog(Level level, String msg, Throwable e) {
		String lmsg = level + ": " + msg;
		if (e != null) {
			lmsg += ": " + e.getMessage();
		}
		if (e != null) {
			e.printStackTrace(System.out);
		}
		sendToBrowserConsole(lmsg, e);
	}

	private native void sendToBrowserConsole(String msg, Throwable e) /*-{
																		if ($wnd.console && $wnd.console.info) {
																		if (e != null) {
																		$wnd.console.info(msg, e);
																		} else {
																		$wnd.console.info(msg);
																		}
																		}else{
																		if (e != null) {
																		if (window.console) {
																		window.console.log(msg + ":" + e);
																		} else {
																		document.title = msg + "," + e;
																		}
																		}else{
																		if (window.console) {
																		window.console.log(msg);
																		} else {
																		document.title = msg;
																		}
																		}
																		}
																		}-*/;

	@Override
	public void onError(Throwable e) {
		//LSystem.AUTO_REPAINT = false;
	}

}

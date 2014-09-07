package loon;

import java.lang.reflect.Method;

import loon.utils.MathUtils;

/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 * 
 * Example1:
 * 
 *  sendRESTCoin("rGmaiL8f7VDRrYouZokr5qv61b5zvhePcp", "cping","Thank you donate to LGame", 100);
 * 
 * 通过RTXP金融协议,向指定抵制发送指定货币(也可以直接充值和发送BTC到此协议网络中)
 * 
 */
public class LRipple {

	public static void sendRESTCoin(String address, String name, String label,
			long amount) {
		LRipple.sendRESTCoin(address, name, label, amount, "XRP",
				MathUtils.random(1, 9999));
	}

	public static void sendRESTCoin(String address, String name, String label,
			long amount, String currency, long dt) {
		java.net.URI uri;
		String page = "https://ripple.com//send?to=" + address + "&name="
				+ name + "&label=" + label.replace(" ", "%20") + "&amount="
				+ amount + "/" + currency + "&dt=" + dt;
		try {
			uri = new java.net.URI(page);
			java.awt.Desktop.getDesktop().browse(uri);
		} catch (Exception e) {
			try {
				browse(page);
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
	}

	private static void browse(String url) throws Exception {
		String osName = System.getProperty("os.name", "");
		if (osName.startsWith("Mac OS")) {
			Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
			Method openURL = fileMgr.getDeclaredMethod("openURL",
					new Class[] { String.class });
			openURL.invoke(null, new Object[] { url });
		} else if (osName.startsWith("Windows")) {
			Runtime.getRuntime().exec(
					"rundll32 url.dll,FileProtocolHandler " + url);
		} else {
			String[] browsers = { "firefox", "opera", "konqueror", "epiphany",
					"mozilla", "netscape" };
			String browser = null;
			for (int count = 0; count < browsers.length && browser == null; count++) {
				if (Runtime.getRuntime()
						.exec(new String[] { "which", browsers[count] })
						.waitFor() == 0) {
					browser = browsers[count];
				}
			}
			if (browser == null) {
				throw new Exception("Could not find web browser");
			} else {
				Runtime.getRuntime().exec(new String[] { browser, url });
			}
		}
	}
}

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
package loon;

import loon.utils.MathUtils;

/**
 * LGame自带的简易金融接口(所有版本通用),可以利用Ripple协议进行简单的金融转移.即通过RTXP金融协议,向指定抵制发送指定货币(
 * 也可以直接充值和发送BTC到此协议网络中)
 * <p>
 * <h3>向指定地址发送XRP</h3>
 * <p>
 * 
 * <pre class="prettyprint">
 * sendRESTCoin(&quot;rGmaiL8f7VDRrYouZokr5qv61b5zvhePcp&quot;, &quot;cping&quot;,
 * 		&quot;Thank you donate to LGame&quot;, 100);
 * </pre>
 */
public class LRipple {

	public static void sendRESTCoin(String address, String name, String label,
			long amount) {
		LRipple.sendRESTCoin(address, name, label, amount, "XRP",
				MathUtils.random(1, 9999));
	}

	public static void sendRESTCoin(String address, String name, String label,
			long amount, String currency, long dt) {
		String page = "https://ripple.com//send?to=" + address + "&name="
				+ name + "&label=" + label.replace(" ", "%20") + "&amount="
				+ amount + "/" + currency + "&dt=" + dt;
		LSystem.openURL(page);
	}

}

package loon.net;

import loon.core.LSystem;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class HttpHeader {
	/**
	 * 虚拟的浏览器数据,用以欺骗服务器
	 */
	final static String[] HTTP_USER_AGENT = new String[] {
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.6) Gecko/20070725 Firefox/2.0.0.6",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727; .NET CLR 1.1.4322)",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.6) Gecko/20070725 Firefox/2.0.0.6",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; SV1; .NET CLR 1.0.3705;..NET CLR 1.1.4322; InfoPath.1; Media Center PC 4.0; .NET CLR 2.0.50727)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.1)",
			"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)" };

	private String get;

	private String host;

	private String accept;

	private String referer;

	private String cookie;

	private String userAgent;

	private String userAgentValue;

	private String range;

	private String pragma;

	private String cacheControl;

	private String connection;

	public HttpHeader() {
		userAgentValue = HTTP_USER_AGENT[LSystem.random
				.nextInt(HTTP_USER_AGENT.length)];
		get = "GET ";
		host = "Host: ";
		accept = "Accept: */*\r\n";
		referer = "Referer: ";
		cookie = "Cookie: ";
		userAgent = "User-Agent: " + userAgentValue + "\r\n";
		range = "Range: bytes=0-\r\n";
		pragma = "Pragma: no-cache\r\n";
		cacheControl = "Cache-Control: no-cache\r\n";
		connection = "Connection: close\r\n\r\n";
	}

	public void setGet(String g) {
		int t = get.indexOf(" ");
		get = get.substring(0, t + 1);
		get = get + g + "\r\n";
	}

	public void setHost(String h) {
		int t = host.indexOf(" ");
		host = host.substring(0, t + 1);
		host = host + h + "\r\n";
	}

	public void setRange(String r) {
		int t = range.indexOf("=");
		range = range.substring(0, t + 1);
		range = range + r + "\r\n";
	}

	public void setReferer(String ref) {
		int t = referer.indexOf(":");
		referer = referer.substring(0, t + 1);
		referer = referer + ref + "\r\n";
	}

	public void setCookie(String c) {
		int t = cookie.indexOf(":");
		cookie = cookie.substring(0, t + 1);
		cookie = cookie + c + "\r\n";
	}

	public String getUserAgentValue() {
		return userAgentValue;
	}

	public String getHeaderString() {
		return get + host + accept + userAgent + range + pragma + cacheControl
				+ connection;
	}

}

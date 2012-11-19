package loon.net;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * Copyright 2008 - 2011
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
/**
 * 这是一个简单的http模拟发包工具，自0.3.2版起再次提供(更早期版本中内置过一次,
 * 但考虑到Android内置有apache的HttpClient包而取消，目前为了统一跨平台编码标准 
 * 决定重新加入，并略有改进 PS:取消了原有的断点续传下载方式，需要者可翻看旧版源码)。
 */
public class HttpClient extends HttpClientAbstract {

	public HttpClient(String url) {
		super(url);
	}

	public HttpClient(URL url) {
		super(url);
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void postData(String data) {
		this.postData = data;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public int getResponseLength() {
		return responseLength;
	}

	public HashMap<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(HashMap<String, String> cookies) {
		this.cookies = cookies;
	}

	public void freeCookies() {
		cookies.clear();
		cookie = "";
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return this.method;
	}

	public String getCookie() {
		return cookie;
	}

	public void dispose() {
		stop();
	}
	
}

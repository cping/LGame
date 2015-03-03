package loon.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import loon.LSystem;
import loon.core.LRelease;
import loon.net.Base64Coder;
import loon.utils.StringUtils;


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
abstract class HttpClientAbstract implements LRelease {

	final static String POST = "POST";

	final static String GET = "GET";

	final static String HTTPS = "HTTPS";

	protected int timeOut, responseCode, responseLength, bufferedSize;

	protected String cookie, method, postData, digest, userName, passWord;

	protected HttpURLConnection connection;

	protected HashMap<String, String> cookies;

	protected HashMap<String, String> headerMap;

	protected InputStream inputStream;

	protected boolean isRunning;

	private HttpHeader header;

	private URL open;

	private String urlString;

	private String accept, acceptLanguage, acceptCharset;

	public HttpClientAbstract(String urlString) {
		try {
			this.urlString = urlString;
			this.open(new URL(urlString.startsWith("http://")
					|| urlString.startsWith("https://")
					|| urlString.startsWith("ftp://") ? urlString
					: ("http://" + urlString).intern()));
		} catch (MalformedURLException e) {
		}
	}

	private javax.net.ssl.HostnameVerifier hv = new javax.net.ssl.HostnameVerifier() {
		@Override
		public boolean verify(String urlHostName,
				javax.net.ssl.SSLSession session) {
			return true;
		}
	};

	protected static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new Trust();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());
	}

	public static class Trust implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		@Override
		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		@Override
		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

	public HttpClientAbstract(URL url) {
		open(url);
	}

	private void open(URL url) {
		this.cookies = new HashMap<String, String>(10);
		this.headerMap = new HashMap<String, String>(10);
		this.open = url;
		this.header = new HttpHeader();
		this.method = "GET";
		this.timeOut = 180000;
	}

	private void postCookies() {
		StringBuffer sbr = new StringBuffer();
		Iterator<?> it = cookies.entrySet().iterator();
		do {
			if (!it.hasNext()) {
				break;
			}
			Entry<?, ?> entry = (Entry<?, ?>) it.next();
			sbr.append(entry.getKey());
			sbr.append("=");
			sbr.append(entry.getValue());
			if (it.hasNext()) {
				sbr.append("; ");
			}
		} while (true);
		if (sbr.length() > 0) {
			connection.setRequestProperty("Cookie", cookie = sbr.toString());
			header.setCookie(cookie);
		}
	}

	public void start() {
		try {
			boolean foundRedirect;
			do {
				if (HTTPS.equalsIgnoreCase(open.getProtocol())) {
					trustAllHttpsCertificates();
					javax.net.ssl.HttpsURLConnection
							.setDefaultHostnameVerifier(hv);
					connection = (javax.net.ssl.HttpsURLConnection) open
							.openConnection();
				} else {
					connection = (HttpURLConnection) open.openConnection();
					// 设定Cookies
					postCookies();

				}
				if (digest != null) {
					connection.setRequestProperty("Authorization", digest);
				}
				connection.setDoOutput(POST.equalsIgnoreCase(method));
				connection.setDoInput(true);
				connection.setUseCaches(false);
				connection.setInstanceFollowRedirects(false);
				// 超时设定
				if (timeOut > 0) {
					System.setProperty("sun.net.client.defaultConnectTimeout",
							String.valueOf(timeOut));
					System.setProperty("sun.net.client.defaultReadTimeout",
							String.valueOf(timeOut));
				}
				connection.setRequestMethod(method);
				connection.setRequestProperty("User-Agent", header
						.getUserAgentValue());
				connection
						.setRequestProperty(
								"Accept",
								accept == null ? "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, */*"
										: accept);
				connection.setRequestProperty("Accept-Language",
						acceptLanguage == null ? "zh-CN" : acceptLanguage);
				connection
						.setRequestProperty(
								"Accept-Charset",
								acceptCharset == null ? "ISO-8859-1,utf-8;q=0.7,*;q=0.7"
										: acceptCharset);
				connection.setRequestProperty("Content-type", "text/html");
				connection.setRequestProperty("Cache-Control", "no-cache");
				if (POST.equalsIgnoreCase(method)) {
					connection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					if (postData != null) {
						DataOutputStream out = new DataOutputStream(connection
								.getOutputStream());
						out.writeBytes(postData);
						out.flush();
						out.close();
					}
				}
				if (headerMap != null && headerMap.size() > 0) {
					Set<?> headersSet = headerMap.entrySet();
					for (Iterator<?> it = headersSet.iterator(); it.hasNext();) {
						Entry<?, ?> entry = (Entry<?, ?>) it.next();
						connection.setRequestProperty((String) entry.getKey(),
								(String) entry.getValue());
					}
				}
				// 尝试连接
				connection.connect();
				cookie = connection.getHeaderField("Set-Cookie");
				if (cookie != null) {
					// 注入Cookies
					setCookies(StringUtils.split(cookie, ";"));
				}
				// 获得响应状态
				responseCode = connection.getResponseCode();
				// 获得返回的数据长度
				responseLength = connection.getContentLength();

				if (responseCode == 302) {
					// 重定向
					String location = connection.getHeaderField("Location");

					open = new URL(location);
					foundRedirect = true;
				} else {
					if (responseCode == 200 || responseCode == 201) {
						inputStream = connection.getInputStream();
					} else {
						inputStream = connection.getErrorStream();
					}
					bufferedSize = responseLength == -1 ? 2048 : responseLength;
					foundRedirect = false;
				}
				// 如果重定向则继续
			} while (foundRedirect);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.isRunning = true;
	}

	public HttpDownload getHttpDownload() {
		return new HttpDownload(this);
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * 返回当前请求的端口号
	 * 
	 * @return
	 */
	public int getPort() {
		return ((open != null) && (open.getPort() != -1)) ? open.getPort() : 80;
	}

	/**
	 * 获得当前链接
	 * 
	 * @return
	 */
	public HttpURLConnection getConnection() {
		try {
			if (HTTPS.equalsIgnoreCase(open.getProtocol())) {
				trustAllHttpsCertificates();
				javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(hv);
				connection = (javax.net.ssl.HttpsURLConnection) open
						.openConnection();
			} else {
				connection = (HttpURLConnection) open.openConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * 检查对应的本地文件是否存在
	 * 
	 * @return
	 */
	public boolean exists() {
		File file = new File(getFileName());
		if (!file.exists()) {
			return false;
		}
		int size = -1;
		try {
			HttpURLConnection connection = getConnection();
			connection.connect();
			size = connection.getContentLength();
			connection.disconnect();
		} catch (Exception e) {

		}
		return size == file.length();
	}

	/**
	 * 返回当前请求下载的文件名
	 * 
	 * @return
	 */
	public String getFileName() {
		if (open == null) {
			return null;
		}
		return open.getPath().substring(open.getPath().lastIndexOf("/") + 1,
				open.getPath().length());
	}

	/**
	 * 设定cookies
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setCookie(String key, String value) throws IOException {
		cookies.put(key.trim(), value.trim());
	}

	/**
	 * 设定cookies
	 * 
	 * @param cookie
	 * @throws IOException
	 */
	public void setCookies(HashMap<String, String> cookie) throws IOException {
		if (cookie == null) {
			return;
		} else {
			cookies.putAll(cookie);
			return;
		}
	}

	/**
	 * 设定cookies
	 * 
	 * @param cookie
	 * @throws IOException
	 */
	public void setCookies(String[] cookie) throws IOException {
		if (cookie == null) {
			return;
		}
		try {
			for (int i = 0; i < cookie.length; i++) {
				String[] ret = cookie[i].split("=");
				setCookie(ret[0], ret[1]);
			}
		} catch (Exception e) {
		}
	}

	public String doHTML() {
		return doHTML(null);
	}

	/**
	 * 以指定格式显示网页字符串
	 * 
	 * @param encoding
	 * @return
	 */
	public String doHTML(String encoding) {
		if (!isRunning) {
			start();
		}
		if (connection == null) {
			return "";
		}
		try {
			if (encoding != null) {
				return readString(inputStream, bufferedSize, encoding);
			} else {
				return readString(inputStream, bufferedSize, LSystem.encoding);
			}
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	synchronized static String readString(InputStream in, int size,
			final String encoding) throws IOException {
		StringBuffer sbr = new StringBuffer();
		int nSize = size;
		if (nSize == 0) {
			nSize = 2048;
		}
		char[] buffer = new char[nSize];
		int offset = 0;
		InputStreamReader isr = new InputStreamReader(in, encoding);
		while ((offset = isr.read(buffer)) != -1) {
			sbr.append(buffer, 0, offset);
		}
		try {
			if (in != null) {
				in.close();
				in = null;
			}
			if (isr != null) {
				isr.close();
				isr = null;
			}
		} catch (Exception e) {
		}
		return sbr.toString();
	}

	public void setParameter(HashMap<?, ?> parameter) {
		if (parameter != null) {
			Set<?> entrySet = parameter.entrySet();
			for (Iterator<?> it = entrySet.iterator(); it.hasNext();) {
				Entry<?, ?> header = (Entry<?, ?>) it.next();
				String key = (String) header.getKey();
				String value = (String) header.getValue();
				if ("user".equals(key)) {
					userName = value;
				} else if ("pass".equals(key)) {
					passWord = value;
				} else if ("method".equals(key)) {
					method = value;
				} else if ("data".equals(key)) {
					postData = value;
				} else {
					headerMap.put(key, value);
				}
			}
		}
		if (userName != null && passWord != null) {
			digest = "Basic "
					+ Base64Coder
							.encode((userName + ":" + passWord).getBytes());
		}
	}

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getAcceptCharset() {
		return acceptCharset;
	}

	public void setAcceptCharset(String acceptCharset) {
		this.acceptCharset = acceptCharset;
	}

	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}

	public String getURLString() {
		return urlString;
	}

	public URL getURL() {
		return open;
	}

	public void stop() {
		if (inputStream != null) {
			try {
				inputStream.close();
				inputStream = null;
			} catch (IOException e) {
			}
		}
		if (connection != null) {
			connection.disconnect();
			connection = null;
		}
		this.isRunning = false;
	}

}

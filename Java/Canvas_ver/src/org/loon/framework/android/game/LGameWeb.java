package org.loon.framework.android.game;

import java.io.IOException;
import java.util.Hashtable;

import org.loon.framework.android.game.core.LSystem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * 
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
 * @email ceponline@yahoo.com.cn
 * @version 0.1.0
 */
public class LGameWeb extends WebView {

	/**
	 * Web自定义脚本(name即代表JavaScript中类名,以"name."形式执行Object封装的具体对象)
	 * 比如构建一个脚本类名为App,提供一个具体类Home，其中只有一个函数go。那么执行脚本
	 * App.go时即自动调用相关的Home类中同名函数，并且在Android系统中执行。
	 * 
	 */
	public interface JavaScript {

		// 执行的对象，实际内部应该封装具体类
		public Object getObject();

		// 脚本类名
		public String getName();

	}

	/**
	 * Web加载进度监听器
	 * 
	 */
	public interface WebProcess {

		// 页面开始加载
		public void onPageStarted(String url, Bitmap favicon);

		// 页面加载完成
		public void onPageFinished(String url);

		// 资源载入
		public void onLoadResource(String url);

		// 即将重载链接前
		public void shouldOverrideUrlLoading(String url);

		// 接受请求
		public void onReceivedHttpAuthRequest(HttpAuthHandler handler,
				String host, String realm);

	}

	private LGameAndroid2DActivity activity;

	private WebSettings webSettings;

	private String url;

	public LGameWeb(String url) {
		this((LGameAndroid2DActivity) LSystem.getActivity(), null, url);
	}

	public LGameWeb(String url, WebProcess webProcess) {
		this((LGameAndroid2DActivity) LSystem.getActivity(), webProcess, url);
	}

	public LGameWeb(LGameAndroid2DActivity activity, String url) {
		this(activity, null, url);
	}

	public LGameWeb(final LGameAndroid2DActivity activity,
			final WebProcess webProcess, final String url) {
	
		super(activity);
		this.url = url;
		this.activity = activity;
		// 允许显示滚动条
		this.setHorizontalScrollBarEnabled(true);
		// 清空原有的缓存数据
		this.clearCache(true);
		// 隐藏当前View
		this.setVisible(false);
		// 不要背景图
		this.setBackgroundDrawable(null);
		// 进行细节设置
		webSettings = getSettings();
		// 数据库访问权限开启
		webSettings.setAllowFileAccess(true);
		// 密码保存与Form信息不保存
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		// 响应JavaScript事件
		webSettings.setJavaScriptEnabled(true);
		// 允许JavaScript脚本打开新的窗口
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 允许自动加载图像资源
		webSettings.setLoadsImagesAutomatically(true);
		// 不支持网页缩放
		webSettings.setSupportZoom(false);

		// 当流程监听存在时
		if (webProcess != null) {
			setWebViewClient(new WebViewClient() {

				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					webProcess.onPageStarted(url, favicon);
					super.onPageStarted(view, url, favicon);
				}

				public void onPageFinished(WebView view, String url) {
					webProcess.onPageFinished(url);
					super.onPageFinished(view, url);
				}

				public void onLoadResource(WebView view, String url) {
					webProcess.onLoadResource(url);
					super.onLoadResource(view, url);
				}

				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					webProcess.shouldOverrideUrlLoading(url);
					return super.shouldOverrideUrlLoading(view, url);
				}

				public void onReceivedHttpAuthRequest(WebView view,
						HttpAuthHandler handler, String host, String realm) {
					webProcess.onReceivedHttpAuthRequest(handler, host, realm);
					super.onReceivedHttpAuthRequest(view, handler, host, realm);
				}

			});
		}

		// 加载进度条
		final ProgressBar progress = new ProgressBar(activity);
		activity.addView(progress, Location.CENTER);
		setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(final WebView view,
					final int newProgress) {
				LSystem.post(new Runnable() {
					public void run() {
						progress.setProgress(newProgress);
						progress.setVisibility(newProgress == 100 ? View.GONE
								: View.VISIBLE);
						if (newProgress == 100) {
							activity.removeView(progress);
						}
						setVisible(newProgress == 100 ? true : false);
					}
				});

			}
		});

		if (url != null) {
			loadUrl(url);
		}
	}

	/**
	 * 像当前Web界面进行内部标记性赋值
	 * 
	 * @param name
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void setWebParams(String name, Object value) {
		Hashtable<String, Object> params = null;
		if (getTag() == null) {
			params = new Hashtable<String, Object>();
			setTag(params);
		} else {
			params = (Hashtable<String, Object>) getTag();
		}
		params.put(name, value);
	}

	/**
	 * 获得当前Web界面的内部标记性传参
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object getWebParams(String name) {
		if (getTag() == null) {
			setTag(new Hashtable<String, Object>());
		}
		Hashtable<String, Object> params = (Hashtable<String, Object>) getTag();
		return params.get(name);
	}

	/**
	 * 添加一组自制JavaScript脚本到当前Web界面当中
	 * 
	 * @param sprites
	 */
	public void addJavaScripts(JavaScript[] sprites) {
		for (int i = 0; i < sprites.length; i++) {
			this.addJavascriptInterface(sprites[i].getObject(), sprites[i]
					.getName());
		}
	}

	/**
	 * 添加自制JavaScript脚本到当前Web界面当中
	 * 
	 * @param sprite
	 */
	public void addJavaScripts(JavaScript sprite) {
		this.addJavascriptInterface(sprite.getObject(), sprite.getName());
	}

	public void setVisible(boolean isVisible) {
		if (isVisible) {
			this.setVisibility(View.VISIBLE);
		} else {
			this.setVisibility(View.GONE);
		}
	}

	public void callScriptFunction(String function) {
		super.loadUrl("javascript:" + function);
	}

	/**
	 * 通过Intent进行跳转
	 * 
	 * @param intent
	 */
	public void loadIntent(Intent intent) {
		this.loadUrl(intent.getStringExtra(android.app.SearchManager.QUERY));
	}

	/**
	 * 通过Url进行跳转
	 */
	public void loadUrl(String url) {
		boolean isURL = url.startsWith("http://") || url.startsWith("https://")
				|| url.startsWith("ftp://");
		if (!isURL) {
			try {
				if (activity.getAssets().open(url) != null) {
					super.loadUrl("file:///android_asset/" + url);
				} else {
					super.loadUrl("http://" + url);
				}
			} catch (IOException e) {
				super.loadUrl("http://" + url);
			}
		} else {
			super.loadUrl(url);
		}
	}

	public void loadData(String data) {
		loadData(data, "text/html", LSystem.encoding);
	}

	public void loadData(String data, String encoding) {
		super.loadData(data, "text/html", encoding);
	}

	public void loadData(String data, String mimeType, String encoding) {
		super.loadData(data, mimeType, encoding);
	}

	public WebSettings getWebSettings() {
		return webSettings;
	}

	public String getURL() {
		return url;
	}

}

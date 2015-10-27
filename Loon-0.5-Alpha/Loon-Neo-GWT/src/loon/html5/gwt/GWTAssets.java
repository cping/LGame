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

import java.util.List;

import loon.Assets;
import loon.Sound;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.jni.XDomainRequest;
import loon.utils.Scale;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Window;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class GWTAssets extends Assets {

	private static final boolean LOG_XHR_SUCCESS = false;

	private final GWTGame plat;

	private String pathPrefix = GWT.getModuleBaseForStaticFiles();
	private Scale assetScale = null;

	public void setPathPrefix(String prefix) {
		pathPrefix = prefix;
	}

	public void setAssetScale(float scaleFactor) {
		this.assetScale = new Scale(scaleFactor);
	}

	@Override
	public Image getImageSync(String path) {
		for (Scale.ScaledResource rsrc : assetScale().getScaledResources(path)) {
			return adaptImage(pathPrefix + path, rsrc.scale);
		}
		return new GWTImage(plat.graphics(), new Throwable(
				"Image missing from manifest: " + path));
	}

	@Override
	public Image getImage(String path) {
		Scale assetScale = (this.assetScale == null) ? Scale.ONE
				: this.assetScale;
		List<Scale.ScaledResource> rsrcs = assetScale.getScaledResources(path);
		return getImage(rsrcs.get(0).path, rsrcs.get(0).scale);
	}

	protected GWTImage getImage(String path, Scale scale) {
		String url = pathPrefix + path;

		return adaptImage(url, scale);
	}

	@Override
	public Image getRemoteImage(String url) {
		return adaptImage(url, Scale.ONE);
	}

	@Override
	public Image getRemoteImage(String url, int width, int height) {
		return adaptImage(url, Scale.ONE).preload(width, height);
	}

	@Override
	public Sound getSound(String path) {
		GWTResourcesLoader gwtFile = Loon.self.resources.internal(pathPrefix + path);
		return new GWTSound(gwtFile.path());
	}

	@Override
	public String getTextSync(String path) throws Exception {
		GWTResourcesLoader gwtFile = Loon.self.resources.internal(pathPrefix + path);
		return gwtFile.readString();
	}

	@Override
	public GoFuture<String> getText(final String path) {
		GoPromise<String> result = GoPromise.create();
		final String fullPath = pathPrefix + path;
		try {
			doXhr(fullPath, result);
		} catch (JavaScriptException e) {
			if (Window.Navigator.getUserAgent().indexOf("MSIE") != -1) {
				doXdr(fullPath, result);
			} else {
				GWTResourcesLoader gwtFile = Loon.self.resources.internal(fullPath);
				try {
					result.succeed(gwtFile.readString());
				} catch (Exception ex) {
					result.succeed(null);
				}
			}
		}
		return result;
	}

	@Override
	public byte[] getBytesSync(String path) throws Exception {
		GWTResourcesLoader gwtFile = Loon.self.resources.internal(pathPrefix + path);
		return gwtFile.readBytes();
	}

	@Override
	protected ImageImpl.Data load(String path) throws Exception {
		throw new UnsupportedOperationException("unused");
	}

	@Override
	protected ImageImpl createImage(boolean async, int rwid, int rhei,
			String source) {
		ImageElement img = Document.get().createImageElement();
		setCrossOrigin(img, "anonymous");
		img.setSrc(source);
		return new GWTImage(plat.graphics(), plat.graphics().scale(), img,
				source);
	}

	GWTAssets(GWTGame plat) {
		super(plat.asyn());
		this.plat = plat;
	}

	private Scale assetScale() {
		return (assetScale != null) ? assetScale : plat.graphics().scale();
	}

	private void doXdr(final String path, final GoPromise<String> result) {
		XDomainRequest xdr = XDomainRequest.create();
		xdr.setHandler(new XDomainRequest.Handler() {
			@Override
			public void onTimeout(XDomainRequest xdr) {
				plat.log().error("xdr::onTimeout[" + path + "]()");
				result.fail(new Exception("Error getting " + path + " : "
						+ xdr.getStatus()));
			}

			@Override
			public void onProgress(XDomainRequest xdr) {
				if (LOG_XHR_SUCCESS)
					plat.log().debug("xdr::onProgress[" + path + "]()");
			}

			@Override
			public void onLoad(XDomainRequest xdr) {
				if (LOG_XHR_SUCCESS)
					plat.log().debug("xdr::onLoad[" + path + "]()");
				result.succeed(xdr.getResponseText());
			}

			@Override
			public void onError(XDomainRequest xdr) {
				plat.log().error("xdr::onError[" + path + "]()");
				result.fail(new Exception("Error getting " + path + " : "
						+ xdr.getStatus()));
			}
		});
		if (LOG_XHR_SUCCESS)
			plat.log().debug("xdr.open('GET', '" + path + "')...");
		xdr.open("GET", path);
		if (LOG_XHR_SUCCESS)
			plat.log().debug("xdr.send()...");
		xdr.send();
	}

	private void doXhr(final String path, final GoPromise<String> result) {
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int readyState = xhr.getReadyState();
				if (readyState == XMLHttpRequest.DONE) {
					int status = xhr.getStatus();
					if (status != 0 && (status < 200 || status >= 400)) {
						plat.log().error(
								"xhr::onReadyStateChange[" + path + "]"
										+ "(readyState = " + readyState
										+ "; status = " + status + ")");
						result.fail(new Exception("Error getting " + path
								+ " : " + xhr.getStatusText()));
					} else {
						if (LOG_XHR_SUCCESS)
							plat.log().debug(
									"xhr::onReadyStateChange[" + path + "]"
											+ "(readyState = " + readyState
											+ "; status = " + status + ")");
						result.succeed(xhr.getResponseText());
					}
				}
			}
		});
		if (LOG_XHR_SUCCESS)
			plat.log().debug("xhr.open('GET', '" + path + "')...");
		xhr.open("GET", path);
		if (LOG_XHR_SUCCESS)
			plat.log().debug("xhr.send()...");
		xhr.send();
	}

	private String getKey(String fullPath) {
		String key = fullPath.substring(fullPath.lastIndexOf('/') + 1);
		int dotCharIdx = key.indexOf('.');
		return dotCharIdx != -1 ? key.substring(0, dotCharIdx) : key;
	}

	private GWTImage adaptImage(String url, Scale scale) {
		GWTResourcesLoader files = 	Loon.self.resources.internal(pathPrefix + url);
		return new GWTImage(plat.graphics(), scale,
				files.preloader.images.get(files.path()), url);
	}

	private native void setCrossOrigin(Element elem, String state) /*-{
		if ('crossOrigin' in elem)
			elem.setAttribute('crossOrigin', state);
	}-*/;
}

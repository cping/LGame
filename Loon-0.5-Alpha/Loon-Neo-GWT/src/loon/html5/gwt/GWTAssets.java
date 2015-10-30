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
import loon.LSystem;
import loon.Sound;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.html5.gwt.preloader.Blob;
import loon.jni.XDomainRequest;
import loon.utils.ObjectMap;
import loon.utils.Scale;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Window;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class GWTAssets extends Assets {

	private final static String GWT_DEF_RES = "assets/";

	private final static boolean LOG_XHR_SUCCESS = false;

	private final GWTGame game;

	private Scale assetScale = null;

	public void setPathPrefix(String prefix) {
		if (!prefix.startsWith(GWT_DEF_RES)) {
			pathPrefix = prefix;
		}
	}

	public void setAssetScale(float scaleFactor) {
		this.assetScale = new Scale(scaleFactor);
	}

	@Override
	public Image getImageSync(String path) {
		for (Scale.ScaledResource rsrc : assetScale().getScaledResources(path)) {
			return localImage(pathPrefix + path, rsrc.scale);
		}
		return new GWTImage(game.graphics(), new Throwable(
				"Image missing from manifest: " + path));
	}

	@Override
	public Image getImage(String path) {
		Scale assetScale = (this.assetScale == null) ? Scale.ONE
				: this.assetScale;
		List<Scale.ScaledResource> rsrcs = assetScale.getScaledResources(path);
		return localImage(rsrcs.get(0).path, rsrcs.get(0).scale);
	}

	@Override
	public Image getRemoteImage(String path) {
		return localImage(path, Scale.ONE);
	}

	@Override
	public Image getRemoteImage(String path, int width, int height) {
		return localImage(path, Scale.ONE).preload(width, height);
	}

	@Override
	public Sound getSound(String path) {
		path = getPath(path);
		if (path.startsWith(LSystem.FRAMEWORK_IMG_NAME)) {
			path = GWT_DEF_RES + path;
		}
		GWTResourcesLoader gwtFile = Loon.self.resources.internal(path);
		return new GWTSound(gwtFile.path());
	}

	@Override
	public String getTextSync(String path) throws Exception {
		path = getPath(path);
		if (path.startsWith(LSystem.FRAMEWORK_IMG_NAME)) {
			path = GWT_DEF_RES + path;
		}
		GWTResourcesLoader gwtFile = Loon.self.resources.internal(path);
		if (gwtFile.preloader.isText(path)) {
			return gwtFile.readString();
		}
		ObjectMap<String, String> res = gwtFile.preloader.texts;
		String tmp = res.get(path = gwtFile.path());
		if (tmp == null
				&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(LSystem.getFileName(path = gwtFile.path()));
		}
		if (tmp == null) {
			tmp = res.get(LSystem.getFileName(path = (GWT_DEF_RES + path)));
		}
		if (tmp == null) {
			game.log().warn("file " + path + " not found");
		}
		return tmp;
	}

	@Override
	public GoFuture<String> getText(String path) {
		GoPromise<String> result = GoPromise.create();
		path = getPath(path);
		if (path.startsWith(LSystem.FRAMEWORK_IMG_NAME)) {
			path = GWT_DEF_RES + path;
		}
		try {
			doXhr(path, result);
		} catch (JavaScriptException e) {
			if (Window.Navigator.getUserAgent().indexOf("MSIE") != -1) {
				doXdr(path, result);
			} else {
				GWTResourcesLoader gwtFile = Loon.self.resources.internal(path);
				if (gwtFile.preloader.isText(path)) {
					try {
						result.succeed(gwtFile.readString());
					} catch (Exception ex) {
						result.succeed(null);
					}
				} else {
					ObjectMap<String, String> res = gwtFile.preloader.texts;
					String tmp = res.get(path = gwtFile.path());
					if (tmp == null
							&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
						tmp = res
								.get(LSystem.getFileName(path = gwtFile.path()));
					}
					if (tmp == null) {
						tmp = res.get(LSystem
								.getFileName(path = (GWT_DEF_RES + path)));
					}
					if (tmp == null) {
						game.log().warn("file " + path + " not found");
					}
					try {
						result.succeed(tmp);
					} catch (Exception ex) {
						result.succeed(null);
					}
				}
			}
		}
		return result;
	}

	@Override
	public byte[] getBytesSync(String path) throws Exception {
		path = getPath(path);
		if (path.startsWith(LSystem.FRAMEWORK_IMG_NAME)) {
			path = GWT_DEF_RES + path;
		}
		GWTResourcesLoader gwtFile = Loon.self.resources.internal(path);
		if (gwtFile.preloader.isBinary(path)) {
			return gwtFile.readBytes();
		}
		ObjectMap<String, Blob> res = gwtFile.preloader.binaries;
		Blob tmp = res.get(path = gwtFile.path());
		if (tmp == null
				&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(LSystem.getFileName(path = gwtFile.path()));
		}
		if (tmp == null) {
			tmp = res.get(LSystem.getFileName(path = (GWT_DEF_RES + path)));
		}
		if (tmp == null) {
			game.log().warn("file " + path + " not found");
		}
		return Loon.self.resources.internal(path).readBytes();
	}

	@Override
	protected ImageImpl.Data load(String path) throws Exception {
		path = getPath(path);
		if (path.startsWith(LSystem.FRAMEWORK_IMG_NAME)) {
			path = GWT_DEF_RES + path;
		}
		Exception error = null;
		for (Scale.ScaledResource rsrc : assetScale().getScaledResources(path)) {
			try {
				ImageElement image = localImageElement(path);
				Scale viewScale = game.graphics().scale(), imageScale = rsrc.scale;
				float viewImageRatio = viewScale.factor / imageScale.factor;
				if (viewImageRatio < 1f) {
					ImageData data = GWTImage.scaleImage(image, viewImageRatio);
					ImageElement img = Document.get().createImageElement();
					img.setWidth(data.getWidth());
					img.setHeight(data.getHeight());
					image = img;
					imageScale = viewScale;
				}
				return new ImageImpl.Data(imageScale, image, image.getWidth(),
						image.getHeight());
			} catch (Exception fnfe) {
				error = fnfe;
			}
		}
		game.log().warn(
				"Could not load image: " + path + " [error=" + error + "]");
		throw error != null ? error : new Exception(path);
	}

	@Override
	protected ImageImpl createImage(boolean async, int rwid, int rhei,
			String source) {
		ImageElement img = Document.get().createImageElement();
		setCrossOrigin(img, "anonymous");
		img.setSrc(source);
		return new GWTImage(game.graphics(), game.graphics().scale(), img,
				source);
	}

	GWTAssets(GWTGame game) {
		super(game.asyn());
		this.game = game;
		GWTAssets.pathPrefix = "";
	}

	private Scale assetScale() {
		return (assetScale != null) ? assetScale : game.graphics().scale();
	}

	private void doXdr(final String path, final GoPromise<String> result) {
		XDomainRequest xdr = XDomainRequest.create();
		xdr.setHandler(new XDomainRequest.Handler() {
			@Override
			public void onTimeout(XDomainRequest xdr) {
				game.log().error("xdr::onTimeout[" + path + "]()");
				result.fail(new Exception("Error getting " + path + " : "
						+ xdr.getStatus()));
			}

			@Override
			public void onProgress(XDomainRequest xdr) {
				if (LOG_XHR_SUCCESS)
					game.log().debug("xdr::onProgress[" + path + "]()");
			}

			@Override
			public void onLoad(XDomainRequest xdr) {
				if (LOG_XHR_SUCCESS)
					game.log().debug("xdr::onLoad[" + path + "]()");
				result.succeed(xdr.getResponseText());
			}

			@Override
			public void onError(XDomainRequest xdr) {
				game.log().error("xdr::onError[" + path + "]()");
				result.fail(new Exception("Error getting " + path + " : "
						+ xdr.getStatus()));
			}
		});
		if (LOG_XHR_SUCCESS) {
			game.log().debug("xdr.open('GET', '" + path + "')...");
		}
		xdr.open("GET", path);
		if (LOG_XHR_SUCCESS) {
			game.log().debug("xdr.send()...");
		}
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
						game.log().error(
								"xhr::onReadyStateChange[" + path + "]"
										+ "(readyState = " + readyState
										+ "; status = " + status + ")");
						result.fail(new Exception("Error getting " + path
								+ " : " + xhr.getStatusText()));
					} else {
						if (LOG_XHR_SUCCESS)
							game.log().debug(
									"xhr::onReadyStateChange[" + path + "]"
											+ "(readyState = " + readyState
											+ "; status = " + status + ")");
						result.succeed(xhr.getResponseText());
					}
				}
			}
		});
		if (LOG_XHR_SUCCESS) {
			game.log().debug("xhr.open('GET', '" + path + "')...");
		}
		xhr.open("GET", path);
		if (LOG_XHR_SUCCESS) {
			game.log().debug("xhr.send()...");
		}
		xhr.send();
	}

	private GWTImage localImage(String path, Scale scale) {
		path = getPath(path);
		if (path.startsWith(LSystem.FRAMEWORK_IMG_NAME)) {
			path = GWT_DEF_RES + path;
		}
		GWTResourcesLoader files = Loon.self.resources.internal(path);
		if (files.preloader.isImage(path)) {
			return new GWTImage(game.graphics(), scale,
					files.preloader.images.get(path), path);
		}
		ObjectMap<String, ImageElement> res = files.preloader.images;
		ImageElement tmp = res.get(path = files.path());
		if (tmp == null
				&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(LSystem.getFileName(path = files.path()));
		}
		if (tmp == null) {
			tmp = res.get(LSystem.getFileName(path = (GWT_DEF_RES + path)));
		}
		if (tmp == null) {
			game.log().warn("file " + path + " not found");
			return new GWTImage(game.graphics(), scale, createEmptyCanvas(50,
					50), path);
		}
		return new GWTImage(game.graphics(), scale, tmp, path);
	}

	private ImageElement localImageElement(String path) {
		path = getPath(path);
		if (path.startsWith(LSystem.FRAMEWORK_IMG_NAME)) {
			path = GWT_DEF_RES + path;
		}
		GWTResourcesLoader files = Loon.self.resources.internal(path);
		if (files.preloader.isImage(path)) {
			return files.preloader.images.get(path);
		}
		ObjectMap<String, ImageElement> res = files.preloader.images;
		ImageElement tmp = res.get(path = files.path());
		if (tmp == null
				&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(LSystem.getFileName(path = files.path()));
		}
		if (tmp == null) {
			tmp = res.get(LSystem.getFileName(path = (GWT_DEF_RES + path)));
		}
		if (tmp == null) {
			game.log().warn("file " + path + " not found");
		}
		return tmp;
	}

	private CanvasElement createEmptyCanvas(int w, int h) {
		CanvasElement elem = Document.get().createCanvasElement();
		elem.setWidth(w);
		elem.setHeight(h);
		setCrossOrigin(elem, "anonymous");
		Context2d context = elem.getContext2d();
		context.setFillStyle("rgba(255,255,255,255)");
		context.fillRect(0, 0, w, h);
		return elem;
	}

	private native void setCrossOrigin(Element elem, String state) /*-{
		if ('crossOrigin' in elem)
			elem.setAttribute('crossOrigin', state);
	}-*/;
}

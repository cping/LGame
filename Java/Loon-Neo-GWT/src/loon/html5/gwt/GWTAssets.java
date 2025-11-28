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

import java.nio.ByteBuffer;
import java.util.HashMap;

import loon.Assets;
import loon.LSystem;
import loon.Sound;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.html5.gwt.preloader.Blob;
import loon.html5.gwt.preloader.PreloaderBundle;
import loon.jni.TypedArrayHelper;
import loon.jni.XDomainRequest;
import loon.jni.XDomainRequest.Handler;
import loon.opengl.TextureSource;
import loon.utils.ObjectMap;
import loon.utils.PathUtils;
import loon.utils.Scale;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.reply.Function;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.typedarrays.shared.Int8Array;
import com.google.gwt.typedarrays.shared.TypedArrays;
import com.google.gwt.user.client.Window;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.google.gwt.xhr.client.XMLHttpRequest.ResponseType;

public class GWTAssets extends Assets {

	public interface ImageManifest {
		int[] imageSize(String path);
	}

	public void setImageManifest(ImageManifest manifest) {
		imageManifest = manifest;
	}

	private final static boolean LOG_XHR_SUCCESS = false;

	private final GWTGame game;

	private ImageManifest imageManifest;

	private final HashMap<String, PreloaderBundle> clientBundles = new HashMap<String, PreloaderBundle>();

	private Scale assetScale = null;

	public void addClientBundle(String regExp, PreloaderBundle clientBundle) {
		clientBundles.put(regExp, clientBundle);
	}

	public void setAssetScale(float scaleFactor) {
		this.assetScale = new Scale(scaleFactor);
	}

	protected String getURLPath(String fileName) {
		return "url('" + StringUtils.replace(getPathPrefix(), "\\", "/") + fileName + "')";
	}

	@Override
	public Image getImageSync(String path) {
		if (game.gwtconfig != null && game.gwtconfig.asynResource) {
			return getBundleImageSync(path);
		}
		for (Scale.ScaledResource rsrc : assetScale().getScaledResources(path)) {
			return localImage(getFixPath(path), rsrc.scale);
		}
		return new GWTImage(game.graphics(), new Throwable("Image missing from manifest: " + path));
	}

	@Override
	public Image getImage(String path) {
		if (game.gwtconfig != null && game.gwtconfig.asynResource) {
			return getBundleImage(path);
		}
		Scale assetScale = (this.assetScale == null) ? Scale.ONE : this.assetScale;
		TArray<Scale.ScaledResource> rsrcs = assetScale.getScaledResources(path);
		return localImage(rsrcs.get(0).path, rsrcs.get(0).scale);
	}

	@Override
	public Image getRemoteImage(String path) {
		if (game.gwtconfig != null && game.gwtconfig.asynResource) {
			return addBundleImage(path, Scale.ONE);
		}
		return localImage(path, Scale.ONE);
	}

	@Override
	public Image getRemoteImage(String path, int width, int height) {
		if (game.gwtconfig != null && game.gwtconfig.asynResource) {
			return addBundleImage(path, Scale.ONE).preload(width, height);
		}
		return localImage(path, Scale.ONE).preload(width, height);
	}

	@Override
	public Sound getSound(String path) {
		if (game.gwtconfig != null && game.gwtconfig.asynResource) {
			String url = getFixPath(path);
			PreloaderBundle clientBundle = getBundle(path);
			if (clientBundle != null) {
				String key = toKey(path);
				DataResource resource = (DataResource) getResource(key, clientBundle);
				if (resource != null) {
					url = resource.getSafeUri().asString();
				}
			} else {
				url += ".mp3";
			}
			return new GWTSound(url);
		}
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		GWTResourcesLoader gwtFile = Loon.self.resources.internal(path);
		return new GWTSound(gwtFile.path());
	}

	@Override
	public String getTextSync(String path) throws Exception {
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		GWTResourcesLoader gwtFile = Loon.self.resources.internal(path);
		if (gwtFile.preloader.isText(path)) {
			return gwtFile.readString();
		}
		ObjectMap<String, String> res = gwtFile.preloader.texts;
		String tmp = res.get(path = gwtFile.path());
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(path.substring(path.indexOf('/') + 1, path.length()));
		}
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(LSystem.getFileName(path = gwtFile.path()));
		}
		if (tmp == null) {
			tmp = res.get(LSystem.getFileName(path = (getFixPath(path))));
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
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		try {
			return doXhr(path, XMLHttpRequest.ResponseType.Default).map(new Function<XMLHttpRequest, String>() {
				public String apply(XMLHttpRequest xhr) {
					return xhr.getResponseText();
				}
			});
		} catch (JavaScriptException e) {
			if (Window.Navigator.getUserAgent().indexOf("MSIE") != -1) {
				return doXdr(path).map(new Function<XDomainRequest, String>() {
					public String apply(XDomainRequest xdr) {
						return xdr.getResponseText();
					}
				});
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
					if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
						tmp = res.get(path.substring(path.indexOf('/') + 1, path.length()));
					}
					if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
						tmp = res.get(LSystem.getFileName(path = gwtFile.path()));
					}
					if (tmp == null) {
						tmp = res.get(LSystem.getFileName(path = (getFixPath(path))));
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

	private final static String getFixPath(String path) {
		return PathUtils.normalizeCombinePaths(LSystem.getPathPrefix(), path);
	}

	@Override
	public GoFuture<byte[]> getBytes(final String path) {
		String fullpath = getPath(path);
		if (fullpath.startsWith(LSystem.getSystemImagePath())) {
			fullpath = getFixPath(path);
		}
		if (!TypedArrays.isSupported()) {
			final GoPromise<byte[]> result = GoPromise.create();
			try {
				result.succeed(getBytesSync(path));
			} catch (Exception ex) {
				result.fail(new UnsupportedOperationException("TypedArrays not supported by this browser."));
			}
			return result;
		}
		try {
			return doXhr(fullpath, XMLHttpRequest.ResponseType.ArrayBuffer).map(new Function<XMLHttpRequest, byte[]>() {
				public byte[] apply(XMLHttpRequest xhr) {
					ByteBuffer buffer = TypedArrayHelper.wrap(xhr.getResponseArrayBuffer());
					byte[] arr = new byte[buffer.remaining()];
					buffer.get(arr);
					buffer.position(0);
					return arr;
				}
			});
		} catch (Exception ex) {
			final GoPromise<byte[]> result = GoPromise.create();
			try {
				result.succeed(getBytesSync(path));
			} catch (Exception exc) {
				return null;
			}
			return result;
		}
	}

	@Override
	public byte[] getBytesSync(String path) throws Exception {
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		GWTResourcesLoader gwtFile = Loon.self.resources.internal(path);
		if (gwtFile.preloader.isBinary(path)) {
			return gwtFile.readBytes();
		}
		ObjectMap<String, Blob> res = gwtFile.preloader.binaries;
		Blob tmp = res.get(path = gwtFile.path());
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(path.substring(path.indexOf('/') + 1, path.length()));
		}
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(LSystem.getFileName(path = gwtFile.path()));
		}
		if (tmp == null) {
			tmp = res.get(LSystem.getFileName(path = (getFixPath(path))));
		}
		if (tmp == null) {
			game.log().warn("file " + path + " not found");
		}
		return Loon.self.resources.internal(path).readBytes();
	}

	@Override
	protected ImageImpl.Data load(String path) throws Exception {
		if (path == null || TextureSource.RenderCanvas.equals(path)) {
			return null;
		}
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
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
				return new ImageImpl.Data(imageScale, image, image.getWidth(), image.getHeight());
			} catch (Exception fnfe) {
				error = fnfe;
			}
		}
		game.log().warn("Could not load image: " + path + " [error=" + error + "]");
		throw error != null ? error : new Exception(path);
	}

	@Override
	protected ImageImpl createImage(boolean async, int rwid, int rhei, String source) {
		ImageElement img = Document.get().createImageElement();
		img.setSrc(source);
		return new GWTImage(game.graphics(), game.graphics().scale(), img, source);
	}

	GWTAssets(GWTGame game) {
		super(game.asyn());
		this.game = game;
		if (game.gwtconfig != null && game.gwtconfig.asynResource) {
			setPathPrefix(PathUtils.normalizeCombinePaths(GWT.getModuleBaseForStaticFiles(), LSystem.getPathPrefix()));
		}
	}

	private Scale assetScale() {
		return (assetScale != null) ? assetScale : game.graphics().scale();
	}

	private GoFuture<XDomainRequest> doXdr(final String path) {
		final GoPromise<XDomainRequest> result = GoPromise.create();
		XDomainRequest xdr = XDomainRequest.create();
		xdr.setHandler(new Handler() {
			@Override
			public void onTimeout(XDomainRequest xdr) {
				game.log().error("xdr::onTimeout[" + path + "]()");
				result.fail(new Exception("Error getting " + path + " : " + xdr.getStatus()));
			}

			@Override
			public void onProgress(XDomainRequest xdr) {
				if (LOG_XHR_SUCCESS) {
					game.log().debug("xdr::onProgress[" + path + "]()");
				}
			}

			@Override
			public void onLoad(XDomainRequest xdr) {
				if (LOG_XHR_SUCCESS) {
					game.log().debug("xdr::onLoad[" + path + "]()");
				}
				result.succeed(xdr);
			}

			@Override
			public void onError(XDomainRequest xdr) {
				game.log().error("xdr::onError[" + path + "]()");
				result.fail(new Exception("Error getting " + path + " : " + xdr.getStatus()));
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
		return result;
	}

	private GoFuture<XMLHttpRequest> doXhr(final String path, final XMLHttpRequest.ResponseType responseType) {
		final GoPromise<XMLHttpRequest> result = GoPromise.create();
		XMLHttpRequest xhr = XMLHttpRequest.create();
		if (LOG_XHR_SUCCESS) {
			game.log().debug("xhr.open('GET', '" + path + "')...");
		}
		xhr.open("GET", path);
		xhr.setResponseType(responseType);
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int readyState = xhr.getReadyState();
				if (readyState == XMLHttpRequest.DONE) {
					int status = xhr.getStatus();
					if (status != 0 && (status < 200 || status >= 400)) {
						game.log().error("xhr::onReadyStateChange[" + path + "]" + "(readyState = " + readyState
								+ "; status = " + status + ")");
						result.fail(new Exception("Error getting " + path + " : " + xhr.getStatusText()));
					} else {
						if (LOG_XHR_SUCCESS)
							game.log().debug("xhr::onReadyStateChange[" + path + "]" + "(readyState = " + readyState
									+ "; status = " + status + ")");
						result.succeed(xhr);
					}
				}
			}
		});
		if (LOG_XHR_SUCCESS) {
			game.log().debug("xhr.send()...");
		}
		xhr.send();
		return result;
	}

	private GWTImage localImage(String path, Scale scale) {
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		GWTResourcesLoader files = Loon.self.resources.internal(path);
		if (files.preloader.isImage(path)) {
			return new GWTImage(game.graphics(), scale, files.preloader.images.get(path), path);
		}
		ObjectMap<String, ImageElement> res = files.preloader.images;
		ImageElement tmp = res.get(path = files.path());
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(path.substring(path.indexOf('/') + 1, path.length()));
		}
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(LSystem.getFileName(path = files.path()));
		}
		if (tmp == null) {
			tmp = res.get(LSystem.getFileName(path = (getFixPath(path))));
		}
		if (tmp == null) {
			return getBundleImage(PathUtils.getCombinePaths(GWT.getModuleBaseForStaticFiles(), path), scale);
		}
		return new GWTImage(game.graphics(), scale, tmp, path);
	}

	private GWTImage loadUrlImage(String url, Scale scale) {
		final ImageElement image = Document.get().createImageElement();
		final GWTImage gwtimage = new GWTImage(game.graphics(), scale, image, url);
		final XMLHttpRequest request = XMLHttpRequest.create();
		request.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				if (xhr.getReadyState() == XMLHttpRequest.DONE) {
					if (xhr.getStatus() == 200) {
						Int8Array data = TypedArrays.createInt8Array(xhr.getResponseArrayBuffer());
						Blob blob = new Blob(data);
						GWTScriptLoader.setCrossOrigin(image, "crossOrigin");
						final String ext = PathUtils.getExtension(url);
						if (StringUtils.isEmpty(ext)) {
							image.setSrc(blob.toBase64("image/png"));
						} else {
							image.setSrc(blob.toBase64("image/" + ext));
						}
						gwtimage.setImageElement(image);
					}
				}
			}
		});
		request.open("GET", url);
		request.setResponseType(ResponseType.ArrayBuffer);
		request.send();
		return gwtimage;
	}

	private ImageElement localImageElement(String path) {
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		GWTResourcesLoader files = Loon.self.resources.internal(path);
		if (files.preloader.isImage(path)) {
			return files.preloader.images.get(path);
		}
		ObjectMap<String, ImageElement> res = files.preloader.images;
		ImageElement tmp = res.get(path = files.path());
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(path.substring(path.indexOf('/') + 1, path.length()));
		}
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = res.get(LSystem.getFileName(path = files.path()));
		}
		if (tmp == null) {
			tmp = res.get(LSystem.getFileName(path = (getFixPath(path))));
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
		Context2d context = elem.getContext2d();
		context.setFillStyle("rgba(255,255,255,255)");
		context.fillRect(0, 0, w, h);
		return elem;
	}

	private native void setCrossOrigin(Element elem, String state) /*-{
		if ('crossOrigin' in elem)
			elem.setAttribute('crossOrigin', state);
	}-*/;

	private String toKey(String fullPath) {
		String key = fullPath.substring(fullPath.lastIndexOf('/') + 1);
		int dotCharIdx = key.indexOf('.');
		return dotCharIdx != -1 ? key.substring(0, dotCharIdx) : key;
	}

	private ResourcePrototype getResource(String key, PreloaderBundle clientBundle) {
		ResourcePrototype resource = clientBundle.getResource(key);
		return resource;
	}

	private PreloaderBundle getBundle(String collection) {
		PreloaderBundle clientBundle = null;
		for (HashMap.Entry<String, PreloaderBundle> entry : clientBundles.entrySet()) {
			String regExp = entry.getKey();
			if (RegExp.compile(regExp).exec(collection) != null) {
				clientBundle = entry.getValue();
			}
		}
		return clientBundle;
	}

	protected GWTImage getBundleImage(String path, Scale scale) {
		String url = getFixPath(path);
		PreloaderBundle clientBundle = getBundle(url);
		if (clientBundle == null) {
			clientBundle = getBundle(path);
		}
		if (clientBundle != null) {
			String key = toKey(url);
			ImageResource resource = (ImageResource) getResource(key, clientBundle);
			if (key == null) {
				key = toKey(path);
				resource = (ImageResource) getResource(key, clientBundle);
			}
			if (resource != null) {
				url = resource.getSafeUri().asString();
			}
			return addBundleImage(url, scale);
		} else {
			return loadUrlImage(url, scale);
		}
	}

	private GWTImage addBundleImage(String url, Scale scale) {
		ImageElement img = Document.get().createImageElement();
		setCrossOrigin(img, "anonymous");
		img.setSrc(url);
		return new GWTImage(game.graphics(), scale, img, url);
	}

	public Image getBundleImage(String path) {
		Scale assetScale = (this.assetScale == null) ? Scale.ONE : this.assetScale;
		TArray<Scale.ScaledResource> rsrcs = assetScale.getScaledResources(path);
		return getBundleImage(rsrcs.get(0).path, rsrcs.get(0).scale);
	}

	public Image getBundleImageSync(String path) {
		if (imageManifest == null) {
			throw new UnsupportedOperationException("getImageSync(" + path + ")");
		} else {
			for (Scale.ScaledResource rsrc : assetScale().getScaledResources(path)) {
				int[] size = imageManifest.imageSize(rsrc.path);
				if (size == null)
					continue;
				return getBundleImage(rsrc.path, rsrc.scale).preload(size[0], size[1]);
			}
			return new GWTImage(game.graphics(), new Throwable("Image missing from manifest: " + path));
		}
	}

}

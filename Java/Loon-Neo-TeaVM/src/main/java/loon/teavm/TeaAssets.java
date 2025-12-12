/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.teavm;

import org.teavm.jso.JSObject;
import org.teavm.jso.ajax.ReadyStateChangeHandler;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.canvas.ImageData;
import org.teavm.jso.core.JSString;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

import loon.Assets;
import loon.Asyn;
import loon.LSystem;
import loon.Sound;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.canvas.ImageImpl.Data;
import loon.opengl.TextureSource;
import loon.teavm.assets.AssetData;
import loon.teavm.assets.AssetPreloader;
import loon.teavm.audio.HowlEmptySound;
import loon.teavm.dom.ConvertUtils;
import loon.utils.CollectionUtils;
import loon.utils.PathUtils;
import loon.utils.Scale;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.reply.Function;
import loon.utils.reply.GoFuture;
import loon.utils.reply.GoPromise;

public class TeaAssets extends Assets {

	private final boolean LOG_XHR_SUCCESS;

	private TeaGame _game = null;

	private Scale _assetScale = null;

	protected TeaAssets(TeaGame g, Asyn s) {
		super(g.asyn());
		this._game = g;
		LOG_XHR_SUCCESS = (this._game.setting != null) ? this._game.setting.isDebug : false;
	}

	protected String getURLPath(String fileName) {
		return "url('" + StringUtils.replace(getPathPrefix(), "\\", "/") + fileName + "')";
	}

	@Override
	public Image getImageSync(String path) {
		for (Scale.ScaledResource rsrc : assetScale().getScaledResources(path)) {
			return localImage(getFixPath(path), rsrc.scale);
		}
		return new TeaImage(_game.graphics(), new Throwable("Image missing from manifest: " + path));
	}

	@Override
	public Image getImage(String path) {
		Scale assetScale = (this._assetScale == null) ? Scale.ONE : this._assetScale;
		TArray<Scale.ScaledResource> rsrcs = assetScale.getScaledResources(path);
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

	private TeaImage localImage(String path, Scale scale) {
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		return new TeaImage(_game.graphics(), scale, getAssetData(path).getImageElement(), path);
	}

	private TeaImage loadUrlImage(String url, Scale scale) {
		final HTMLImageElement image = TeaCanvasUtils.createImage();
		final TeaImage gwtimage = new TeaImage(_game.graphics(), scale, image, url);
		final XMLHttpRequest request = new XMLHttpRequest();
		request.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void stateChanged() {
				if (request.getReadyState() == XMLHttpRequest.DONE) {
					if (request.getStatus() == 200) {
						JSObject jsResponse = request.getResponse();
						Int8Array data = null;
						ArrayBuffer arrayBuffer = null;
						if (JSString.isInstance(jsResponse)) {
							String responseStr = Loon.toString(jsResponse);
							data = ConvertUtils.getInt8Array(responseStr.getBytes());
							arrayBuffer = data.getBuffer();
						} else {
							ArrayBuffer response = (ArrayBuffer) jsResponse;
							data = new Int8Array(response);
							arrayBuffer = response;
						}
						final TeaBlob blob = new TeaBlob(arrayBuffer, data);
						Loon.setCrossOrigin(image, "crossOrigin");
						String ext = PathUtils.getExtension(url);
						if ("jpg".equals(ext)) {
							ext = "jpeg";
						}
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
		request.setResponseType("arraybuffer");
		request.send();
		return null;
	}

	@Override
	public Sound getSound(String path) {
		TeaAudio audio = Loon.self.getAudio();
		if (audio != null) {
			final AssetPreloader assets = Loon.self.getPreloader();
			TeaResourceLoader gwtFile = assets.internal(path);
			if (gwtFile.exists()) {
				return audio.newSound(gwtFile);
			}
			return audio.newSound(getAssetData(path));
		}
		return new HowlEmptySound();
	}

	private final AssetData getAssetData(String path) {
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		final AssetPreloader assets = Loon.self.getPreloader();
		TeaResourceLoader gwtFile = assets.internal(path);
		if (gwtFile.exists()) {
			return assets.getInternal(path = gwtFile.path());
		}
		AssetData tmp = assets.getInternal(path = gwtFile.path());
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = assets.getInternal(path.substring(path.indexOf('/') + 1, path.length()));
			if (tmp == null) {
				tmp = assets.getInternal(path.substring(path.indexOf('\\') + 1, path.length()));
			}
		}
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = assets.getInternal(LSystem.getFileName(path = gwtFile.path()));
		}
		if (tmp == null) {
			tmp = assets.getInternal(LSystem.getFileName(path = (getFixPath(path))));
		}
		if (tmp == null) {
			tmp = assets.getAllInternal(path);
		}
		if (tmp == null) {
			_game.log().warn("file " + path + " not found");
			tmp = new AssetData(path);
		} else {
			return tmp;
		}
		return tmp;
	}

	@Override
	public String getTextSync(String path) throws Exception {
		return new String(getAssetData(path).getBytes(), LSystem.ENCODING);
	}

	@Override
	public GoFuture<String> getText(String path) {
		GoPromise<String> result = GoPromise.create();
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		try {
			return doXhr(path, "text").map(new Function<XMLHttpRequest, String>() {
				public String apply(XMLHttpRequest xhr) {
					return xhr.getResponseText();
				}
			});
		} catch (Exception e) {
			final AssetPreloader assets = Loon.self.getPreloader();
			TeaResourceLoader gwtFile = assets.internal(path);
			if (gwtFile.exists()) {
				try {
					result.succeed(gwtFile.readString());
				} catch (Exception ex) {
					result.succeed(null);
				}
			} else {
				try {
					result.succeed(new String(getAssetData(path).getBytes(), LSystem.ENCODING));
				} catch (Exception ex) {
					result.succeed(null);
				}
			}
		}
		return result;
	}

	@Override
	public byte[] getBytesSync(String path) throws Exception {
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		return CollectionUtils.copyOf(getAssetData(path).getBytes());
	}

	private Scale assetScale() {
		return (_assetScale != null) ? _assetScale : _game.graphics().scale();
	}

	@Override
	protected Data load(String path) throws Exception {
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
				HTMLImageElement image = localImageElement(path);
				Scale viewScale = _game.graphics().scale(), imageScale = rsrc.scale;
				float viewImageRatio = viewScale.factor / imageScale.factor;
				if (viewImageRatio < 1f) {
					ImageData data = TeaImage.scaleImage(image, viewImageRatio);
					HTMLImageElement img = TeaCanvasUtils.createImage();
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
		_game.log().warn("Could not load image: " + path + " [error=" + error + "]");
		throw error != null ? error : new Exception(path);
	}

	private GoFuture<XMLHttpRequest> doXhr(final String path, final String responseType) {
		final GoPromise<XMLHttpRequest> result = GoPromise.create();
		XMLHttpRequest xhr = new XMLHttpRequest();
		if (LOG_XHR_SUCCESS) {
			_game.log().debug("xhr.open('GET', '" + path + "')...");
		}
		xhr.open("GET", path);
		xhr.setResponseType(responseType);

		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void stateChanged() {
				int readyState = xhr.getReadyState();
				if (readyState == XMLHttpRequest.DONE) {
					int status = xhr.getStatus();
					if (status != 0 && (status < 200 || status >= 400)) {
						_game.log().error("xhr::onReadyStateChange[" + path + "]" + "(readyState = " + readyState
								+ "; status = " + status + ")");
						result.fail(new Exception("Error getting " + path + " : " + xhr.getStatusText()));
					} else {
						if (LOG_XHR_SUCCESS)
							_game.log().debug("xhr::onReadyStateChange[" + path + "]" + "(readyState = " + readyState
									+ "; status = " + status + ")");
						result.succeed(xhr);
					}
				}
			}
		});
		if (LOG_XHR_SUCCESS) {
			_game.log().debug("xhr.send()...");
		}
		xhr.send();
		return result;
	}

	private HTMLImageElement localImageElement(String path) {
		return getAssetData(path).getImageElement();
	}

	@Override
	protected ImageImpl createImage(boolean async, int rawWidth, int rawHeight, String source) {
		HTMLImageElement img = null;
		if (async) {
			img = TeaCanvasUtils.createImage();
			img.setWidth(rawWidth);
			img.setHeight(rawHeight);
			img.setSrc(source);
		} else {
			img = localImageElement(source);
			if (img == null) {
				return loadUrlImage(source, _game.graphics().scale());
			}
		}
		return new TeaImage(_game.graphics(), _game.graphics().scale(), img, source);
	}

	private final static String getFixPath(String path) {
		return PathUtils.normalizeCombinePaths(LSystem.getPathPrefix(), path);
	}

	protected HTMLCanvasElement createEmptyCanvas(int w, int h) {
		HTMLCanvasElement canvasTmp = TeaCanvasUtils.createCanvas();
		canvasTmp.setWidth(w);
		canvasTmp.setHeight(h);
		CanvasRenderingContext2D context = TeaCanvasUtils.getContext2d(canvasTmp);
		context.setFillStyle("rgba(255,255,255,255)");
		context.fillRect(0, 0, w, h);
		return canvasTmp;
	}
}

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

import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.canvas.ImageData;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.dom.xml.Document;

import loon.Assets;
import loon.Asyn;
import loon.LSystem;
import loon.Sound;
import loon.canvas.ImageImpl;
import loon.canvas.ImageImpl.Data;
import loon.opengl.TextureSource;
import loon.teavm.TeaGame.TeaSetting;
import loon.teavm.assets.AssetData;
import loon.teavm.assets.AssetPreloader;
import loon.teavm.audio.HowlMusic;
import loon.utils.Base64Coder;
import loon.utils.CollectionUtils;
import loon.utils.ObjectMap;
import loon.utils.PathUtils;
import loon.utils.Scale;
import loon.utils.StringUtils;
import loon.utils.html.HtmlImage;
import loon.utils.res.loaders.PreloadAssets;
import loon.utils.res.loaders.PreloadLoader;

public class TeaAssets extends Assets {

	public interface ImageManifest {
		int[] imageSize(String path);
	}

	public void setImageManifest(ImageManifest manifest) {
		_imageManifest = manifest;
	}

	private TeaGame _game;

	private TeaSetting _setting;

	private ImageManifest _imageManifest;

	private Scale _assetScale = null;

	protected TeaAssets(TeaGame g, Asyn s) {
		super(g.asyn());
		this._game = g;
		setPathPrefixEmpty();
	}

	protected String getURLPath(String fileName) {
		return "url('" + StringUtils.replace(getPathPrefix(), "\\", "/") + fileName + "')";
	}

	@Override
	public Sound getSound(String path) {
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		final AssetPreloader assets = Loon.self.getPreloader();
		TeaResourceLoader gwtFile = assets.internal(path);
		if (gwtFile.exists()) {
			return new HowlMusic(gwtFile);
		}
		boolean result = assets.contains(path = gwtFile.path());
		String finalPath = path;
		if (!result && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			result = assets.contains(finalPath = path.substring(path.indexOf('/') + 1, path.length()));
		}
		if (!result && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			result = assets.contains(finalPath = LSystem.getFileName(path = gwtFile.path()));
		}
		if (!result) {
			result = assets.contains(finalPath = LSystem.getFileName(path = (getFixPath(path))));
		}
		if (!result) {
			_game.log().warn("file " + path + " not found");
		}
		return new HowlMusic(assets.internal(finalPath));
	}

	@Override
	public String getTextSync(String path) throws Exception {
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		final AssetPreloader assets = Loon.self.getPreloader();
		TeaResourceLoader gwtFile = assets.internal(path);
		if (gwtFile.exists()) {
			return gwtFile.readString();
		}
		AssetData tmp = assets.getInternal(path = gwtFile.path());
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = assets.getInternal(path.substring(path.indexOf('/') + 1, path.length()));
		}
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = assets.getInternal(LSystem.getFileName(path = gwtFile.path()));
		}
		if (tmp == null) {
			tmp = assets.getInternal(LSystem.getFileName(path = (getFixPath(path))));
		}
		if (tmp == null) {
			_game.log().warn("file " + path + " not found");
		}
		return new String(tmp.getBytes(), LSystem.ENCODING);
	}

	@Override
	public byte[] getBytesSync(String path) throws Exception {
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		final AssetPreloader assets = Loon.self.getPreloader();
		TeaResourceLoader gwtFile = assets.internal(path);
		if (gwtFile.exists()) {
			return gwtFile.readBytes();
		}
		AssetData tmp = assets.getInternal(path = gwtFile.path());
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = assets.getInternal(path.substring(path.indexOf('/') + 1, path.length()));
		}
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = assets.getInternal(LSystem.getFileName(path = gwtFile.path()));
		}
		if (tmp == null) {
			tmp = assets.getInternal(LSystem.getFileName(path = (getFixPath(path))));
		}
		if (tmp == null) {
			_game.log().warn("file " + path + " not found");
		}
		return CollectionUtils.copyOf(tmp.getBytes());
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
					HTMLImageElement img = (HTMLImageElement) HTMLDocument.current().createElement(_setting.imageName);
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

	private HTMLImageElement localImageElement(String path) {
		path = getPath(path);
		if (path.startsWith(LSystem.getSystemImagePath())) {
			path = getFixPath(path);
		}
		final AssetPreloader assets = Loon.self.getPreloader();
		TeaResourceLoader files = assets.internal(path);
		AssetData tmp = assets.getInternal(path = files.path());
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = assets.getInternal(path.substring(path.indexOf('/') + 1, path.length()));
		}
		if (tmp == null && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
			tmp = assets.getInternal(LSystem.getFileName(path = files.path()));
		}
		if (tmp == null) {
			tmp = assets.getInternal(LSystem.getFileName(path = (getFixPath(path))));
		}
		if (tmp == null) {
			_game.log().warn("file " + path + " not found");
		} else {
			return createImage("image/" + PathUtils.getExtension(path), tmp.getBytes());
		}
		return null;
	}

	@Override
	protected ImageImpl createImage(boolean async, int rawWidth, int rawHeight, String source) {
		HTMLImageElement img = (HTMLImageElement) HTMLDocument.current().createElement(_setting.imageName);
		img.setSrc(source);
		return new TeaImage(_game.graphics(), _game.graphics().scale(), img, source);
	}

	private final static String getFixPath(String path) {
		return PathUtils.normalizeCombinePaths(LSystem.getPathPrefix(), path);
	}

	private HTMLImageElement createImage(String mimeType, byte[] bytes) {
		HTMLImageElement imageTmp = (HTMLImageElement) HTMLDocument.current().createElement(_setting.imageName);
		imageTmp.setSrc("data:" + mimeType + ";base64," + Base64Coder.encode(bytes));
		return imageTmp;
	}

	private HTMLCanvasElement createEmptyCanvas(int w, int h) {
		HTMLCanvasElement canvasTmp = (HTMLCanvasElement) HTMLDocument.current().createElement(_setting.canvasName);
		canvasTmp.setWidth(w);
		canvasTmp.setHeight(h);
		CanvasRenderingContext2D context = (CanvasRenderingContext2D) canvasTmp.getContext(_setting.canvasMethod);
		context.setFillStyle("rgba(255,255,255,255)");
		context.fillRect(0, 0, w, h);
		return canvasTmp;
	}
}

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
package loon.robovm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import loon.Assets;
import loon.LSystem;
import loon.Sound;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.utils.Scale;
import loon.utils.reply.Port;

import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.uikit.UIImage;

public class RoboVMAssets extends Assets {

	private final static String IOS_DEF_RES = "assets/";
	private RoboVMNet net;
	private final RoboVMGame game;
	private final File bundleRoot = new File(NSBundle.getMainBundle()
			.getBundlePath());
	private File assetRoot = new File(bundleRoot, pathPrefix);

	public RoboVMAssets(RoboVMGame game) {
		super(game.asyn());
		this.game = game;
		this.net = new RoboVMNet(game.asyn());
	}

	public void setPathPrefix(String pathPrefix) {
		Assets.pathPrefix = pathPrefix;
		this.assetRoot = new File(bundleRoot, pathPrefix);
	}

	@Override
	public Image getRemoteImage(final String url, final int width,
			final int height) {
		final ImageImpl image = createImage(true, width, height, url);
		net.req(url).execute()
				.onSuccess(new Port<RoboVMAbstractNet.Response>() {
					public void onEmit(RoboVMAbstractNet.Response rsp) {
						try {
							image.succeed(toData(Scale.ONE, new UIImage(
									new NSData(rsp.payload()))));
						} catch (Throwable t) {
							game.log().warn(
									"Failed to decode remote image [url=" + url
											+ "]", t);
							image.fail(t);
						}
					}
				}).onFailure(new Port<Throwable>() {
					public void onEmit(Throwable cause) {
						image.fail(cause);
					}
				});
		return image;
	}

	@Override
	public Sound getSound(String path) {
		return createSound(path, false);
	}

	@Override
	public Sound getMusic(String path) {
		return createSound(path, true);
	}

	@Override
	public String getTextSync(String path) throws Exception {
		game.log().debug("Loading text " + path);
		return new String(getBytesSync(path), "UTF-8");
	}

	@Override
	public byte[] getBytesSync(String path) throws Exception {
		File fullPath = resolvePath(path);
		game.log().debug("Loading bytes " + fullPath);
		FileInputStream in = new FileInputStream(fullPath);
		try {
			byte[] data = new byte[(int) fullPath.length()];
			if (in.read(data) != data.length) {
				throw new IOException("Failed to read entire file: " + fullPath);
			}
			return data;
		} finally {
			in.close();
		}
	}

	@Override
	protected ImageImpl.Data load(String path) throws Exception {
		Exception error = null;
		for (Scale.ScaledResource rsrc : game.graphics().scale()
				.getScaledResources(path)) {
			File fullPath = resolvePath(rsrc.path);
			if (!fullPath.exists()) {
				continue;
			}
			try {
				UIImage img = new UIImage(fullPath);
				if (img != null) {
					return toData(rsrc.scale, img);
				}
			} catch (Exception ex) {
				game.log().warn("Failed to load image '" + fullPath + "'.");
				error = new Exception("Failed to load " + fullPath);
			}
		}
		if (error == null) {
			File fullPath = resolvePath(path);
			game.log().warn("Missing image '" + fullPath + "'.");
			error = new FileNotFoundException(fullPath.toString());
		}
		throw error;
	}

	@Override
	protected ImageImpl createImage(boolean async, int rwid, int rhei,
			String source) {
		return new RoboVMImage(game, async, rwid, rhei, source);
	}

	private ImageImpl.Data toData(Scale scale, UIImage image) {
		CGImage bitmap = image.getCGImage();
		return new ImageImpl.Data(scale, bitmap, (int) bitmap.getWidth(),
				(int) bitmap.getHeight());
	}

	protected File resolvePath(String path) {
		File file = new File(assetRoot, path);
		if (!file.exists()) {
			path = getPath(path);
			if (path.startsWith(LSystem.FRAMEWORK_IMG_NAME)) {
				path = IOS_DEF_RES + path;
			}
			file = new File(assetRoot, path);
			if (!file.exists()
					&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
				file =  new File(assetRoot,path.substring(path.indexOf('/') + 1, path.length()));
			}
			if (!file.exists()
					&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
				file = new File(LSystem.getFileName(path = file.getAbsolutePath()));
			}
			if (!file.exists()) {
				file =  new File(LSystem.getFileName(path = (IOS_DEF_RES + path)));
			}
		}
		return file;
	}

	protected RoboVMAudio _audio;

	protected RoboVMAudio getNativeAudio() {
		if (_audio == null) {
			_audio = new RoboVMAudio(game, game.config.openALSources);
		}
		return _audio;
	}

	private Sound createSound(String path, boolean isMusic) {
		if (_audio == null) {
			_audio = new RoboVMAudio(game, game.config.openALSources);
		}
		for (String encpath : new String[] { path + ".caf", path + ".aifc",
				path + ".mp3" }) {
			File fullPath = resolvePath(encpath);
			if (!fullPath.exists()) {
				continue;
			}
			return _audio.createSound(fullPath, isMusic);
		}

		game.log().warn("Missing sound: " + path);
		return new Sound.Error(new FileNotFoundException(path));
	}
}
